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
from proto.message_pb2 import MetricResponse, RobotStatus, Status, Position, ImagePosition
from server.bluetooth_channel import BluetoothControlServicer
from utils.logger import Logger


class ControlServicer(grpc_service_pb2_grpc.GRPCServiceServicer):

    def __init__(self, host, config: ServerConfig, serial_channel: SerialAioChannel, context: RobotContext):
        self.host = host
        self.port = config.port
        self.serial_channel = serial_channel
        self.recognition_server_url = config.recognition_server_url
        self.context = context
        self.camera = None

        # init the camera module
        if config.camera:
            self.camera = picamera.PiCamera()
            self.camera.start_preview()
            # FIXME: may require a sleep

        self._logger = Logger('Backend gRPC server', welcome=False, severity_levels={'StreamHandler': 'DEBUG'})

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

    # function to get the image's position
    @staticmethod
    def get_target_coord():
        # FIXME
        robot_position = context.get_position()
        x = robot_position.x
        y = robot_position.y
        direction = robot_position.dir
        if direction == Position.Direction.NORTH:
            return x + 2, y
        elif direction == Position.Direction.EAST:
            return x, y - 2
        elif direction == Position.Direction.SOUTH:
            return x - 2, y
        elif direction == Position.Direction.WEST:
            return x, y + 2
        else:
            print("Error - get_target_coord()")

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
                # TODO: get image position here, send to android, and save picture

                image_np = np.frombuffer(stream.getvalue(), dtype=np.uint8)
                detection = {'class_names': [class_name], 'confidence': [confidence], 'bbox': [bbox]}
                loop.run_in_executor(None, self.save_photo, image_np, detection, '1.jpg')

                # get target image's coordinates
                x, y = self.get_target_coord()
                # debug
                print(x, y)

                # send to updated coordinates to android
                response = ImagePosition(id=image_id, x=x, y=y)
                context.set_image_positions(response)

        status = bool(result)
        return Status(status=status)

    def save_photo(self, image_np, detection, file_name):
        image = cv2.imdecode(image_np, cv2.IMREAD_COLOR)
        annotated_image = DarknetModel.draw_annotations(image, detection, CLASS_COLORS)
        self._logger.info(f'Save result image at {file_name}')
        cv2.imwrite(file_name, annotated_image)


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
