# 2021-1-CECD4-Hashtag-3
* 상품 트래킹과 바코드 다중인식을 적용한 쇼핑 어플리케이션

## 팀원
```
2018112011 최수정   
2018112021 최은지   
2019112007 권예진
```

## 프로젝트 설명
* 대형 마트 및 소매점에서 계산대 없이 스마트폰을 사용하여 결제할 수 있는 시스템.   
* 실시간으로 카트 내부의 영상을 송출하여 카트에 물품을 담고, 빼는 행위만으로 상품을 결제할 수 있다.   


## 사용 기술
* YOLO v4
* Python OpenCV
* Android Kotlin (Client)
* Flask (Server)
* MySQL (Database)

             
## 실행 방법
#### 데이터 셋
상품 / 바코드 객체 인식을 위해 딥러닝 YOLO를 사용합니다.    
https://github.com/AlexeyAB/darknet 을 이용해 이미지를 학습하였으며 학습된 이미지 예시는 아래와 같습니다.

#### 클라이언트

#### 서버
서버 구성을 위해 Flask를 사용하였습니다.   
또한 Flask에서 ORM으로 작업하기 위해 SQLAlchemy를 이용합니다.     
https://flask.palletsprojects.com/en/2.0.x/     
https://flask-sqlalchemy.palletsprojects.com/en/2.x/     


1. python -m venv venv
2. .\venv\Scripts\activate
3. pip install -r requirements.txt
4. .env 제작
5. flask db init
6. flask db migrate
7. flask db upgrade
8. flask run



