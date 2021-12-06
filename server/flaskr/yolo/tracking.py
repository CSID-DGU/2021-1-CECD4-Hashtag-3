import time
import cv2
import numpy as np

#from dbr import *
from .tracker import *

CONFIDENCE_THRESHOLD = 0.2
NMS_THRESHOLD = 0.4
COLORS = [(0, 255, 255), (255, 255, 0), (0, 255, 0), (255, 0, 0)]
tracker = EuclideanDistTracker()
images = []
# input : video? image?


def main(img):
    #images = []
    count = 0

    net = cv2.dnn.readNet("yolov4-obj_last.weights", "yolov4-obj.cfg")
    net.setPreferableBackend(cv2.dnn.DNN_BACKEND_CUDA)
    net.setPreferableTarget(cv2.dnn.DNN_TARGET_CUDA)
    # make darknet model
    model = cv2.dnn_DetectionModel(net)
    model.setInputParams(size=(416, 416), scale=1/255, swapRB=True)

    #images = []

    print("START")

    data = np.fromfile(img, np.uint8)
    frame = cv2.imdecode(data, cv2.IMREAD_UNCHANGED)  # 사진 읽어옴
    height1, width1, _ = frame.shape
    height1, width1 = 816, 816
    x1 = int(width1*0.1)
    x2 = width1-x1
    x3 = width1
    y1 = height1
    y2 = int(height1 * 0.5)
    y3 = height1
    line = [x1, x2, x3, y1, y2, y3]
    first = False

    origin = frame
    frame = cv2.resize(frame, (816, 816), fx=0, fy=0,
                       interpolation=cv2.INTER_CUBIC)
    # 1. detect object and return output
    classes, scores, boxes = model.detect(
        frame, CONFIDENCE_THRESHOLD, NMS_THRESHOLD)

    # 2. Object Tracking
    # boxes_cids = [x, y, w, h, (class, id)]
    decoding_list, dont_decoding, update_cart, now_barcode = tracker.update(
        classes, boxes, frame, line)
    barcode_data = []

    for decoding_info in decoding_list:
        class_id, info = decoding_info
        x, y, w, h, in_out, decode_info = info
        cx, cy = (x+x+w)//2, (y+y+h)//2
        if class_id[0] == 0:
            id_name = "BARCODE "
        else:
            id_name = "PRODUCT"
        text = f"ID ={str(class_id[1])} {id_name}"
        cv2.putText(frame, text, (x-15, h+y - 15),
                    cv2.FONT_HERSHEY_PLAIN, 1, (255, 0, 0), 1)
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
    #cv2.line(frame, (cx, cy), (cx, cy), (0, 0, 255), 5)

    for dont in dont_decoding:
        class_id, info = dont
        x, y, w, h, in_out, decode_info = info
        cv2.rectangle(frame, (x, y), (x+w, h+y), (255, 0, 255), 2)
        barcode_data.append([False, None, False])

    for cart in update_cart:
        class_id, info = cart
        x, y, w, h, in_out, decode_info = info
        #barcode_data.append([in_out, decode_info, True])
        if class_id[0] == 0:
            id_name = "BARCODE " + decode_info
        else:
            id_name = "PRODUCT" + decode_info
        text = f"ID ={str(class_id[1])} {id_name}"
        cv2.putText(frame, text, (x, h+y - 15),
                    cv2.FONT_HERSHEY_PLAIN, 1, (255, 0, 0), 1)
        if in_out is True:
            color = (0, 255, 255)
        else:
            color = (0, 255, 100)
        cv2.rectangle(frame, (x, y), (x+w, h+y), color, 2)

    for decode_info in now_barcode:
        barcode_data.append([True, decode_info, False])

    frame = cv2.line(frame, (x1, y1), (x1, y2), (255, 255, 0), 3)
    frame = cv2.line(frame, (x1, y2), (x2, y2), (255, 255, 0), 3)
    frame = cv2.line(frame, (x2, y2), (x2, y3), (255, 255, 0), 3)

    images.append(frame)
    # cv2.imwrite("output_img/"+str(cnt)+".jpg", frame)
    # cnt += 1
    #cv2.imshow("Frame", frame)
    #key = cv2.waitKey()
    return barcode_data  # , frame
    # 이건 왜 여기 있는거지?
    cv2.destroyAllWindows()


def get_product_info():
    products = tracker.get_all_product()
    return products


def change_to_avi():
    print("비디오 생성 시작")
    print(f"len(images) = {len(images)}")
    fourcc = cv2.VideoWriter_fourcc(*'DIVX')
    output_filename = f"./data/out_test(2).avi"
    out = cv2.VideoWriter(output_filename, fourcc, 30.0, (816, 816))
    for i in range(len(images)):
        out.write(images[i])
    print(f"{len(images)}비디오 생성 완료 ")
    out.release()


if __name__ == '__main__':
    main()
