import json

import google.protobuf.json_format

import core.serial
from core import message_pb2 as core_dot_message__pb2


def add_bt_rpc_servicer_to_server(servicer, channel):

    rpc_method_handlers = {
        'Echo': core.serial.unary_unary_rpc_method_handler(
            servicer.Echo,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.EchoRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.EchoResponse),
        ),
        'Forward': core.serial.unary_unary_rpc_method_handler(
            servicer.Forward,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.MoveRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.Position),
        ),
    }

    channel.add_generic_rpc_handlers(rpc_method_handlers)


def _default_serializer_gen(proto):
    def _serializer(message):
        assert isinstance(message, proto)
        data = google.protobuf.json_format.MessageToDict(
            message, including_default_value_fields=True, preserving_proto_field_name=True
        )
        return json.dumps(data)

    return _serializer


def _default_deserializer_gen(proto):

    def _deserializer(text):
        return google.protobuf.json_format.Parse(
            text=text,
            message=proto,
        )
    return _deserializer


