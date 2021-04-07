import cv2 as cv
import os
import random
import imutils
import numpy as np
from PIL import Image
count=1

""" for image in os.listdir("./background"):
    img =cv.imread("./background/"+image)
    img = cv.resize(img,(720,480))
    cv.imwrite("./background/"+str(count)+".jpeg",img)
    count+=1
exit(0) """

def increase_brightness(img, value):
    hsv = cv.cvtColor(img, cv.COLOR_BGR2HSV)
    h, s, v = cv.split(hsv)

    lim = 255 - value
    v[v > lim] = 255
    v[v <= lim] += value

    final_hsv = cv.merge((h, s, v))
    img = cv.cvtColor(final_hsv, cv.COLOR_HSV2BGR)
    return img

def noisy(noise_typ,image):

    if noise_typ == "gauss":
        row,col,ch= image.shape
        mean = 0
        #var = 0.1
    #sigma = var**0.5
        gauss = np.random.normal(mean,0.1,(row,col,ch))
        noisy = image + gauss
        return noisy
def create_samples(img_path,no_of_samples,class_no):
    
    img = cv.imread(img_path)
    # 4 backgrounds currently
    background_image_array=[]
    for image in os.listdir("./background"):
        background_image = cv.imread("./background/"+image)
        background_image_array.append(background_image)
    
    
    
    #get 6 sizes
    count=0
    size_array = [(25,25),(50,50),(100,100),(150,150),(200,200),(250,250)]
    for background_img in background_image_array:
        
        for size in size_array:
            img_resized = cv.resize(img.copy(),size)

            #get random angle

            for i in range(0,no_of_samples):
                angle = random.randint(0,10)
                img_rotated = imutils.rotate_bound(img_resized,angle)


                #width and height of the image
                h, w, c = img_rotated.shape
                bg_h, bg_w, bg_c = background_image.shape

                #get x and y coordinates
                x_image = int(random.randint(0,bg_w-w))
                y_image = int(random.randint(0,bg_h-h))
                
                x_center = round((((w/2)+x_image)/bg_w),6)
                y_center = round(((h/2)+y_image)/bg_h,6)
                w_percent = round(w/bg_w,6)
                h_percent = round(h/bg_h,6)

                #crop into background image


                bg_img = background_img.copy()

                bg_img[y_image:y_image+h,x_image:x_image+w, :] = img_rotated
                img_res = bg_img
                

                #increase brightness
                brightness_increment = random.randint(0,40)
                img_res = increase_brightness(img_res,brightness_increment)
                

                # add gaussian nosie
                img_res = noisy("gauss",img_res.copy())
                img_res = img_res.astype('uint8')

                #resize image to 480xx480
                img_res = cv.resize(img_res,(480,480))

                #write into iamge file               
                #cv.imwrite("./debug/image.jpg",img_res)
                cv.imwrite("./data/labels/"+class_no+"_"+str(count)+".jpg",img_res)



                # write test file into file
                # with open("./data/labels/" +class_no+"_"+str(count)+ ".txt","x") as f:
                #     f.write(str(class_no)+" "+str(x_center)+" "+ str(y_center)+" " +str(w_percent)+" "+str(h_percent)+"\n")
                count+=1


#2400 images for each

for sample_image in os.listdir("./sample"):
    class_no = sample_image[0:2]
    print(class_no)
    path = "./sample/"+sample_image
    create_samples(path,100,class_no)

