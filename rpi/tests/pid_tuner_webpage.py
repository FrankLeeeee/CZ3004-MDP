#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Author: Li Yuanming
Email: yli056@e.ntu.edu.sg
Date: 3/23/2021
"""
import base64

import matplotlib as mpl
import matplotlib.pyplot as plt
import pandas as pd
import requests
import streamlit as st

BACKEND_URL = 'http://10.27.88.202:8000'
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


def clear_feedback():
  response = requests.post(f'{BACKEND_URL}/clear')
  return response


def set_pid_parameter():
  payload = {
    'left': {'Kp': Kp_l, 'Ki': Ki_l, 'Kd': Kd_l},
    'right': {'Kp': Kp_r, 'Ki': Ki_r, 'Kd': Kd_r}
  }
  return requests.post(f'{BACKEND_URL}/param', json=payload)


def robot_move(direction):
  payload = {'direction': direction}
  return requests.post(f'{BACKEND_URL}/move', params=payload)


def plot_left_motor_graph(result):
  # left motor
  fig, ax = plt.subplots()
  ax.step(result['feedback_time'], result['left_feedback'], color=COLOR_LIST[0], label='Feedback', where='post')
  if result['left_input']:
    ax.step(
      result['feedback_time'],
      result['left_input'], color=COLOR_LIST[1], label='Input', where='post'
    )
    ax.set_ylim(None, max(result['left_input']) * 1.4)
    ax.set_xlabel('Time (s)')
    ax.set_ylabel('Speed')
    ax.legend()

    return fig


def plot_right_motor_graph(result):
    # right motor
    fig, ax = plt.subplots()
    ax.step(result['feedback_time'], result['right_feedback'], color=COLOR_LIST[0], label='Feedback', where='post')
    if result['right_input']:
      # right_input_time, right_input_value = zip(*result['right_input'])
      # right_input_time = [0] + [t - base_time for t in right_input_time]
      # right_input_value = [0] + list(right_input_value)
      ax.step(
        result['feedback_time'],
        result['right_input'], color=COLOR_LIST[1], label='Input', where='post'
      )
      ax.set_ylim(None, max(result['right_input']) * 1.4)
    ax.set_xlabel('Time (s)')
    ax.set_ylabel('Speed')
    ax.legend()

    return fig


def get_table_download_link(df):
  """Generates a link allowing the data in a given panda dataframe to be downloaded
  in:  dataframe
  out: href string
  """
  csv = df.to_csv(index=False)
  b64 = base64.b64encode(csv.encode()).decode()  # some strings <-> bytes conversions necessary here
  return f'<a href="data:file/csv;base64,{b64}">Download csv file</a>'


set_style()

st.set_page_config(layout='wide')
st.title('PID Tuner')

col1, col2 = st.beta_columns((2, 1))
with col1:
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
    # normalize time
    result = get_feedback()
    base_time = result['feedback_time'][0]
    result['feedback_time'] = [t - base_time for t in result['feedback_time']]
    # build the data frame
    df = pd.DataFrame(result)
    st.subheader('Left Motor Graph')
    st.pyplot(plot_left_motor_graph(result))
  else:
    # normalize time
    result = get_feedback()
    base_time = result['feedback_time'][0]
    result['feedback_time'] = [t - base_time for t in result['feedback_time']]
    # build the data frame
    df = pd.DataFrame(result)
    st.subheader('Right Motor Graph')
    st.pyplot(plot_right_motor_graph(result))

  if st.button('Clear Feedback'):
    r = clear_feedback()
    if r.status_code == 200:
      st.write('Clear successfully')
    else:
      st.write(f'Fail with: {r.json()}')

with col2:
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

  st.header('Motor Data')
  st.dataframe(df)
  st.markdown(get_table_download_link(df), unsafe_allow_html=True)

st.header('Driving Test')
if st.button('Up'):
  r = robot_move('UP')
if st.button('Left'):
  r = robot_move('LEFT')
if st.button('Right'):
  r = robot_move('RIGHT')
if st.button('Down'):
  r = robot_move('DOWN')
