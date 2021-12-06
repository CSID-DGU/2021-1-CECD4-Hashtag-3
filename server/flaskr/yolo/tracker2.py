import math
from dbr import *
# 직선거리 기반 트랙커


class EuclideanDistTracker:
    def __init__(self):
        # 객체의 중심 위치 저장
        # {(class,id):(cx, cy)}
        self.all_center_points = {}
        self.all_barcode_info = {}
        self.all_product_info = {}
        # 아이디의 개수 카운팅
        # self.id_count = 0
        # 0 : barcode/ 1 : product
        self.reader = BarcodeReader()
        license_key = "t0070fQAAAGbyNqWdbXGQuG/k3sw/lDS5BzrZbGVFI61KIVV9WEj0wPV/pgMiFVBM/+4clrLaRbvjFV1NfhjW0g3NxklHJpMbhA=="
        self.reader.init_license(license_key)
        self.decode_data = []
        self.class_ids = {0: 0, 1: 0}

    def get_all_product(self):
        return self.all_product_info

    def is_inside(self, barcode, product):
        codex, codey, codew, codeh = barcode
        prox, proy, prow, proh = product

        if prox <= codex and proy <= codey and codex + codew <= prox + prow and codey + codeh <= proy + proh:
            return True
        else:
            return False

    def decodeframe(self, frame, left, top, right, bottom):
        settings = self.reader.reset_runtime_settings()
        settings = self.reader.get_runtime_settings()
        settings.region_bottom = bottom
        settings.region_left = left
        settings.region_right = right
        settings.region_top = top
        # settings.barcode_format_ids = EnumBarcodeFormat.BF_QR_CODE
        # settings.expected_barcodes_count = 1
        # 지정된 JSON 파일의 설정으로 런타임 설정 update
        self.reader.update_runtime_settings(settings)

        try:
            # 정의 된 형식의 이미지 픽셀을 포함하는 메모리 버퍼에서 바코드를 디코딩
            text_results = self.reader.decode_buffer(frame)
            if text_results != None:
                for text_result in text_results:
                    self.decode_data.append(text_result.barcode_text)
            return text_results  # 있으면 결과
        except BarcodeReaderError as bre:
            print(bre)  # 예외처리

        return None

    def update_info(self, class_id, frame, bbox, objects_bbs_cids, in_out):
        x, y, w, h = bbox
        if class_id[0] == 0:  # 바코드 일때
            result = self.decodeframe(frame, x, y, (x+w), (y+h))
            barcode_text = " "
            if result is not None:  # 결과가 있을때
                barcode_text = result[0].barcode_text
                self.all_barcode_info[(class_id)] = (x, y, w, h, barcode_text)

        else:  # 상품일때
            flag = False
            barcode_text = " "
            for barcode in self.all_barcode_info:
                barx, bary, barw, barh, info = self.all_barcode_info[barcode]
                if self.is_inside((barx, bary, barw, barh), (x, y, w, h)):
                    self.all_product_info[(class_id)] = (
                        (x, y, w, h), info)
                    flag = True
                    barcode_text = info
            if(not flag):
                self.all_product_info[(class_id)] = (
                    x, y, w, h)

        return x, y, w, h, class_id, in_out, barcode_text

    def update(self, classes, bboxes, frame, line):
        # object의 bbox와 class_id정보
        objects_bbs_cids = []
        x1, x2, x3, y1, y2, y3 = line
        a = (y2-y1)//x1
        b = y1
        c = y2
        d = (y3-y2)//(x3-x2)
        f = y2 - ((y3-y2)*x2)//(x3-x2)

        # 밖에 있는 객체 리스트에서 삭제
        if self.all_center_points != None:
            for class_id, bbox_in_out in list(self.all_center_points.items()):
                if bbox_in_out[4] == False:
                    print(f"before {self.all_center_points}")
                    del self.all_center_points[class_id]
                    print(f"after{self.all_center_points}")

        # bbox = [x,y,w,h] 정보 포함
        # one_class = 0 or 1
        for one_class, bbox in zip(classes, bboxes):

            x, y, w, h = bbox
            x2, y2 = x+w, y+h
            # 중심 x,y값 계산
            cx = (x+x+w)//2
            cy = (y+y+h)//2
            one_class = int(one_class)

            in_out = True  # in : True, out : False

            if cx < x1 and cy < a*cx+b:
                in_out = False
            elif x1 < cx < x2 and cy < c:
                in_out = False
            elif x2 < cx < x3 and cy < d*cx+f:
                in_out = False
            else:
                pass

            # 이전에 이미 인식된 객체인지 확인
            same_object = False

            for class_id, bbox_tmp in self.all_center_points.items():
                x_tmp, y_tmp, w_tmp, h_tmp, in_out_tmp, barcode_tmp = bbox_tmp
                x2_tmp, y2_tmp = x_tmp+w_tmp, y_tmp+h_tmp
                cx_tmp = (x_tmp+x_tmp+w_tmp)//2
                cy_tmp = (y_tmp+y_tmp+h_tmp)//2
                # 같은 객체인 경우 거리 측정 / 다른 객체면 pass
                if one_class == class_id[0]:
                    # dist : 현재 객체의 중심점과 이전 프레임에 인식된 (모든)객체의 중심점 직선 거리
                    dist = math.hypot(cx-cx_tmp, cy-cy_tmp)
                    area = abs(w*h-w_tmp*h_tmp)  # 넓이 비교 값
                    dot1 = math.hypot(x-x_tmp, y-y_tmp)
                    dot2 = math.hypot(x2-x2_tmp, y2-y2_tmp)
                    dot = abs(dot1-dot2)

                    # 거리가 25이하이면 id 객체 중심 위치 업데이트
                    # print(
                    #     f"dist = {dist}, area = {area}, dot1 = {dot1}, dot2 = {dot2}")
                    if dist < 100 and area < 5000 and dot < 500:
                        new_class_id = class_id
                        x, y, w, h, class_id, in_out, barcode = self.update_info(
                            class_id, frame, bbox, objects_bbs_cids, in_out)
                        self.all_center_points[class_id] = (
                            x, y, w, h, in_out, barcode)
                        objects_bbs_cids.append(
                            [x, y, w, h, class_id, in_out, barcode])
                        # 기존에 있던 객체임을 표시
                        same_object = True
                        break

            # 새로운 객체가 감지된 경우
            if same_object is False:
                new_class_id = (one_class, self.class_ids[one_class])
                x, y, w, h, class_id, in_out, barcode = self.update_info(
                    new_class_id, frame, bbox, objects_bbs_cids, in_out)
                self.all_center_points[class_id] = (
                    x, y, w, h, in_out, barcode)
                objects_bbs_cids.append(
                    [x, y, w, h, class_id, in_out, barcode])
                self.class_ids[one_class] += 1

        return objects_bbs_cids
