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
import inspect
from typing import Optional
from uuid import uuid4

import bluetooth

from config import config
from core import message_pb2 as core_dot_message__pb2
from core.bt_service_pb2_serial import add_bt_rpc_servicer_to_server, BtRPCServiceServicer
from core.message_pb2 import EchoResponse, RobotInfo, TurnRequest, Status, Position, EmptyRequest, MoveRequest, \
    EchoRequest, RobotStatus, RobotMode
from core.robot_context import RobotContext
from server.serial_channel import SerialAioChannel
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
        self._handler = dict()
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
                data = await self._loop.run_in_executor(None, self._client_sock.recv, BUFFER_SIZE)
                self._logger.debug(f'Receive: {data}')
                self._buffer += data
                if BT_MESSAGE_SEPARATOR in self._buffer:
                    lines = self._buffer.split(BT_MESSAGE_SEPARATOR)
                    self._buffer = lines[-1]  # whatever was left over
                    for line in lines[:-1]:
                        data: str = line.decode()
                        method, request = data.split('\\')
                        await self._queue.put((method, request.encode()))
        except OSError:
            self._logger.info('Connection lost.')

    async def _runner(self):
        while True:
            method, request = await self._queue.get()
            try:
                handler = self._handler[method]
                if inspect.iscoroutinefunction(handler):
                    response = await handler(request)
                else:
                    response = handler(request)
            except Exception as exc:
                response = str(exc)
                self._logger.error(f'Exception when executing RPC call {method} with {request}')
            response = response.encode()
            async with self._channel_lock:
                self._logger.debug(response)
                await self._loop.run_in_executor(None, self._client_sock.send, response)

    def stop(self):
        self._client_sock.close()
        self._server_sock.close()

    def add_generic_rpc_handlers(self, handlers):
        self._handler.update(handlers)


class BluetoothControlServicer(BtRPCServiceServicer):

    def __init__(self, uart_serial_channel: SerialAioChannel, context: RobotContext):
        self.uart_serial_channel = uart_serial_channel
        self.context = context

    async def Echo(self, request: EchoRequest) -> EchoResponse:
        return EchoResponse(message=request.message, status=True)

    async def Forward(self, request: MoveRequest) -> RobotInfo:
        step = request.step
        if step < 0 or step > 20 - 2:  # robot is 3 x 3
            raise ValueError(f'step in {request} should not be less than 0 or larger than 20.')
        # client = ArduinoRPCServiceStub(self.uart_serial_channel)
        await self.context.set_robot_status(RobotStatus.FORWARD)
        # response = await client.Forward(request)
        await self.context.set_robot_status(RobotStatus.STOP)
        await self.context.set_forward(step=request.step)
        response = await self.context.get_robot_info()
        return response

    async def TurnLeft(self, request: TurnRequest) -> RobotInfo:
        angle = request.angle
        if angle < 0 or angle > 180:
            raise ValueError(f'angle in {request.angle} should not be less than 0 or larger than 180.')
        elif angle % 90 != 0:
            raise ValueError(f'angle in {request.angle} should be a multiple of 90.')
        # client = ArduinoRPCServiceStub(self.uart_serial_channel)
        await self.context.set_robot_status(RobotStatus.TURN_LEFT)
        # response = await client.TurnLeft(request)
        await self.context.set_robot_status(RobotStatus.STOP)
        await self.context.set_turn(-request.angle)
        return await self.context.get_robot_info()

    async def TurnRight(self, request: TurnRequest) -> RobotInfo:
        angle = request.angle
        if angle < 0 or angle > 180:
            raise ValueError(f'angle in {request.angle} should not be less than 0 or larger than 180.')
        elif angle % 90 != 0:
            raise ValueError(f'angle in {request.angle} should be a multiple of 90.')
        # client = ArduinoRPCServiceStub(self.uart_serial_channel)
        await self.context.set_robot_status(RobotStatus.TURN_RIGHT)
        # response = await client.TurnRight(request)
        await self.context.set_robot_status(RobotStatus.STOP)
        await self.context.set_turn(request.angle)
        return await self.context.get_robot_info()

    async def GetRobotInfo(self, request: EmptyRequest) -> RobotInfo:
        return await self.context.get_robot_info()

    async def SetPosition(self, request: Position) -> Status:
        await self.context.set_position(request)
        return self.context.get_robot_info()

    async def SetWayPoint(self, request: Position) -> Status:
        pass

    def RemoveWayPoint(self, request: EmptyRequest) -> Status:
        pass

    def SetRobotMode(self, request: RobotMode) -> Status:
        pass

    def Terminate(self, request: EmptyRequest) -> Status:
        pass


async def test():
    loop = asyncio.get_event_loop()
    context = RobotContext(loop)
    uart_channel = SerialAioChannel(url=config.serial_url)
    servicer = BluetoothControlServicer(uart_serial_channel=object(), context=context)
    server = BluetoothAioServer()
    add_bt_rpc_servicer_to_server(servicer, server)

    await server.start()
    await server.accept()

    await asyncio.sleep(600)


if __name__ == '__main__':
    asyncio.run(test())
