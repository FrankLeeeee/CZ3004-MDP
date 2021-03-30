# Installation

## Install environment
```shell script
pip install -r requirements.txt

# Clone the Darknet
git clone https://github.com/AlexeyAB
```

## Install Darknet

### Option 1: Download from the [Google Drive](https://drive.google.com/drive/u/1/folders/1qw8CFmSymAr-SRBuqNfH9dZKl8DwKLpi)

1. Download the specific version (architecture and CUDA if you have GPU) to `./darknet`
2. Make a soft link `ln -sf "$PWD/darknet/<libdarknet_your_specific_darknet_version>.so" "${PWD}/darknet/libdarknet.so"`

### Option 2: Compile by yourself

Requirement: `make`

#### 1. Change the directory to `darknet`
```shell script
cd darknet
```

#### 2. Modify `Makefile`

*   Without GPU
```makefile
OPENCV=1
LIBSO=1
```

*   With GPU
```makefile
GPU=1
CUDNN=1
CUDNN_HALF=1
OPENCV=1
AVX=1
OPENMP=1
LIBSO=1
```

#### 3. Make the library
```shell script
make
```

### Download Weight Files

Download weights from [Google Drive](https://drive.google.com/file/d/1QNc93VfulhZf_J2sZpTLPlNRbYvW9p9h/view?usp=sharing) and unzip it.

## Usage

Run server:
```shell script
export PYTHONPATH="${PWD}/../"
export DARKNET_PATH="${PWD}/darknet"

python server/grpc_server.py
```

Run client:
```shell script
export PYTHONPATH="${PWD}/../"

python server/client.py
```

## Reference

[Darknet](https://github.com/AlexeyAB/darknet#how-to-compile-on-linux-using-cmake)
