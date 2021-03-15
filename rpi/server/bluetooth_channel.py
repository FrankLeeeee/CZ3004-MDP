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

from proto.arduino_service_pb2_serial import ArduinoRPCServiceStub
from proto.bt_service_pb2_serial import BtRPCServiceServicer
from proto.message_pb2 import EchoResponse, RobotInfo, TurnRequest, Status, Position, EmptyRequest, MoveRequest, \
    EchoRequest, RobotStatus, RobotMode
from core.robot_context import RobotContext
from core.serial.channel import SerialAioChannel


class BluetoothControlServicer(BtRPCServiceServicer):

    def __init__(self, uart_serial_channel: SerialAioChannel, context: RobotContext):
        self.uart_serial_channel = uart_serial_channel
        self.context = context

    async def Echo(self, request: EchoRequest) -> EchoResponse:
        client = ArduinoRPCServiceStub(self.uart_serial_channel)
        response = await client.Echo(request)
        return response

    async def Forward(self, request: MoveRequest) -> RobotInfo:
        step = request.step
        if step < 0 or step > 20 - 2:  # robot is 3 x 3
            raise ValueError(f'step in {request} should not be less than 0 or larger than 20.')
        client = ArduinoRPCServiceStub(self.uart_serial_channel)
        self.context.set_robot_status(RobotStatus.FORWARD)
        response = await client.Forward(request)
        self.context.set_robot_status(RobotStatus.STOP)
        await self.context.set_forward(step=request.step)
        return self.context.get_robot_info()

    async def TurnLeft(self, request: TurnRequest) -> RobotInfo:
        angle = request.angle
        if angle < 0 or angle > 180:
            raise ValueError(f'angle in {request.angle} should not be less than 0 or larger than 180.')
        elif angle % 90 != 0:
            raise ValueError(f'angle in {request.angle} should be a multiple of 90.')
        client = ArduinoRPCServiceStub(self.uart_serial_channel)
        self.context.set_robot_status(RobotStatus.TURN_LEFT)
        response = await client.TurnLeft(request)
        self.context.set_robot_status(RobotStatus.STOP)
        self.context.set_turn(-request.angle)
        return self.context.get_robot_info()

    async def TurnRight(self, request: TurnRequest) -> RobotInfo:
        angle = request.angle
        if angle < 0 or angle > 180:
            raise ValueError(f'angle in `TurnRequest.angle` should not be less than 0 or larger than 180, '
                             f'Got {angle}')
        elif angle % 90 != 0:
            raise ValueError(f'angle in `TurnRequest.angle` should be a multiple of 90, '
                             f'Got {angle}')
        client = ArduinoRPCServiceStub(self.uart_serial_channel)
        self.context.set_robot_status(RobotStatus.TURN_RIGHT)
        response = await client.TurnRight(request)
        self.context.set_robot_status(RobotStatus.STOP)
        self.context.set_turn(request.angle)
        return self.context.get_robot_info()

    def GetRobotInfo(self, request: EmptyRequest) -> RobotInfo:
        robot_info = self.context.get_robot_info()
        return robot_info

    def SetPosition(self, request: Position) -> Status:
        self.context.set_position(request)
        return Status(status=True)

    def SetWayPoint(self, request: Position) -> Status:
        self.context.set_way_point(request)
        return Status(status=True)

    def RemoveWayPoint(self, request: EmptyRequest) -> Status:
        self.context.remove_way_point()
        return Status(status=True)

    def SetRobotMode(self, request: RobotMode) -> Status:
        self.context.set_robot_mode(request)
        loop = asyncio.get_event_loop()
        loop.call_soon_threadsafe(self.context.start_flag.set)
        return Status(status=True)

    async def Reset(self, request: EmptyRequest) -> Status:
        client = ArduinoRPCServiceStub(self.uart_serial_channel)
        response = await client.Terminate(request)
        self.context.reset()
        return Status(status=True)

    async def TerminateEx(self, request: EmptyRequest) -> Status:
        pass
