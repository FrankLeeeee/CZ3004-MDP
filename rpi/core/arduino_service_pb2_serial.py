import json

import google.protobuf.json_format

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
            def _deserializer(text):
                return google.protobuf.json_format.Parse(
                    text=text,
                    message=message()
                )
            return _deserializer

        def serializer(message):
            data = google.protobuf.json_format.MessageToDict(message, including_default_value_fields=True)
            return json.dumps(data)

        # request_serializer, response_deserializer
        self.Echo = channel.unary_unary(
            'Echo',
            request_serializer=serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.EchoResponse),
        )
        self.Forward = channel.unary_unary(
            'Forward',
            request_serializer=serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.MetricResponse),
        )
        self.TurnLeft = channel.unary_unary(
            'TurnLeft',
            request_serializer=serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.MetricResponse),
        )
        self.TurnRight = channel.unary_unary(
            'TurnRight',
            request_serializer=serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.MetricResponse),
        )
        self.GetMetrics = channel.unary_unary(
            'GetMetrics',
            request_serializer=serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.MetricResponse),
        )
        self.Calibration = channel.unary_unary(
            'Calibration',
            request_serializer=serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.Status),
        )
        self.Terminate = channel.unary_unary(
            'Terminate',
            request_serializer=serializer,
            response_deserializer=deserializer_factory(core_dot_message__pb2.Status),
        )
