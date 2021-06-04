import cv2 as cv
import numpy as np
import time
from dbr import *

# Initialize Dynamsoft Barcode Reader
reader = BarcodeReader()
# Apply for a trial license: https://www.dynamsoft.com/customer/license/trialLicense
license_key = "LICENSE KEY"
reader.init_license(license_key)

def decodeframe(frame, left, top, right, bottom):
    settings = reader.reset_runtime_settings() 
    settings = reader.get_runtime_settings()
    settings.region_bottom  = bottom
    settings.region_left    = left
    settings.region_right   = right
    settings.region_top     = top
    settings.barcode_format_ids = EnumBarcodeFormat.BF_QR_CODE
    settings.expected_barcodes_count = 1
    reader.update_runtime_settings(settings) #지정된 JSON 파일의 설정으로 런타임 설정 update

    try:
        text_results = reader.decode_buffer(frame) # 정의 된 형식의 이미지 픽셀을 포함하는 메모리 버퍼에서 바코드를 디코딩

        if text_results != None:
            return text_results[0] # 있으면 결과
            # for text_result in text_results:
            #     print("Barcode Format :")
            #     print(text_result.barcode_format_string)
            #     print("Barcode Text :")
            #     print(text_result.barcode_text)
            #     print("Localization Points : ")
            #     print(text_result.localization_result.localization_points)
            #     print("-------------")
    except BarcodeReaderError as bre:
        print(bre) # 예외처리

    return None


# Load an image
frame = cv.imread("jpg/qrbarcode2.PNG") # 사진 읽어옴

threshold = 0.6  

# Load class names and YOLOv3-tiny model
classes = open('qrcode.names').read().strip().split('\n')
net = cv.dnn.readNetFromDarknet('qrcode-yolov3-tiny.cfg', 'qrcode-yolov3-tiny.weights')
net.setPreferableBackend(cv.dnn.DNN_BACKEND_OPENCV)
# net.setPreferableTarget(cv.dnn.DNN_TARGET_CPU) # DNN_TARGET_OPENCL DNN_TARGET_CPU DNN_TARGET_CUDA

start_time = time.monotonic()
# Convert frame to blob
blob = cv.dnn.blobFromImage(frame, 1/255, (416, 416), swapRB=True, crop=False)
elapsed_ms = (time.monotonic() - start_time) * 1000
print('blobFromImage in %.1fms' % (elapsed_ms))

def postprocess(frame, outs):
    frameHeight, frameWidth = frame.shape[:2]

    classIds = []
    confidences = []
    boxes = []

    for out in outs:
        for detection in out:
            scores = detection[5:]
            classId = np.argmax(scores)
            confidence = scores[classId]
            if confidence > threshold:
                x, y, width, height = detection[:4] * np.array([frameWidth, frameHeight, frameWidth, frameHeight])
                left = int(x - width / 2)
                top = int(y - height / 2)
                classIds.append(classId)
                confidences.append(float(confidence))
                boxes.append([left, top, int(width), int(height)])

    indices = cv.dnn.NMSBoxes(boxes, confidences, threshold, threshold - 0.1)
    for i in indices:
        i = i[0]
        box = boxes[i]
        left = box[0]
        top = box[1]
        width = box[2]
        height = box[3]

        # Draw bounding box for objects
        cv.rectangle(frame, (left, top), (left + width, top + height), (0, 0, 255))

        # Draw class name and confidence
        label = '%s:%.2f' % (classes[classIds[i]], confidences[i])
        cv.putText(frame, label, (left, top - 15), cv.FONT_HERSHEY_SIMPLEX, 0.5, (255,0,0))

        result = decodeframe(frame, left, top, left + width, top + height) # decodeframe을 통해 디코딩 결과를 QR 위 글씨를 적는다.
        # Draw barcode results
        if not result is None:
            label = '%s' % (result.barcode_text)
            cv.putText(frame, label, (left, top - 5), cv.FONT_HERSHEY_SIMPLEX, 0.5, (255,0,0))

# Determine the output layer
ln = net.getLayerNames()
ln = [ln[i[0] - 1] for i in net.getUnconnectedOutLayers()]

net.setInput(blob) # input 설정 
start_time = time.monotonic()
# Compute
outs = net.forward(ln) #네트워크 실행
elapsed_ms = (time.monotonic() - start_time) * 1000
print('forward in %.1fms' % (elapsed_ms))

start_time = time.monotonic()
postprocess(frame, outs) # 출력
elapsed_ms = (time.monotonic() - start_time) * 1000
print('postprocess in %.1fms' % (elapsed_ms))


# imageHeight, imageWidth = frame.shape[:2]
# resizeHeight = int(0.5 * imageHeight) 
# resizeWidth = int(0.5 * imageWidth ) 
# resizeImageNDArray = cv.resize(frame, (resizeHeight, resizeWidth), interpolation = cv.INTER_CUBIC)


cv.imshow('QR Detection', frame) #imshow
cv.waitKey()


