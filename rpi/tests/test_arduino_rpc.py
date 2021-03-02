import asyncio

from core.arduino_service_pb2_serial import ArduinoRPCServiceStub
from core.message_pb2 import EchoRequest
from server.serial_comm import SerialAioChannel


async def test_arduino_rpc():
    async with SerialAioChannel('/dev/cu.usbserial-141140') as channel:
        stub = ArduinoRPCServiceStub(channel)
        request = EchoRequest(message=b'a')
        response = await stub.Echo(request)
        print(response)

if __name__ == '__main__':
    asyncio.run(test_arduino_rpc())
