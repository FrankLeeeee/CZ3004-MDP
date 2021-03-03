#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021

Serial communication for interfacing with the Arduino.

Reference:
    * https://tinkering.xyz/async-serial/
    * https://pyserial-asyncio.readthedocs.io/en/latest/shortintro.html
"""
import asyncio
import os
from typing import Optional, Callable

import serial_asyncio
from functools import partial

from utils.constants import SEPARATOR
from utils.logger import Logger


class SerialProtocol(asyncio.Protocol):
    def __init__(self, queue: asyncio.Queue, logger: Logger):
        """Store the queue.
        """
        super().__init__()
        self._queue = queue
        self._buffer: Optional[bytes] = None
        self._logger = logger

    def connection_made(self, transport: serial_asyncio.SerialTransport):
        """Store the serial transport and prepare to receive data.
        """
        self._buffer = bytes()
        transport.serial.rts = False
        transport.flush()
        self._logger.info('Reader connection created')

    def data_received(self, data):
        """Store characters until a newline is received.
        """
        self._logger.debug(data)
        self._buffer += data
        if SEPARATOR in self._buffer:
            lines = self._buffer.split(SEPARATOR)
            self._buffer = lines[-1]  # whatever was left over
            for line in lines[:-1]:
                asyncio.ensure_future(self._queue.put(line))

    def connection_lost(self, exc):
        self._logger.info(f'Connection lost, reason: {exc}')


class SerialAioChannel(object):
    def __init__(self, url, baudrate=115200, loop=None):
        self.url = url
        self.baudrate = baudrate
        self.transport: Optional[serial_asyncio.SerialTransport] = None
        self.protocol = None

        self._loop: Optional[asyncio.AbstractEventLoop] = None
        self._queue: Optional[asyncio.Queue] = None
        self._channel_lock = asyncio.Lock(loop=loop)
        self._logger = Logger('Serial Server', welcome=False, severity_levels={'StreamHandler': 'DEBUG'})
        self.protocol_cls = None

    async def start(self, loop=None):
        self._loop = loop or asyncio.get_event_loop()
        self._queue = asyncio.Queue(loop=loop)
        self.protocol_cls = partial(SerialProtocol, self._queue, self._logger)
        self.transport, self.protocol = await serial_asyncio.create_serial_connection(
            self._loop, self.protocol_cls, self.url, baudrate=self.baudrate
        )
        await asyncio.sleep(3)

        self._logger.info(f'Listening on device {self.url} with {self.baudrate}')

    async def read_channel(self):
        return await self._queue.get()

    async def write_channel(self, data: bytes):
        self._logger.debug(data)
        # self.transport.write not working for windows (nt) FIXME: pyserial-asyncio v0.5
        async with self._channel_lock:
            if os.name == "nt":
                await self._loop.run_in_executor(None, self.transport.serial.write_channel, data)
            else:
                self.transport.write(data)

    def close(self):
        self.transport.close()

    def unary_unary(self, method: bytes, request_serializer: Callable, response_deserializer: Callable):
        """Creates a UnaryUnaryMultiCallable for a unary-unary method."""

        async def _callable(request):
            request_data: bytes = request_serializer(request)
            if SEPARATOR in request_data:
                raise ValueError(f'Invalid character {SEPARATOR} found at char({request_data.index(SEPARATOR)})'
                                 f'in request data: {request_data}')
            await self.write_channel(method + request_data + SEPARATOR)
            response = await self.read_channel()
            return response_deserializer(response)

        return _callable

    async def __aenter__(self):
        await self.start()
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        self.close()
