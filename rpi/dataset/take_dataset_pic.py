#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021
"""
import io
import time

import requests
import typer
import picamera

from core.common import Label, ImageEncodeFormat

SERVER_URL = 'http://155.69.146.35:8000'
URL = 'http://155.69.146.35:8000/upload/'


def make_dataset(label: Label, format: ImageEncodeFormat = ImageEncodeFormat.JPEG):
    with picamera.PiCamera() as camera:
        camera.start_preview()
        # warm-up
        time.sleep(2)
        closed = False
        while not closed:
            with io.BytesIO() as stream:
                camera.capture(stream, format=format.value)
                files = {'file': stream.getvalue()}
                payload = {'label': label.value, 'format': format.value}
                response = requests.post(URL, files=files, params=payload)
                response.close()
            try:
                yield response.json()
            except GeneratorExit:
                closed = True


app = typer.Typer()


@app.command()
def ping():
    """Ping the server"""
    response = requests.get(SERVER_URL)
    print(response)


@app.command()
def take_photo(label_class: Label, max_number: int = 100):
    dataset_gen = make_dataset(label_class)
    typer.echo(f'Taking photos with label={label_class}, max number of shot: {max_number}')
    typer.echo('Enter anykey if you are ready for the next shot')

    for i in range(max_number):
        try:
            input()
            print(f'Shot {i}:', next(dataset_gen))
        except KeyboardInterrupt:
            dataset_gen.close()
            break


if __name__ == '__main__':
    app()
