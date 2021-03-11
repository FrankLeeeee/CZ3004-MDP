#include "Motor.h"
#include "Sensor.h"
#include "Comms.h"
#include "stdint.h"
#include "Arduino.h"
#include <ArduinoJson.h>

#define RESPONSE_MSG_LEN 40
#define ECHO 0x01
#define FORWARD 0x02
#define LEFT 0x03
#define RIGHT 0x04
#define GETMETRICS 0x05
#define GETCALIBRATION 0x06
#define TERMINATE 0x07

#define PARSE_MSG_BUFFER_SIZE 64

String message;
const char c_separator = ';';
const char c_separator_sequence[] = {';', ';', ';', ';', ';'};
const uint8_t sequence_length = 5;
uint8_t counter = 0;

void loopSeq()
{
  getSensorReading();
  char temp;
  while (Serial.available() > 0)
  {
    getSensorReading();
    temp = Serial.read();
    message.concat((char)temp);

    if (temp == c_separator)
      counter++;

    if (counter == sequence_length)
    { // find the end of message
      counter = 0;
      message.remove(message.length() - sequence_length, sequence_length);
      parseMessage();
      message = String("");
    }
  }
}

void parseMessage()
{
  char command = message.charAt(0);
  int argument = 0;
  uint8_t msg[RESPONSE_MSG_LEN];
  int msg_len = 0;

  if (message.length() > 1)
    argument = (int)message.charAt(1);

  switch (command)
  {
  case ECHO:
    msg_len = echoHandler(argument, msg);
    break;

  case FORWARD:
    //add forward function
    msg_len = forwardHandler(argument, msg);
    break;

  case LEFT:
    //add left function
    msg_len = turnLeftHandler(argument, msg);
    break;

  case RIGHT:
    //right function
    msg_len = turnRightHandler(argument, msg);
    break;

  case GETMETRICS:
    msg_len = getMetricsHandler(argument, msg);
    break;

  case GETCALIBRATION:
    //add calibrate function
    msg_len = calibrationHandler(argument, msg);

    break;

  case TERMINATE:
    //terminate function
    msg_len = terminateHandler(argument, msg);

    break;
  default:
    break;
  }
  memcpy(msg + msg_len, c_separator_sequence, sequence_length);
  Serial.write(msg, msg_len + sequence_length);
}
int echoHandler(int argument, uint8_t *const response)
{
  bool status = true;

  return echo_response_serializer((char)argument, status, response);
}
int forwardHandler(int argument, uint8_t *response)
{

  bool status = true;

  moveF(argument);
  delay(300);
  float data_values[] = {(float)getAvg1(), (float)getAvg2(), (float)getAvg3(), (float)getAvg4(), (float)getAvg5(), (float)getAvg6()};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int turnLeftHandler(int argument, uint8_t *response)
{
  bool status = true;

  turnL(argument);
  delay(300);
  float data_values[] = {(float)getAvg1(), (float)getAvg2(), (float)getAvg3(), (float)getAvg4(), (float)getAvg5(), (float)getAvg6()};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int turnRightHandler(int argument, uint8_t *response)
{
  bool status = true;

  turnR(argument);
  delay(300);
  float data_values[] = {(float)getAvg1(), (float)getAvg2(), (float)getAvg3(), (float)getAvg4(), (float)getAvg5(), (float)getAvg6()};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int getMetricsHandler(int argument, uint8_t *response)
{
  bool status = true;
  float data_values[] = {(float)getAvg1(), (float)getAvg2(), (float)getAvg3(), (float)getAvg4(), (float)getAvg5(), (float)getAvg6()};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int calibrationHandler(int argument, uint8_t *response)
{
  bool status = true;

  calibrateProc();
  delay(300);
  float data_values[] = {(float)getAvg1(), (float)getAvg2(), (float)getAvg3(), (float)getAvg4(), (float)getAvg5(), (float)getAvg6()};

  return status_serializer(status, response);
}

int terminateHandler(int argument, uint8_t *response)
{
  bool status = true;

  return status_serializer(status, response);
}

int status_serializer(bool status, uint8_t *response)
{
  *response = (uint8_t)status;
  return 1;
}

int echo_response_serializer(char message, bool status, uint8_t *response)
{
  *response = (uint8_t)(message + 1);
  //*(response + 1) = ARGUMENT_SEPARATOR;
  *(response + 1) = (uint8_t)status;
  return 2;
}

int metric_response_serializer(float *value_ptr, int value_len, bool status, uint8_t *response)
{
  int float_len = sizeof(float);
  int data_values_size = float_len * value_len;

  for (auto i = 0; i < value_len; i++)
    memcpy(response + i * float_len, (uint8_t *)(value_ptr + i), float_len);

  //*(response + data_values_size++) = ARGUMENT_SEPARATOR;
  *(response + data_values_size++) = (uint8_t)status;

  return data_values_size;
}