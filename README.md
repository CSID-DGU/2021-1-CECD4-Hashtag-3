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
* 실시간으로 카트 내부의 영상을 송출하여 카트에 물품을 담고, 빼는 행위만으로 상품을 결제할 수 있습니다.<br/><br/>
 

## 사용 기술
* YOLO v4
* Python OpenCV
* Android Kotlin (Client)
* Flask (Server)
* MySQL (Database)<br/><br/>


## 실행 방법<br/>
### 데이터 셋
상품 및 바코드 객체 인식을 위해 딥러닝 YOLO를 사용합니다.    
https://github.com/AlexeyAB/darknet 을 이용해 이미지를 학습하였으며 학습된 이미지 예시는 아래와 같습니다. 

<p align="center">
    <img src="/image-train/input.PNG" width="300" height="300" >
    <img src="/image-train/output.PNG" width="300" height="300" >
</p><br/>

### 클라이언트<br/>
* https://github.com/JetBrains/kotlin

#### 스크린샷
<p align="center">
    <img src="/image-train/1.jpg" width="150" height="300" >
    <img src="/image-train/2.jpg" width="150" height="300" >
    <img src="/image-train/3.jpg" width="150" height="300" >
    <img src="/image-train/4.jpg" width="150" height="300" >
    <img src="/image-train/5.jpg" width="150" height="300" >
</p><br/>

       
### 서버<br/>
* 서버 구성을 위해 Flask를 사용하였습니다.   
* [Flask](https://flask.palletsprojects.com/en/2.0.x/)
* 또한 Flask에서 ORM으로 작업하기 위해 SQLAlchemy를 이용합니다.     
* [SQLAlchemy](https://flask-sqlalchemy.palletsprojects.com/en/2.x/)     

window에서 실행 시
```
1. python -m venv venv
2. .\venv\Scripts\activate
3. pip install -r requirements.txt
4. .env 제작
5. flask db init
6. flask db migrate
7. flask db upgrade
8. flask run
```

Ubuntu에서 실행 시
```
1. GPU사용을 위해 OpenCV 설정하기
2. source venv/bin/activate
3. flask run
```

## 결과
![out_test(1)](https://user-images.githubusercontent.com/62283017/137354306-d42014db-5ab3-4013-97fe-03e67428ebb3.gif)
![out_test(1) (1)](https://user-images.githubusercontent.com/62283017/137355024-64e4fcf2-bd88-4b59-a81d-1f82cd491bc7.gif)
