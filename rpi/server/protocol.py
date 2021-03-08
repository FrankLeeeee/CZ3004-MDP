#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 3/8/2021

Serial communication for interfacing with the Arduino.

Reference:
    * https://tinkering.xyz/async-serial/
    * https://pyserial-asyncio.readthedocs.io/en/latest/shortintro.html
"""
import asyncio
from typing import Optional

import serial_asyncio

from utils.constants import SERIAL_MESSAGE_SEPARATOR, BT_MESSAGE_SEPARATOR
from utils.logger import Logger


class UartProtocol(asyncio.Protocol):
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
        self._logger.debug(f'Read {data}')
        self._buffer += data
        if SERIAL_MESSAGE_SEPARATOR in self._buffer:
            lines = self._buffer.split(SERIAL_MESSAGE_SEPARATOR)
            self._buffer = lines[-1]  # whatever was left over
            for line in lines[:-1]:
                asyncio.ensure_future(self._queue.put(line))

    def connection_lost(self, exc):
        self._logger.info(f'Connection lost, reason: {exc}')


class BluetoothProtocol(asyncio.Protocol):

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
        """Store characters until a `BT_MESSAGE_SEPARATOR` is received."""
        self._logger.debug(f'Read {data}')
        self._buffer += data
        if BT_MESSAGE_SEPARATOR in self._buffer:
            lines = self._buffer.split(BT_MESSAGE_SEPARATOR)
            self._buffer = lines[-1]  # whatever was left over
            for line in lines[:-1]:
                data: str = line.decode()
                method, request = data.split('\\')
                asyncio.ensure_future(self._queue.put((method, request.encode())))

    def connection_lost(self, exc):
        self._logger.info(f'Connection lost, reason: {exc}')
