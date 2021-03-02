#include "Motor.h"
#include "Sensor.h"
#include "Arduino.h"
#include "Comms.h"
#include <ArduinoJson.h>
#define MESSAGE_SEPARATOR ';'


void readInput();
void parseMessage();

uint8_t rpi_receive[1024];
uint8_t rpi_receive_cur = 0;
String message;
 

uint8_t temp1;
 uint8_t rpi_receive1[1024] = {'C','A', 'L', 'I', 'B', 'R', 'A','T','E','\\','{','"','s','t','e','p','"',':','3','}',';'};

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



void loop() {
  char temp;
  while (Serial.available() > 0) {
      temp = Serial.read();

      // send the message to queue
      if (temp == (uint8_t) MESSAGE_SEPARATOR) {  // find the end of message
          parseMessage();
          message = String("");
      }
      else
          message.concat((char)temp);
    }
}


void parseMessage() {  
  char command = message.charAt(0);
  int argument = 0;
  if (message.length() > 1){
    argument = (int)message.charAt(1);
  }

    switch(command){
    case '1':
    Serial.println("case 1");
    break;

    case '2':
    Serial.println("case 2");
    break;
  }

  
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
      //receiveMessage(rpi_receive1);
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
