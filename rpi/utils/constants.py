from pathlib import Path

PROJECT_ROOT_PATH = Path(__file__).parents[1].absolute()

# Protocol version
VERSION = 1

# Session
SESSION_LENGTH = 1

DATA_LEN_LENGTH = 4
# VERSION (1) + DATA LEN (4) + SESSION_CODE_LENGTH CODE (1)
HEADER_LENGTH = 1 + DATA_LEN_LENGTH + SESSION_LENGTH

BYTE_ORDER = 'little'
SEPARATOR = b'\x12\x0c'
