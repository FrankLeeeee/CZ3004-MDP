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
}

void loop()
{
  readInput();
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
      getSensorReading();
      // default: getSensorReading();
      break;
    case 'f':
      encodeMessage();
      break;
    case 'g':
      decodeMessage();
      break;
    }
  }
  // else{
  //   getSensorReading();
  // }
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
