import base64
import cv2

returnedImg_file = "returnedImgSample.jpg"
with open(returnedImg_file, "rb") as fid:
    data = fid.read()
returnedImg = base64.b64encode(data)
filename = 'some_image.jpg'  # I assume you have a way of picking unique filenames
with open(filename, 'wb') as f:
        f.write(returnedImg)