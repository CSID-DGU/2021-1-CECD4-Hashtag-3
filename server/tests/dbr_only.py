import cv2 as cv
import numpy as np
import time
import io
from dbr import *

# Initialize Dynamsoft Barcode Reader
def main(img):
    reader = BarcodeReader()
    # Apply for a trial license: https://www.dynamsoft.com/customer/license/trialLicense
    license_key = "t0069fQAAAEa2EJJHDv9UNOS3CgvAm98JE+aUy21n+Nw5oho6HLqNBfvxdd6Y0pkdxoVhxLkflSAC3/MHNHziWZZu0LpCWwB4"
    reader.init_license(license_key)
    color = (0,0,255)
    thickness = 2
    decode_data = []
    
    def decodeframe(frame):
        try:
            text_results = reader.decode_buffer(frame)

            if text_results != None:
                for text_result in text_results:
                    # print("Barcode Format :")
                    # print(text_result.barcode_format_string)
                    decode_data.append(text_result.barcode_text)
                    # print("Barcode Text :")
                    # print(text_result.barcode_text)
                    # print("Localization Points : ")
                    # print(text_result.localization_result.localization_points)
                    # print("-------------")
                    points = text_result.localization_result.localization_points

                    cv.line(frame, points[0], points[1], color, thickness)
                    cv.line(frame, points[1], points[2], color, thickness)
                    cv.line(frame, points[2], points[3], color, thickness)
                    cv.line(frame, points[3], points[0], color, thickness)

                    cv.putText(frame, text_result.barcode_text, (min([point[0] for point in points]), min([point[1] for point in points])), cv.FONT_HERSHEY_SIMPLEX, 1, color, thickness)
        except BarcodeReaderError as bre:
            print(bre)


    # Load an frame
    data = np.fromfile(img, np.uint8)
    frame = cv.imdecode(data,cv.IMREAD_UNCHANGED)# 사진 읽어옴
    #frame = cv.imread("test/qrbarcode.PNG") # 사진 읽어옴
    decodeframe(frame)

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

    #cv.imshow('QR Detection', frame)
    cv.waitKey()
    return decode_data

if __name__ == '__main__':
    main()
