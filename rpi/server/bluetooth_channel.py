#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Author: Albert Huang <albert@csail.mit.edu>
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021

Bluetooth communication for interfacing with the tablet.
"""
import asyncio
from typing import Optional, Callable
from uuid import uuid4

import bluetooth

from utils.constants import BT_MESSAGE_SEPARATOR
from utils.logger import Logger

BUFFER_SIZE = 1024


class BluetoothAioServer(object):

    def __init__(self, bd_address=None, port=7, uuid=None, proto=bluetooth.RFCOMM):
        self.bd_address = bd_address or bluetooth.read_local_bdaddr()[0]
        self.port = port
        self.uuid = uuid or str(uuid4())
        self.proto = proto

        self._loop: Optional[asyncio.AbstractEventLoop] = None
        self._queue: Optional[asyncio.Queue] = None
        self._channel_lock = asyncio.Lock(loop=self._loop)
        self._server_sock: Optional[bluetooth.BluetoothSocket] = None
        self._client_sock: Optional[bluetooth.BluetoothSocket] = None
        self._logger = Logger('Bluetooth Channel', welcome=False, severity_levels={'StreamHandler': 'DEBUG'})

    async def start(self, loop=None):
        self._loop = loop or asyncio.get_event_loop()
        self._queue = asyncio.Queue(loop=loop)

        self._server_sock = bluetooth.BluetoothSocket(self.proto)
        self._server_sock.bind((self.bd_address, self.port))
        await self._loop.run_in_executor(None, self._server_sock.listen, 1)

        bluetooth.advertise_service(
            self._server_sock, 'Bluetooth Channel', service_id=self.uuid,
            service_classes=[self.uuid, bluetooth.SERIAL_PORT_CLASS],
            profiles=[bluetooth.SERIAL_PORT_PROFILE],
            # protocols=[bluetooth.OBEX_UUID]
        )

        self._logger.info(f'Listening on device {self.bd_address} at port {self.port}. UUID={self.uuid}')

    async def accept(self, timeout: Optional[int] = None):
        future = self._loop.run_in_executor(None, self._server_sock.accept)
        client_sock, client_info = await asyncio.wait_for(future, timeout=timeout, loop=self._loop)
        self._client_sock = client_sock

        # start the data receive task
        self._loop.create_task(self._data_receive_task())

        self._logger.info(f'Accepting from {client_info}')

    async def _data_receive_task(self):
        try:
            while True:
                data = self._client_sock.recv(BUFFER_SIZE)
                self._logger.debug(f'Receive: {data}')
                await self._queue.put(data)
        except OSError:
            self._logger.info('Connection lost.')

    async def read_channel(self):
        return await self._queue.get()

    async def write_channel(self, data: bytes):
        await self._loop.run_in_executor(None, self._client_sock.send(data))

    def stop(self):
        self._client_sock.close()
        self._server_sock.close()

    def unary_unary(self, method: bytes, request_serializer: Callable, response_deserializer: Callable):
        """Creates a UnaryUnaryMultiCallable for a unary-unary method."""

        async def _callable(request):
            request_data: bytes = request_serializer(request)
            if BT_MESSAGE_SEPARATOR in request_data:
                raise ValueError(
                    f'Invalid character {BT_MESSAGE_SEPARATOR} found at '
                    f'char({request_data.index(BT_MESSAGE_SEPARATOR)})'
                    f'in request data: {request_data}')
            await self.write_channel(method + request_data + BT_MESSAGE_SEPARATOR)
            response = await self.read_channel()
            return response_deserializer(response)

        return _callable


def unary_unary_rpc_method_handler(behavior,
                                   request_deserializer=None,
                                   response_serializer=None):
    def _handler(request_data: bytes):
        request = request_deserializer(request_data)
        response = behavior(request)
        return response_serializer(response)

    return _handler


class BluetoothControlServicer(object):

    async def HeartBeat(self, request: bytes):
        return request


async def test():
    server = BluetoothAioServer()
    await server.start()
    await server.accept()

    await server.read_channel()
    server.stop()


if __name__ == '__main__':
    asyncio.run(test())
