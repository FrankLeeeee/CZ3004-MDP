# Create Dataset
We create the dataset by manually taken photos using Raspberry Pi. As Raspberry Pi has a storage of Flash, which is very slow in writing, we send the shot images to a server, so that the server save them in cloud.

## Usage

### 1. Start the server
```shell script
python -m uvicorn http_server:app --host <your host IP>
```

### 2. Start the Raspberry Pi

For example, shot images for label blue_six:
```shell script
python take_dataset_pic.py take-photo blue_six
```
This will guide you to take 100 images labeled as blue six. The first image will take a longer time (around 1 second) for camera initialization.
You can stop the photo taking using ^C.
For more usage and argument hints, please refer to:
```shell script
# all command help
python take_dataset_pic.py --help
# take photo command help
python take_dataset_pic.py take-photo --help
```

