# HOW TO USE

## DOWNLOAD AND INSTALLATION

ON LINUX:
1) git clone this repository
2) In `Makefile` add input your build configurations
..* Without gpu build.
      - set OPENCV = 1
      - LIBSO = 1
..* GPU Build
      - set OPENCV = 1
      - LIBSO = 1
      - CUDNN_HALF =1
      - CUDNN = 1
      - GPU = 1
4) Make sure you have **CMAKE Version >= 3.12 **
5) Cmake from darknet directory `cd ./darknet`
6) `run_video.py` runs inference on raw images from `images_taken\` folder and writes the image with the bounding box with * confidence level >0.75 * into `images_detected\` folder 
7)  a `.txt` file is also generated in the `images_detected\` folder that is in this format `class_id-1`,`bbox_x`, `bbox_y`, `bbox_w`, `bbox_h`,`confidence`
