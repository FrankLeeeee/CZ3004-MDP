# HOW TO USE

## DOWNLOAD AND INSTALLATION

ON LINUX:
1) git clone this repository
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
6) `run_video.py` runs inference on raw images from `images_taken\` folder and writes the image with the bounding box with * confidence level >0.75 * into `images_detected\` folder 
7)  a `.txt` file is also generated in the `images_detected\` folder that is in this format `class_id-1`,`bbox_x`, `bbox_y`, `bbox_w`, `bbox_h`,`confidence`
8) For more information visit Alexey's repository on [Darknet](https://github.com/AlexeyAB/darknet#how-to-compile-on-linux-using-cmake)
