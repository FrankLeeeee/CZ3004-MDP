from ctypes import *
import random
import os
import cv2
import time
from darknet import darknet
import argparse
from threading import Thread, enumerate
from queue import Queue


    
class Prediction:

    def __init__(
        self,
        dst_folder_path = "./images_detected",
        yolo_cfg = "yolo-obj.cfg",
        yolo_obj_data = "obj.data",
        weight_chosen = "yolo-obj_50000.weights"
        ):

        #set where to take images and where to put detected images
        self.dst_path = dst_folder_path
        self.count = 0

        #loads model
        self.network, self.class_names, self.class_colors = darknet.load_network(
            #args.config_file
            yolo_cfg,
            #args.data_file
            yolo_obj_data,
            #args.weights
            weight_chosen,
            batch_size=1
        )

        #debug
        #self.two_count=0
        #self.detected_image_count =0
        #self.zero_count=0
        print("load completed successfully")
    

    def image_detection(self,raw_image,thresh):
        # Darknet doesn't accept numpy images.
        # Create one with image we reuse for each detect
        width = darknet.network_width(self.network)
        height = darknet.network_height(self.network)
        darknet_image = darknet.make_image(width, height, 3)

        image = raw_image
        image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        image_resized = cv2.resize(image_rgb, (width, height),
                                interpolation=cv2.INTER_LINEAR)

        darknet.copy_image_from_bytes(darknet_image, image_resized.tobytes())
        
        detections = darknet.detect_image(self.network, self.class_names, darknet_image, thresh=thresh)
        
        #exception handling for no detection case
        print(len(detections))
        if len(detections) == 0:
            self.zero_count+=1
            return None, None
        else:
            #get best detection from the list
            best_detection = [detections[-1]]
            
            #debug
            #if(len(best_detection)>1):
            #    self.two_count+=1
            #self.detected_image_count+=1
            #print(self.detected_image_count)
            #print("*"*30)
            #debug

            darknet.free_image(darknet_image)
            image = darknet.draw_boxes(detections, image_resized, self.class_colors)
            
            return cv2.cvtColor(image, cv2.COLOR_BGR2RGB), detections

    #saving annotations

    def convert2relative(self,image, bbox):
        """
        YOLO format use relative coordinates for annotation
        """
        x, y, w, h = bbox
        height, width, _ = image.shape

        #this returns actual coordinates
        return x,y,w,h

    def get_annotations(self, image, detections):

        file_name = self.dst_path+"/"+str(self.count) + ".txt"
        for label, confidence, bbox in detections:
            x, y, w, h = self.convert2relative(image, bbox)
            label = self.class_names.index(label)
            confidence = float(confidence)
        
        return label,(x,y,w,h),confidence

    def predict(self,image_np):

        image = cv2.resize(image_np,(720,480))

        #threshold for confidence level
        thresh = 0.5
        
        # detect image
        image_detected, detections = self.image_detection(
            image, thresh
        )
        
        #can change the return value to what you want, exception handling when detection is None
        if image_detected is None:
            print("No image detected")
            return None
        else:
            #save image into detected image path
            image_path = self.dst_path+"/"+str(self.count)+".jpg"
            cv2.imwrite(image_path,image_detected)
            class_id,(x,y,w,h),confidence = self.get_annotations(image,detections)
            
            #naming for detected files
            self.count+=1
            #actual class_id is +1 of the one used in training cos yolo nneed start with 0
            return class_id+1,(x,y,w,h),confidence


#note if raw_image have detected images all <threshold the program will return nothing    
if __name__ == "__main__":

    
    test_path = "/home/kevin/workspace/CZ3004-MDP/image_recognition/images_taken"
    
    p1 = Prediction()


    #mass test
    for image_name in os.listdir(test_path):
        image = cv2.imread(test_path+"/"+image_name)
        p1.predict(image)
    
    #print("Two Count: ",p1.two_count)
    #print("zero Count: ",p1.zero_count)
    #print("detected Count: ",p1.detected_image_count)
    #print("Missed Count: ",301-p1.detected_image_count-p1.zero_count)
    #indiv test
    '''test_path = "/home/kevin/workspace/CZ3004-MDP/image_recognition/images_taken/1.jpg"
    image=cv2.imread(test_path)
    p1 = Prediction()
    p1.predict(image)'''