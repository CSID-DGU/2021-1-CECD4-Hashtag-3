from pyzbar.pyzbar import decode
from PIL import Image
import numpy as np
import cv2 as cv
import io

def main():
    # in_memory_file = io.BytesIO()
    # img.save(in_memory_file)
    # data = np.fromstring(in_memory_file.getvalue(),dtype=np.uint8)
            
    # frame = cv.imdecode(data,cv.IMREAD_UNCHANGED)# 사진 읽어옴

    output_codes = decode(Image.open('12.jpg'))
    print(output_codes)


    print(type(output_codes))
    print(len(output_codes)) # how many barcodes are found.

if __name__ == '__main__':
    main()
