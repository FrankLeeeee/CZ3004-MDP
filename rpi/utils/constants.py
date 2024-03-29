import shutil
from pathlib import Path

PROJECT_ROOT_PATH = Path(__file__).parents[1].absolute()
IMAGE_ROOT_DIR = PROJECT_ROOT_PATH / 'image'
if IMAGE_ROOT_DIR.exists():
    shutil.rmtree(IMAGE_ROOT_DIR)
IMAGE_ROOT_DIR.mkdir(exist_ok=True)

METHOD_LENGTH = 1
BYTE_ORDER = 'little'
SERIAL_MESSAGE_SEPARATOR = b';;;;;'
BT_MESSAGE_SEPARATOR = b';'

ARENA_WIDTH = 15
ARENA_HEIGHT = 20
ROBOT_ONE_SIDE_SIZE = 1
