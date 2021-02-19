from server.constant import SESSION_CODE_LENGTH, VERSION, CommandCode

BYTE_ORDER = 'little'


def generate_session():
    session_id = 0
    while True:
        yield session_id
        session_id = (session_id + 1) % (256 ** SESSION_CODE_LENGTH)


def make_request(command_code: CommandCode, session_id: int, request_data: bytes):
    # header: VERSION (1) + DATA LEN (4) + COMMAND CODE (1)
    # body: SESSION ID (SESSION_LENGTH) + DATA
    data_len = SESSION_CODE_LENGTH + len(request_data)
    return VERSION.to_bytes(1, BYTE_ORDER) + data_len.to_bytes(4, BYTE_ORDER) + command_code.value + \
        bytes([session_id]) + request_data


def parse_header(header: bytes):
    assert len(header) == 6
    version, data_len, command_code = header[:0], header[1:4], header[5:]
    version = int.from_bytes(version, BYTE_ORDER)
    data_len = int.from_bytes(data_len, BYTE_ORDER)
    command_code = CommandCode[command_code]

    return version, data_len, command_code


def parse_body(body: bytes):
    raise NotImplementedError()
