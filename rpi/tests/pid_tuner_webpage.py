#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 3/23/2021
"""
import matplotlib as mpl
import matplotlib.pyplot as plt

import requests
import streamlit as st

BACKEND_URL = 'http://localhost:8000'
COLOR_LIST = ['#fe4a49', '#2ab7ca', '#fed766', 'gray', 'black', 'orange', '#00CD66', '#4D5139']


def set_style():
    plt.style.use('classic')

    nice_fonts = {
        # Use LaTeX to write all text
        # "text.usetex": True,
        "font.family": "serif",
        # Use 10pt font in plots, to match 10pt font in document
        "axes.labelsize": 16,
        "font.size": 16,
        # Make the legend/label fonts a little smaller
        "legend.fontsize": 12,
        "xtick.labelsize": 16,
        "ytick.labelsize": 16,
    }

    mpl.rcParams.update(nice_fonts)


def set_motor_speed():
    payload = {'motor': motor_type, 'speed': speed, 'running_time': running_time}
    response = requests.post(f'{BACKEND_URL}/speed', params=payload)
    return response


def get_feedback():
    response = requests.get(f'{BACKEND_URL}/feedback')
    return response.json()


def set_pid_parameter():
    payload = {
        'left': {'Kp': Kp_l, 'Ki': Ki_l, 'Kd': Kd_l},
        'right': {'Kp': Kp_r, 'Ki': Ki_r, 'Kd': Kd_r}
    }
    return requests.post(f'{BACKEND_URL}/param', json=payload)


def plot_left_motor_graph(result):
    feedback_times = result['feedback_time']
    base_time = feedback_times[0]
    feedback_times = [t - base_time for t in feedback_times]

    # left motor
    fig, ax = plt.subplots()
    ax.plot(feedback_times, result['left_feedback'], color=COLOR_LIST[0], label='Feedback')
    if result['left_input']:
        left_input_time, left_input_value = zip(*result['left_input'])
        ax.step(
            [t - base_time for t in left_input_time],
            left_input_value, color=COLOR_LIST[1], label='Input', where='post'
        )
    ax.set_xlabel('Time (s)')
    ax.set_ylabel('Speed')
    ax.legend()

    return fig


def plot_right_motor_graph(result):
    feedback_times = result['feedback_time']
    base_time = feedback_times[0]
    feedback_times = [t - base_time for t in feedback_times]

    # right motor
    fig, ax = plt.subplots()
    ax.plot(feedback_times, result['right_feedback'], color=COLOR_LIST[0], label='Feedback')
    if result['right_input']:
        right_input_time, left_input_value = zip(*result['right_input'])
        ax.step(
            [t - base_time for t in right_input_time],
            left_input_value, color=COLOR_LIST[1], label='Input', where='post'
        )
    ax.set_xlabel('Time (s)')
    ax.set_ylabel('Speed')
    ax.legend()

    return fig


set_style()

st.title('PID Tuner')

st.header('Tune motor')
motor_type = st.selectbox('Motor Type', options=['LEFT', 'RIGHT'])
speed = st.number_input('Motor Speed', min_value=0)
running_time = st.number_input('Running Time (ms)', min_value=0)
if st.button('Set Motor'):
    r = set_motor_speed()
    if r.status_code == 200:
        st.write('Set successfully.')
    else:
        st.write(f'Fail with: {r.json()}')

graph_type = st.selectbox('Graph Type', options=['left_motor', 'right_motor'])
if graph_type == 'left_motor':
    result = get_feedback()
    st.subheader('Left Motor Graph')
    st.pyplot(plot_left_motor_graph(result))
else:
    result = get_feedback()
    st.subheader('Right Motor Graph')
    st.pyplot(plot_right_motor_graph(result))

st.header('Set PID value')
Kp_l = st.number_input('Kp (Left)', min_value=0)
Ki_l = st.number_input('Ki (Left)')
Kd_l = st.number_input('Kd (Left)')
Kp_r = st.number_input('Kp (Right)', min_value=0)
Ki_r = st.number_input('Ki (Right)')
Kd_r = st.number_input('Kd (Right)')
if st.button('Set PID Value'):
    r = set_pid_parameter()
    if r.status_code == 200:
        st.write('Set successfully.')
    else:
        st.write(f'Fail with: {r.json()}')
