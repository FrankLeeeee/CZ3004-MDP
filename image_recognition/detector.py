#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 3/15/2021
"""
import os
from pathlib import Path

import cv2

os.environ['DARKNET_PATH'] = str(Path(__file__).absolute().parent / 'darknet')

from image_recognition.darknet import darknet


def predict(network, image: bytes, class_names, thresh=.25):
    darknet_image = darknet.make_image(width, height, 3)
    darknet.copy_image_from_bytes(darknet_image, image)
    detections = darknet.detect_image(network, class_names, darknet_image, thresh=thresh)
    return detections


def draw(image, detections, class_colors):
    image = darknet.draw_boxes(detections, image, class_colors)
    return cv2.cvtColor(image, cv2.COLOR_BGR2RGB), detections


net, class_names, class_colors = darknet.load_network('./output/yolo-obj.cfg', './output/obj.data', './output/yolov4-tiny.conv.29')
width = darknet.network_width(net)
height = darknet.network_height(net)
image = cv2.imread('./images_taken/2.jpg')

# preprocessing
image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
image_resized = cv2.resize(image_rgb, (width, height), interpolation=cv2.INTER_LINEAR)

print(predict(net, bytes(image_resized), class_names))
