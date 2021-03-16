import asyncio
import json
import time
from pathlib import Path

import cv2
import grpc
import numpy as np

from image_recognition.server.prediction import DarknetModel
from proto.predict_pb2 import InferRequest
from proto.predict_pb2_grpc import PredictStub
from utils.dtype import serialize_byte_tensor, type_to_data_type

CLASS_COLORS = {
    'up_arrow': (123, 22, 203), 'down_arrow': (81, 61, 180), 'right_arrow': (18, 11, 153),
    'left_arrow': (24, 111, 96), 'Go': (128, 168, 66), 'Six': (203, 83, 218), 'Seven': (123, 62, 117),
    'Eight': (68, 248, 22), 'Nine': (96, 35, 93), 'Zero': (208, 15, 130), 'Alphabet_V': (40, 47, 84),
    'Alphabet_W': (135, 104, 73), 'Alphabet_X': (223, 42, 23), 'Alphabet_Y': (218, 235, 102),
    'Alphabet_Z': (31, 9, 7)
}


def make_request_from_numpy(input_tensor: np.array):
    """Make the RESTful request here.
    @model_name: the name of your model served; If you have multiple models are serving, you can call this
    function respectively to make different kinds of requests.
    @transformer: the data pre-processing method you can put here, it depends on your model input shapes.
    TODO: refer https://github.com/triton-inference-server/server/blob/796b631bd08f8e48ca4806d814f090636599a8f6/src/clients/python/library/tritonclient/grpc/__init__.py#L64
    """

    if not isinstance(input_tensor, (np.ndarray,)):
        raise ValueError('input_tensor must be a numpy array')
    data_type = type_to_data_type(input_tensor.dtype)
    shape = input_tensor.shape

    request = InferRequest()
    # _input.name = name
    request.ClearField('shape')
    request.shape.extend(shape)
    request.datatype = data_type.name
    _raw_content = None

    if request.datatype == 'TYPE_BYTES':
        _raw_content = serialize_byte_tensor(input_tensor).tobytes()
    else:
        _raw_content = input_tensor.tobytes()

    if _raw_content is not None:
        request.raw_input_contents.extend([_raw_content])
    return request


async def detect(image_stream, url):
    image_np = np.frombuffer(image_stream, dtype=np.uint8)
    request = make_request_from_numpy(image_np)

    async with grpc.aio.insecure_channel(url) as channel:
        stub = PredictStub(channel)
        response = await stub.ModelInfer(request)

    return json.loads(response.json)


if __name__ == '__main__':
    image_path = Path(__file__).absolute().parent / 'data/100.jpg'
    with open(image_path, 'rb') as f:
        image = f.read()
    tick = time.time()
    detection_result = asyncio.run(detect(image, '155.69.146.35:50051'))
    print(f'End-to-end inference time: {time.time() - tick}')
    print(f'Result: {detection_result}')

    # draw annotation
    image = cv2.imread(str(image_path))
    image_path = Path(image_path)
    result_dir = image_path.absolute().parent / 'result'
    result_dir.mkdir(exist_ok=True)
    annotated_image = DarknetModel.draw_annotations(image, detection_result, CLASS_COLORS)
    print(f'Save result image at {result_dir / image_path.name}.')
    cv2.imwrite(f'{result_dir / image_path.name}', annotated_image)
