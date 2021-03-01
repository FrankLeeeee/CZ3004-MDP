import asyncio

from core.arduino_service_pb2_serial import ArduinoRPCServiceStub
from core.message_pb2 import MoveRequest
from server.serial_comm import SerialAioChannel


async def test_arduino_rpc():
    async with SerialAioChannel('/dev/cu.usbmodem1411401') as channel:
        stub = ArduinoRPCServiceStub(channel)
        request = MoveRequest(step=1)
        response = await stub.Forward(request)
        print(response)

if __name__ == '__main__':
    asyncio.run(test_arduino_rpc())
