import cv2 as cv
import sys
import Train
import numpy as np
import os
from Train import Train
DW = "Display Window"

def test_image_detect(image_label,colour):
    train = Train()
    current_dir="./data/data/"+image_label
    result_dir = "./resultsv1/"+image_label
    for filename in os.listdir(current_dir):
        if filename.endswith(".jpeg"):
            img = cv.imread(current_dir+"/"+filename)
            #input image
            train.input_image(img)
            if colour == "BLUE":
                train.segment_image_by_colour(train.BLUE)
            elif colour == "GREEN":
                train.segment_image_by_colour(train.GREEN)
            
            elif colour == "RED":
                train.segment_image_by_colour(train.RED)
            
            elif colour == "YELLOW":
                train.segment_image_by_colour(train.YELLOW)
            
            elif colour == "WHITE":
                train.segment_image_by_colour(train.WHITE)
            else:
                print("ENTER VALID COLOUR")
                return
            _ ,_ ,image_after_detection = train.detect_object()
            cv.imwrite(result_dir +"/"+ filename,image_after_detection)


def detect_green_test():
    img = cv.imread("./data/data/green_circle/602394ab361bfc7b67353472.jpeg")
    train = Train()
    #input image
    img = train.input_image(img)
    cv.imshow(DW,img)
    cv.waitKey(0)

    #segment image by filtering by colour
    segmented_img = train.segment_image_by_colour(train.GREEN)
    cv.imshow(DW,segmented_img)
    cv.waitKey(0)

    #detect the object
    equalised_img,contour_img,image_after_detection = train.detect_object()

    cv.imshow(DW,equalised_img)
    cv.waitKey(0)
    cv.imshow(DW,contour_img)
    cv.waitKey(0)
    cv.imshow(DW,image_after_detection)
    cv.waitKey(0)
 
def detect_blue_test():
    img = cv.imread("./data/data/blue_down/60239aa7361bfc7b673535d6.jpeg")
    train = Train()
    #input image
    img = train.input_image(img)
    cv.imshow(DW,img)
    cv.waitKey(0)

    #segment image by filtering by colour
    segmented_img = train.segment_image_by_colour(train.BLUE)
    cv.imshow(DW,segmented_img)
    cv.waitKey(0)

    #detect the object
    equalised_img,contour_img,image_after_detection = train.detect_object()

    cv.imshow(DW,equalised_img)
    cv.waitKey(0)
    cv.imshow(DW,contour_img)
    cv.waitKey(0)
    cv.imshow(DW,image_after_detection)
    cv.waitKey(0)

def detect_red_test():
    img = cv.imread("./data/data/red_eight/60224e64361bfc711b9d967d.jpeg")
    train = Train()
    #input image
    img = train.input_image(img)
    cv.imshow(DW,img)
    cv.waitKey(0)

    #segment image by filtering by colour
    segmented_img = train.segment_image_by_colour(train.RED)
    cv.imshow(DW,segmented_img)
    cv.waitKey(0)

    #detect the object
    equalised_img,contour_img,image_after_detection = train.detect_object()

    cv.imshow(DW,equalised_img)
    cv.waitKey(0)
    cv.imshow(DW,contour_img)
    cv.waitKey(0)
    cv.imshow(DW,image_after_detection)
    cv.waitKey(0)

def detect_yellow_test():
    img = cv.imread("./data/data/yellow_right/602393e4361bfc7b6735343d.jpeg")
    train = Train()
    #input image
    img = train.input_image(img)
    cv.imshow(DW,img)
    cv.waitKey(0)

    #segment image by filtering by colour
    segmented_img = train.segment_image_by_colour(train.YELLOW)
    cv.imshow(DW,segmented_img)
    cv.waitKey(0)

    #detect the object
    equalised_img,contour_img,image_after_detection = train.detect_object()

    cv.imshow(DW,equalised_img)
    cv.waitKey(0)
    cv.imshow(DW,contour_img)
    cv.waitKey(0)
    cv.imshow(DW,image_after_detection)
    cv.waitKey(0)

def detect_white_test():
    img = cv.imread("./data/data/white_up/60239bd6361bfc7b6735364b.jpeg")
    train = Train()
    #input image
    img = train.input_image(img)
    cv.imshow(DW,img)
    cv.waitKey(0)

    #segment image by filtering by colour
    segmented_img = train.segment_image_by_colour(train.WHITE)
    cv.imshow(DW,segmented_img)
    cv.waitKey(0)

    #detect the object
    equalised_img,contour_img,image_after_detection = train.detect_object()

    cv.imshow(DW,equalised_img)
    cv.waitKey(0)
    cv.imshow(DW,contour_img)
    cv.waitKey(0)
    cv.imshow(DW,image_after_detection)
    cv.waitKey(0)
#detect and save images into results1 folder for blue images    
""" test_image_detect("blue_down","BLUE")
test_image_detect("blue_six","BLUE")
test_image_detect("blue_y","BLUE")

test_image_detect("green_circle","GREEN")
test_image_detect("green_seven","GREEN")
test_image_detect("green_w","GREEN")
#still need testing
test_image_detect("red_eight","RED")
test_image_detect("red_left","RED")
test_image_detect("red_v","RED")

test_image_detect("white_nine","WHITE")
test_image_detect("white_up","WHITE")
test_image_detect("white_x","WHITE")

test_image_detect("yellow_right","YELLOW")
test_image_detect("yellow_z","YELLOW")
test_image_detect("yellow_zero","YELLOW") """

#white 6 needs tuning
weights_path = './cascades/'
white_six_cascade = cv.CascadeClassifier(weights_path+'white_six.xml')




#white_six_cascade.load(weights_path)
cap = cv.VideoCapture(0)

while True:
    ret,img = cap.read()
    if ret is None:
        print("no capture")
    
    gray = cv.cvtColor(img,cv.COLOR_BGR2GRAY)
    blue_sixes = white_six_cascade.detectMultiScale(gray,1.3,5)

    for(x,y,w,h) in blue_sixes:
        cv.rectangle(
            img,
            (x,y),
            (x+w,y+h),
            (0,255,255),
            2
        )
    cv.imshow('img',img)
    k = cv.waitKey(30)
    if k == 27:
        break

cap.release()
cv.destroyAllWindows()