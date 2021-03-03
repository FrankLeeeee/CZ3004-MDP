import asyncio

import pytest

from core.arduino_service_pb2_serial import ArduinoRPCServiceStub
from core.message_pb2 import EchoRequest, MoveRequest, TurnRequest, EmptyRequest
from config import config
from server.serial_channel import SerialAioChannel


@pytest.mark.asyncio
async def test_arduino_rpc_echo():
    async with SerialAioChannel(config.serial_url) as channel:
        stub = ArduinoRPCServiceStub(channel)
        request = EchoRequest(message=b'a')
        response = await stub.Echo(request)
        print(response)


@pytest.mark.asyncio
async def test_arduino_rpc_forward():
    async with SerialAioChannel(config.serial_url) as channel:
        stub = ArduinoRPCServiceStub(channel)
        request = MoveRequest(step=10)
        response = await stub.Forward(request)
        print(response)


@pytest.mark.asyncio
async def test_arduino_rpc_turn_left():
    async with SerialAioChannel(config.serial_url) as channel:
        stub = ArduinoRPCServiceStub(channel)
        request = TurnRequest(angle=90)
        response = await stub.TurnLeft(request)
        print(response)


@pytest.mark.asyncio
async def test_arduino_rpc_turn_right():
    async with SerialAioChannel(config.serial_url) as channel:
        stub = ArduinoRPCServiceStub(channel)
        request = TurnRequest(angle=90)
        response = await stub.TurnLeft(request)
        print(response)


@pytest.mark.asyncio
async def test_arduino_rpc_get_metrics():
    async with SerialAioChannel(config.serial_url) as channel:
        stub = ArduinoRPCServiceStub(channel)
        request = EmptyRequest()
        response = await stub.GetMetrics(request)
        print(response)


@pytest.mark.asyncio
async def test_arduino_rpc_calibration():
    async with SerialAioChannel(config.serial_url) as channel:
        stub = ArduinoRPCServiceStub(channel)
        request = EmptyRequest()
        response = await stub.Calibration(request)
        print(response)


@pytest.mark.asyncio
async def test_arduino_rpc_terminate():
    async with SerialAioChannel(config.serial_url) as channel:
        stub = ArduinoRPCServiceStub(channel)
        request = EmptyRequest()
        response = await stub.Terminate(request)
        print(response)


if __name__ == '__main__':
    asyncio.run(test_arduino_rpc_get_metrics())
