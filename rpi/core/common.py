#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021
"""
from enum import Enum


class Label(Enum):
    BLUE_SIX = 'blue_six'
    GREEN_SEVEN = 'green_seven'
    RED_EIGHT = 'red_eight'
    WHITE_NINE = 'white_nine'
    YELLOW_ZERO = 'yellow_zero'
    RED_V = 'red_v'
    GREEN_W = 'green_w'
    WHITE_X = 'white_x'
    BLUE_Y = 'blue_y'
    YELLOW_Z = 'yellow_z'
    YELLOW_RIGHT = 'yellow_right'
    GREEN_CIRCLE = 'green_circle'
    RED_LEFT = 'red_left'
    BLUE_DOWN = 'blue_down'
    WHITE_UP = 'white_up'


class Label2(Enum):
    UNKNOWN = 0
    UP_ARROW = 1
    DOWN_ARROW = 2
    RIGHT_ARROW = 3
    LEFT_ARROW = 4
    GO = 5
    SIX = 6
    SEVEN = 7
    EIGHT = 8
    NINE = 9
    ZERO = 10
    ALPHABET_V = 11
    ALPHABET_W = 12
    ALPHABET_X = 13
    ALPHABET_Y = 14
    ALPHABET_Z = 15


class ImageEncodeFormat(Enum):
    JPEG = 'jpeg'
    PNG = 'png'
