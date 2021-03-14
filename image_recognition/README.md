# HOW TO USE

## DOWNLOAD AND INSTALLATION

ON LINUX:
1) git clone the repository https://github.com/AlexeyAB
2) pip install OPENCV latest
3) Make sure you have **CMAKE Version >= 3.12 **
4) In `Makefile` change your build configurations

   * __Without gpu build:__
   
   
     1)  set OPENCV = 1
     1)  LIBSO = 1
   * __GPU Build:__
     1)  set OPENCV = 1
     1)  LIBSO = 1
     1)  CUDNN_HALF =1
     1)  CUDNN = 1
     1)  GPU = 1
5) Cmake from darknet directory `cd ./darknet`
6) `Prediction.py` runs inference on raw images in numpy array format and returns and writes the image with the bounding box with * confidence level >0.5 * into `images_detected\` folder 
7) predict() in Prediction class returns the `class_id`,`(x_coord,y_coord,width_bbox,h_bbox)`,`confidence` of the prediction
8) For more information visit Alexey's repository on [Darknet](https://github.com/AlexeyAB/darknet#how-to-compile-on-linux-using-cmake)
