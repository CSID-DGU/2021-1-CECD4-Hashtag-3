from flask import Blueprint, Flask, redirect, request, jsonify, send_file
import requests
from . import create_app
from .models import *
from sqlalchemy.ext.declarative import DeclarativeMeta
from flask_mail import Mail, Message
import os
import json

bp = Blueprint('oauth', __name__, url_prefix='/oauth')
app = create_app()
mail = Mail(app)


@bp.route('/')
def kakao_login_callback():
    code = request.args.get("code")
    client_id = os.getenv('CLIENT_ID')
    redirect_uri = request.host_url + "/oauth"
    token_request = requests.get(
        f"https://kauth.kakao.com/oauth/token?grant_type=authorization_code&client_id={client_id}&redirect_uri={redirect_uri}&code={code}"
    )
    token_json = token_request.json()
    header = {"Authorization": "Bearer " + str(token_json.get('access_token'))}
    user_info = requests.post(
        "https://kapi.kakao.com/v2/user/me", headers=header
    )

    user_json = user_info.json().get('kakao_account')
    user = {}
    user['name'] = user_json.get('profile').get('nickname')
    user['email'] = user_json.get('email')
    user['token'] = token_json.get('access_token')
    return json.dumps(user, ensure_ascii=False).encode('utf8')


@bp.route('/kakao')
def kakao_login():
    client_id = os.getenv('CLIENT_ID')
    redirect_uri = request.host_url + "/oauth"
    kakao_oauthurl = f"https://kauth.kakao.com/oauth/authorize?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code"
    return redirect(kakao_oauthurl)


@bp.route('/login', methods=['POST'])
def login():
    if request.method == 'POST':
        id = request.form.get("id")
        password = request.form.get("password")

        user = User.query.filter(
            User.id == id, User.password == password).first()

        if user is None:
            info = {"name": "", "email": "", "success": "false"}
        else:
            info = {"name": user.name, "email": user.email, "success": "true"}

    return jsonify(info)


@bp.route('/register', methods=['POST'])
def register():
    if request.method == 'POST':
        id = request.form.get("id")
        password = request.form.get("password")
        name = request.form.get("name")
        email = request.form.get("email")
        phone = request.form.get("phone")

        userid = User.query.filter(User.id == id).first()
        success = "false"

        if userid is None:
            user = User(id=id, password=password, name=name,
                        email=email, phone=phone)
            success = "true"
            db.session.add(user)
            db.session.commit()
        else:
            user = User(id="", password="", name="",
                        email="", phone="")

        user = user.toDict()
        user["success"] = success

    return jsonify(user)


@bp.route('/message', methods=['POST'])
def send_kakaotalk():
    token = request.form.get("token")
    item = request.form.get("item")
    header = {"Content-Type": "application/x-www-form-urlencoded",
              "Authorization": "Bearer " + str(token)}

    arr = item.split()
    items = []

    for i in range(0, int(len(arr)-2), 2):
        ele = {"item": arr[i], "item_op": arr[i+1]}
        items.append(ele)

    message = {
        "object_type": "feed",
        "content": {
            "title": "결제 내역 영수증",
            "image_url": "http://pngimg.com/uploads/qr_code/qr_code_PNG38.png",
            "image_width": 640,
            "image_height": 640,
            "link": {
                "web_url": "http://www.daum.net",
                "mobile_web_url": "http://m.daum.net",
                "android_execution_params": "contentId=100",
                "ios_execution_params": "contentId=100"
            }
        },
        "item_content": {
            "items": items,
            "sum": "Total",
            "sum_op": arr[len(arr)-1]
        }
    }

    body = {"template_object": json.dumps(message)}
    user_info = requests.post(
        "https://kapi.kakao.com/v2/api/talk/memo/default/send", headers=header, data=body
    )
    print(user_info.json())
    return jsonify(user_info.json())
