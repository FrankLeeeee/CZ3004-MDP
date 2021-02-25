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
from typing import Optional

import serial_asyncio
from functools import partial

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
        self._buffer += data
        if b'\n' in self._buffer:
            lines = self._buffer.split(b'\n')
            self._buffer = lines[-1]  # whatever was left over
            for line in lines[:-1]:
                asyncio.ensure_future(self._queue.put(line))
        print(data)

    def connection_lost(self, exc):
        self._logger.error(f'Connection lost, reason: {exc}')


class SerialAioTransport(object):
    def __init__(self, url, baudrate=115200, loop=None):
        self.url = url
        self.baudrate = baudrate
        self.transport: Optional[serial_asyncio.SerialTransport] = None
        self.protocol = None

        self._loop = loop or asyncio.get_event_loop()
        self._queue = asyncio.Queue(loop=loop)
        self._logger = Logger('Serial Server')
        self.protocol_cls = partial(SerialProtocol, self._queue, self._logger)

    async def start(self):
        self.transport, self.protocol = await serial_asyncio.create_serial_connection(
            self._loop, self.protocol_cls, self.url, baudrate=self.baudrate
        )
        await asyncio.sleep(3)

    async def read(self):
        return await self._queue.get()

    async def write(self, data: bytes):
        # FIXME: self.transport.write not working for windows (nt)
        # self.transport.write(data)
        return await self._loop.run_in_executor(None, self.transport.serial.write, data)

    def close(self):
        self.transport.close()
