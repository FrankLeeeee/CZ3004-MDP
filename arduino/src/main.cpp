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
  sensorInit();
  while (!Serial)
    Serial.flush();
}

void loop()
{
  getSensorReading();
  loopSeq();
  // readInput();
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
      moveF(1);
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
      // printSensorReading();
       printSensorBlocks();
      //Serial.println(getAvg3());
      // Serial.print(getAvg1());
      // Serial.print(" ");
      // Serial.println(getAvg2());
      // Serial.print(" ");
      // Serial.println(getAvg4());
      break;
    case 'f':
      //encodeMessage();
      break;
    case 'g':
      //receiveMessage(rpi_receive1);
      break;
    case 'r':
      calibrateProc();
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
  turnR(90);
  delay(500);
  calibrateProc();
  delay(500);
  turnR(90);
  delay(500);
  calibrateProc();
  delay(500);
  turnR(90);
  delay(500);
  while(1){
    moveF(1);
    delay(500);
    moveF(1);
    delay(500);
    turnR(90);
    delay(500);
  }

  // turnL(90);
  // delay(500);
  // calibrateProc();
  // delay(500);
  // turnL(90);
  // delay(500);
  // calibrateProc();
  // delay(500);
  // turnL(90);
  // delay(500);
  // moveF(20);
  // delay(500);
  // turnL(90);
  // delay(500);
  // moveF(20);
  // delay(500);
  // turnL(90);
  // delay(500);
  // moveF(20);
  // delay(500);
  // turnL(90);
  // delay(500);
  // moveF(20);
  // delay(500);
  // turnR(90);
  // delay(500);
  // calibrateProc();
  // delay(500);
  // turnR(90);
  // delay(500);

  // moveF(20);
  // delay(500);
  // turnL(90);
  // delay(500);
  // calibrateProc();
  // delay(500);
  // turnL(90);
  // delay(500);
  // moveF(20);
  // delay(500);
  // turnR(90);
  // delay(500);
  // turnR(90);
  // delay(500);

  // for(int i = 0;i<8;i++){
  //   moveF(1);
  //   delay(300);
  // }
  // turnR(180);
  // delay(100);
  // for(int i = 0;i<8;i++){
  //   moveF(1);
  //   delay(300);
  // }
}
