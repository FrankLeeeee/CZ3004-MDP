#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021

gRPC server for interfacing with the PC.
"""
import asyncio

import yaml

from config import ServerConfig
from core import grpc_service_pb2_grpc
from core.arduino_service_pb2_serial import ArduinoRPCServiceStub
from core.grpc_aio_server import GRPCAioServer
from server.serial_comm import SerialAioChannel
from utils.constants import PROJECT_ROOT_PATH
from utils.logger import Logger


class ControlServicer(grpc_service_pb2_grpc.GRPCServiceServicer):

    def __init__(self, host, config: ServerConfig, serial_channel: SerialAioChannel):
        self.host = host
        self.port = config.port
        self.serial_channel = serial_channel
        self._logger = Logger('Backend gRPC server', welcome=False, severity_levels={'StreamHandler': 'DEBUG'})

    async def Echo(self, request, context):
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.Echo(request)
        return response

    async def Forward(self, request, context):
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.Forward(request)
        return response

    async def TurnLeft(self, request, context):
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.TurnLeft(request)
        return response

    async def TurnRight(self, request, context):
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.TurnRight(request)
        return response

    async def Calibrate(self, request, context):
        serial_client = ArduinoRPCServiceStub(self.serial_channel)
        response = await serial_client.Calibration(request)
        return response


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
    await serial_channel.start(loop=loop)


@BackendRPCServer.register_hook('after_server_stop')
async def after_server_stop(loop):  # noqa
    serial_channel.close()


if __name__ == '__main__':
    with open(PROJECT_ROOT_PATH / 'server/config.yml') as f:
        config = yaml.safe_load(f)
    config = ServerConfig.parse_obj(config)

    host_ = '0.0.0.0'

    print(f'Using configuration:\n{config}')

    serial_channel = SerialAioChannel(config.serial_url, baudrate=config.baudrate)
    server = BackendRPCServer(host=host_, config=config, serial_channel=serial_channel)

    loop = asyncio.get_event_loop()

    loop.create_task(runner())
    loop.run_forever()
