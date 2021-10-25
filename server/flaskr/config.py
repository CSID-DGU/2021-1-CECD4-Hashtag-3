import os
from dotenv import load_dotenv

load_dotenv()

db = {
    'user' : os.getenv('DB_USER'),
    'password' : os.getenv('DB_PASSWORD'),
    'host' : os.getenv('DB_HOST'),
    'port' : os.getenv('DB_PORT'),
    'database' : os.getenv('DB_DATABASE')
}

SQLALCHEMY_DATABASE_URI = f"mysql+mysqlconnector://{db['user']}:{db['password']}@{db['host']}:{db['port']}/{db['database']}?charset=utf8" 

SQLALCHEMY_TRACK_MODIFICATIONS = False

CACHE_TYPE = "simple"


MAIL_SERVER = 'smtp.gmail.com'
MAIL_PORT = 465
MAIL_USERNAME = os.getenv('MAIL_USERNAME')
MAIL_PASSWORD = os.getenv('MAIL_PASSWORD')
MAIL_USE_TLS = False
MAIL_USE_SSL = True
