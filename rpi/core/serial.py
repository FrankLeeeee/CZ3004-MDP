import inspect


def unary_unary_rpc_method_handler(behavior,
                                   request_deserializer=None,
                                   response_serializer=None):
    def _handler(request_data: bytes):
        request = request_deserializer(request_data)
        response = behavior(request)
        return response_serializer(response)

    async def _async_handler(request_data: bytes):
        request = request_deserializer(request_data)
        response = await behavior(request)
        return response_serializer(response)

    return _async_handler if inspect.iscoroutinefunction(behavior) else _handler
