
import math
from dbr import *
import time
# 직선거리 기반 트랙커


class EuclideanDistTracker:
    def __init__(self):
        # cart = 상품 barcode 정보와 개수 저장하는 dictionary
        # cart = { barcode info : count }
        self.cart = {}
        # 객체의 중심 위치 저장 given true/false
        # {(class,id):(x,y,w,h,in_out,barcode,given,same_count,start_in_out,max_area)}
        self.all_product_info = {}
        # {(class,id):(x,y,w,h,in_out,barcode,given,same_count,start_in_out,max_area)}
        self.all_barcode_info = {}
        # all_matchings = product class_id와 barcode class_id 쌍
        self.all_matchings = {}

        # ??
        self.now_barcode_info = []
        # 아이디의 개수 카운팅
        # self.id_count = 0
        # 0 : barcode/ 1 : product
        self.reader = BarcodeReader()
        license_key = "t0070fQAAAGbyNqWdbXGQuG/k3sw/lDS5BzrZbGVFI61KIVV9WEj0wPV/pgMiFVBM/+4clrLaRbvjFV1NfhjW0g3NxklHJpMbhA=="
        self.reader.init_license(license_key)
        self.decode_data = []
        self.class_ids = {0: 0, 1: 0}
        self.before_time = time.time()

    def get_all_product(self):
        return self.cart

    # 바코드가 product안에 있는지 확인
    def is_inside(self, barcode, product):
        codex, codey, codew, codeh = barcode
        prox, proy, prow, proh = product

        if prox <= codex and proy <= codey and codex + codew <= prox + prow and codey + codeh <= proy + proh:
            return True
        else:
            return False
    # 바코드 디코딩 후 정보 리턴

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
            if text_results is not None:
                for text_result in text_results:
                    self.decode_data.append(text_result.barcode_text)
            return text_results  # 있으면 결과
        except BarcodeReaderError as bre:
            print(bre)  # 예외처리

        return None
    # ID 찾는 함수

    def id_update(self, one_class, bbox, cart_in_out, barcode):
        # 이전에 이미 인식된 객체인지 확인
        same_object = False
        x, y, w, h = bbox
        area = x*y
        w_h_rate = w/h
        ERROR = 0.2
        new_class_id = ()

        # 중심 x,y값 계산
        cx = (x+x+w)//2
        cy = (y+y+h)//2
        if one_class == 0:
            all_item = self.all_barcode_info
            area_standard = 10000
        else:
            all_item = self.all_product_info
            area_standard = 25000

        for class_id, info_tmp in all_item.items():
            x_tmp, y_tmp, w_tmp, h_tmp, in_out_tmp, barcode_tmp, given_tmp, false_count_tmp, start_in_out_tmp, max_area_tmp = info_tmp
            cx_tmp = (x_tmp+x_tmp+w_tmp)//2
            cy_tmp = (y_tmp+y_tmp+h_tmp)//2
            area_tmp = x_tmp*y_tmp
            w_h_rate_tmp = w/h

            # 같은 객체인 경우 and 아직 할당 안된 경우 거리 측정 / 다른 객체면 pass
            # add_tmp ; 상품은 카트에 추가된 경우 계속 True라 업데이트 안함.
            if one_class == class_id[0] and given_tmp == False:
                # dist : 현재 객체의 중심점과 이전 프레임에 인식된 (모든)객체의 중심점 직선 거리
                dist = math.hypot(cx-cx_tmp, cy-cy_tmp)
                dist_standard = 100

                if one_class == 0:  # barcode 인 경우
                    area_standard = 5000
                else:
                    area_standard = 25000

                # 거리가 60이하이고, 비율 및 넓이도 기준 미만이면  id 업데이트 및 정보 업데이트
                if dist < dist_standard and abs(w_h_rate - w_h_rate_tmp) < ERROR and abs(area-area_tmp) < area_standard:
                    max_area = max(w*h, max_area_tmp)
                    if one_class == 1:
                        if class_id in self.all_matchings:
                            barcode = self.all_barcode_info[self.all_matchings[class_id]][5]
                            self.use_barcode(self.all_matchings[class_id])
                        else:
                            barcode, bar_class_id = self.matching_barcode(bbox)
                            if barcode is not None:
                                self.all_matchings[bar_class_id] = class_id
                                self.all_matchings[class_id] = bar_class_id
                    if barcode is not None and barcode_tmp is not None:
                        if len(barcode) < len(barcode_tmp):
                            barcode = barcode_tmp
                    if barcode is not None:
                        all_item[class_id] = (
                            x, y, w, h, cart_in_out, barcode, True, false_count_tmp, start_in_out_tmp, max_area)
                    else:
                        all_item[class_id] = (
                            x, y, w, h, cart_in_out, barcode_tmp, True, false_count_tmp, start_in_out_tmp, max_area)

                    new_class_id = class_id
                    same_object = True
                    break
                else:
                    pass

        # 1. 새로운 객체가 감지된 경우
        if same_object == False:
            bar_class_id = ()
            if one_class == 1:
                barcode, bar_class_id = self.matching_barcode(bbox)
            # else:
            #     self.now_barcode_info.append(barcode)

            if barcode is not None or (one_class == 1 and cart_in_out == False):
                new_class_id = (one_class, self.class_ids[one_class])
                all_item[new_class_id] = (
                    x, y, w, h, cart_in_out, barcode, True, 0, cart_in_out, w*h)
                self.class_ids[one_class] += 1
                if one_class == 1 and barcode is not None:
                    self.all_matchings[bar_class_id] = new_class_id
                    self.all_matchings[new_class_id] = bar_class_id

        return new_class_id

    # product 안에 있는 barcode를 매칭해 barcode정보 리턴하는 함수
    def matching_barcode(self, pro_bbox):
        for class_id, bar_info in self.all_barcode_info.items():
            # 해당 프레임에서 디코딩(인식) 된 바코드인 경우
            # x, y, w, h, in_out, barcode, given, false_count, start_in_out, max_area = bar_info
            if bar_info[6] == True and bar_info[7] == 0:
                # self.all_barcode_info[class_id] = (x, y, w, h, in_out, barcode, given, 1, start_in_out, max_area)
                self.use_barcode(class_id)
                bar_bbox = bar_info[0:4]
                if self.is_inside(bar_bbox, pro_bbox):
                    return bar_info[5], class_id
            else:
                pass  # 해당 프레임에 나타나지 않은 바코드는 패스
        return None, None

    def use_barcode(self, class_id):
        x, y, w, h, in_out, barcode, given, false_count, start_in_out, max_area = self.all_barcode_info[
            class_id]
        self.all_barcode_info[class_id] = (
            x, y, w, h, in_out, barcode, given, 1, start_in_out, max_area)

    # 네 꼭짓점 중 하나라도 밖에 있으면 카트 밖

    def is_in_cart(self, bbox, line):
        x1, x2, x3, y1, y2, y3 = line
        x, y, w, h = bbox
        x = [x+(w//8)*3,x+(w//8)*5]
        cx = (x+x+w)//2
        cy = (y+y+h)//2
        
        for i in range(0,2):
            if (x[i] < x1 and y+(h//8)*3 < y1) or (x1 < x[i] < x2 and y+(h//8)*3 < y2) or ( x2 < x[i] < x3 and y+(h//8)*3 < y3) : 
                return False
        return True

    def update(self, classes, bboxes, frame, line):

        decoding_list = []
        x1, x2, x3, y1, y2, y3 =line
        a = y1
        b = y2
        c = y3
        barcode_bbx = []
        product_bbx = []
        dont_decoding = []
        update_cart = []
        self.now_barcode_info = []

        # given false로 초기화
        for class_id, bbox_tmp in self.all_barcode_info.items():
            x, y, w, h, in_out, barcode, _, false_count, start_in_out, max_area = bbox_tmp
            self.all_barcode_info[class_id] = (
                x, y, w, h, in_out, barcode, False, 0, start_in_out, max_area)
        # given false로 초기화
        for class_id, bbox_tmp in self.all_product_info.items():
            x, y, w, h, in_out, barcode, _, false_count, start_in_out, max_area = bbox_tmp
            self.all_product_info[class_id] = (
                x, y, w, h, in_out, barcode, False, false_count, start_in_out, max_area)
        # barcode와 product bbox 분리
        for one_class, bbox in zip(classes, bboxes):
            if one_class == 0:
                barcode_bbx.append(bbox)
            else:
                product_bbx.append(bbox)
        # barcode에 대해서 디코딩 후 ID부여
        if barcode_bbx:
            for bar_bbox in barcode_bbx:
                x, y, w, h = bar_bbox
                cx = (x+x+w)//2
                cy = (y+y+h)//2
                barcode_text = ""

                cart_in_out = self.is_in_cart(bar_bbox, line)

                result = self.decodeframe(frame, x, y, (x+w), (y+h))
                if result is not None:  # 결과가 있을때
                    barcode_text = result[0].barcode_text
                    print(barcode_text)
                    self.now_barcode_info.append(barcode_text)
                    self.id_update(0, bar_bbox, cart_in_out, barcode_text)
                else:
                    pass
        # barcode 매칭 후 product ID부여
        if product_bbx:
            for pro_bbox in product_bbx:
                x, y, w, h = pro_bbox
                cx = (x+x+w)//2
                cy = (y+y+h)//2

                cart_in_out = self.is_in_cart(pro_bbox, line)
                self.id_update(1, pro_bbox, cart_in_out, None)

                # barcode_text, bar_class_id = self.matching_barcode(pro_bbox)
                # pro_class_id = self.id_update(
                #     1, pro_bbox, cart_in_out, barcode_text)
                # if barcode_text is not None:
                #     self.all_matchings[bar_class_id] = pro_class_id
                #     self.all_matchings[pro_class_id] = bar_class_id
                # else:
                #     pass

        # out 후 인식 안 된 경우 카트에서 상품 -1
        # in 후 인식 안 된 경우 카트에서 상품 +1
        for class_id, pro_info in list(self.all_product_info.items()):
            # x,y,w,h,current_in_out,barcode,given,false_count,start_in_out
            x, y, w, h, cart_in_out, barcode, given, false_count, start_in_out, max_area = pro_info
            bclass_id = None
            if class_id in self.all_matchings:
                bclass_id = self.all_matchings[class_id]
            if given == False:
                false_count += 1
            else:
                false_count = 0
            self.all_product_info[class_id] = (
                x, y, w, h, cart_in_out, barcode, given, false_count, start_in_out, max_area)

            if barcode is None:
                if false_count > 5:
                    # print(f"인식이 안되므로 삭제합니다.... {class_id}\n")
                    del self.all_product_info[class_id]
                else:
                    if  time.time() - self.before_time >3 :
                        dont_decoding.append([class_id, pro_info[0:6]])
                        self.before_time = time.time()
                # print(f"바코드 없음............ {class_id}")
            # elif 1 < same_count < 5 :
            #     print(f"상품을 끝까지 트래킹 하지 못하는것 같습니다.... {class_id}")
            # or given == False: # or (start_time - time.time())> 3*(10**7) :
            elif max_area*0.3 > w*h or false_count > 5:
                wrong = False
                if barcode in self.cart:
                    if cart_in_out == True:
                        self.cart[barcode] += 1
                        print(f"장바구니에서 {class_id} // {barcode}제품을 1개 추가함\n")
                    else:
                        self.cart[barcode] -= 1
                        print(f"장바구니에서 {class_id} // {barcode}제품을 1개 제거함\n")
                        if self.cart[barcode] == 0:
                            del self.cart[barcode]
                else:
                    if cart_in_out == True:
                        self.cart[barcode] = 1
                        print(
                            f"장바구니에 새로운 {class_id} // {barcode}제품을 1개 추가함\n")
                    else:
                        # print( f"장바구니에 없는데 제거하고자 함....{class_id} // {barcode}\n")
                        wrong = True
                del self.all_product_info[class_id]
                if wrong is False:
                    update_cart.append([class_id, pro_info[0:6]])
                    # 장바구니에 추가 또는 삭제되는 경우 정보 삭제
            else:
                decoding_list.append([class_id, pro_info[0:6]])
        if decoding_list != []:
            print(f"decoding = {decoding_list}\n")
        if update_cart != []:
            print(f"update_cart = {update_cart}\n")
        return decoding_list, dont_decoding, update_cart, self.now_barcode_info
