from __future__ import print_function
import grpc

import logging

from concurrent import futures
import time

import image_procedure

# import the generated classes
import image_procedure_pb2
import image_procedure_pb2_grpc


# based on .proto service
class ImageProcedureServicer(image_procedure_pb2_grpc.ImageProcedureServicer):

    def getPred(self, request, context):
        response = image_procedure_pb2.Prediction()
        response.id, response.returnedImg, response.bb_x_coor, response.bb_y_coor  = image_procedure.predict(request.b64image,  request.width, request.height)
        return response


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    # add the defined class to the server
    image_procedure_pb2_grpc.add_ImageProcedureServicer_to_server(
        ImageProcedureServicer(), server)
    # listen on port 5005
    print('Starting server. Listening on port 50051.')
    server.add_insecure_port('[::]:50051')
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':
    logging.basicConfig()
    serve()


