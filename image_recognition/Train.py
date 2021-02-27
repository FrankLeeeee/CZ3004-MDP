import cv2 as cv
import numpy as np
import sys

class Train:
    #empty construtor
    def __init__(self):
        #Constants
        self.DW = "Display Window"
        self.BLUE = 1
        self.GREEN = 2
        self.RED = 3
        self.WHITE = 4
        self.YELLOW = 5
        #empty variables
        self.bgr_img =None
        self.im_gray = None
        self.equalised_img=None
        #self.thresh_img =None
        self.segmented_img = None
        self.image_after_detection = None

        # masks for eachh colour: Blue, Green, Red, White, Yellow
        #masks for blue
        #self.lower_blue = np.array([60,50,38])
        #self.upper_blue = np.array([120,20,255])

        self.lower_blue = np.array([90,50,50])
        self.upper_blue = np.array([120,255,255])
        #masks for Green
        #cant detect the lower green values ,need make mask more accurate
        self.lower_green = np.array([50,30,50])
        self.upper_green = np.array([70,255,255])

        #masks for Red 
        #red is separated into 2 channels
        self.lower_red_1 = np.array([0,40,80])
        self.upper_red_1 = np.array([5,255,255])
        self.lower_red_2 = np.array([170,140,80])
        self.upper_red_2 = np.array([180,255,255])

        #Mask for White
        self.lower_white = np.array([0,12,30])
        self.upper_white = np.array([255,20,255])
        #Mask for Yellow
        self.lower_yellow = np.array([20,50,100])
        self.upper_yellow = np.array([30,255,255])

    
    def input_image(self, bgr_image):
        self.bgr_img = bgr_image
        return self.bgr_img

    # https://docs.opencv.org/master/df/d9d/tutorial_py_colorspaces.html
    # convert to hsv format for easier segmentation
    #segmentation of colours
    #maybe apply gaussian blur before segmetnations
    def segment_image_by_colour(self,colour):
        #convert from bgr format to hsv format
        img_hsv = cv.cvtColor(self.bgr_img.copy(),cv.COLOR_BGR2HSV)
        #make a mask of array[0,1,...]and apply colour mask by comparing bitwise to actual image
        if colour == self.BLUE:
            mask = cv.inRange(img_hsv,self.lower_blue,self.upper_blue)
        elif colour == self.GREEN:
            mask = cv.inRange(img_hsv,self.lower_green,self.upper_green)
        elif colour == self.RED:
            mask1 = cv.inRange(img_hsv,self.lower_red_1,self.upper_red_1)
            mask2 = cv.inRange(img_hsv,self.lower_red_2,self.upper_red_2)
            mask = mask1+mask2
        elif colour == self.WHITE:
            mask = cv.inRange(img_hsv,self.lower_white,self.upper_white)
        elif colour == self.YELLOW:
            mask = cv.inRange(img_hsv,self.lower_yellow,self.upper_yellow)
        else:
            print("Colour not in list. Add a valid colour")
        
        # apply mask to filter out colour
        self.segmented_img = cv.bitwise_and(self.bgr_img.copy(),self.bgr_img.copy(),mask=mask)
        

        print(self.segmented_img)
        return self.segmented_img

    def detect_object(self):
        #convert blue segmented image to grayscale
        self.segmented_img = cv.cvtColor(self.segmented_img.copy(),cv.COLOR_HSV2BGR)
        self.im_gray = cv.cvtColor(self.segmented_img.copy(),cv.COLOR_BGR2GRAY)

        #histogram equalise the image to make the contours more easy for processing
        self.equalised_img = cv.equalizeHist(self.im_gray.copy())
        
        #apply a threshhold to eliminate noise. img will be converted go grayscale
        #(100,100,100) is gotten from tuning on the first blue down image
        #_,self.thresh_img = cv.threshold(self.equalised_img.copy(),100,100,100)
        #Find all the contours of the grayscale images
        contours,hierarchy = cv.findContours(self.equalised_img.copy(),cv.RETR_TREE,cv.CHAIN_APPROX_SIMPLE)
        if contours == None:
            print("no contours found")
            return
        # Find the contour with the maximum area/ length
        max_contour_area=-1
        max_index=None
        count = 0
        for c in contours:
            if(cv.contourArea(c)> max_contour_area):
                max_index = count
                max_contour_area = cv.contourArea(c)
            count+=1
        
        # draw contour with maximum area on the image
        contour_img = cv.drawContours(self.bgr_img.copy(),contours[max_index],-1,(0,0,255),3)

        # draw a bounding box for the contours
        x,y,w,h = cv.boundingRect(contours[max_index])
        self.image_after_detection = cv.rectangle(
        self.bgr_img,
        (x,y),
        (x+w,y+h),
        (0,255,0),
        2
        )
        with open("./data/data/info.dat","w") as f:
            f.write(
                
            )
        # draw all the contours on the image
        self.image_after_detection = cv.drawContours(self.image_after_detection,contours,-1,(0,0,255),3)

        # return the maximum contour area image and the image after detection
        return self.equalised_img, contour_img,self.image_after_detection,x,y,w,h


