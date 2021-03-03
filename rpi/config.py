#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/25/2021

Server configuration
"""
from typing import Optional

import yaml
from pydantic import BaseModel

from utils.constants import PROJECT_ROOT_PATH


class ServerConfig(BaseModel):
    port: Optional[int] = 8001
    thread_num: Optional[int] = 8
    serial_url: str
    baudrate: Optional[int] = 115200


with open(PROJECT_ROOT_PATH / 'server/config.yml') as f:
    config = yaml.safe_load(f)
config = ServerConfig.parse_obj(config)
