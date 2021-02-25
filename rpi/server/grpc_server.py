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
import signal
from concurrent import futures
from typing import Dict, Type, Callable

import grpc

from config import GRPCServerConfig
from core import grpc_service_pb2_grpc
from core.grpc_service_pb2 import MoveResponse, TurnResponse, MetricResponse, PositionResponse
from utils.logger import Logger


class GRPCAioServer(object):
    """gRPC asyncio server is a template server class.
    Args:
        host (str): Host address. Default listening on [::].
        port (int): Binding port number. Default to value in `grpc_config.port`.
        thread_concurrency (int): Number of thread concurrency. Default to value in `grpc_config.thread_num`.
        servicers: A dictionary of servicers for registrations. The key is servicer class, and the value is a
            method defining how to add the service to the server.
    Attributes:
        server (grpc.aio.Server): gRPC Aio Server.
    References:
        https://www.roguelynn.com/words/asyncio-graceful-shutdowns/
    """

    def __init__(
            self,
            servicers: Dict[Type, Callable],
            thread_concurrency,
            host='[::]',
            port=50051,
            args=(),
            kwargs=None,
    ):
        if kwargs is None:
            kwargs = dict()

        self.options = [
            # ('grpc.so_reuseport', 1),  # reusable port
            ('grpc.max_send_message_length', -1),
            ('grpc.max_receive_message_length', -1),
        ]
        self.bind_address = f'{host}:{port}'
        self.thread_concurrency = thread_concurrency
        self.logger = Logger('gRPC server')

        grpc.aio.init_grpc_aio()
        self.server = grpc.aio.server(
            futures.ThreadPoolExecutor(max_workers=self.thread_concurrency),
            options=self.options,
        )
        self.servicers = list()
        for servicer, add_servicer_to_server_func in servicers.items():
            servicer = servicer(*args, **kwargs)
            self.servicers.append(servicer)
            add_servicer_to_server_func(servicer, self.server)
        self.server.add_insecure_port(self.bind_address)

    async def start(self):
        """Start gRPC server."""
        loop = asyncio.get_event_loop()
        for s in (signal.SIGHUP, signal.SIGTERM, signal.SIGINT):
            loop.add_signal_handler(s, lambda sig_num=s: asyncio.create_task(self.shutdown(sig_num)))
        await self.server.start()
        self.logger.info(f'Listening on {self.bind_address}.')

    async def join(self):
        await self.server.wait_for_termination()

    async def shutdown(self, s=signal.SIGINT):
        """Stop gRPC server."""
        loop = asyncio.get_event_loop()
        self.logger.info(f'Receive exit signal {s.name}')
        await self.server.stop(1)

        current_task = asyncio.current_task(loop=loop)
        tasks = list()
        for task in asyncio.all_tasks(loop=loop):
            if task is not current_task:
                if not task.cancelled():
                    task.cancel()
                tasks.append(task)

        await asyncio.gather(*tasks, return_exceptions=True)
        loop.stop()
        self.logger.info('gRPC server stopped.')


class ControlServicer(grpc_service_pb2_grpc.GRPCControlServiceServicer):

    def __init__(self, host, config: GRPCServerConfig):
        self.host = host
        self.port = config.port
        self._logger = Logger('Backend gRPC server')

    def Forward(self, request, context):
        return MoveResponse(status=True)

    def Backward(self, request, context):
        return MoveResponse(status=True)

    def Left(self, request, context):
        return MoveResponse(status=True)

    def Right(self, request, context):
        return MoveResponse(status=True)

    def TurnClockwise(self, request, context):
        return TurnResponse(status=True)

    def TurnAntiClockwise(self, request, context):
        return TurnResponse(status=True)


class DataServicer(grpc_service_pb2_grpc.GRPCDataServiceServicer):
    def __init__(self, host, config: GRPCServerConfig):
        self.host = host
        self.port = config.port
        self._logger = Logger('Backend gRPC server')

    async def GetMetrics(self, request, context):
        id = request.id
        response = MetricResponse()
        response.values[id] = 0.
        return response

    def GetPosition(self, request, context):
        return PositionResponse(status=True)


class BackendRPCServer(GRPCAioServer):
    def __init__(self, host: str, config: GRPCServerConfig):
        super().__init__(
            servicers={
                ControlServicer: grpc_service_pb2_grpc.add_GRPCControlServiceServicer_to_server,
                DataServicer: grpc_service_pb2_grpc.add_GRPCDataServiceServicer_to_server,
            },
            thread_concurrency=config.thread_num,
            port=config.port,
            kwargs={
                'host': host,
                'config': config,
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

    server = BackendRPCServer(host=host_, config=config)
    loop = asyncio.get_event_loop()

    loop.create_task(runner())
    loop.run_forever()
