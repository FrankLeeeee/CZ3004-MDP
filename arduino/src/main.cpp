#include "Motor.h"
#include "Sensor.h"
#include "Arduino.h"
#include "Comms.h"
#define MESSAGE_SEPARATOR ';'
#define ECHO 0x01
#define FORWARD 0x02
#define LEFT 0x03
#define RIGHT 0x04
#define GETMETRICS 0x05
#define GETCALIBRATION 0x06
#define TERMINATE 0x07

#define PARSE_MSG_BUFFER_SIZE 64
#define MESSAGE_SEPARATOR ';'
#define ARGUMENT_SEPARATOR ','

void parseMessage();
void readInput();

// Handlers
int echoHandler(int arguement, byte *response);
int forwardHandler(int arguement, int argument2, byte *response);
int turnLeftHandler(int arguement, int argument2, int argument3, byte *response);
int turnRightHandler(int arguement, int argument2, int argument3, byte *response);
int getMetricsHandler(int arguement, byte *response);
int calibrationHandler(int argument, byte *response);
int terminateHandler(int argument, byte *response);

// Serializers
int echo_response_serializer(char message, bool status, byte *response);
int metric_response_serializer(float *value_ptr, int value_len, bool status, byte *response);
int status_serializer(bool status, byte *response);

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
  int argument2 = 0;
  int argument3 = 0;
  if (message.length() > 1)
  {
    if (message.length() > 2)
    {
      argument2 = (int)message.charAt(2);
    }
    if (message.length() > 3)
    {
      argument3 = (int)message.charAt(3);
    }
    argument = (int)message.charAt(1);
  }

  switch (command)
  {
  case '1':
    msg_len = echoHandler(argument, msg);
    break;

  case '2':
    //add forward function
    msg_len = forwardHandler(argument, argument2, msg);
    break;

  case '3':
    //add left function
    msg_len = turnLeftHandler(argument, argument2, argument3, msg);
    break;

  case '4':
    //right function
    msg_len = turnRightHandler(argument, argument2, argument3, msg);
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
  }
  Serial.write(msg, msg_len);
}

int echoHandler(int argument, byte *response)
{
  bool status = true;
  return echo_response_serializer((char)argument, status, response);
}

int forwardHandler(int argument, int argument2, byte *response)
{

  bool status = true;
  Serial.println("here");
  if (argument2 != 0)
  {
    argument = argument - 48;
    argument = argument * 10;
    argument2 = argument2 - 48;
    argument += argument2;
  }
  else
  {
    argument = argument - 48;
  }
  Serial.println(argument);
  moveF(argument);
  float data_values[] = {(float)getAvg1(), (float)getAvg2(), (float)getAvg3(), (float)getAvg4(), (float)getAvg5(), (float)getAvg6()};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int turnLeftHandler(int argument, int argument2, int argument3, byte *response)
{
  bool status = true;
  if (argument3 != 0)
  {
    argument = argument - 48;
    argument = argument * 100;
    argument2 = argument2 - 48;
    argument2 = argument2 * 10;
    argument3 = argument3 - 48;
    argument += argument2;
    argument += argument3;
  }
  else if (argument2 != 0)
  {
    argument = argument - 48;
    argument = argument * 10;
    argument2 = argument2 - 48;
    argument += argument2;
  }
  else
  {
    argument = argument - 48;
  }
  Serial.println(argument);
  turnL(argument);
  float data_values[] = {(float)getAvg1(), (float)getAvg2(), (float)getAvg3(), (float)getAvg4(), (float)getAvg5(), (float)getAvg6()};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int turnRightHandler(int argument, int argument2, int argument3, byte *response)
{
  bool status = true;
  if (argument3 != 0)
  {
    argument = argument - 48;
    argument = argument * 100;
    argument2 = argument2 - 48;
    argument2 = argument2 * 10;
    argument3 = argument3 - 48;
    argument += argument2;
    argument += argument3;
  }
  else if (argument2 != 0)
  {
    argument = argument - 48;
    argument = argument * 10;
    argument2 = argument2 - 48;
    argument += argument2;
  }
  else
  {
    argument = argument - 48;
  }
  Serial.println(argument);
  turnR(argument);
  float data_values[] = {(float)getAvg1(), (float)getAvg2(), (float)getAvg3(), (float)getAvg4(), (float)getAvg5(), (float)getAvg6()};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int getMetricsHandler(int argument, byte *response)
{
  bool status = true;
  float data_values[] = {(float)getAvg1(), (float)getAvg2(), (float)getAvg3(), (float)getAvg4(), (float)getAvg5(), (float)getAvg6()};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int calibrationHandler(int argument, byte *response)
{
  bool status = true;

  return status_serializer(status, response);
}

int terminateHandler(int argument, byte *response)
{
  bool status = true;

  return status_serializer(status, response);
}

int status_serializer(bool status, byte *response)
{
  *response = (byte)status;
  *(response + 1) = MESSAGE_SEPARATOR;
  return 2;
}

int echo_response_serializer(char message, bool status, byte *response)
{
  *response = (byte)(message + 1);
  *(response + 1) = ARGUMENT_SEPARATOR;
  *(response + 2) = (byte)status;
  *(response + 3) = MESSAGE_SEPARATOR;
  return 4;
}

int metric_response_serializer(float *value_ptr, int value_len, bool status, byte *response)
{
  int float_len = sizeof(float);
  int data_values_size = float_len * value_len;

  for (auto i = 0; i < value_len; i++)
    memcpy(response + i * float_len, (byte *)(value_ptr + i), float_len);

  *(response + data_values_size++) = ARGUMENT_SEPARATOR;
  *(response + data_values_size++) = (byte)status;
  *(response + data_values_size++) = MESSAGE_SEPARATOR;

  return data_values_size;
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
      break;
    case 'p':
      delay(3000);
      avoidObstacle90();
      break;
    case 'o':
      delay(3000);
      avoidObstacleDiag();
      break;
    case 't':
      delay(3000);
      testFunction();
      break;
    default:
      getSensorReading();
    }
  }
  else
  {
    getSensorReading();
  }
}

void testFunction()
{
  // while(1){
  //   turnL(90);
  //   delay(500);
  // }
  while (1)
  {
    moveF(50);
    delay(500);
    turnL(90);
    delay(500);
    turnL(90);
    delay(500);
    moveF(50);
    delay(500);
    turnR(90);
    delay(500);
    turnR(90);
    delay(500);
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
