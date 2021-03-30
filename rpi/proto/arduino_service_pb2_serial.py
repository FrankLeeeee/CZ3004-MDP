import struct

import proto.message_pb2 as core_dot_message__pb2
from core.serial.channel import SerialAioChannel


class ArduinoRPCServiceStub(object):
    """Missing associated documentation comment in .proto file."""

    def __init__(self, channel: SerialAioChannel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """

        def metric_response_deserializer(text: bytes) -> core_dot_message__pb2.MetricResponse:
            # 6 sensor data (float, 4 bytes) + status (bool, 1 byte)
            assert len(text) == 4 * 6 + 1, 'Metric Response should contain 25 bytes'

            response = core_dot_message__pb2.MetricResponse()
            for i in range(6):
                response.values[i + 1] = struct.unpack('<f', text[i * 4: (i + 1) * 4])[0]
            response.status = struct.unpack('?', text[24:])[0]

            return response

        def status_deserializer(text: bytes) -> core_dot_message__pb2.Status:
            # status (bool, 1 byte)
            assert len(text) == 1, 'Status Response should contain 1 byte'

            response = core_dot_message__pb2.Status()
            response.status = struct.unpack('?', text[0:])[0]

            return response

        def echo_response_deserializer(text: bytes) -> core_dot_message__pb2.EchoResponse:
            # status (bool, 1 byte)
            assert len(text) == 2, 'Echo Response should contain 2 bytes'

            response = core_dot_message__pb2.EchoResponse()
            response.message = text[:1]
            response.status = struct.unpack('?', text[1:])[0]

            return response

        def default_serializer_gen(message_proto):
            """Generate default serializer with message protobuf type check."""

            def default_serializer(message):
                assert isinstance(message, message_proto)
                data = b''
                for field in message_proto.DESCRIPTOR.fields:
                    value = getattr(message, field.name)
                    if isinstance(value, bytes):
                        data += value
                    elif isinstance(value, int):
                        data += struct.pack('<I', value)
                    else:
                        data += bytes(data)
                return data

            return default_serializer

        # request_serializer, response_deserializer
        self.Echo = channel.unary_unary(
            b'\x01',
            request_serializer=default_serializer_gen(core_dot_message__pb2.EchoRequest),
            response_deserializer=echo_response_deserializer,
        )
        self.Forward = channel.unary_unary(
            b'\x02',
            request_serializer=default_serializer_gen(core_dot_message__pb2.MoveRequest),
            response_deserializer=metric_response_deserializer,
        )
        self.TurnLeft = channel.unary_unary(
            b'\x03',
            request_serializer=default_serializer_gen(core_dot_message__pb2.TurnRequest),
            response_deserializer=metric_response_deserializer,
        )
        self.TurnRight = channel.unary_unary(
            b'\x04',
            request_serializer=default_serializer_gen(core_dot_message__pb2.TurnRequest),
            response_deserializer=metric_response_deserializer,
        )
        self.GetMetrics = channel.unary_unary(
            b'\x05',
            request_serializer=default_serializer_gen(core_dot_message__pb2.EmptyRequest),
            response_deserializer=metric_response_deserializer,
        )
        self.Calibration = channel.unary_unary(
            b'\x06',
            request_serializer=default_serializer_gen(core_dot_message__pb2.CalibrationRequest),
            response_deserializer=status_deserializer,
        )
        self.Terminate = channel.unary_unary(
            b'\x07',
            request_serializer=default_serializer_gen(core_dot_message__pb2.EmptyRequest),
            response_deserializer=status_deserializer,
        )
