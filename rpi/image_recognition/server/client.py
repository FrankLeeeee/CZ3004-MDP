import cv2 as cv
import logging

import grpc

import detector_pb2
import detector_pb2_grpc

def get_detection(stub):
    #to be changed to image from rpi camera
    raw_img = cv.imread("../sample/01.jpeg")
    #convert to bytes
    is_success, im_buf_arr = cv.imencode(".jpg",raw_img)
    raw_byte_im = im_buf_arr.tobytes()

    #hard coded arena  coordinates
    x_coord = 1
    y_coord = 2

    #fill the request
    image = detector_pb2.Image(image=raw_byte_im)
    coordinates = detector_pb2.Coordinate(x_coordinates=x_coord,y_coordinates=y_coord)
    request = detector_pb2.Camera_packet(
        image = image,
        coordinates = coordinates
    )
    
    #calling Detect_image() function on server by using services on stub 
    response = stub.Detect_image(request)
    print(response.class_id)
    print(response.image.image)
    print(response.coordinates.x_coordinates)
    print(response.coordinates.y_coordinates)

def run():
    #instantiate same channel as server
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = detector_pb2_grpc.detectorStub(channel)
        print("-------------- Get Detection --------------")
        get_detection(stub)

if __name__ == '__main__':
    logging.basicConfig()
    run()