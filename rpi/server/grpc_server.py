#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021

gRPC server for interfacing with the PC.
"""
import asyncio

from config import ServerConfig, config
from core import grpc_service_pb2_grpc
from core.arduino_service_pb2_serial import ArduinoRPCServiceStub
from core.bt_service_pb2_serial import add_bt_rpc_servicer_to_server
from core.grpc_aio_server import GRPCAioServer
from core.message_pb2 import MetricResponse, RobotStatus, Status
from core.robot_context import RobotContext
from core.serial.channel import SerialAioChannel
from core.serial.server import SerialAioServer
from server.bluetooth_channel import BluetoothControlServicer
from utils.logger import Logger


class ControlServicer(grpc_service_pb2_grpc.GRPCServiceServicer):

    def __init__(self, host, config: ServerConfig, serial_channel: SerialAioChannel, context: RobotContext):
        self.host = host
        self.port = config.port
        self.serial_channel = serial_channel
        self.context = context
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
        await self.context.set_robot_status(RobotStatus.FORWARD)
        response = await serial_client.Forward(request)
        await self.context.set_robot_status(RobotStatus.STOP)
        await self.context.set_forward(step=request.step)
        return response

    async def TurnLeft(self, request, context):
        if request.angle > 180:
            self._logger.error(f'Invalid request: {request}, angle should be less than or equal to 180.')
            return MetricResponse(status=False)
        await self.context.set_robot_status(RobotStatus.TURN_LEFT)
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.TurnLeft(request)
        await self.context.set_robot_status(RobotStatus.STOP)
        await self.context.set_turn(angle=-request.angle)
        return response

    async def TurnRight(self, request, context):
        await self.context.set_robot_status(RobotStatus.TURN_RIGHT)
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.TurnRight(request)
        await self.context.set_robot_status(RobotStatus.STOP)
        await self.context.set_turn(angle=request.angle)
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

    async def SetMap(self, request, context):
        await self.context.set_map(request)
        return Status(status=True)

    async def GetWayPoint(self, request, context):
        return await self.context.get_way_point()


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
def after_server_stop(loop):  # noqa
    uart_channel.close()
    bt_server.stop()


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
