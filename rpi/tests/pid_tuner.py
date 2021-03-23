#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 3/23/2021
"""
import asyncio
import struct
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


class Direction(Enum):
    UP = 'UP'
    DOWN = 'DOWN'
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
motor_feedback = list()
FEEDBACK_SIZE = 4096


async def watch_feedback_task():
    while True:
        timestamp, feedback_byte = await uart.read_channel()
        left_input, right_input, left_feedback, right_feedback = feedback_byte.decode().split(' ')
        motor_feedback.append(
            (timestamp, float(left_input), float(right_input), float(left_feedback), float(right_feedback)))
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


@app.post('/param')
async def set_param(params: ParameterConfig):
    parameters = [params.left.Kp, params.left.Ki, params.left.Kd]
    parameters += [params.right.Kp, params.right.Ki, params.right.Kd]

    method = ConfigParam
    args = bytes()
    for param in parameters:
        args += struct.pack('<f', param)

    await uart.write_channel(method + args + SERIAL_MESSAGE_SEPARATOR)


@app.post('/move')
async def robot_move(direction: Direction):
    if direction == Direction.UP:
        method = 'u'
    elif direction == Direction.DOWN:
        method = 'd'
    elif direction == Direction.LEFT:
        method = 'l'
    else:
        method = 'r'

    await uart.write_channel(method + SERIAL_MESSAGE_SEPARATOR)


@app.get('/feedback')
async def get_motor_feedback():
    if len(motor_feedback):
        times, left, right, left_feedback, right_feedback = zip(*motor_feedback)
        return {
            'feedback_time': times,
            'left_input': left, 'right_input': right,
            'left_feedback': left_feedback, 'right_feedback': right_feedback,
        }
    else:
        return {
            'feedback_time': [0], 'left_feedback': [0], 'right_feedback': [0], 'left_input': [0], 'right_input': [0],
        }


@app.post('/clear')
def clear_feedback():
    motor_feedback.clear()


@app.on_event('startup')
async def startup():
    await uart.start()
    loop = asyncio.get_event_loop()
    loop.create_task(watch_feedback_task())


@app.on_event('shutdown')
async def shutdown():
    await uart.close()


if __name__ == '__main__':
    import uvicorn

    uvicorn.run(app, host='0.0.0.0', port=8000)
