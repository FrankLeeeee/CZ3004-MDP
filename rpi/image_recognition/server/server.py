from concurrent import futures

import logging
import detector_pb2
import detector_pb2_grpc

import numpy as np
import cv2 as cv
import grpc
#import darknet here
#import darknet
#make a servicer to provide service
class DetectorServicer(detector_pb2_grpc.detectorServicer):
    
    def __init__(self):
        #empty for now
        #initiate darknet object here

        pass

    def Detect_image(self,request,context):

        #unpack request 
        raw_image_bytes = request.image.image
        raw_image_decoded = cv.imdecode(np.frombuffer(raw_image_bytes,np.uint8),-1)

        cv.imwrite("../server_image/raw_image.jpg",raw_image_decoded)

        # call the rpc Detect image function from servicer
        detected_img = cv.imread("../sample/00.jpeg")
        is_success, im_buf_arr = cv.imencode(".jpg",detected_img)
        byte_im = im_buf_arr.tobytes()
        print("here")
        #hard coded for now
        class_id = "14"
        #hard coded coordinates
        x_coord = 14
        y_coord = 30

        #fill the response
        response = detector_pb2.Pc_packet(
            class_id=class_id,
            image = detector_pb2.Image(
                image = byte_im
            ),
            coordinates = detector_pb2.Coordinate(
                x_coordinates = x_coord,
                y_coordinates = y_coord
            )
        )
        return response

def serve():
    #initialise a server object
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))

    #add servicer to server 
    detector_pb2_grpc.add_detectorServicer_to_server(DetectorServicer(),server)

    # add port to listen to
    server.add_insecure_port('[::]:50051')

    server.start()
    #server listens until termination
    server.wait_for_termination()


if __name__ == '__main__':
    logging.basicConfig()
    serve()
