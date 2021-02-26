#include "Motor.h"
#include "Sensor.h"
#include "Arduino.h"
#include "Comms.h"

void readInput();

uint8_t rpi_receive[1024] = {0x02, 0x31, 0xFF, 0xAB, 0xFF, 0xFF, 0xFF, 0xFF, 0xF1, 0xCC};

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
      printSensorReading();
      break;
    case 'f':
      encodeMessage();
      break;
    case 'g':
      // decodeMessage();
      receiveMessage(rpi_receive);
      break;
    }
  }
  else{
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
