#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/25/2021

Server configuration
"""
import asyncio
import logging
from enum import Enum
from pydoc import locate
from typing import Optional

import yaml
from pydantic import BaseModel, root_validator

from utils.constants import PROJECT_ROOT_PATH


class SeverityLevel(Enum):
    NOTSET = 'NOTSET'
    DEBUG = 'DEBUG'
    INFO = 'INFO'
    WARNING = 'WARNING'
    WARN = 'WARN'
    ERROR = 'ERROR'
    CRITICAL = 'CRITICAL'

    @classmethod
    def _missing_(cls, value):
        for member in cls:
            if str(member.name).lower() == str(value).lower():
                # save to value -> member mapper
                cls._value2member_map_[value] = member
                return member


class HandlerConfig(BaseModel):
    StreamHandler: Optional[SeverityLevel]
    FileHandler: Optional[SeverityLevel]

    class Config:
        use_enum_values = True


class LoggerConfig(BaseModel):
    name: str
    welcome: Optional[bool] = True
    filename: Optional[str]
    severity_levels: Optional[HandlerConfig]


class SerialDeviceConfig(BaseModel):
    url: str
    protocol: type
    logger: LoggerConfig

    @root_validator(pre=True)
    def check_protocol_type(cls, values):
        protocol = values.get('protocol')
        if isinstance(protocol, str):
            protocol = locate(protocol)

        if not issubclass(protocol, asyncio.Protocol):
            raise ValueError(f'protocol should be an subclass of {asyncio.Protocol}, '
                             f'got {protocol}')

        values['protocol'] = protocol
        return values

    class Config:
        json_encoders = {
            type: str
        }


class UartSerialConfig(SerialDeviceConfig):
    baudrate: Optional[int] = 115200


class BluetoothConfig(SerialDeviceConfig):
    pass


class ServerConfig(BaseModel):
    port: Optional[int] = 8001
    thread_num: Optional[int] = 8
    uart: UartSerialConfig
    bluetooth: Optional[BluetoothConfig]


with open(PROJECT_ROOT_PATH / 'server/config.yml') as f:
    config = yaml.safe_load(f)
config = ServerConfig.parse_obj(config)
