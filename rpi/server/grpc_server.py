#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021

gRPC server for interfacing with the PC.
"""
import asyncio
import io
from typing import Tuple

import cv2
import numpy as np
import picamera

from config import ServerConfig, config
from core.common import Label2
from core.grpc_aio_server import GRPCAioServer
from core.robot_context import RobotContext
from core.serial.channel import SerialAioChannel
from core.serial.server import SerialAioServer
from image_recognition.client import detect, CLASS_COLORS
from image_recognition.server.prediction import DarknetModel
from proto import grpc_service_pb2_grpc
from proto.arduino_service_pb2_serial import ArduinoRPCServiceStub
from proto.bt_service_pb2_serial import add_bt_rpc_servicer_to_server
from proto.message_pb2 import MetricResponse, RobotStatus, Status, Position, ImagePosition, ImageResponse
from server.bluetooth_channel import BluetoothControlServicer
from utils.constants import IMAGE_ROOT_DIR
from utils.logger import Logger


class ControlServicer(grpc_service_pb2_grpc.GRPCServiceServicer):

    def __init__(self, host, config: ServerConfig, serial_channel: SerialAioChannel, context: RobotContext):
        self.host = host
        self.port = config.port
        self.serial_channel = serial_channel
        self.recognition_server_url = config.recognition_server_url
        self.context = context
        self.camera = None
        self.image_paths = set()

        # init the camera module
        if config.camera:
            self.camera = picamera.PiCamera()
            self.camera.start_preview()
            # FIXME: may require a sleep

        self._logger = Logger('Backend gRPC server', welcome=False, severity_levels={'StreamHandler': 'DEBUG'})

    @staticmethod
    def get_target_coord(bbox: Tuple[float, float, float, float]):
        """Get image coordination when RPi is configured in a right wall hugging manner."""
        x, y, w, h = bbox

        # find the image pasted on left block, middle block or right block
        detection_center = x + w / 2
        block_offset = 0
        if detection_center <= 1 / 3:
            block_offset = -1
        elif detection_center > 2 / 3:
            block_offset = 1
        # FIXME: try the image size to get distance?
        distance_offset = 2

        robot_position = context.get_position()
        x, y = robot_position.x, robot_position.y
        direction = robot_position.dir
        if direction == Position.Direction.WEST:
            # RPi facing north
            return x + block_offset, y + distance_offset
        elif direction == Position.Direction.NORTH:
            # RPi facing east
            return x + distance_offset, y - block_offset
        elif direction == Position.Direction.EAST:
            # RPi facing south
            return x - block_offset, y - distance_offset
        else:
            # RPi facing west
            return x - distance_offset, y + block_offset

    async def Echo(self, request, context):
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.Echo(request)
        return response

    async def Forward(self, request, context):
        if request.step > 255:
            self._logger.error(f'Invalid request: {request}, step should be less than or equal to 255.')
            return MetricResponse(status=False)
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        self.context.set_robot_status(RobotStatus.FORWARD)
        response = await serial_client.Forward(request)
        self.context.set_robot_status(RobotStatus.STOP)
        await self.context.set_forward(step=request.step)
        return response

    async def TurnLeft(self, request, context):
        if request.angle > 180:
            self._logger.error(f'Invalid request: {request}, angle should be less than or equal to 180.')
            return MetricResponse(status=False)
        self.context.set_robot_status(RobotStatus.TURN_LEFT)
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.TurnLeft(request)
        self.context.set_robot_status(RobotStatus.STOP)
        self.context.set_turn(angle=-request.angle)
        return response

    async def TurnRight(self, request, context):
        self.context.set_robot_status(RobotStatus.TURN_RIGHT)
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.TurnRight(request)
        self.context.set_robot_status(RobotStatus.STOP)
        self.context.set_turn(angle=request.angle)
        return response

    async def Calibrate(self, request, context):
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.Calibration(request)
        return response

    async def WaitForRobotStart(self, request, context):
        await self.context.start_flag.wait()
        return Status(status=True)

    async def GetMetrics(self, request, context):
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.GetMetrics(request)
        return response

    async def StopRobot(self, request, context):
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.Terminate(request)
        return response

    def SetMap(self, request, context):
        self.context.set_map(request)
        return Status(status=True)

    def GetWayPoint(self, request, context):
        return self.context.get_way_point()

    async def TakePhoto(self, request, context):
        loop = asyncio.get_event_loop()
        with io.BytesIO() as stream:
            self.camera.capture(stream, format='jpeg')
            result = await detect(stream.getvalue(), self.recognition_server_url)
            if result:
                # take only one
                class_name = result['class_names'][-1]
                confidence = result['confidence'][-1]
                bbox = result['bbox'][-1]
                image_id = Label2[class_name.upper()].value

                # get target image's coordinates
                x, y = self.get_target_coord(bbox)
                self._logger.debug((x, y))
                # send to updated coordinates to android
                image_position = ImagePosition(id=image_id, x=x, y=y)
                self.context.set_image_positions(image_position)

                # save image photo
                image_np = np.frombuffer(stream.getvalue(), dtype=np.uint8)
                detection = {'class_names': [class_name], 'confidence': [confidence], 'bbox': [bbox]}
                loop.run_in_executor(None, self.save_photo, image_np, detection,
                                     f'{IMAGE_ROOT_DIR / f"{image_id}.jpg"}')
            image_np = np.frombuffer(stream.getvalue(), dtype=np.uint8)
            with open('xxx.jpg', 'wb') as f:
                f.write(image_np)

        status = bool(len(result))
        return Status(status=status)

    def GetImageResult(self, request, context):
        result_image = None
        for image_path in self.image_paths:
            image_path = str(image_path)
            img = cv2.imread(image_path)
            if result_image is None:
                result_image = img.copy()
            else:
                result_image = cv2.hconcat([img, result_image])
        if result_image is not None:
            _, result_image = cv2.imencode('.jpg', result_image)
            response = ImageResponse(raw_image=bytes(result_image))
        else:
            response = ImageResponse()
        return response

    def save_photo(self, image_np, detection, file_name):
        image = cv2.imdecode(image_np, cv2.IMREAD_COLOR)
        annotated_image = DarknetModel.draw_annotations(image, detection, CLASS_COLORS)
        self.image_paths.add(file_name)
        self._logger.info(f'Save result image at {file_name}')
        cv2.imwrite(str(file_name), annotated_image)


class BackendRPCServer(GRPCAioServer):
    def __init__(self, host: str, config: ServerConfig, **deps):
        super().__init__(
            servicers={
                ControlServicer: grpc_service_pb2_grpc.add_GRPCServiceServicer_to_server,
            },
            thread_concurrency=config.thread_num,
            port=config.port,
            kwargs={
                'host': host,
                'config': config,
                **deps,
            }
        )


async def runner():
    await server.start()
    await server.join()


@BackendRPCServer.register_hook('before_server_start')
async def before_server_start(loop):
    context.set_loop(loop)
    await uart_channel.start(loop=loop)
    await bt_server.start()


@BackendRPCServer.register_hook('after_server_stop')
async def after_server_stop(loop):  # noqa
    await uart_channel.close()
    await bt_server.stop()


if __name__ == '__main__':
    host_ = '0.0.0.0'

    print(f'Using configuration:\n{config}')

    context = RobotContext()
    uart_channel = SerialAioChannel(config.uart)
    server = BackendRPCServer(host=host_, config=config, serial_channel=uart_channel, context=context)

    # bluetooth
    bt_servicer = BluetoothControlServicer(uart_channel, context=context)
    bt_server = SerialAioServer(config.bluetooth)
    add_bt_rpc_servicer_to_server(bt_servicer, bt_server)

    loop = asyncio.get_event_loop()

    loop.create_task(runner())
    loop.run_forever()
