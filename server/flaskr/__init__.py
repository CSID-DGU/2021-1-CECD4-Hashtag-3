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

    def get_resource_as_string(name, charset='utf-8'):
        with app.open_resource(name) as f:
            return f.read().decode(charset)

    app.config.from_object(config)
    app.jinja_env.globals['get_resource_as_string'] = get_resource_as_string

    db.init_app(app)
    migrate.init_app(app, db)

    from . import models
    from . import routes
    from . import oauth
    app.register_blueprint(routes.bp)
    app.register_blueprint(oauth.bp)

    return app
