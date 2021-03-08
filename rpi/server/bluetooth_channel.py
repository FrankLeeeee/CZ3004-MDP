#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Author: Albert Huang <albert@csail.mit.edu>
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021

Bluetooth communication for interfacing with the tablet.
"""
from core.arduino_service_pb2_serial import ArduinoRPCServiceStub
from core.bt_service_pb2_serial import BtRPCServiceServicer
from core.message_pb2 import EchoResponse, RobotInfo, TurnRequest, Status, Position, EmptyRequest, MoveRequest, \
    EchoRequest, RobotStatus, RobotMode
from core.robot_context import RobotContext
from core.serial.channel import SerialAioChannel


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
        client = ArduinoRPCServiceStub(self.uart_serial_channel)
        await self.context.set_robot_status(RobotStatus.FORWARD)
        response = await client.Forward(request)
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
        client = ArduinoRPCServiceStub(self.uart_serial_channel)
        await self.context.set_robot_status(RobotStatus.TURN_LEFT)
        response = await client.TurnLeft(request)
        await self.context.set_robot_status(RobotStatus.STOP)
        await self.context.set_turn(-request.angle)
        return await self.context.get_robot_info()

    async def TurnRight(self, request: TurnRequest) -> RobotInfo:
        angle = request.angle
        if angle < 0 or angle > 180:
            raise ValueError(f'angle in `TurnRequest.angle` should not be less than 0 or larger than 180, '
                             f'Got {angle}')
        elif angle % 90 != 0:
            raise ValueError(f'angle in `TurnRequest.angle` should be a multiple of 90, '
                             f'Got {angle}')
        client = ArduinoRPCServiceStub(self.uart_serial_channel)
        await self.context.set_robot_status(RobotStatus.TURN_RIGHT)
        response = await client.TurnRight(request)
        await self.context.set_robot_status(RobotStatus.STOP)
        await self.context.set_turn(request.angle)
        return await self.context.get_robot_info()

    async def GetRobotInfo(self, request: EmptyRequest) -> RobotInfo:
        robot_info = await self.context.get_robot_info()
        return robot_info

    async def SetPosition(self, request: Position) -> Status:
        await self.context.set_position(request)
        return Status(status=True)

    async def SetWayPoint(self, request: Position) -> Status:
        await self.context.set_way_point(request)
        return Status(status=True)

    async def RemoveWayPoint(self, request: EmptyRequest) -> Status:
        await self.context.remove_way_point()
        return Status(status=True)

    async def SetRobotMode(self, request: RobotMode) -> Status:
        await self.context.set_robot_status(request)
        self.context.start_flag.set()
        return Status(status=True)

    async def Terminate(self, request: EmptyRequest) -> Status:
        client = ArduinoRPCServiceStub(self.uart_serial_channel)
        response = await client.Terminate(request)
        await self.context.set_robot_status(RobotStatus.STOP)
        self.context.start_flag.clear()
        return Status(status=True)
