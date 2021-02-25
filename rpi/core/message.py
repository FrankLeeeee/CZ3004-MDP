from utils.constants import SESSION_LENGTH, VERSION, SEPARATOR, BYTE_ORDER


def generate_session():
    session_id = 0
    while True:
        yield session_id
        session_id = (session_id + 1) % (256 ** SESSION_LENGTH)


def make_call(method: str, session_id: int, request_data: bytes):
    # header: VERSION (1) + body len (4) + SESSION ID (SESSION_LENGTH)
    # body: Method + SEPARATOR + data
    body = method.encode() + SEPARATOR + request_data
    body_len = len(body)
    header = VERSION.to_bytes(1, BYTE_ORDER) + body_len.to_bytes(4, BYTE_ORDER) + \
        session_id.to_bytes(SESSION_LENGTH, BYTE_ORDER)
    return header + body


def parse_header(header: bytes):
    assert len(header) == 6
    version, data_len = header[:1], header[1:]
    version = int.from_bytes(version, BYTE_ORDER)
    data_len = int.from_bytes(data_len, BYTE_ORDER)

    return version, data_len


def parse_body(body: bytes):
    session_id, data = body[:SESSION_LENGTH], body[SESSION_LENGTH:]
    session_id = int.from_bytes(session_id, BYTE_ORDER)
    pos = data.find(SEPARATOR)
    method, request = data[:pos], data[pos:]
    return session_id, method, request
