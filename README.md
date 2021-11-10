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
* Android Studio에서 Kotlin을 사용하였습니다.   
* 서버 통신을 위해 retrofit과 okhttp를 사용하였습니다.
* 앱 하단에 BottomNavigationView를 적용하였고, 전반적으로 fragment화면을 사용하였습니다.
* 장바구니 화면에서 Listview를 사용하였고, 실시간 영상 촬영 화면에서 camera preview를 사용하였습니다. 


### 실행 영상<br/>
<p align="center">
 
<img width="20%" src="https://user-images.githubusercontent.com/62542277/141079224-5f6d4d97-6988-4db4-a671-b861d73de65b.gif"/>
<img width="20%" src="https://user-images.githubusercontent.com/62542277/141080502-e15ecf4a-0a78-4479-868d-c7e1acd5ff21.gif"/>
<img width="20%" src="https://user-images.githubusercontent.com/62542277/141080833-8834c33d-6c61-4850-8cf0-621c1753c268.gif"/> 
</p><br/>
  <p align="center">
<img width="20%" src="https://user-images.githubusercontent.com/62542277/141080930-7ae3a11c-3076-4e16-9204-4f35b5416380.gif"/>
 <img width="20%" src="https://user-images.githubusercontent.com/62542277/141082548-f588613f-df4d-41b4-871a-fa340e652fc9.gif"/>
<img width="20%" src="https://user-images.githubusercontent.com/62542277/141082563-0765da26-c3f6-4481-9299-6eef374d8c94.gif"/>

<img width="20%" src="https://user-images.githubusercontent.com/62542277/141079612-a8b4e9a6-6d9d-46ec-a373-c5bb85c84b86.gif"/>
 </p><br/>
 
 #### 스크린샷
<p align="center">
    <img src="https://user-images.githubusercontent.com/62542277/141062219-97e92b17-9a41-47d3-949b-46ab685557ee.jpg" width="150" height="300" >
    <img src="https://user-images.githubusercontent.com/62542277/141062258-672cb949-8be6-4718-ad1f-0a859940384d.jpg" width="150" height="300" >
    <img src="https://user-images.githubusercontent.com/62542277/141062247-8257fb52-add5-41a1-a9d2-f096b520dbaa.jpeg" width="150" height="300" >
   <img src="https://user-images.githubusercontent.com/62542277/141062266-d6416f25-3fb5-4598-9c91-a8f21f7080b8.jpg" width="150" height="300" >
 <img src="https://user-images.githubusercontent.com/62542277/141062253-4ece6a9b-529b-4cdc-98f9-c9eefb963341.jpg" width="150" height="300" >
   <img src="https://user-images.githubusercontent.com/62542277/141062241-b7976e69-9d7a-4192-a1c9-00b44046afa9.jpg" width="150" height="300" >
   <img src="https://user-images.githubusercontent.com/62542277/141062292-1670cb91-9bdb-4c3c-8282-4b6bf39e43f7.jpg" width="150" height="300" >
   <img src="https://user-images.githubusercontent.com/62542277/141062299-6228439a-46c9-41ec-a604-00c948b9d74a.jpg" width="150" height="300" >
   <img src="https://user-images.githubusercontent.com/62542277/141062273-977e2122-9175-41b1-bc74-21398a049826.jpg" width="150" height="300" >
   <img src="https://user-images.githubusercontent.com/62542277/141062279-6ae37bd5-9a25-4e5e-afb9-fbe40a46c26e.jpg" width="150" height="300" >
    <img src="https://user-images.githubusercontent.com/62542277/141062284-1d0ed93f-5a69-4e16-84d9-208b0e9f7204.jpg" width="150" height="300" >
    <img src="https://user-images.githubusercontent.com/62542277/141062287-c4e78760-eeee-479f-a560-7adff7740d8c.jpg" width="150" height="300" >
 
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

## 트래킹 화면
<p align="center">
    <img src="https://user-images.githubusercontent.com/62283017/137354306-d42014db-5ab3-4013-97fe-03e67428ebb3.gif" width="400" height="400" >
    <img src="https://user-images.githubusercontent.com/62283017/137355024-64e4fcf2-bd88-4b59-a81d-1f82cd491bc7.gif" width="400" height="400" >
</p><br/>
