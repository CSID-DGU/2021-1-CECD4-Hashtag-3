from flask import Blueprint, Flask, request, jsonify
from . import create_app
from .yolo import yolo_dbr
from .yolo import dbr_only
from .yolo import yolo_decode
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
        #codes = yolo_dbr.main(img_file)
        
        # YOLO 테스트 시
        codes = yolo_decode.main(img_file)

        dic = {}
        data = []

        for code in codes:
            if code in dic:
                dic[code] +=1
            else:
                dic[code] = 1           

        print(dic)
        for code in dic:
            code_info = Barcode.query.filter(Barcode.cnum == code).first()
            if code_info is not None:
                pro_info = Product.query.filter(Product.id == code_info.product_id).first()
                if pro_info is not None:
                    product = {}
                    product['name'] = pro_info.name
                    product['price'] = pro_info.price
                    product['count'] = dic[code]
                    data.append(product)
    
    return jsonify(data)

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
            (flag, encodedImage)= cv2.imencode(".jpg", outputFrame)
            # ensure the frame was successfully encoded
            if not flag:
                continue
        # yield the output frame in the byte format
        yield (b'--frame\r\n' b'Content-Type: image/jpeg\r\n\r\n' +
               bytearray(encodedImage) + b'\r\n')
               
@bp.route("/video-test")
def video_feed():
    # return the response generated along with the specific media
    # type (mime type)
    return Response(generate(),
                    mimetype="multipart/x-mixed-replace; boundary=frame")


@bp.route('/mail', methods=['POST'])
def send_mail():
    if request.method == 'POST':
        email = request.form.get("email")
        item = request.form.get("item")

        msg = Message('Hello', sender= 'code110100@gmail.com', recipients=[email])
        msg.body = '결제가 완료되었습니다'
        mail.send(msg)
        success = {'success': 'true'}

    return jsonify(success)
