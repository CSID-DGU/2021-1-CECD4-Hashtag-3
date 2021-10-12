import time
import cv2
import numpy as np

#from dbr import *
from .tracker import *

CONFIDENCE_THRESHOLD = 0.2
NMS_THRESHOLD = 0.4
COLORS = [(0, 255, 255), (255, 255, 0), (0, 255, 0), (255, 0, 0)]
tracker = EuclideanDistTracker()

# input : video? image?


def main(img):
    images = []
    count = 0

    net = cv2.dnn.readNet("yolov4-obj_last.weights", "yolov4-obj.cfg")
    net.setPreferableBackend(cv2.dnn.DNN_BACKEND_OPENCV)
    # net.setPreferableTarget(cv2.dnn.DNN_TARGET_CUDA)
    # make darknet model
    model = cv2.dnn_DetectionModel(net)
    model.setInputParams(size=(416, 416), scale=1/255, swapRB=True)

    images = []

    print("START")

    data = np.fromfile(img, np.uint8)
    frame = cv2.imdecode(data, cv2.IMREAD_UNCHANGED)  # 사진 읽어옴
    height1, width1, _ = frame.shape
    x1 = (130 * 416)//width1
    x2 = (370 * 416) // width1
    x3 = 416
    y1 = 416
    y2 = (100*416)//height1
    y3 = 416

    line = [x1, x2, x3, y1, y2, y3]

    # 1. detect object and return output
    classes, scores, boxes = model.detect(
        frame, CONFIDENCE_THRESHOLD, NMS_THRESHOLD)

    # 2. Object Tracking
    # boxes_cids = [x, y, w, h, (class, id)]
    boxes_cids = tracker.update(classes, boxes, frame, line)
    barcode_data = []
    for box_cid in boxes_cids:
        x, y, w, h, class_id, in_out, decode_info = box_cid
        cx, cy = (x+x+w)//2, (y+y+h)//2
        if class_id[0] == 0:
            id_name = "BARCODE " + decode_info
        else:
            id_name = "PRODUCT"
            if decode_info != ' ':
                barcode_data.append([in_out, decode_info])
        in_out_string = "IN" if in_out == True else "OUT"
        text = f"{in_out_string} ID ={str(class_id[1])} {id_name}"
        cv2.putText(frame, text, (x-15, y - 15),
                    cv2.FONT_HERSHEY_PLAIN, 1, (255, 0, 0), 1)
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
        cv2.line(frame, (cx, cy), (cx, cy), (0, 0, 255), 5)
        frame = cv2.line(frame, (0, y1), (x1, y2), (255, 255, 0), 3)
        frame = cv2.line(frame, (x1, y2), (x2, y2), (255, 255, 0), 3)
        frame = cv2.line(frame, (x2, y2), (x3, y3), (255, 255, 0), 3)

    cv2.imshow("detections", frame)
    key = cv2.waitKey()
    return barcode_data

    images.append(frame)

    cv2.destroyAllWindows()


def get_product_info():
    products = tracker.get_all_product()
    barcode_list = list(map(lambda x: products[x][1], products))
    return barcode_list


if __name__ == '__main__':
    main()
