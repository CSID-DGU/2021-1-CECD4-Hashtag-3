from . import db
from sqlalchemy import inspect


class Product(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(128), nullable=False)
    image = db.Column(db.String(128))
    price = db.Column(db.Integer, nullable=False)
    ptype = db.Column(db.String(128), nullable=False)
    create_date = db.Column(db.DateTime())
    expirt_date = db.Column(db.String(128))
    information = db.Column(db.Text())

    def toDict(self):
        return {c.key: getattr(self, c.key) for c in inspect(self).mapper.column_attrs}


class Barcode(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    cnum = db.Column(db.String(128), nullable=False)
    product_id = db.Column(db.Integer)


class CartList(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    code = db.Column(db.String(128))
    name = db.Column(db.String(128))
    price = db.Column(db.Integer)
    count = db.Column(db.Integer)
    command = db.Column(db.Integer, default=False, nullable=False)

    def toDict(self):
        return {c.key: getattr(self, c.key) for c in inspect(self).mapper.column_attrs}
