import abc
import json

import google.protobuf.json_format

import core.serial.call
from proto import message_pb2 as core_dot_message__pb2


def add_bt_rpc_servicer_to_server(servicer, channel):
    rpc_method_handlers = {
        'Echo': core.serial.call.unary_unary_rpc_method_handler(
            servicer.Echo,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.EchoRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.EchoResponse),
        ),
        'Forward': core.serial.call.unary_unary_rpc_method_handler(
            servicer.Forward,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.MoveRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.RobotInfo),
        ),
        'TurnLeft': core.serial.call.unary_unary_rpc_method_handler(
            servicer.TurnLeft,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.TurnRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.RobotInfo),
        ),
        'TurnRight': core.serial.call.unary_unary_rpc_method_handler(
            servicer.TurnRight,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.TurnRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.RobotInfo),
        ),
        'GetRobotInfo': core.serial.call.unary_unary_rpc_method_handler(
            servicer.GetRobotInfo,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.EmptyRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.RobotInfo),
        ),
        'SetPosition': core.serial.call.unary_unary_rpc_method_handler(
            servicer.SetPosition,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.Position),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.Status),
        ),
        'SetWayPoint': core.serial.call.unary_unary_rpc_method_handler(
            servicer.SetWayPoint,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.Position),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.Status),
        ),
        'RemoveWayPoint': core.serial.call.unary_unary_rpc_method_handler(
            servicer.RemoveWayPoint,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.EmptyRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.Status),
        ),
        'SetRobotMode': core.serial.call.unary_unary_rpc_method_handler(
            servicer.SetRobotMode,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.RobotMode),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.Status),
        ),
        'Calibrate': core.serial.call.unary_unary_rpc_method_handler(
            servicer.Calibrate,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.EmptyRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.Status),
        ),
        'Reset': core.serial.call.unary_unary_rpc_method_handler(
            servicer.Reset,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.EmptyRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.Status),
        ),
        'TerminateEx': core.serial.call.unary_unary_rpc_method_handler(
            servicer.TerminateEx,
            request_deserializer=_default_deserializer_gen(core_dot_message__pb2.EmptyRequest),
            response_serializer=_default_serializer_gen(core_dot_message__pb2.Status),
        )
    }

    channel.add_generic_rpc_handlers(rpc_method_handlers)


class BtRPCServiceServicer(abc.ABC):

    @abc.abstractmethod
    def Echo(
            self,
            request: core_dot_message__pb2.EchoRequest,
    ) -> core_dot_message__pb2.EchoResponse:
        raise NotImplementedError('Method not implemented!')

    @abc.abstractmethod
    def Forward(
            self,
            request: core_dot_message__pb2.EchoRequest,
    ) -> core_dot_message__pb2.RobotInfo:
        raise NotImplementedError('Method not implemented!')

    @abc.abstractmethod
    def TurnLeft(
            self,
            request: core_dot_message__pb2.TurnRequest,
    ) -> core_dot_message__pb2.RobotInfo:
        raise NotImplementedError('Method not implemented!')

    @abc.abstractmethod
    def TurnRight(
            self,
            request: core_dot_message__pb2.TurnRequest,
    ) -> core_dot_message__pb2.RobotInfo:
        raise NotImplementedError('Method not implemented!')

    @abc.abstractmethod
    def GetRobotInfo(
            self,
            request: core_dot_message__pb2.EmptyRequest
    ) -> core_dot_message__pb2.RobotInfo:
        raise NotImplementedError('Method not implemented!')

    @abc.abstractmethod
    def SetPosition(
            self,
            request: core_dot_message__pb2.Position
    ) -> core_dot_message__pb2.Status:
        raise NotImplementedError('Method not implemented!')

    @abc.abstractmethod
    def SetWayPoint(
            self,
            request: core_dot_message__pb2.Position,
    ) -> core_dot_message__pb2.Status:
        raise NotImplementedError('Method not implemented')

    @abc.abstractmethod
    def RemoveWayPoint(
            self,
            request: core_dot_message__pb2.EmptyRequest,
    ) -> core_dot_message__pb2.Status:
        raise NotImplementedError('Method not implemented')

    @abc.abstractmethod
    def SetRobotMode(
            self,
            request: core_dot_message__pb2.RobotMode,
    ) -> core_dot_message__pb2.Status:
        raise NotImplementedError('Method not implemented')

    @abc.abstractmethod
    def Calibrate(
            self,
            request: core_dot_message__pb2.EmptyRequest,
    ) -> core_dot_message__pb2.Status:
        raise NotImplementedError('Method not implemented')

    @abc.abstractmethod
    def Reset(
            self,
            request: core_dot_message__pb2.EmptyRequest,
    ) -> core_dot_message__pb2.Status:
        raise NotImplementedError('Method not implemented')

    @abc.abstractmethod
    def TerminateEx(
            self,
            request: core_dot_message__pb2.EmptyRequest,
    ) -> core_dot_message__pb2.Status:
        raise NotImplementedError('Method not implemented')


def _default_serializer_gen(proto):
    def _serializer(message):
        assert isinstance(message, proto), f'Expected message type {proto}, got {type(message)}'
        data = google.protobuf.json_format.MessageToDict(
            message, preserving_proto_field_name=True
        )
        return json.dumps(data)

    return _serializer


def _default_deserializer_gen(proto):
    def _deserializer(text):
        return google.protobuf.json_format.Parse(
            text,
            message=proto(),
        )

    return _deserializer
