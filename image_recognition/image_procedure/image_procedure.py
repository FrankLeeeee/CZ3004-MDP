# A procedure which decodes base64 image, runs some machine learning model/ operation(s) (in our case we'll just return the mean of the pixel value)

import numpy as np 
import base64
import zlib
import cv2
returnedImg_file = "serverImg/returnedImgSample.jpg"
def predict(b64img_compressed, w, h):
    b64decoded = base64.b64decode(b64img_compressed)
    with open("serverImg/receivedImg.jpg", 'wb') as f:
        f.write(b64decoded)
    # imgarr = np.frombuffer(decompressed, dtype=np.uint8).reshape(w, h, -1)
    # id
    class_id = "1"
    # bounding_box
    x_coor = "0.5"
    y_coor = "0.5"
    with open(returnedImg_file, "rb") as fid:
        data = fid.read()
    returnedImg = base64.b64encode(data)
    return class_id, returnedImg, x_coor, y_coor