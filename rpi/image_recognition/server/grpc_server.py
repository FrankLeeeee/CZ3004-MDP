#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 7/9/2020
The gRPC server of a GPU worker, for accepting upcoming request. The server is bind to the address local network
address (i.e. all machines within the network can find the gRPC server) and listening on port 50075.
Notes:
If you see some error like:
```
in send fds sock.sendmsg([msg], [(socket.SOL_SOCKET, socket.SCM_RIGHTS, fds)])
    BrokenPipeError: [Errno 32] Broken pipe
```
That means you do not set enough number of open file limit. Please increase the number by:
```
ulimit -n 4096
```
References:
    gRPC Aio: https://github.com/grpc/grpc/blob/master/examples/python/helloworld/async_greeter_server.py
    Combine python asyncio and multiprocessing:
        https://stackoverflow.com/questions/56944282/how-to-combine-python-asyncio-and-multiprocessing
"""
import asyncio
import json
from pathlib import Path

import cv2
import numpy as np

from core.grpc_aio_server import GRPCAioServer
from image_recognition.server.prediction import DarknetModel
from proto.predict_pb2 import InferResponse, InferRequest
from proto.predict_pb2_grpc import PredictServicer, add_PredictServicer_to_server

from utils.dtype import deserialize_bytes_tensor, model_data_type_to_np
from utils.logger import Logger

ROOT = Path(__file__).absolute().parents[1]


class InferenceServicer(PredictServicer):
    """
    Backend gRPC Servicer.
    """

    def __init__(self, device_id: int):
        super().__init__()
        self.device_id = device_id

        # TODO: get weight config from yaml
        self.model_runner = DarknetModel(
            ROOT / 'output/yolo-obj.cfg',
            ROOT / 'output/obj.data',
            ROOT / 'output/yolo-obj_50000.weights',
            device=self.device_id
        )
        self.logger = Logger(name=f'\\{self.device_id}', welcome=False)

    @classmethod
    def as_numpy(cls, request: InferRequest):
        """
        From https://github.com/triton-inference-server/server/blob/796b631bd08f8e48ca4806d814f090636599a8f6/src/clients/python/library/tritonclient/grpc/__init__.py#L1588
        Get the tensor data for input associated with this object in numpy format
        Args:
            request (InferRequest):
        Returns:
            np.array: The numpy array containing the response data for the tensor or
                None if the data for specified tensor name is not found.
        """
        index = 0

        # if request.name == name:
        shape = list(request.shape)
        datatype = request.datatype
        if index < len(request.raw_input_contents):
            if datatype == 'TYPE_BYTES':
                # String results contain a 4-byte string length
                # followed by the actual string characters. Hence,
                # need to decode the raw bytes to convert into
                # array elements.
                np_array = deserialize_bytes_tensor(
                    request.raw_input_contents[index])
            else:
                np_array = np.frombuffer(
                    request.raw_input_contents[index],
                    dtype=model_data_type_to_np(datatype)
                )
        else:
            np_array = np.empty(0)
        np_array = np.resize(np_array, shape)

        return np_array

    def ModelInfer(self, request, context) -> InferResponse:
        """Inference the given request."""

        inputs = self.as_numpy(request)
        inputs = cv2.imdecode(inputs, cv2.IMREAD_COLOR)
        result = self.model_runner.predict(inputs)

        response = InferResponse(json=json.dumps(result))

        return response


class InferenceServer(GRPCAioServer):
    def __init__(
            self,
            device_id: int,
            thread_concurrency: int,
            port: int,
    ):
        super().__init__(
            servicers={InferenceServicer: add_PredictServicer_to_server},
            thread_concurrency=thread_concurrency,
            port=port,
            kwargs={
                'device_id': device_id,
            },
        )


async def runner():
    await server.start()
    await server.join()


if __name__ == '__main__':
    host_ = '0.0.0.0'

    # print(f'Using configuration:\n{config}')
    server = InferenceServer(0, thread_concurrency=8, port=50051)  # config=config)

    loop = asyncio.get_event_loop()

    loop.create_task(runner())
    loop.run_forever()
