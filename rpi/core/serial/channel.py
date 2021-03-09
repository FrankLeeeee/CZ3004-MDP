#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021

Serial communication channel.

Reference:
    * https://tinkering.xyz/async-serial/
    * https://pyserial-asyncio.readthedocs.io/en/latest/shortintro.html
"""
import asyncio
import os
import random
from typing import Optional, Callable

import serial_asyncio
from functools import partial

from serial import SerialException

from config import SerialDeviceConfig
from utils.constants import SERIAL_MESSAGE_SEPARATOR
from utils.logger import Logger


class SerialAioChannel(object):
    def __init__(self, config: SerialDeviceConfig):
        self.config = config
        self.transport: Optional[serial_asyncio.SerialTransport] = None
        self.protocol = None

        self._auto_reconnect_task: Optional[asyncio.Task] = None

        self._loop: Optional[asyncio.AbstractEventLoop] = None
        self._queue: Optional[asyncio.Queue] = None
        self._channel_lock: Optional[asyncio.Lock] = None
        self._logger = Logger(**config.logger.dict(exclude_none=True))
        self.protocol_cls = config.protocol

    async def start(self, loop=None):
        self._logger.info(f'Start with configuration:\n {self.config.json(indent=2)}')
        self._loop = loop or asyncio.get_event_loop()
        self._queue = asyncio.Queue(loop=loop)
        self._channel_lock = asyncio.Lock(loop=loop)

        await self._connection()

        # subscribe for auto reconnect
        self._auto_reconnect_task = self._loop.create_task(self._auto_reconnect_runner())

    async def _connection(self):
        # the idea is from CSMA/CD. Retry 16 times and with each fail, expand the time slot size by 2 times until
        # 1024.
        time_slot = 1
        if self.config.wait_for_connection.enable:
            trial = 1
        else:
            trial = self.config.wait_for_connection.max_retry
        while True:
            try:
                protocol = partial(self.protocol_cls, self._queue, self._logger)
                self.transport, self.protocol = await serial_asyncio.create_serial_connection(
                    self._loop, protocol,
                    **self.config.dict(
                        exclude_none=True,
                        exclude={'logger', 'protocol', 'wait_for_connection', 'auto_reconnect'}
                    )
                )
                break
            except SerialException as exc:
                if trial < self.config.wait_for_connection.max_retry:
                    self._logger.info(exc)
                    backoff_time = random.randint(1, time_slot)
                    time_slot = min(time_slot * 2, 1024)
                    self._logger.warning(f'Attempt #{trial} fail to start device {self.config.url}, '
                                         f'retry after {backoff_time}s...')
                    await asyncio.sleep(backoff_time)
                    trial += 1
                else:
                    self._logger.error(exc)
                    raise exc

        await asyncio.sleep(3)

        self._logger.info(f'Listening on device {self.config.url}.')

    async def _auto_reconnect_runner(self):
        auto_reconnect_config = self.config.auto_reconnect
        while auto_reconnect_config.enable:
            await self.protocol.disconnect.wait()
            await asyncio.sleep(auto_reconnect_config.cooldown)
            await self._connection()

    async def read_channel(self):
        return await self._queue.get()

    async def write_channel(self, data: bytes):
        self._logger.debug(f'Write {data}')
        # self.transport.write not working for windows (nt) FIXME: pyserial-asyncio v0.5
        async with self._channel_lock:
            if os.name == "nt":
                await self._loop.run_in_executor(None, self.transport.serial.write_channel, data)
            else:
                self.transport.write(data)

    def unary_unary(self, method: bytes, request_serializer: Callable, response_deserializer: Callable):
        """Creates a UnaryUnaryMultiCallable for a unary-unary method."""

        async def _callable(request):
            request_data: bytes = request_serializer(request)
            if SERIAL_MESSAGE_SEPARATOR in request_data:
                raise ValueError(f'Invalid character {SERIAL_MESSAGE_SEPARATOR} found at '
                                 f'char({request_data.index(SERIAL_MESSAGE_SEPARATOR)})'
                                 f'in request data: {request_data}')
            await self.write_channel(method + request_data + SERIAL_MESSAGE_SEPARATOR)
            response = await self.read_channel()
            return response_deserializer(response)

        return _callable

    async def close(self):
        self.transport.close()
        if not self._auto_reconnect_task.cancelled():
            self._auto_reconnect_task.cancel()
            await self._auto_reconnect_task

    async def __aenter__(self):
        await self.start()
        return self

    async def __aexit__(self, exc_type, exc_val, exc_tb):
        await self.close()
