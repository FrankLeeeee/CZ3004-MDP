import os
from collections import defaultdict
from pathlib import Path

import cv2
import numpy as np

try:
    from image_recognition.darknet import darknet
except OSError as e:
    print(f'Error when loading Darknet: {e}, skip loading the lib.')


class DarknetModel(object):

    def __init__(
            self,
            yolo_cfg: os.PathLike,
            yolo_obj_data: os.PathLike,
            weight_chosen: os.PathLike,
            device: int = None,
            batch_size: int = 1,
    ):
        self.batch_size = batch_size
        self.device = device or 0 if darknet.hasGPU else None
        if self.device:
            darknet.set_gpu = device

        self.network, self.class_names, self.class_colors = \
            darknet.load_network(str(yolo_cfg), str(yolo_obj_data), str(weight_chosen), batch_size=self.batch_size)
        self._width = darknet.network_width(self.network)
        self._height = darknet.network_height(self.network)

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
            result['class_names'].append(class_name)
            result['confidence'].append(float(confidence) / 100)
            result['bbox'].append(
                (bbox[0] / self._width, bbox[1] / self._height, bbox[2] / self._width, bbox[3] / self._height)
            )

        return dict(result)

    @staticmethod
    def _bbox2points(bbox):
        """
        From bounding box yolo format
        to corner points cv2 rectangle
        """
        x, y, w, h = bbox
        xmin = int(round(x - (w / 2)))
        xmax = int(round(x + (w / 2)))
        ymin = int(round(y - (h / 2)))
        ymax = int(round(y + (h / 2)))
        return xmin, ymin, xmax, ymax

    @staticmethod
    def draw_annotations(image_np: np.ndarray, detections, class_colors):

        # convert detections to darknet format
        if not detections:
            return image_np

        names = detections['class_names']
        height, width, _ = image_np.shape

        bboxes = list()
        for bbox in detections['bbox']:
            bboxes.append((bbox[0] * width, bbox[1] * height, bbox[2] * width, bbox[3] * height))

        detections_darknet = list(zip(names, detections['confidence'], bboxes))

        # from darknet.drawbox
        for label, confidence, bbox in detections_darknet:
            left, top, right, bottom = DarknetModel._bbox2points(bbox)
            cv2.rectangle(image_np, (left, top), (right, bottom), class_colors[label], 1)
            cv2.putText(image_np, "{} [{:.2f}]".format(label, float(confidence)),
                        (left, top - 5), cv2.FONT_HERSHEY_SIMPLEX, 0.5,
                        class_colors[label], 2)

        return image_np


if __name__ == "__main__":

    # set DARKNET_PATH first
    ROOT = Path(__file__).absolute().parent
    net = DarknetModel(ROOT / 'output/yolo-obj.cfg', ROOT / 'output/obj.data', ROOT / 'output/yolo-obj_50000.weights')
    image = cv2.imread(str(ROOT / 'images_taken/2.jpg'))
    result = net.predict(image)
    annotated_image = net.draw_annotations(image, result, net.class_colors)
    cv2.imwrite('2.jpg', annotated_image)
