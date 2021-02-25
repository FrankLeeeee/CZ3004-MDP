#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021

gRPC server for interfacing with the PC.
"""
import asyncio
import signal

import grpc


class GRPCAioServer(object):
    """gRPC asyncio server is a template server class.
    Args:
        host (str): Host address. Default listening on [::].
        port (int): Binding port number. Default to value in `grpc_config.port`.
        thread_concurrency (int): Number of thread concurrency. Default to value in `grpc_config.thread_num`.
    Attributes:
        server (grpc.aio.Server): gRPC Aio Server.
    References:
        https://www.roguelynn.com/words/asyncio-graceful-shutdowns/
    """

    def __init__(
            self,
            servicer_cls,
            add_servicer_to_server_func,
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
        self.servicer = servicer_cls(*args, **kwargs)
        add_servicer_to_server_func(self.servicer, self.server)
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
        if hasattr(self.servicer, 'terminate'):
            self.servicer.terminate()

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
