import asyncio
import inspect

from config import SerialDeviceConfig
from core.serial.channel import SerialAioChannel
from utils.logger import Logger


class SerialAioServer(object):
    def __init__(self, config: SerialDeviceConfig):
        self._channel = SerialAioChannel(config)
        self._handler = dict()
        self.is_running = False

    @property
    def logger(self) -> Logger:
        return getattr(self._channel, '_logger')

    async def start(self, loop=None):
        loop = loop or asyncio.get_event_loop()
        self.is_running = True
        await self._channel.start(loop=loop)
        loop.create_task(self._runner())

    def add_generic_rpc_handlers(self, handlers):
        self._handler.update(handlers)

    async def _runner(self):
        while self.is_running:
            method, request = await self._channel.read_channel()
            try:
                handler = self._handler[method]
                if inspect.iscoroutinefunction(handler):
                    response = await handler(request)
                else:
                    response = handler(request)
            except Exception as exc:
                response = str(exc)
                self.logger.error(f'Exception when executing RPC call {method} with {request}')
            response = response.encode()
            await self._channel.write_channel(response)

    async def stop(self):
        await self._channel.close()
        self.is_running = False
