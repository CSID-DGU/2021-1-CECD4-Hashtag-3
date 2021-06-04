# QR Detection with OpenCV Python and YOLO Model
The samples demonstrate how to detect QR with [YOLO](https://pjreddie.com/darknet/yolo/) and how to decode QR with [Dynamsoft Barcode Reader](https://www.dynamsoft.com/Products/Dynamic-Barcode-Reader.aspx).

## Installation

- OpenCV 
    
    ```
    pip install opencv-python
    ```

- Dynamsoft Barcode Reader

    ```
    pip install dbr
    ```

## Usage

#### QR Detection

Image file:

```
python3 opencv-yolo.py
```

Camera:

```
python3 opencv-yolo-camera.py
```

![OpenCV YOLO for QR detection](https://www.dynamsoft.com/codepool/wp-content/uploads/2020/11/opencv-yolo-qr-detection.gif)

#### QR Decoding

```py
from dbr import *

reader = BarcodeReader()
# Apply for a trial license: https://www.dynamsoft.com/customer/license/trialLicense
license_key = "LICENSE KEY"
reader.init_license(license_key)
settings = reader.reset_runtime_settings() 
settings = reader.get_runtime_settings()
settings.region_bottom  = bottom
settings.region_left    = left
settings.region_right   = right
settings.region_top     = top
reader.update_runtime_settings(settings)

try:
    text_results = reader.decode_buffer(frame)

    if text_results != None:
        for text_result in text_results:
            print("Barcode Format :")
            print(text_result.barcode_format_string)
            print("Barcode Text :")
            print(text_result.barcode_text)
            print("Localization Points : ")
            print(text_result.localization_result.localization_points)
            print("-------------")
except BarcodeReaderError as bre:
    print(bre)
```

Image file:

```
python3 yolo-dbr.py
```

Camera:

```
python3 yolo-dbr-camera.py
```



## References
- https://opencv-tutorial.readthedocs.io/en/latest/yolo/yolo.html
- https://docs.opencv.org/master/d6/d0f/group__dnn.html
- https://docs.opencv.org/3.4/db/d30/classcv_1_1dnn_1_1Net.html
- https://github.com/opencv/opencv/blob/master/samples/dnn/object_detection.py

