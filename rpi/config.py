#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/25/2021

Server configuration
"""
from typing import Optional

from pydantic import BaseModel


class GRPCServerConfig(BaseModel):
    port: Optional[int] = 8001
    thread_num: Optional[int] = 8
