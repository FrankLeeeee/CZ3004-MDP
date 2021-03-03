from __future__ import print_function
import grpc

import logging

# import the generated classes
import image_procedure_pb2
import image_procedure_pb2_grpc


import grpc
# data encoding

import numpy as np 
import base64
import zlib


img_file = "clientImg/imgSample.jpg"


def run():
    # NOTE(gRPC Python Team): .close() is possible on a channel and should be
    # used in circumstances in which the with statement does not fit the needs
    # of the code.
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = image_procedure_pb2_grpc.ImageProcedureStub(channel)
        # encoding image/numpy array
        with open(img_file, "rb") as fid:
            data = fid.read()
        encoded_string = base64.b64encode(data)
        # create a valid request message
        image_req = image_procedure_pb2.B64Image(b64image = encoded_string, width = 720, height = 480)

        # make the call
        
        res_future = stub.getPred.future(image_req)
        response = res_future.result()
        # printing response
        print(response.id)
        image_64_decode = base64.b64decode(response.returnedImg) 
        with open("clientImg/receivedImg.jpg", 'wb') as f:
            f.write(image_64_decode)
        print(response.bb_x_coor," " ,response.bb_y_coor)


if __name__ == '__main__':
    logging.basicConfig()
    run()

# base64.b64encode(cv2.imread(img_file))
# print(cv2.imread(img_file))

