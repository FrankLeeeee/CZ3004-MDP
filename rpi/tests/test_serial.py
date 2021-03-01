#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/26/2021
"""
import asyncio

from server.serial_comm import SerialAioChannel, SerialProtocol


def _data_received_simple(self, data):
    """Override meth:`SerialProtocol.data_received` method"""
    asyncio.ensure_future(self._queue.put(data))


async def main():
    SerialProtocol.data_received = _data_received_simple
    transport = SerialAioChannel(url='/dev/cu.usbmodem1411401')
    send_char = b'A'
    await transport.start()
    send_task = asyncio.ensure_future(transport.write_channel(send_char))
    receive_task = asyncio.ensure_future(transport.read_channel())

    await send_task
    await receive_task
    received = receive_task.result()

    print(f'Send character {send_char} ({ord(send_char)}).')
    print(f'Got character {received.decode()} ({ord(received)}), '
          f'expected character {chr(ord(send_char) + 1)} ({ord(send_char) + 1})')
    transport.close()


if __name__ == '__main__':
    asyncio.run(main())
