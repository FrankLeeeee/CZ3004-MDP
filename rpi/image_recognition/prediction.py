import os
from collections import defaultdict
from pathlib import Path

import cv2
import numpy as np

from darknet import darknet


class DarknetModel:

    def __init__(
            self,
            yolo_cfg: os.PathLike,
            yolo_obj_data: os.PathLike,
            weight_chosen: os.PathLike,
            batch_size: int = 1,
    ):
        self.batch_size = batch_size
        self.network, self.class_names, self.class_colors = \
            darknet.load_network(str(yolo_cfg), str(yolo_obj_data), str(weight_chosen), batch_size=self.batch_size)
        self._width = darknet.network_width(self.network)
        self._height = darknet.network_height(self.network)

        print("load completed successfully")

    def preprocess(self, image_np: np.ndarray):
        image_rgb = cv2.cvtColor(image_np, cv2.COLOR_BGR2RGB)
        image_resized = cv2.resize(image_rgb, (self._width, self._height), interpolation=cv2.INTER_LINEAR)

        return image_resized

    def predict(self, image_np: np.ndarray, threshold: float = 0.25):
        image_np = self.preprocess(image_np)

        # Darknet doesn't accept numpy images.
        # Create one with image we reuse for each detect
        darknet_image = darknet.make_image(self._width, self._height, 3)
        darknet.copy_image_from_bytes(darknet_image, bytes(image_np))

        detections = darknet.detect_image(self.network, self.class_names, darknet_image, thresh=threshold)
        darknet.free_image(darknet_image)

        result = defaultdict(list)
        for detection in detections:
            # confidence is in percentage, bbox is in (x, y, w, h) as pixels
            class_name, confidence, bbox = detection
            result['class_ids'].append(self.class_names.index(class_name))
            result['confidence'].append(float(confidence) / 100)
            result['bbox'].append(
                (bbox[0] / self._width, bbox[1] / self._height, bbox[2] / self._width, bbox[3] / self._height)
            )

        return dict(result)

    def draw_annotations(self, image_np: np.ndarray, detections):

        # convert detections to darknet format
        names = list()
        for id_ in detections['class_ids']:
            names.append(self.class_names[id_])
        height, width, _ = image_np.shape

        bboxes = list()
        for bbox in detections['bbox']:
            bboxes.append((bbox[0] * width, bbox[1] * height, bbox[2] * width, bbox[3] * height))

        detections_darknet = list(zip(names, detections['confidence'], bboxes))
        image_np = darknet.draw_boxes(detections_darknet, image_np, self.class_colors)
        return image_np


if __name__ == "__main__":

    # set DARKNET_PATH first
    ROOT = Path(__file__).absolute().parent
    net = DarknetModel(ROOT / 'output/yolo-obj.cfg', ROOT / 'output/obj.data', ROOT / 'output/yolo-obj_50000.weights')
    image = cv2.imread(str(ROOT / 'images_taken/2.jpg'))
    result = net.predict(image)
    annotated_image = net.draw_annotations(image, result)
    cv2.imwrite('2.jpg', annotated_image)
