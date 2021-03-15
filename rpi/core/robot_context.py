import asyncio
from typing import Optional

from proto.message_pb2 import Position, RobotStatus, MapDescription, RobotInfo, ImagePosition, RobotMode
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
        self._loop: Optional[asyncio.AbstractEventLoop] = None
        self.start_flag: Optional[asyncio.Event] = None

    def set_loop(self, loop):
        self._loop = loop
        self.start_flag = asyncio.Event(loop=loop)

    def get_position(self):
        pos = Position()
        pos.MergeFrom(self._pos)
        return pos

    def set_position(self, pos):
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
        self._pos_dirty = True
        if self._pos.dir == Position.Direction.NORTH:
            self._pos.y = min(self._pos.y + step, ARENA_HEIGHT - 1 - ROBOT_ONE_SIDE_SIZE)
        elif self._pos.dir == Position.Direction.SOUTH:
            self._pos.y = max(int(self._pos.y) - step, 0 + ROBOT_ONE_SIDE_SIZE)
        elif self._pos.dir == Position.Direction.EAST:
            self._pos.x = min(self._pos.x + step, ARENA_WIDTH - ROBOT_ONE_SIDE_SIZE)
        else:
            self._pos.x = max(int(self._pos.x - step), 0 + ROBOT_ONE_SIDE_SIZE)

    def set_turn(self, angle: int):
        """Update position dir."""
        self._pos_dirty = True
        angle_discrete = angle // 90
        self._pos.dir = (self._pos.dir + angle_discrete) % 4

    def get_map(self):
        map_description = MapDescription()
        map_description.MergeFrom(self._map)
        return map_description

    def set_map(self, map_description: MapDescription):
        self._map_dirty = True
        self._map = map_description

    def get_robot_status(self):
        return self._robot_status

    def set_robot_status(self, robot_status_new: RobotStatus):
        self._robot_status_dirty = True
        self._robot_status = robot_status_new

    def get_image_positions(self):
        image_position = list()
        for image_position in self._image_positions:
            new_image_position = ImagePosition()
            new_image_position.MergeFrom(image_position)
            image_position.append(new_image_position)
        return image_position

    def set_image_positions(self, image: ImagePosition):
        self._image_positions_dirty = True
        self._image_positions.append(image)

    def get_robot_info(self) -> RobotInfo:
        robot_info = RobotInfo()
        if self._pos_dirty:
            robot_info.pos.MergeFrom(self.get_position())
        if self._map_dirty:
            robot_info.map.MergeFrom(self.get_map())
        if self._image_positions_dirty:
            robot_info.images.extend(self.get_image_positions())
        if self._robot_status_dirty:
            robot_info.robot_status = self.get_robot_status()

        self._pos_dirty = False
        self._map_dirty = False
        self._image_positions_dirty = False
        self._robot_status_dirty = False

        return robot_info

    def set_way_point(self, position: Position):
        if position.x < 0 or position.x > ARENA_WIDTH - 1:
            raise ValueError(f'Way point in `Position.x` should not be less than 0 or larger '
                             f'than {ARENA_WIDTH - 1}')
        if position.y < 0 or position.y > ARENA_HEIGHT - 1:
            raise ValueError(f'Way point in `Position.x` should not be less than 0 or larger '
                             f'than {ARENA_HEIGHT - 1}')

        position.dir = Position.Direction.NORTH
        self._way_point = Position()
        self._way_point.MergeFrom(position)

    def get_way_point(self) -> Position:
        way_point = Position()
        way_point.MergeFrom(self._way_point)
        return way_point

    def remove_way_point(self):
        self._way_point = Position(x=-1, y=-1)

    def set_robot_mode(self, mode: RobotMode):
        # set robot mode will start the robot
        self._robot_mode = RobotMode()
        self._robot_mode.MergeFrom(mode)

    def get_robot_mode(self) -> RobotMode:
        robot_mode = RobotMode()
        robot_mode.MergeFrom(self._robot_mode)
        return self._robot_mode

    def reset(self):
        self._pos = Position(x=1, y=1)
        self._pos_dirty = True
        self._map = MapDescription()
        self._map_dirty = True
        self._robot_status = RobotStatus.STOP
        self._robot_status_dirty = True
        # TODO: set image
        self._image_positions = list()
        self._image_positions_dirty = True

        # other context
        self._way_point = Position(x=-1, y=-1)
        self._robot_mode = RobotMode
        self._loop.call_soon_threadsafe(self.start_flag.clear)
