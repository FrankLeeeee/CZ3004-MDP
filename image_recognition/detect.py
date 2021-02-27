import os
import cv2 as cv
import numpy as np

class detect_and_recognise:
    def __init__(self):
        #loads all cascades
        weights_path = './cascades/'
        self.white_nine_cascade = cv.CascadeClassifier(weights_path+"white_nine.xml")
        self.white_x_cascade =cv.CascadeClassifier(weights_path+"white_x.xml")
        self.white_up_cascade =cv.CascadeClassifier(weights_path+"white_up.xml")

        self.green_seven_cascade = cv.CascadeClassifier(weights_path+"green_seven.xml")
        self.green_w_cascade =cv.CascadeClassifier(weights_path+"green_w.xml")
        self.green_circle_cascade =cv.CascadeClassifier(weights_path+"green_circle.xml")

        #self.red_eight_cascade = cv.CascadeClassifier(weights_path+"red_eight.xml")
        self.yellow_zero_cascade  =cv.CascadeClassifier(weights_path+"yellow_zero.xml")
        self.yellow_z_cascade  =cv.CascadeClassifier(weights_path+"yellow_z.xml")
        self.yellow_right_cascade  =cv.CascadeClassifier(weights_path+"yellow_right.xml")
        
        self.red_v_cascade =cv.CascadeClassifier(weights_path+"red_v.xml")
        self.red_left_cascade =cv.CascadeClassifier(weights_path+"red_left.xml")
        self.red_eight_cascade =cv.CascadeClassifier(weights_path+"red_eight.xml")
        
        self.blue_y_cascade =cv.CascadeClassifier(weights_path+"blue_y.xml")
        self.blue_down_cascade =cv.CascadeClassifier(weights_path+"blue_down.xml")
        #change cascade to train blue 6
        self.blue_six_cascade =cv.CascadeClassifier(weights_path+"white_nine.xml")


        #get keypoints and descriptions of the samples
        self.blue_six_sample = cv.imread("./data/data/sample/blue_six.jpeg",cv.IMREAD_GRAYSCALE)
        self.blue_down_sample = cv.imread("./data/data/sample/blue_down.jpeg",cv.IMREAD_GRAYSCALE)
        self.blue_y_sample = cv.imread("./data/data/sample/blue_y.jpeg",cv.IMREAD_GRAYSCALE)
        
        self.green_circle_sample = cv.imread("./data/data/sample/green_circle.jpeg",cv.IMREAD_GRAYSCALE)
        self.green_seven_sample = cv.imread("./data/data/sample/green_seven.jpeg",cv.IMREAD_GRAYSCALE)
        self.green_w_sample = cv.imread("./data/data/sample/green_w.jpeg",cv.IMREAD_GRAYSCALE)
        
        self.red_eight_sample = cv.imread("./data/data/sample/red_eight.jpeg",cv.IMREAD_GRAYSCALE)
        self.red_left_sample = cv.imread("./data/data/sample/red_left.jpeg",cv.IMREAD_GRAYSCALE)
        self.red_v_sample = cv.imread("./data/data/sample/red_v.jpeg",cv.IMREAD_GRAYSCALE)
        
        self.white_nine_sample = cv.imread("./data/data/sample/white_nine.jpeg",cv.IMREAD_GRAYSCALE)
        self.white_up_sample = cv.imread("./data/data/sample/white_up.jpeg",cv.IMREAD_GRAYSCALE)
        self.white_x_sample = cv.imread("./data/data/sample/white_x.jpeg",cv.IMREAD_GRAYSCALE)
        
        self.yellow_right_sample = cv.imread("./data/data/sample/yellow_right.jpeg",cv.IMREAD_GRAYSCALE)
        self.yellow_z_sample = cv.imread("./data/data/sample/yellow_z.jpeg",cv.IMREAD_GRAYSCALE)
        self.yellow_zero_sample = cv.imread("./data/data/sample/yellow_zero.jpeg",cv.IMREAD_GRAYSCALE)

        #detect and compute the keypoints and descriptions
        # dunnid orb i guess , we use sift instead
        self.orb = cv.ORB_create()
        self.sift = cv.SIFT_create()
        
        self.blue_six_sample_kp, self.blue_six_sample_des = self.sift.detectAndCompute(self.blue_six_sample,None)
        self.blue_down_sample_kp, self.blue_down_sample_des = self.sift.detectAndCompute(self.blue_down_sample,None)
        self.blue_y_sample_kp, self.blue_y_sample_des = self.sift.detectAndCompute(self.blue_y_sample,None)
        
        self.green_circle_sample_kp, self.green_circle_sample_des = self.sift.detectAndCompute(self.green_circle_sample,None)
        self.green_seven_sample_kp, self.green_seven_sample_des = self.sift.detectAndCompute(self.green_seven_sample,None)
        self.green_w_sample_kp, self.green_w_sample_des = self.sift.detectAndCompute(self.green_w_sample,None)
        
        self.red_eight_sample_kp, self.red_eight_sample_des = self.sift.detectAndCompute(self.red_eight_sample,None)
        self.red_left_sample_kp, self.red_left_sample_des = self.sift.detectAndCompute(self.red_left_sample,None)
        self.red_v_sample_kp, self.red_v_sample_des = self.sift.detectAndCompute(self.red_v_sample,None)
        
        self.white_nine_sample_kp, self.white_nine_sample_des = self.sift.detectAndCompute(self.white_nine_sample,None)
        self.white_up_sample_kp, self.white_up_sample_des = self.sift.detectAndCompute(self.white_up_sample,None)
        self.white_x_sample_kp, self.white_x_sample_des = self.sift.detectAndCompute(self.white_x_sample,None)
        
        self.yellow_right_sample_kp, self.yellow_right_sample_des = self.sift.detectAndCompute(self.yellow_right_sample,None)
        self.yellow_zero_sample_kp, self.yellow_zero_sample_des = self.sift.detectAndCompute(self.yellow_zero_sample,None)
        self.yellow_z_sample_kp, self.yellow_z_sample_des = self.sift.detectAndCompute(self.yellow_z_sample,None)
        
        #store descriptors and keypoints in a dictionary
        self.des_and_kp={
            "blue_six":[self.blue_six_sample_kp,self.blue_six_sample_des],
            "blue_down":[self.blue_down_sample_kp,self.blue_down_sample_des],
            "blue_y":[self.blue_y_sample_kp,self.blue_y_sample_des],
            "green_circle":[self.green_circle_sample_kp,self.green_circle_sample_des],
            "green_seven":[self.green_seven_sample_kp,self.green_seven_sample_des],
            "green_w":[self.green_w_sample_kp,self.green_w_sample_des],
            "red_eight":[self.red_eight_sample_kp,self.red_eight_sample_des],
            "red_left":[self.red_left_sample_kp,self.red_left_sample_des],
            "red_v":[self.red_v_sample_kp,self.red_v_sample_des],
            "white_nine":[self.white_nine_sample_kp,self.white_nine_sample_des],
            "white_up":[self.white_up_sample_kp,self.white_nine_sample_des],
            "white_x":[self.white_x_sample_kp,self.white_x_sample_des],
            "yellow_right":[self.yellow_right_sample_kp,self.yellow_right_sample_des],
            "yellow_zero":[self.yellow_zero_sample_kp,self.yellow_zero_sample_des],
            "yellow_z": [self.yellow_z_sample_kp,self.yellow_z_sample_des]
        }

        self.cropped_img=None
        self.detected_image="No image detected"
        print("load finished")


    #function to turn on camera
    def start_video(self):
        cap = cv.VideoCapture(0)
        #if camera can read, proceed to detect frames
        while(True):
            ret, self.frame = cap.read()
            #check if camera can read
            if ret is None:
                print("cap could not read.Check camera source")
                exit(0)

            #if camera is able to read
            else:
                #initialise resulting frame with a copy of the raw frame
                self.frame_res = self.frame.copy()

                #detect_image-- detects image and draws rectangle on resulting frame: frame_res
                self.detect()
                #self.recognise()
                #show frame res
                cv.imshow("img",self.frame_res)
                k = cv.waitKey(30)
                if k == 27:
                    break
        cap.release()
        cv.destroyAllWindows()

    '''function to start detecting, displaying input
    function returns bounding box coordinates for image recognition'''
    def detect(self):
        #convert frame to grayscale
        gray = cv.cvtColor(self.frame.copy(),cv.COLOR_BGR2GRAY)
        
        #detect bounding boxes in grayscale image
        #nearest neightbours numbers are tuned to give the best results
        blue_downs = self.blue_down_cascade.detectMultiScale(gray,1.01,20)
        blue_ys = self.blue_y_cascade.detectMultiScale(gray,1.05,40)
        #actually white 9
        blue_sixes = self.blue_six_cascade.detectMultiScale(gray,1.3,12)

        
        white_nines = self.white_nine_cascade.detectMultiScale(gray,1.3,12)
        white_ups = self.white_up_cascade.detectMultiScale(gray,1.01,15)
        white_xs = self.white_x_cascade.detectMultiScale(gray,1.05,70)
        
        green_sevens = self.green_seven_cascade.detectMultiScale(gray,1.05,100)
        green_ws=self.green_w_cascade.detectMultiScale(gray,1.05,30)
        green_circles=self.green_circle_cascade.detectMultiScale(gray,1.01,65)
        

        red_vs=self.red_v_cascade.detectMultiScale(gray,1.05,70)
        red_lefts=self.red_left_cascade.detectMultiScale(gray,1.01,85)
        red_eights=self.red_eight_cascade.detectMultiScale(gray,1.05,70)

        yellow_zeros =self.yellow_zero_cascade.detectMultiScale(gray,1.05,40)
        yellow_zs =self.yellow_z_cascade.detectMultiScale(gray,1.05,70)
        yellow_rights =self.yellow_right_cascade.detectMultiScale(gray,1.3,12)
        
        #red_eight dont need cascade because white 6 detects
        #red_eights = self.red_eight_cascade.detectMultiScale(gray,1.3,12)

        '''(once trained)insert other cascades here'''

        #plot rectangle
        #W needs tuning
        class_names=["white_nines","green_sevens","yellow_zeros","green_ws","red_vs"
        ,"white_ups","white_xs","green_circles","red_lefts","red_eights","yellow_zs",
        "yellow_rights","blue_downs","blue_ys","blue_sixes"]

        detected_classes =[white_nines,green_sevens,yellow_zeros,green_ws,red_vs
        ,white_ups,white_xs,green_circles,red_lefts,red_eights,yellow_zs,yellow_rights,
        blue_downs,blue_ys,blue_sixes]
        #for each_class in images_detected:
            #show detected images

        index=0
        for detected_images in detected_classes:
            if detected_images is None:
                print("No detected class")
            else:
                print(len(detected_images))
                for (x,y,w,h )in detected_images:
                    #crop images of the detected image for recognition
                    cv.rectangle(
                        self.frame_res,
                        (x,y),
                        (x+w,y+h),
                        (0,255,0),
                        2
                    )
                    cv.putText(
                        self.frame_res,
                        class_names[index],
                        (x,y-10),
                        cv.FONT_HERSHEY_SIMPLEX,
                        0.9,
                        (0,0,255),
                        2
                    )
            index+=1
        """ self.cropped_img = self.frame_res.copy()[y:y+h, x:x+w]
        print(self.cropped_img) """
        #self.recognise()


    '''Function to crop the bounding box of the original image and recognise matching
    features of the image. returns string of identification -use ORB FAST feature matching'''
    def recognise(self):
        if self.cropped_img is None:
            print("no cropped images")
        else:
            kp,des = self.sift.detectAndCompute(self.cropped_img,None)
            if des is None:
                print("no descriptors")

            #if descriptors are present, find matches and image
            else:
                bf = cv.BFMatcher()
                #find sample with the max match
                max_matches= 0
                # find the image with the max number of matches
                for name,sample in self.des_and_kp.items():
                    #match descriptors of frame and the sample descriptor
                    matches = bf.knnMatch(des,sample[1],k=2)
                    #find the number of good matches with ratio test, where distance>0.75
                    good_matches_count=0
                    for m,n in matches:
                        if m.distance < 0.75*n.distance:
                            good_matches_count+=1
                    
                    #check if the sample image is indeed the best match
                    if good_matches_count > max_matches:
                        max_matches = good_matches_count
                        self.detected_image = name
                
                #if max matches == 0
                if max_matches == 0:
                    self.detected_image = "No Image found"
                    print(self.detected_image)
                #if there are good matches
                else:
                    print(self.detected_image)
                    cv.rectangle(
                            self.frame_res,
                            (self.x,self.y),
                            (self.x+self.w,self.y+self.h),
                            (0,255,0),
                            2
                    )
                    cv.putText(
                        self.frame_res,
                        self.detected_image,
                        (self.x,self.y-10),
                        cv.FONT_HERSHEY_SIMPLEX,
                        0.9,
                        (0,0,255),
                        2
                    )






c = detect_and_recognise()
c.start_video()