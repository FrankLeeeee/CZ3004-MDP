from core.constant import SESSION_CODE_LENGTH, VERSION, CommandCode

BYTE_ORDER = 'little'
SEP = b'\x00\x00'


def generate_session():
    session_id = 0
    while True:
        yield session_id
        session_id = (session_id + 1) % (256 ** SESSION_CODE_LENGTH)


def make_call(method: CommandCode, session_id: int, request_data: bytes):
    # header: VERSION (1) + body len (4)
    # body: SESSION ID (SESSION_LENGTH) + Method + 0x0000 + data
    body = method.value.to_bytes(1, BYTE_ORDER) + SEP + request_data
    body_len = len(body)
    return VERSION.to_bytes(1, BYTE_ORDER) + body_len.to_bytes(4, BYTE_ORDER) + body


def parse_header(header: bytes):
    assert len(header) == 6
    version, data_len = header[:1], header[1:]
    version = int.from_bytes(version, BYTE_ORDER)
    data_len = int.from_bytes(data_len, BYTE_ORDER)

    return version, data_len


def parse_body(body: bytes):
    session_id, data = body[:SESSION_CODE_LENGTH], body[SESSION_CODE_LENGTH:]
    session_id = int.from_bytes(session_id, BYTE_ORDER)
    pos = data.find(SEP)
    method, request = data[:pos], data[pos:]
    return session_id, method, request
