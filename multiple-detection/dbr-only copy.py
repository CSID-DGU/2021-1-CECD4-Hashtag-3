import cv2 as cv
import numpy as np
import imutils
import time
from dbr import *

# Initialize Dynamsoft Barcode Reader
reader = BarcodeReader()
# Apply for a trial license: https://www.dynamsoft.com/customer/license/trialLicense
license_key = "LICENSE KEY"
reader.init_license(license_key)
color = (0,0,255)
thickness = 2

objects1 = ["image/1.0.jpg","image/1.1.png","image/1.2.png","image/1.3.png","image/1.4.png"]
objects2 = ["image/2.0.jpg","image/2.1.png","image/2.2.png","image/2.3.png","image/2.4.png","image/2.5.png"]
objects3 = ["image/3.0.jpg","image/3.1.jpg","image/3.2.jpg"]


def decodeframe(frame):
    try:
        text_results = reader.decode_buffer(frame)

        if text_results != None:
            for text_result in text_results:
                print("Barcode Format :")
                print(text_result.barcode_format_string) # QR_CODE, EAN_13 포맷 정보를 알려줌
                print("Barcode Text :")
                print(text_result.barcode_text) # [Attention(exceptionCode:-10016)] **tp://www*dongguk*e****bs*kr**n*ex*jsp 코드 정보
                print("Localization Points : ")
                print(text_result.localization_result.localization_points) # [(90, 52), (191, 52), (190, 154), (90, 154)] 4개의 꼭짓점 위치
                print("-------------")
                points = text_result.localization_result.localization_points

                cv.line(frame, points[0], points[1], color, thickness)
                cv.line(frame, points[1], points[2], color, thickness)
                cv.line(frame, points[2], points[3], color, thickness)
                cv.line(frame, points[3], points[0], color, thickness)

                cv.putText(frame, text_result.barcode_text, (min([point[0] for point in points]), min([point[1] for point in points])), cv.FONT_HERSHEY_SIMPLEX, 1, color, thickness)
    except BarcodeReaderError as bre:
        print(bre)


# Load an frame

def printImage(objects):
    cnt = 0
    for obj in objects:
        frame = cv.imread(obj) 
        if cnt == 0:
            imageHeight, imageWidth = frame.shape[:2]
            resizeHeight = int(0.8 * imageHeight) 
            resizeWidth = int(0.4 * imageWidth) 
            resizeImageNDArray = cv.resize(frame, (resizeHeight, resizeWidth), interpolation = cv.INTER_CUBIC)
        elif cnt == 1:
            imageHeight, imageWidth = frame.shape[:2]
            resizeHeight = int(0.5 * imageHeight) 
            resizeWidth = int(0.2 * imageWidth) 
            resizeImageNDArray = cv.resize(frame, (resizeHeight, resizeWidth), interpolation = cv.INTER_CUBIC)
        else:
            imageHeight, imageWidth = frame.shape[:2]
            resizeHeight = int(0.8 * imageHeight) 
            resizeWidth = int(0.6 * imageWidth) 
            resizeImageNDArray = cv.resize(frame, (resizeHeight, resizeWidth), interpolation = cv.INTER_CUBIC)

        cv.imshow('QR Detection', resizeImageNDArray)
        cnt += 1
        cv.waitKey(2000)
        cv.destroyAllWindows()

    cnt = 0
    for obj in objects:
        frame = cv.imread(obj) 
        decodeframe(frame)
        if cnt == 0:
            imageHeight, imageWidth = frame.shape[:2]
            resizeHeight = int(0.8 * imageHeight) 
            resizeWidth = int(0.4 * imageWidth) 
            resizeImageNDArray = cv.resize(frame, (resizeHeight, resizeWidth), interpolation = cv.INTER_CUBIC)
        elif cnt == 2:
            imageHeight, imageWidth = frame.shape[:2]
            resizeHeight = int(0.5 * imageHeight) 
            resizeWidth = int(0.2 * imageWidth) 
            resizeImageNDArray = cv.resize(frame, (resizeHeight, resizeWidth), interpolation = cv.INTER_CUBIC)
        else:
            imageHeight, imageWidth = frame.shape[:2]
            resizeHeight = int(0.8 * imageHeight) 
            resizeWidth = int(0.6 * imageWidth) 
            resizeImageNDArray = cv.resize(frame, (resizeHeight, resizeWidth), interpolation = cv.INTER_CUBIC)
        cv.imshow('QR Detection', resizeImageNDArray)
        cv.waitKey(2000)
        cv.destroyAllWindows()

printImage(objects1)
printImage(objects2)
printImage(objects3)


# threshold = 0.6  

# # Load class names and YOLOv3-tiny model
# classes = open('qrcode.names').read().strip().split('\n')
# net = cv.dnn.readNetFromDarknet('qrcode-yolov3-tiny.cfg', 'qrcode-yolov3-tiny.weights')
# net.setPreferableBackend(cv.dnn.DNN_BACKEND_OPENCV)
# # net.setPreferableTarget(cv.dnn.DNN_TARGET_CPU) # DNN_TARGET_OPENCL DNN_TARGET_CPU DNN_TARGET_CUDA

# start_time = time.monotonic()
# # Convert frame to blob
# blob = cv.dnn.blobFromframe(frame, 1/255, (416, 416), swapRB=True, crop=False)
# elapsed_ms = (time.monotonic() - start_time) * 1000
# print('blobFromframe in %.1fms' % (elapsed_ms))

# def postprocess(frame, outs):
#     frameHeight, frameWidth = frame.shape[:2]

#     classIds = []
#     confidences = []
#     boxes = []

#     for out in outs:
#         for detection in out:
#             scores = detection[5:]
#             classId = np.argmax(scores)
#             confidence = scores[classId]
#             if confidence > threshold:
#                 x, y, width, height = detection[:4] * np.array([frameWidth, frameHeight, frameWidth, frameHeight])
#                 left = int(x - width / 2)
#                 top = int(y - height / 2)
#                 classIds.append(classId)
#                 confidences.append(float(confidence))
#                 boxes.append([left, top, int(width), int(height)])

#     indices = cv.dnn.NMSBoxes(boxes, confidences, threshold, threshold - 0.1)
#     for i in indices:
#         i = i[0]
#         box = boxes[i]
#         left = box[0]
#         top = box[1]
#         width = box[2]
#         height = box[3]

#         # Draw bounding box for objects
#         cv.rectangle(frame, (left, top), (left + width, top + height), (0, 0, 255))

#         # Draw class name and confidence
#         label = '%s:%.2f' % (classes[classIds[i]], confidences[i])
#         cv.putText(frame, label, (left, top - 15), cv.FONT_HERSHEY_SIMPLEX, 0.5, (255,0,0))

#         result = decodeframe(frame, left, top, left + width, top + height)
#         # Draw barcode results
#         if not result is None:
#             label = '%s' % (result.barcode_text)
#             cv.putText(frame, label, (left, top - 5), cv.FONT_HERSHEY_SIMPLEX, 0.5, (255,0,0))

# # Determine the output layer
# ln = net.getLayerNames()
# ln = [ln[i[0] - 1] for i in net.getUnconnectedOutLayers()]

# net.setInput(blob)
# start_time = time.monotonic()
# # Compute
# outs = net.forward(ln)
# elapsed_ms = (time.monotonic() - start_time) * 1000
# print('forward in %.1fms' % (elapsed_ms))

# start_time = time.monotonic()
# postprocess(frame, outs)
# elapsed_ms = (time.monotonic() - start_time) * 1000
# print('postprocess in %.1fms' % (elapsed_ms))


