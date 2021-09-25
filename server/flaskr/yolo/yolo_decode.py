import cv2
import time
import numpy as np
from dbr import *

CONFIDENCE_THRESHOLD = 0.2
NMS_THRESHOLD = 0.4
COLORS = [(0, 255, 255), (255, 255, 0), (0, 255, 0), (255, 0, 0)]

def main(img):

    # Initialize Dynamsoft Barcode Reader
    reader = BarcodeReader()
    # Apply for a trial license: https://www.dynamsoft.com/customer/license/trialLicense
    license_key = "t0069fQAAAEa2EJJHDv9UNOS3CgvAm98JE+aUy21n+Nw5oho6HLqNBfvxdd6Y0pkdxoVhxLkflSAC3/MHNHziWZZu0LpCWwB4"
    reader.init_license(license_key)
    decode_data = []

    def decodeframe(frame, left, top, right, bottom):
        settings = reader.reset_runtime_settings() 
        settings = reader.get_runtime_settings()
        settings.region_bottom  = bottom
        settings.region_left    = left
        settings.region_right   = right
        settings.region_top     = top
        #settings.barcode_format_ids = EnumBarcodeFormat.BF_QR_CODE
        #settings.expected_barcodes_count = 1
        reader.update_runtime_settings(settings) #지정된 JSON 파일의 설정으로 런타임 설정 update

        try:
            text_results = reader.decode_buffer(frame) # 정의 된 형식의 이미지 픽셀을 포함하는 메모리 버퍼에서 바코드를 디코딩
            if text_results != None:
                for text_result in text_results:
                    # print("Barcode Format :")
                    # print(text_result.barcode_format_string)
                    # print("Barcode Text :")
                    # print(text_result.barcode_text)
                    decode_data.append(text_result.barcode_text)
                    # print("Localization Points : ")
                    # print(text_result.localization_result.localization_points)
                    # print("-------------")
            return text_results # 있으면 결과
        except BarcodeReaderError as bre:
            print(bre) # 예외처리

        return None

    class_names = []

    with open("code.names", "r") as f:
        class_names = [cname.strip() for cname in f.readlines()]

    data = np.fromfile(img, np.uint8)

    frame = cv2.imdecode(data,cv2.IMREAD_UNCHANGED)# 사진 읽어옴

    net = cv2.dnn.readNet("yolov4-obj_last.weights", "yolov4-obj.cfg")
    net.setPreferableBackend(cv2.dnn.DNN_BACKEND_CUDA)
    net.setPreferableTarget(cv2.dnn.DNN_TARGET_CUDA_FP16)

    model = cv2.dnn_DetectionModel(net)
    model.setInputParams(size=(416, 416), scale=1/255, swapRB=True)

    # (grabbed, frame) = img.read()
    # if not grabbed:
    #     exit()

    start = time.time()
    classes, scores, boxes = model.detect(frame, CONFIDENCE_THRESHOLD, NMS_THRESHOLD)
    end = time.time()

    start_drawing = time.time()
    for (classid, score, box) in zip(classes, scores, boxes):
        color = COLORS[int(classid) % len(COLORS)]
        label = "%s : %f" % (class_names[classid[0]], score)
        left = box[0]
        top = box[1]
        width = box[2]
        height = box[3]
        result = decodeframe(frame, left, top, left + width, top + height) 
        cv2.rectangle(frame, (left, top), (left + width, top + height), (0, 0, 255))
        cv2.putText(frame, label, (left, top - 15), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255,0,0))

        if not result is None:
            label = '%s' % (result[0].barcode_text)
            print(result[0].barcode_text)
            cv2.putText(frame, label, (left, top - 5), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255,0,0))

    end_drawing = time.time()
    
    fps_label = "FPS: %.2f (excluding drawing time of %.2fms)" % (1 / (end - start), (end_drawing - start_drawing) * 1000)
    cv2.putText(frame, fps_label, (0, 25), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 0), 2)
    cv2.imshow("detections", frame)
    cv2.waitKey()
    return decode_data


if __name__ == '__main__':
    main()
