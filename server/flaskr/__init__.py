from flask import Flask
from flask_migrate import Migrate
from flask_sqlalchemy import SQLAlchemy
from flask_mail import Mail, Message

from . import config

db = SQLAlchemy()
migrate = Migrate()

def create_app():
    app = Flask(__name__)

    mail = Mail(app)

    app.config.from_object(config)
    
    db.init_app(app)
    migrate.init_app(app, db)

    from . import models
    from . import routes
    app.register_blueprint(routes.bp)

    return app
