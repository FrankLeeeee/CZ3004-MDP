import asyncio
from typing import Optional

from core.message_pb2 import Position, RobotStatus, MapDescription, RobotInfo, ImagePosition, RobotMode
from utils.constants import ARENA_HEIGHT, ROBOT_ONE_SIDE_SIZE, ARENA_WIDTH


class RobotContext(object):
    def __init__(self):
        # for android robot info
        self._pos = Position(x=1, y=1)
        self._pos_dirty = True
        self._map = MapDescription()
        self._map_dirty = True
        self._robot_status = RobotStatus.STOP
        self._robot_status_dirty = True
        self._image_positions = list()
        self._image_positions_dirty = True

        # other context
        self._way_point = Position(x=-1, y=-1)
        self._robot_mode = RobotMode
        self.start_flag: Optional[asyncio.Event] = None
        self._lock: Optional[asyncio.Lock] = None

    def set_loop(self, loop):
        self._lock = asyncio.Lock(loop=loop)
        self.start_flag = asyncio.Event(loop=loop)

    async def get_position(self):
        async with self._lock:
            pos = Position()
            pos.MergeFrom(self._pos)
            return pos

    async def set_position(self, pos):
        async with self._lock:
            self._pos_dirty = True
            self._pos.MergeFrom(pos)

    async def set_forward(self, step: int):
        """Update position x or y coordinate.
        The coordinate and direction is illustrated as following:

        y                                                North
        ^                                                  ^
        |                                           West < o > East
        +---------+---------+   ~~~   +----------+         v
        | (0, 19) | (1, 19) |   ...   | (14, 19) |       South
        |---------+---------+   ~~~   +----------+
        | (0, 18) | (1, 18) |   ...   | (14, 18) |
        +---------+---------+   ~~~   +----------+
        ~   ...   ~   ...   |   ...   .   ...    .
        |---------+---------+   ~~~   +----------+
        | (0, 0)  | (0, 1)  |   ...   | (14, 0)  |
        +---------+---------+   ~~~   +----------+-> x

        And the robot is 3 x 3 in size, so its reachable coordinates are from x -> [1, 13] and y -> [1, 18]
        """
        async with self._lock:
            self._pos_dirty = True
            if self._pos.dir == Position.Direction.NORTH:
                self._pos.y = min(self._pos.y + step, ARENA_HEIGHT - 1 - ROBOT_ONE_SIDE_SIZE)
            elif self._pos.dir == Position.Direction.SOUTH:
                self._pos.y = max(int(self._pos.y) - step, 0 + ROBOT_ONE_SIDE_SIZE)
            elif self._pos.dir == Position.Direction.EAST:
                self._pos.x = min(self._pos.x + step, ARENA_WIDTH - ROBOT_ONE_SIDE_SIZE)
            else:
                self._pos.x = max(int(self._pos.x - step), 0 + ROBOT_ONE_SIDE_SIZE)

    async def set_turn(self, angle: int):
        """Update position dir."""
        async with self._lock:
            self._pos_dirty = True
            angle_discrete = angle // 90
            self._pos.dir = (self._pos.dir + angle_discrete) % 4

    async def get_map(self):
        async with self._lock:
            map_description = MapDescription()
            map_description.MergeFrom(self._map)
            return map_description

    async def set_map(self, map_description: MapDescription):
        async with self._lock:
            self._map_dirty = True
            self._map = map_description

    async def get_robot_status(self):
        async with self._lock:
            return self._robot_status

    async def set_robot_status(self, robot_status_new: RobotStatus):
        async with self._lock:
            self._robot_status_dirty = True
            self._robot_status = robot_status_new

    async def get_image_positions(self):
        async with self._lock:
            image_position = list()
            for image_position in self._image_positions:
                new_image_position = ImagePosition()
                new_image_position.MergeFrom(image_position)
                image_position.append(new_image_position)
            return image_position

    async def set_image_positions(self, image: ImagePosition):
        async with self._lock:
            self._image_positions_dirty = True
            self._image_positions.append(image)

    async def get_robot_info(self) -> RobotInfo:
        robot_info = RobotInfo()
        if self._pos_dirty:
            robot_info.pos.MergeFrom(await self.get_position())
        if self._map_dirty:
            robot_info.map.MergeFrom(await self.get_map())
        if self._image_positions_dirty:
            robot_info.images.extend(await self.get_image_positions())
        if self._robot_status_dirty:
            robot_info.robot_status = await self.get_robot_status()
        async with self._lock:
            self._pos_dirty = False
            self._map_dirty = False
            self._image_positions_dirty = False
            self._robot_status_dirty = False

        return robot_info

    async def set_way_point(self, position: Position):
        if position.x < 0 or position.y > ARENA_WIDTH - 1:
            raise ValueError(f'Way point in `Position.x` should not be less than 0 or larger '
                             f'than {ARENA_WIDTH - 1}')
        if position.y < 0 or position.y > ARENA_HEIGHT - 1:
            raise ValueError(f'Way point in `Position.x` should not be less than 0 or larger '
                             f'than {ARENA_HEIGHT - 1}')

        position.dir = Position.Direction.NORTH
        async with self._lock:
            self._way_point = Position()
            self._way_point.MergeFrom(position)

    async def get_way_point(self) -> Position:
        async with self._lock:
            way_point = Position()
            way_point.MergeFrom(self._way_point)
            return way_point

    async def remove_way_point(self):
        async with self._lock:
            self._way_point = None

    async def set_robot_mode(self, mode: RobotMode):
        # set robot mode will start the robot
        async with self._lock:
            self._robot_mode = RobotMode()
            self._robot_mode.MergeFrom(mode)

    async def get_robot_mode(self) -> RobotMode:
        async with self._lock:
            robot_mode = RobotMode()
            robot_mode.MergeFrom(self._robot_mode)
            return self._robot_mode
