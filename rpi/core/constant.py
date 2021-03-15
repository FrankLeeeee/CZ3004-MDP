# Control Service Code
from enum import Enum


class CommandCode(Enum):
    HeartBeat = b'\x00'
    Forward = b'\x01'
    Backward = b'\x02'
    Left = b'\x03'
    Right = b'\x04'
    TurnClockwise = b'\x05'
    TurnAntiClockwise = b'\x06'
    GetMetrics = b'\x07'
    StreamGetMetrics = b'\x08'
    BroadCast = b'\xff'
