from flask import Blueprint, Flask, redirect, request, jsonify, send_file, render_template
import requests
from . import create_app
from .yolo import tracking
from .yolo import decoding
from .yolo import make_qr
from .models import *
from sqlalchemy.ext.declarative import DeclarativeMeta
from flask_mail import Mail, Message
import json
import time

bp = Blueprint('routes', __name__, url_prefix='/')
app = create_app()
mail = Mail(app)
temp_count = 0


@bp.route('/')
def test():
    return 'test'

# @bp.route('/image', methods=['POST'])
# def check_img():
#     if request.method == 'POST':
#         img_file = request.files['img']
#         codes = decoding.main(img_file)

#         dic = {}
#         data = []

#         for code in codes:
#             key = (code[0], code[1], code[2])
#             # if code in dic:
#             #     dic[code] += 1
#             # else:
#             #     dic[code] = 1
#             if key in dic:
#                 dic[key] += 1
#             else:
#                 dic[key] = 1

#         # for code in dic:
#         #     code_info = Barcode.query.filter(Barcode.cnum == code).first()
#         #     print(code_info)
#         #     if code_info is not None:
#         #         pro_info = Product.query.filter(
#         #             Product.id == code_info.product_id).first()
#         for code, count in dic.items():
#             _, barcode, _ = code
#             code_info = Barcode.query.filter(Barcode.cnum == barcode).first()
#             print(code_info)
#             if code_info is not None:
#                 pro_info = Product.query.filter(
#                     Product.id == code_info.product_id).first()

#                 if pro_info is not None:
#                     product = {"name": pro_info.name, "price": pro_info.price,
#                                "count": count, "success": "true"}
#                     data.append(product)

#         if data == []:
#             dummy = [{"name": "", "price": "",
#                       "count": "", "success": "false"}]
#             return jsonify(dummy)

#     return jsonify(data)


@bp.route('/image', methods=['POST'])
def check_img():
    if request.method == 'POST':
        img_file = request.files['img']
        codes = decoding.main(img_file)

        dic = {}
        data = []

        for key in codes:
            if key in dic:
                dic[key] += 1
            else:
                dic[key] = 1

        # for code in dic:
        #     code_info = Barcode.query.filter(Barcode.cnum == code).first()
        #     print(code_info)
        #     if code_info is not None:
        #         pro_info = Product.query.filter(
        #             Product.id == code_info.product_id).first()
        for barcode, count in dic.items():
            code_info = Barcode.query.filter(Barcode.cnum == barcode).first()
            print(code_info)
            if code_info is not None:
                pro_info = Product.query.filter(
                    Product.id == code_info.product_id).first()

                if pro_info is not None:
                    product = {"name": pro_info.name, "price": pro_info.price,
                               "count": count, "success": "true"}
                    data.append(product)

        if data == []:
            dummy = [{"name": "", "price": "",
                      "count": "", "success": "false"}]
            return jsonify(dummy)

    return jsonify(data)


@bp.route('/tracking', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        img_file = request.files['img']
        codes = tracking.main(img_file)
        # codes = [True/False, decode_info, True/False]
        # [in or out , barcode or none, update or not ]
        # [True/False, decode_info, True] : update
        # [True,decode_info,False] : now decoding
        # [False, None, False] : dont decoding
        dic = {}
        data = []

        for code in codes:
            if (code[0], code[1], code[2]) in dic:
                # key = (code[0], code[1], code[2]), value = count
                dic[(code[0], code[1], code[2])] += 1
            else:
                dic[(code[0], code[1], code[2])] = 1

        # for product in dic:
        for product, count in dic.items():
            in_out, code, check = product
            print(in_out, code, check)

            if code is not None:
                code_info = Barcode.query.filter(Barcode.cnum == code).first()
                # 디코딩 정보에 해당하는 상품 정보가 없는 경우 무시?
                if code_info is None:
                    continue

                pro_info = Product.query.filter(
                    Product.id == code_info.product_id).first()

                command = 1 if in_out else 2
                check_list = CartList.query.filter(
                    CartList.code == code_info.cnum, CartList.command == command).first()

            # if check_list is None:
            #     cart = CartList(code=code_info.cnum, name=pro_info.name,
            #                     price=pro_info.price, count=dic[in_out, code, check],  command=command)
            #     db.session.add(cart)
            #     db.session.commit()

                result = pro_info.name

                if(check):
                    if in_out:
                        result += "이 추가되었습니다"
                    else:
                        result += "이 삭제되었습니다"
                else:
                    result = result + " - " + str(pro_info.price) + "원"

                product = {"name": pro_info.name, "price": pro_info.price,
                           "count": count, "result": result, "success": "true"}
                data.append(product)
            # 디코딩 알림 부분
            else:
                result = "바코드 디코딩이 필요한 상품이 있습니다."
                product = {"name": "1", "price": "1",
                           "count": count, "result": result, "success": "true"}
                data.append(product)
            print(result)

        # print(data)
        if not data:
            dummy = [{"name": "", "price": "",
                      "count": "", "result": "", "success": "false"}]
            return jsonify(dummy)

    return jsonify(data)


@bp.route("/getCartFeed")
def get_cart_feed():
    lists = CartList.query.all()
    carts = list(map(lambda x: CartList.toDict(x), lists))
    dic = tracking.get_product_info()
    tracking.change_to_avi()

    feeds = []

    for code, count in dic.items():
        code_info = Barcode.query.filter(Barcode.cnum == str(code)).first()
        if code_info is None:
            continue

        pro_info = Product.query.filter(
            Product.id == code_info.product_id).first()

        product = {"code": code, "price": pro_info.price,
                   "count": count, "name": pro_info.name}
        feeds.append(product)
        print(product)

    cart_feed = {
        "feeds": carts,
        "carts": feeds
    }
    return cart_feed


@bp.route('/updateFeed', methods=['POST'])
def update_cart_feed():
    feeds = request.form.get("feeds")
    carts = request.form.get("carts")
    print(feeds, carts)

    new_feeds = []
    if feeds != '[]':
        for feed in feeds[1:-1].replace('},', '} *').split('*'):
            feed = json.loads(feed)
            new_feeds.append(feed)

    new_carts = []
    if carts != '[]':
        for cart in carts[1:-1].replace('},', '} *').split('*'):
            cart = json.loads(cart)
            new_carts.append(cart)

    cart_feed = {
        "feeds": new_feeds,
        "carts": new_carts
    }
    return cart_feed


@bp.route('/mail', methods=['POST'])
def send_mail():
    if request.method == 'POST':
        email = request.form.get("email")
        item = request.form.get("item")

        msg = Message('HashTag 상품 결제 내역', sender='결제완료',
                      recipients=[email])

        arr = item.split('\n')
        items = []

        for i in arr:
            spl = i.split()
            item = ""
            if(len(spl) > 2):
                for j in range(0, int(len(spl)-2)):
                    item += " "+spl[j]
                ele = {"item": item, "count":  spl[len(
                    spl)-2], "item_op": spl[len(spl)-1]}
                items.append(ele)

        #msg.body = '결제가 완료되었습니다\n' + item
        msg.html = render_template(
            'index.html', items=items, sending_mail=True)
        qrImg, qrImgArr = make_qr.main(item)
        mail.send(msg)
        # db.session.query(CartList).delete()
        # db.session.commit()

    return send_file("img/qrcode.png", mimetype='image/jpg')


@bp.route('/imgtest', methods=['GET'])
def img_test():
    return request.host_url + "img/qrcode.png"


@bp.route("/video-test")
def video_feed():
    # return the response generated along with the specific media
    # type (mime type)
    return Response(generate(),
                    mimetype="multipart/x-mixed-replace; boundary=frame")


def generate():
    # grab global references to the output frame and lock variables
    global outputFrame, lock
    # loop over frames from the output stream
    while True:
        # wait until the lock is acquired
        with lock:
            # check if the output frame is available, otherwise skip
            # the iteration of the loop
            if outputFrame is None:
                continue
            # encode the frame in JPEG format
            (flag, encodedImage) = cv2.imencode(".jpg", outputFrame)
            # ensure the frame was successfully encoded
            if not flag:
                continue
        # yield the output frame in the byte format
        yield (b'--frame\r\n' b'Content-Type: image/jpeg\r\n\r\n' +
               bytearray(encodedImage) + b'\r\n')
