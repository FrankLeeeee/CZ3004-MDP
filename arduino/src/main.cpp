#include "Motor.h"
#include "Sensor.h"
#include "Arduino.h"
#include "Comms.h"
#include <ArduinoJson.h>
#define MESSAGE_SEPARATOR ';'
#define ECHO 0x01
#define FORWARD 0x02
#define LEFT 0x03
#define RIGHT 0x04
#define GETMETRICS 0x05
#define GETCALIBRATION 0x06
#define TERMINATE 0x07

#define PARSE_MSG_BUFFER_SIZE 64

void readInput();
int echoHandler(int argument, byte* response_ptr);
void parseMessage();
double getAvg1();
double getAvg2();
double getAvg3();
double getAvg4();
double getAvg5();
double getAvg6();

uint8_t rpi_receive[1024];
uint8_t rpi_receive_cur = 0;
String message;
uint8_t temp1;
uint8_t rpi_receive1[1024] = {'C', 'A', 'L', 'I', 'B', 'R', 'A', 'T', 'E', '\\', '{', '"', 's', 't', 'e', 'p', '"', ':', '3', '}', ';'};

//==========================
//===== Main Functions =====
//==========================
void setup()
{
  Serial.begin(115200);
  EncoderInit();
  while (!Serial)
    Serial.flush();
}

void loop()
{
  getSensorReading();
  char temp;
  while (Serial.available() > 0)
  {
    temp = Serial.read();

    // send the message to queue
    if (temp == (uint8_t)MESSAGE_SEPARATOR)
    { // find the end of message
      parseMessage();
      message = String("");
    }
    else
      message.concat((char)temp);
  }
}

void parseMessage()
{
  byte msg[PARSE_MSG_BUFFER_SIZE];
  int msg_len = 0;
  char command = message.charAt(0);
  int argument = 0;
  if (message.length() > 1)
  {
    argument = (int)message.charAt(1);
  }
  
  switch (command)
  {
  case '1':
    msg_len = echoHandler(argument, msg);
    Serial.write(msg, msg_len);
    break;

  case '2':
    Serial.println("here");
    //forward function + send back sensor data
    byte xbuf[5] = {};
    float avg1 = (float)getAvg1();

    memcpy(xbuf, (byte *)&avg1, 4);
    xbuf[5] = ';';

    //byte msg1[] = {(xbuf,';')};
    // Serial.write(xbuf, 5);
    for (auto i = 0; i < 5; i++)
      Serial.print(xbuf[i], HEX);
    break;

  case LEFT:
    //left function + send back sensor data
    break;

  case RIGHT:
    //right function + send back sensor data
    break;

  case GETMETRICS:
    //send sensor data to rpi
    //',',(byte)getAvg2(),',',(byte)getAvg3(),',',(byte)getAvg4(),',',(byte)getAvg5(),',',(byte)getAvg6()

    break;

  case GETCALIBRATION:
    //get calibration data
    break;

  case TERMINATE:
    //terminate function
    break;
  }
}

int echoHandler(int argument, byte* response_ptr) {
  byte response[] = {(byte)(argument + 1), ',', 0x01, ';'};
  memcpy(response_ptr, response, 5);

  return 5;
}

double getAvg1()
{

  unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
  int readSample = 10;
  double sensorReadings[readSample];

  for (int i = 0; i < readSample; i++)
  {
    getSensorReading();
    sensorReadings[i] = get_curFiltered1();
    Serial.print(sensorReadings[i]);
    Serial.print(" ");
  }

  double sumL = 0;

  for (int i = 0; i < readSample; i++)
  {
    sumL = sumL + sensorReadings[i];
  }

  double sensorAvg = sumL / readSample;

  Serial.print("Avg: ");
  Serial.println(sensorAvg);

  unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
  Serial.print("Time taken (ms): ");
  Serial.println(pepe2);

  return (sensorAvg);
}

double getAvg2()
{

  unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
  int readSample = 10;
  double sensorReadings[readSample];

  for (int i = 0; i < readSample; i++)
  {
    getSensorReading();
    sensorReadings[i] = get_curFiltered2();
    Serial.print(sensorReadings[i]);
    Serial.print(" ");
  }

  double sumL = 0;

  for (int i = 0; i < readSample; i++)
  {
    sumL = sumL + sensorReadings[i];
  }

  double sensorAvg = sumL / readSample;

  Serial.print("Avg: ");
  Serial.println(sensorAvg);

  unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
  Serial.print("Time taken (ms): ");
  Serial.println(pepe2);

  return (sensorAvg);
}

double getAvg3()
{

  unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
  int readSample = 10;
  double sensorReadings[readSample];

  for (int i = 0; i < readSample; i++)
  {
    getSensorReading();
    sensorReadings[i] = get_curFiltered3();
    Serial.print(sensorReadings[i]);
    Serial.print(" ");
  }

  double sumL = 0;

  for (int i = 0; i < readSample; i++)
  {
    sumL = sumL + sensorReadings[i];
  }

  double sensorAvg = sumL / readSample;

  Serial.print("Avg: ");
  Serial.println(sensorAvg);

  unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
  Serial.print("Time taken (ms): ");
  Serial.println(pepe2);

  return (sensorAvg);
}

double getAvg4()
{

  unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
  int readSample = 10;
  double sensorReadings[readSample];

  for (int i = 0; i < readSample; i++)
  {
    getSensorReading();
    sensorReadings[i] = get_curFiltered4();
    Serial.print(sensorReadings[i]);
    Serial.print(" ");
  }

  double sumL = 0;

  for (int i = 0; i < readSample; i++)
  {
    sumL = sumL + sensorReadings[i];
  }

  double sensorAvg = sumL / readSample;

  Serial.print("Avg: ");
  Serial.println(sensorAvg);

  unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
  Serial.print("Time taken (ms): ");
  Serial.println(pepe2);

  return (sensorAvg);
}

double getAvg5()
{

  unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
  int readSample = 10;
  double sensorReadings[readSample];

  for (int i = 0; i < readSample; i++)
  {
    getSensorReading();
    sensorReadings[i] = get_curFiltered5();
    Serial.print(sensorReadings[i]);
    Serial.print(" ");
  }

  double sumL = 0;

  for (int i = 0; i < readSample; i++)
  {
    sumL = sumL + sensorReadings[i];
  }

  double sensorAvg = sumL / readSample;

  Serial.print("Avg: ");
  Serial.println(sensorAvg);

  unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
  Serial.print("Time taken (ms): ");
  Serial.println(pepe2);

  return (sensorAvg);
}

double getAvg6()
{

  unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
  int readSample = 10;
  double sensorReadings[readSample];

  for (int i = 0; i < readSample; i++)
  {
    getSensorReading();
    sensorReadings[i] = get_curFiltered6();
    Serial.print(sensorReadings[i]);
    Serial.print(" ");
  }

  double sumL = 0;

  for (int i = 0; i < readSample; i++)
  {
    sumL = sumL + sensorReadings[i];
  }

  double sensorAvg = sumL / readSample;

  Serial.print("Avg: ");
  Serial.println(sensorAvg);

  unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
  Serial.print("Time taken (ms): ");
  Serial.println(pepe2);

  return (sensorAvg);
}

//===== Inputs =====
int state = 0;
// 0 - idle
// 1 - Forward
// 2 - Backward
// 3 - Left
// 4 - Right
// 5 - Braking

void readInput()
{
  if (Serial.available() > 0)
  {
    int inByte = Serial.read();
    switch (inByte)
    {
    case 'w':
      state = 1;
      moveF(10);
      break;
    case 's':
      state = 2;
      moveB(10);
      break;
    case 'a':
      delay(0);
      state = 3;
      turnL(90);
      break;
    case 'd':
      delay(0);
      state = 4;
      turnR(90);
      break;
    case 'b':
      brake();
      state = 5;
      break;
    case 'e':
      printSensorReading();
      break;
    case 'f':
      encodeMessage();
      break;
    case 'g':
      //receiveMessage(rpi_receive1);
      break;
    case 'r':
      wallCalibrate();
    }
  }
  else
  {
    getSensorReading();
  }
}

//char readInput(){
//  if(Serial.available()>0){
//    int inByte = Serial.read();
//    return inByte;
//  }
//}
//
//void setState(){
//  state = readInput();
//      switch(state){
//      case 'w': moveF();
//      case 's': moveB();
//      case 'a': turnL();
//      case 'd': turnR();
//      case 'b': brake();
//     // default: brake();
//      }
//}
