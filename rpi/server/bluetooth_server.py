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
from concurrent.futures.thread import ThreadPoolExecutor

import bluetooth

BD_ADDRESS = 'B8:27:EB:E6:BF:AA'
PORT = 4
BUFFER_SIZE = 1024

uuid = '94f39d29-7d6d-437d-973b-fba39e49d4ee'


class BluetoothServer(object):

    def __init__(self):
        self.server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
        self.server_sock.bind((BD_ADDRESS, PORT))
        self.server_sock.listen(1)

        bluetooth.advertise_service(
            self.server_sock, 'SampleServer', service_id=uuid,
            service_classes=[uuid, bluetooth.SERIAL_PORT_CLASS],
            profiles=[bluetooth.SERIAL_PORT_PROFILE],
            # protocols=[bluetooth.OBEX_UUID]
        )

        self.client_socks = list()

        self._executor = ThreadPoolExecutor(max_workers=4)
        self._loop = asyncio.get_event_loop()

    async def accept(self, timeout: float = None):
        future = self._loop.run_in_executor(self._executor, self.server_sock.accept)
        client_sock, client_info = await asyncio.wait_for(future, timeout=timeout, loop=self._loop)
        self.client_socks.append(client_sock)
        return client_sock, client_info

    async def disconnect(self, client_sock):
        return await self._loop.run_in_executor(self._executor, client_sock.close)

    async def receive(self, buffer: int = BUFFER_SIZE, timeout: float = None):
        future = self._loop.run_in_executor(self._executor, self.client_socks[0].recv, buffer)
        data = await asyncio.wait_for(future, timeout=timeout, loop=self._loop)
        return data

    async def close(self):
        await asyncio.gather(self.disconnect(client_sock) for client_sock in self.client_socks)
        await self._loop.run_in_executor(self._executor, self.server_sock.close)


server = BluetoothServer()
_, client_info = server.accept()
print('Waiting for connection on RFCOMM channel', PORT)

print('Accepted connection from', client_info)

try:
    while True:
        data = server.receive(1024)
        if not data:
            break
        print('Received', data)
except OSError:
    pass

print('Disconnected.')

server.close()
print('All done.')
