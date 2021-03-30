"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 3/29/2020
"""
from typing import Tuple

from core.robot_context import RobotContext
from proto.message_pb2 import Position


def locate_position(context: RobotContext, bbox: Tuple[float, float, float, float]):
    """Get image coordination when RPi is configured in a right wall hugging manner."""
    detection_center, y, _, _ = bbox

    # find the image pasted on left block, middle block or right block
    block_offset = 0
    if detection_center <= 0.3:
        block_offset = -1
    elif detection_center > 0.65:
        block_offset = 1
    # FIXME: try the image size to get distance?
    distance_offset = 2

    robot_position = context.get_position()
    x, y = robot_position.x, robot_position.y
    direction = robot_position.dir
    if direction == Position.Direction.WEST:
        # RPi facing north
        img_x = x + block_offset
        img_y = y + distance_offset
    elif direction == Position.Direction.NORTH:
        # RPi facing east
        img_x = x + distance_offset
        img_y = y - block_offset
    elif direction == Position.Direction.EAST:
        # RPi facing south
        img_x = x - block_offset
        img_y = y - distance_offset
    else:
        # RPi facing west
        img_x = x - distance_offset
        img_y = y + block_offset
    return max(img_x, 0), max(img_y, 0)
