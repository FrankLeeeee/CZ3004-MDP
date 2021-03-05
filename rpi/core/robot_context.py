import asyncio

from core.message_pb2 import Position, RobotStatus, MapDescription, RobotInfo, ImagePosition


class RobotContext(object):
    def __init__(self, loop):
        self._pos = None
        self._pos_dirty = True
        self._map = MapDescription()
        self._map_dirty = True
        self._robot_status = RobotStatus.STOP
        self._robot_status_dirty = True
        self._image_positions = list()
        self._image_positions_dirty = True
        self._lock = asyncio.Lock(loop=loop)

    @property
    async def position(self):
        async with self._lock:
            if self._pos_dirty:
                self._pos_dirty = False
                return self._pos
            return None

    @position.setter
    async def position(self, pos: Position):
        async with self._lock:
            self._pos_dirty = True
            self._pos = pos

    @property
    async def map(self):
        async with self._lock:
            if self._map_dirty:
                self._pos_dirty = False
                return self._pos
            return None

    @map.setter
    async def map(self, map_description: MapDescription):
        async with self._lock:
            self._map_dirty = True
            self._map = map_description

    @property
    async def robot_status(self):
        async with self._lock:
            if self._robot_status_dirty:
                self._robot_status_dirty = False
                return self._robot_status
            return None

    @robot_status.setter
    async def robot_status(self, robot_status_new: RobotStatus):
        async with self._lock:
            self._robot_status_dirty = True
            self._robot_status = robot_status_new

    @property
    async def image_positions(self):
        async with self._lock:
            if self._image_positions_dirty:
                self._image_positions_dirty = False
                return self._image_positions
            return None

    @image_positions.setter
    async def image_positions(self, image: ImagePosition):
        async with self._lock:
            self._image_positions_dirty = True
            self._image_positions.append(image)

    async def get_robot_info(self) -> RobotInfo:
        async with self._lock:
            return RobotInfo(
                pos=self.position,
                map=self.map,
                images=self.image_positions,
                robot_status=self.robot_status
            )
