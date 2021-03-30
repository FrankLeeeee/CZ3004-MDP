#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 2/16/2021
"""
from pathlib import Path

import aiofiles
from bson import ObjectId
from fastapi import FastAPI, File

from core.common import Label, ImageEncodeFormat

app = FastAPI()

# save_base_dir = "${PWD}/data"
save_base_dir = Path(__file__).parent.absolute() / 'data'


# make sub dirs for all labels
for label_ in Label:
    (save_base_dir / label_.value).mkdir(parents=True, exist_ok=True)


@app.post('/upload/')
async def upload_file(label: Label, file: bytes = File(...), format: ImageEncodeFormat = 'jpeg'):
    image_id = ObjectId()
    file_path = (save_base_dir / label.value / str(image_id)).with_suffix(f'.{format.value}')
    
    # async save file
    async with aiofiles.open(file_path, 'wb') as f:
        await f.write(file)

    return {'id': str(image_id), 'file_path': file_path}

