#include "Motor.h"
#include "Sensor.h"
#include "Arduino.h"
#include "Comms.h"

void readInput();
void testFunction();

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
  //loopSeq();
  readInput();
}

//===== Inputs =====

void readInput()
{
  if (Serial.available() > 0)
  {
    int inByte = Serial.read();
    switch (inByte)
    {
    case 'w':
      moveF(2);
      break;
    case 's':
      moveB(1);
      break;
    case 'a':
      turnL(90);
      break;
    case 'd':
      turnR(90);
      break;
    case 'b':
      brake();
      break;
    case 'e':
      //  printSensorReading();
      //Serial.println(getAvg3());
      Serial.print(getDist1(getAvg1()));
      Serial.print(" ");
      Serial.print(getDist2(getAvg2()));
      Serial.print(" ");
      Serial.println(getDist4(getAvg4()));
      break;
    case 'f':
      //encodeMessage();
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
      Serial.print("test");
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
  while (1)
  {
    moveF(5);
    delay(100);
    turnR(90);
    delay(100);
    turnR(90);
  }
}
