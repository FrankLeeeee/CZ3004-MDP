#include "Motor.h"
#include "Sensor.h"
#include "Arduino.h"
#include "Comms.h"

void readInput();

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
  loopSeq();
  //readInput();
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
      //printSensorReading();
      Serial.print(getAvg5());
      Serial.print(" ");
      Serial.println(getAvg6());
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
      delay(3000);
      //testFunction();
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
