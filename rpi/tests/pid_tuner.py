#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 3/23/2021
"""
import asyncio
import struct
import time
from collections import defaultdict
from enum import Enum

import yaml
from fastapi import FastAPI
from pydantic import BaseModel

from config import ServerConfig
from core.serial.channel import SerialAioChannel
from utils.constants import SERIAL_MESSAGE_SEPARATOR, PROJECT_ROOT_PATH


class PIDConfig(BaseModel):
    Kp: float
    Ki: float
    Kd: float


class ParameterConfig(BaseModel):
    left: PIDConfig
    right: PIDConfig


class MotorType(Enum):
    LEFT = 'LEFT'
    RIGHT = 'RIGHT'


ConfigParam = b'\x10'
SetLeftSpeed = b'\x11'
SetRightSpeed = b'\x12'

with open(PROJECT_ROOT_PATH / 'tests/pid_tuner_config.yml') as f:
    config = yaml.safe_load(f)
config = ServerConfig.parse_obj(config)
app = FastAPI()
uart = SerialAioChannel(config.uart)
motor_feedback_per_second = list()
FEEDBACK_SIZE = 4096
motor_speed = defaultdict(list)
motor_feedback = list()


async def watch_feedback_task():
    while True:
        _, feedback_byte = await uart.read_channel()
        left, right = feedback_byte.decode().split(' ')
        motor_feedback_per_second.append((float(left), float(right)))


async def aggregate_task():
    while True:
        await asyncio.sleep(1)
        if len(motor_feedback_per_second) > 0:
            left, right = zip(*motor_feedback_per_second)
            left_sum = sum(left) / len(left)
            right_sum = sum(right) / len(right)
        else:
            left_sum = 0
            right_sum = 0
        motor_feedback.append((time.time(), left_sum, right_sum))
        motor_feedback_per_second.clear()
        del motor_feedback[:-FEEDBACK_SIZE]


@app.post('/speed')
async def set_speed(motor: MotorType = MotorType.LEFT, speed: int = 0, running_time: int = 100):
    args = struct.pack('<I', speed)
    args += struct.pack('<I', running_time)

    if motor == MotorType.LEFT:
        method = SetLeftSpeed
    else:
        method = SetRightSpeed

    await uart.write_channel(method + args + SERIAL_MESSAGE_SEPARATOR)

    cur_time = time.time()
    motor_speed[motor.value.lower()].append((cur_time, speed))
    motor_speed[motor.value.lower()].append((cur_time + running_time / 1000, 0))


@app.post('/param')
async def set_param(params: ParameterConfig):
    parameters = [params.left.Kp, params.left.Ki, params.left.Kd]
    parameters += [params.right.Kp, params.right.Ki, params.right.Kd]

    method = ConfigParam
    args = bytes()
    for param in parameters:
        args += struct.pack('<f', param)

    await uart.write_channel(method + args + SERIAL_MESSAGE_SEPARATOR)


@app.get('/feedback')
async def get_motor_feedback():
    times, left, right = zip(*motor_feedback)
    return {
        'feedback_time': times,
        'left_feedback': left, 'right_feedback': right,
        'left_input': motor_speed['left'], 'right_input': motor_speed['right'],
    }


@app.on_event('startup')
async def startup():
    await uart.start()
    loop = asyncio.get_event_loop()
    loop.create_task(watch_feedback_task())
    loop.create_task(aggregate_task())


@app.on_event('shutdown')
async def shutdown():
    await uart.close()


if __name__ == '__main__':
    import uvicorn

    uvicorn.run(app, host='0.0.0.0', port=8000)
