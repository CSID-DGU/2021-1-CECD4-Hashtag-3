from flask import Blueprint, Flask, request, jsonify, send_file
from . import create_app
from .yolo import tracking
from .yolo import decoding
from .yolo import make_qr
from .models import *
from sqlalchemy.ext.declarative import DeclarativeMeta
from flask_mail import Mail, Message
import json

bp = Blueprint('routes', __name__, url_prefix='/')
app = create_app()
mail = Mail(app)


@bp.route('/')
def test():
    return 'test'


@bp.route('/getItem', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        img_file = request.files['requestFile']
        codes = tracking.main(img_file)

        dic = {}
        data = []

        for code in codes:
            if code[1] in dic:
                dic[code[0], code[1]] += 1
            else:
                dic[code[0], code[1]] = 1

        for product in dic:
            in_out, code = product
            code_info = Barcode.query.filter(Barcode.cnum == code).first()
            if code_info is None:
                break

            pro_info = Product.query.filter(
                Product.id == code_info.product_id).first()

            if pro_info is None:
                break

            command = 1 if in_out else 2
            check_list = CartList.query.filter(
                CartList.code == code_info.cnum, CartList.command == command).first()

            if check_list is None:
                cart = CartList(code=code_info.cnum, name=pro_info.name,
                                price=pro_info.price, count=dic[in_out, code],  command=command)
                db.session.add(cart)
                db.session.commit()

            product = {"name": pro_info.name, "price": pro_info.price,
                       "count": dic[in_out, code]}
            data.append(product)

        print(data)

    return jsonify(data)


@bp.route("/getCartFeed")
def get_cart_feed():
    lists = CartList.query.all()
    feeds = list(map(lambda x: CartList.toDict(x), lists))
    codes = tracking.get_product_info()

    dic = {}
    carts = []
    for code in codes:
        if code in dic:
            dic[code] += 1
        else:
            dic[code] = 1

    for code in dic:
        code_info = Barcode.query.filter(Barcode.cnum == code).first()
        if code_info is None:
            break

        pro_info = Product.query.filter(
            Product.id == code_info.product_id).first()

        if pro_info is None:
            break

        product = {"code": code, "price": pro_info.price,
                   "count": dic[code], "name": pro_info.name}
        carts.append(product)

    cart_feed = {
        "feeds": feeds,
        "carts": carts,
    }
    return cart_feed


@bp.route('/mail', methods=['POST'])
def send_mail():
    if request.method == 'POST':
        email = request.form.get("email")
        item = request.form.get("item")

        msg = Message('HashTag 상품 결제 내역', sender='결제완료',
                      recipients=[email])
        msg.body = '결제가 완료되었습니다' + item
        qrImg, qrImgArr = make_qr.main(item)
        msg.attach("qrcode.png", "image/png", qrImgArr)
        mail.send(msg)
        db.session.query(CartList).delete()
        db.session.commit()

    return send_file("img/qrcode.png", mimetype='image/jpg')


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
