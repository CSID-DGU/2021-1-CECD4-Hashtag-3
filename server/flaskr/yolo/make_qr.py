# Import Library
import qrcode
from PIL import Image
import io
# Generate QR Code


def main(purchase_info):
    img = qrcode.make(purchase_info)
    img.save("qrcode.png")
    imgByteArr = io.BytesIO()
    img.save(imgByteArr, format=img.format)
    imgByteArr = imgByteArr.getvalue()
    return img, imgByteArr


if __name__ == '__main__':
    main()
