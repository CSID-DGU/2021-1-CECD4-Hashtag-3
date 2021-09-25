import cv2 as cv
import numpy as np
import time
import io
from dbr import *
import os

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
        settings.barcode_format_ids = EnumBarcodeFormat.BF_QR_CODE
        settings.expected_barcodes_count = 1
        reader.update_runtime_settings(settings) #지정된 JSON 파일의 설정으로 런타임 설정 update

        try:
            text_results = reader.decode_buffer(frame) # 정의 된 형식의 이미지 픽셀을 포함하는 메모리 버퍼에서 바코드를 디코딩
            print("text_result")
            print(text_results)
            if text_results != None:
                for text_result in text_results:
                    print("Barcode Format :")
                    print(text_result.barcode_format_string)
                    print("Barcode Text :")
                    print(text_result.barcode_text)
                    decode_data.append(text_result.barcode_text)
                    print("Localization Points : ")
                    print(text_result.localization_result.localization_points)
                    print("-------------")
            return text_results # 있으면 결과
        except BarcodeReaderError as bre:
            print(bre) # 예외처리

        return None


    # Load an image
    in_memory_file = io.BytesIO()
    img.save(in_memory_file)
    data = np.fromstring(in_memory_file.getvalue(),dtype=np.uint8)
        
    frame = cv.imdecode(data,cv.IMREAD_UNCHANGED)# 사진 읽어옴
    #frame = cv.imread("test/qrbarcode.PNG") # 사진 읽어옴
    threshold = 0.3  

    # Load class names and YOLOv3-tiny model
    classes = open('code.names').read().strip().split('\n')
    net = cv.dnn.readNet("yolov4-obj_last.weights", "yolov4-obj.cfg")
    net.setPreferableBackend(cv.dnn.DNN_BACKEND_CUDA)
    net.setPreferableTarget(cv.dnn.DNN_TARGET_CUDA_FP16)

    model = cv.dnn_DetectionModel(net)
    model.setInputParams(size=(416, 416), scale=1/255, swapRB=True)

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
        print(indices)
        for i in indices:
            print(i)
            i = i[0]
            box = boxes[i]
            print(box)
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

            return result

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
    img_info = postprocess(frame, outs) # 출력
    elapsed_ms = (time.monotonic() - start_time) * 1000
    print('postprocess in %.1fms' % (elapsed_ms))

    # imageHeight, imageWidth = frame.shape[:2]
    # resizeHeight = int(0.5 * imageHeight) 
    # resizeWidth = int(0.5 * imageWidth ) 
    # resizeImageNDArray = cv.resize(frame, (resizeHeight, resizeWidth), interpolation = cv.INTER_CUBIC)

    # imageHeight, imageWidth = frame.shape[:2]
    # resizeHeight = int(0.5 * imageHeight) 
    # resizeWidth = int(0.5 * imageWidth) 
    # resizeFrame = cv.resize(frame, (resizeHeight, resizeWidth), interpolation = cv.INTER_CUBIC)
    #cv.imshow('CODE Detection', frame) #imshow
    cv.waitKey()
    return decode_data

if __name__ == '__main__':
    main()
