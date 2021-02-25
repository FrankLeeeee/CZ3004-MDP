#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/26/2021
"""
import asyncio

from server.serial_comm import SerialAioTransport


async def main():
    transport = SerialAioTransport(url='COM5')
    await transport.start()
    send_task = asyncio.ensure_future(transport.write(b'Hello\n'))
    receive_task = asyncio.ensure_future(transport.read())

    await send_task
    await receive_task
    print(receive_task.result())
    transport.close()


if __name__ == '__main__':
    asyncio.run(main())
