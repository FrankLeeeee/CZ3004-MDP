#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021

gRPC server for interfacing with the PC.
"""
import argparse
import asyncio

from config import GRPCServerConfig
from core import grpc_service_pb2_grpc
from core.arduino_service_pb2_serial import ArduinoRPCServiceStub
from core.grpc_aio_server import GRPCAioServer
from core.message_pb2 import Status, MetricResponse, Position
from server.serial_comm import SerialAioChannel
from utils.logger import Logger


class ControlServicer(grpc_service_pb2_grpc.GRPCServiceServicer):

    def __init__(self, host, config: GRPCServerConfig, serial_channel: SerialAioChannel):
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
    def __init__(self, host: str, config: GRPCServerConfig, **deps):
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


def get_args():
    parser = argparse.ArgumentParser(description='Model inference gRPC server.')
    parser.add_argument('-t', '--thread-num', type=int, help='thread concurrency of each gRPC process')
    parser.add_argument('--host', type=str, default='localhost', help='Host name or IP. Default to localhost.')
    parser.add_argument('-p', '--port', type=int, help='gRPC bind port number.')
    args = parser.parse_args()

    return args


async def runner():
    await server.start()
    await server.join()


@BackendRPCServer.register_hook('before_server_start')
async def before_server_start(loop):
    await serial_channel.start(loop=loop)


@BackendRPCServer.register_hook('after_server_stop')
def after_server_stop(loop):
    serial_channel.close()


if __name__ == '__main__':
    args_ = get_args()

    update_config_data = {
        'port': args_.port,
        'thread_num': args_.thread_num,
    }
    config = GRPCServerConfig.parse_obj(
        GRPCServerConfig.parse_obj(update_config_data).dict(exclude_none=True)
    )

    host_ = args_.host

    print(f'Using configuration:\n{config}')

    serial_channel = SerialAioChannel('/dev/cu.usbmodem1411401')
    server = BackendRPCServer(host=host_, config=config, serial_channel=serial_channel)

    loop = asyncio.get_event_loop()

    loop.create_task(runner())
    loop.run_forever()
