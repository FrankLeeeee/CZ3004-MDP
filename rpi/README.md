# Raspberry Pi

## Structure

## Install

### 1. Create a virtual environment

Create a virtual environment (.venv) and install required packages
```shell script
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

### 2. Configuration

First, find serial port name for Arduino:
```
python -m serial.tools.list_ports
```
And configure the Arduino port at `config.yml:serial_url`.

### 3. Start the server

```shell script
export PYTHONPATH="${PWD}"
python server/grpc_server.py
```
