import struct

import core.message_pb2 as core_dot_message__pb2
from server.serial_comm import SerialAioChannel


class ArduinoRPCServiceStub(object):
    """Missing associated documentation comment in .proto file."""

    def __init__(self, channel: SerialAioChannel):
        """Constructor.

        Args:
            channel: A grpc.Channel.
        """

        def deserializer_factory(message):
            def _deserializer(text: bytes):
                args = text.split(b',')
                assert len(args) >= 1
                response = message(status=struct.unpack('?', args[-1])[0])

                if message == core_dot_message__pb2.MetricResponse:
                    assert len(args) == 2
                    assert len(args[0]) == 24, 'Metric data should contain 24 bytes'

                    for i in range(24 // 4):
                        response.value[i + 1] = struct.pack('f', args[0])
                    return response
                elif message == core_dot_message__pb2.EchoResponse:
                    assert len(args) == 2
                    response.message = args[0]
                    return response
                else:
                    assert len(args) == 1
                    return response
            return _deserializer

        def default_serializer(message):
            data = list()
            for field in message.DESCRIPTOR.fields:
                value = getattr(message, field.name)
                if isinstance(value, bytes):
                    data.append(value)
                else:
                    data.append(bytes(data))
            return b','.join(data)

        # request_serializer, response_deserializer
        self.Echo = channel.unary_unary(
            b'\x01',
            request_serializer=default_serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.EchoResponse),
        )
        self.Forward = channel.unary_unary(
            b'\x02',
            request_serializer=default_serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.MetricResponse),
        )
        self.TurnLeft = channel.unary_unary(
            b'\x03',
            request_serializer=default_serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.MetricResponse),
        )
        self.TurnRight = channel.unary_unary(
            b'\x04',
            request_serializer=default_serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.MetricResponse),
        )
        self.GetMetrics = channel.unary_unary(
            b'\x05',
            request_serializer=default_serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.MetricResponse),
        )
        self.Calibration = channel.unary_unary(
            b'\x06',
            request_serializer=default_serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.Status),
        )
        self.Terminate = channel.unary_unary(
            b'\x07',
            request_serializer=default_serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.Status),
        )
