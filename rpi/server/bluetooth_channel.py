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
from typing import Optional
from uuid import uuid4

import bluetooth

from core.bt_service_pb2_serial import add_bt_rpc_servicer_to_server
from core.message_pb2 import EchoResponse, Position
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
        self._handler = list()
        self._buffer = b''

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
        self._loop.create_task(self._runner())

        self._logger.info(f'Accepting from {client_info}')

    async def _data_receive_task(self):
        try:
            while True:
                data = self._client_sock.recv(BUFFER_SIZE)
                self._logger.debug(f'Receive: {data}')
                self._buffer += data
                if BT_MESSAGE_SEPARATOR in self._buffer:
                    lines = self._buffer.split(BT_MESSAGE_SEPARATOR)
                    self._buffer = lines[-1]  # whatever was left over
                    for line in lines[:-1]:
                        data: str = line.decode()
                        method, request = data.split('\\')
                        await self._queue.put((method, request))
        except OSError:
            self._logger.info('Connection lost.')

    async def _runner(self):
        method, request = await self._queue.get()
        response = await self._handler[method](request)
        response = response.encode()
        with self._channel_lock:
            await self._loop.run_in_executor(None, self._client_sock.send(response))

    def stop(self):
        self._client_sock.close()
        self._server_sock.close()

    def add_generic_rpc_handlers(self, handlers):
        self._handler = handlers


class BluetoothControlServicer(object):

    async def Echo(self, request):
        return EchoResponse(request.message)

    async def Forward(self, request):
        return Position(request)


async def test():
    servicer = BluetoothControlServicer()
    server = BluetoothAioServer()
    add_bt_rpc_servicer_to_server(servicer, server)

    await server.start()
    await server.accept()

    while True:
        pass


if __name__ == '__main__':
    asyncio.run(test())
