import asyncio
import inspect
import signal
from collections import defaultdict
from concurrent import futures
from typing import Dict, Type, Callable, Any

import grpc

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

    _hooks = defaultdict(list)

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

    @classmethod
    def register_hook(
            cls,
            event: str,
    ):
        """Works as a decorator."""
        def _decorator(hook: Callable[[asyncio.AbstractEventLoop], Any]):
            event_names = ['before_server_start', 'after_server_start', 'before_server_stop', 'after_server_stop']
            assert event in event_names, f'No such event name, expected one of {event_names}, ' \
                                         f'got {event}'
            cls._hooks[event].append(hook)

        return _decorator

    async def _exec_hook(self, hook: Callable[[asyncio.AbstractEventLoop], Any]):
        loop = asyncio.get_event_loop()
        try:
            if inspect.iscoroutinefunction(hook):
                await hook(loop)
            else:
                hook(loop)
        except Exception as exc:
            self.logger.error(f'When executing {hook}, got Exception: {exc}')

    async def start(self):
        """Start gRPC server."""
        loop = asyncio.get_event_loop()
        for s in (signal.SIGHUP, signal.SIGTERM, signal.SIGINT):
            loop.add_signal_handler(s, lambda sig_num=s: asyncio.create_task(self.shutdown(sig_num)))

        # run before sever start hooks
        for hook in self._hooks['before_server_start']:
            await self._exec_hook(hook)

        await self.server.start()

        self.logger.info(f'Listening on {self.bind_address}.')

        # run after sever start hooks
        for hook in self._hooks['after_server_start']:
            await self._exec_hook(hook)

    async def join(self):
        await self.server.wait_for_termination()

    async def shutdown(self, s=signal.SIGINT):
        """Stop gRPC server."""
        loop = asyncio.get_event_loop()

        # run before sever stop hooks
        for hook in self._hooks['before_server_stop']:
            await self._exec_hook(hook)

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

        # run after sever stop hooks
        for hook in self._hooks['after_server_stop']:
            await self._exec_hook(hook)

        loop.stop()
        self.logger.info('gRPC server stopped.')
