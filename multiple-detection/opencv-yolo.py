# https://opencv-tutorial.readthedocs.io/en/latest/yolo/yolo.html
# https://docs.opencv.org/master/d6/d0f/group__dnn.html
# https://docs.opencv.org/3.4/db/d30/classcv_1_1dnn_1_1Net.html
# https://github.com/opencv/opencv/blob/master/samples/dnn/object_detection.py
import cv2 as cv
import numpy as np
import time

# Load an image
frame = cv.imread("jpg/4.4.jpg")
# frame = cv.imread("test.jpg")

threshold = 0.6 # 임계값  
maxWidth = 1280; maxHeight = 720
imgHeight, imgWidth = frame.shape[:2]
hScale = 1; wScale = 1
thickness = 1 # 테두리 ?? 두께 빨간색

if imgHeight > maxHeight:
    hScale = imgHeight / maxHeight
    thickness = 6

if imgWidth > maxWidth:
    wScale = imgWidth / maxWidth
    thickness = 6

# Load class names and YOLOv3-tiny model
classes = open('qrcode.names').read().strip().split('\n')
# net : 인공 신경망 조작 가능 변수
net = cv.dnn.readNetFromDarknet('qrcode-yolov3-tiny.cfg', 'qrcode-yolov3-tiny.weights') #Darknet 에 weights, cfg 파일을 로드
net.setPreferableBackend(cv.dnn.DNN_BACKEND_OPENCV) # opencv에 내장되어있는 dnn 모듈 백엔드 설정???

# net.setPreferableTarget(cv.dnn.DNN_TARGET_CPU) # DNN_TARGET_OPENCL DNN_TARGET_CPU DNN_TARGET_CUDA

start_time = time.monotonic() # 타이머 시간 측정
# Convert frame to blob
# blob : Binary Large Object / ( image name, scale, size, options..) / 신경망 입력 형식
# 형식 통일 시킴. 
blob = cv.dnn.blobFromImage(frame, 1/255, (416, 416), swapRB=True, crop=False) #image를 blob 형태로 바꾼다 scale, size
elapsed_ms = (time.monotonic() - start_time) * 1000 # 이미지 변환(imate to blob) 시간을 측정한다.
print('blobFromImage in %.1fms' % (elapsed_ms)) # 시간 출력

def postprocess(frame, outs): 
    # frame 은 사진, outs은 darknet 통과한 결과
    # 빨간색 테두리를 출력된 결과에 맞춰서 그린다.
    frameHeight, frameWidth = frame.shape[:2] # 사진의 가로, 세로 저장

    classIds = []
    confidences = []
    boxes = []

    print(outs)
    # 각 영역의 class별 score를 추출 → score가 가장 높은 class를 찾고, score값 cofidence에 저장
    # confidence값이 임계값보다 크면 해당 class 객체가 존재한다고 판단하고 정보 저장하는 for문
    for out in outs:
        for detection in out:
            scores = detection[5:] # class별 score 값 추출
            classId = np.argmax(scores) # score 가장 높은 classId를 얻음
            confidence = scores[classId] # 해당 classID의 score를 얻어냄
            print(detection)
            if confidence > threshold: # score가 임계값보다 큰지 비교해서 크면
                # 이미지 크기에 맞게 좌표 값 추출하기  
                x, y, width, height = detection[:4] * np.array([frameWidth, frameHeight, frameWidth, frameHeight])
                # x , y : 중심 좌표 
                left = int(x - width / 2)
                top = int(y - height / 2)
                # 박스에 쓰일 정보 저장
                classIds.append(classId)
                confidences.append(float(confidence))
                boxes.append([left, top, int(width), int(height)])

    # 마지막 파라미터 : 겹치는 bounding box 처리하는 임계값 (높을수록 박스 많이 지움. )
    #NMS로 최종 filtering한다. 필요한 값은 (좌표, id의 score, 임계값, 임계값 - 0.1)
    indices = cv.dnn.NMSBoxes(boxes, confidences, threshold, threshold - 0.1)

    # bounding box 정보를 이용해 
    # 1. 해당 부분만 자른 이미지 추추
    # 2. 원본 이미지에 rectangle과 text 정보 입력
    for i in indices:
        i = i[0]
        box = boxes[i]
        left = box[0]
        top = box[1]
        width = box[2]
        height = box[3]
        # cropped_image : bounding box 위치만 자른 이미지
        cropped_image = frame[top:top + height, left:left + width]
        cv.imshow('cropped', cropped_image)
        cv.imwrite(f'cropped{i}.jpg', cropped_image)

        # Draw bounding box for objects
        cv.rectangle(frame, (left, top), (left + width, top + height), (0, 0, 255), thickness)

        # Draw class name and confidence
        label = '%s:%.2f' % (classes[classIds[i]], confidences[i])
        cv.putText(frame, label, (left, top), cv.FONT_HERSHEY_SIMPLEX, 0.5, (255,255,255))

# Determine the output layer
ln = net.getLayerNames() # 레이어 이름
outputln = [ln[i[0] - 1] for i in net.getUnconnectedOutLayers()]  
# getUnconnectedOutLayers() : output layer를 반환하기 위해 사용
print(ln)
print(outputln)

net.setInput(blob) # 신경망에 넣을 사진 
start_time = time.monotonic() # 시간 측정 타이머
# Compute
outs = net.forward(outputln) # 순방향 네트워크 실행 //  output값(feature값) 얻어내는 부분
elapsed_ms = (time.monotonic() - start_time) * 1000
print('forward in %.1fms' % (elapsed_ms)) # 실행한 시간 출력

start_time = time.monotonic() # 시간 측정
postprocess(frame, outs) # postprocess 함수에 frame이랑 outs를 넣음
elapsed_ms = (time.monotonic() - start_time) * 1000
print('postprocess in %.1fms' % (elapsed_ms)) # 시간 출력

# max높이나 넓이보다 크지 않도록 resize
if hScale > wScale:
    frame = cv.resize(frame, (int(imgWidth / hScale), maxHeight))
elif hScale < wScale:
    frame = cv.resize(frame, (maxWidth, int(imgHeight / wScale)))

cv.imshow('QR Detection', frame)
cv.waitKey()


