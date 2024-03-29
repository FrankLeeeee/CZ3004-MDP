//===== Libraries =====
#include <EnableInterrupt.h>
#include "DualVNH5019MotorShield.h"
#include <PID_v1.h>
#include "SharpIR.h"
#include "math.h"
//=====================

//===== Function Declaration =====
void EncoderInit();
void countTickR();
void countTickL();
void PIDInit();
char readInput();
double calcTickFromDist(double dist);
double getTicksFromAngle(double angle);
void moveF();
void moveF(double dist);
void moveB();
void moveB(double dist);
void turnL();
void turnL(double angle);
void turnR();
void turnR(double angle);
void brake();
void setTickLoop();
void getSensorReading();
double filter(double volt, double oldVal);
double getDist1(double x);
double getDist2(double x);
double getDist3(double x);
double getDist4(double x);
double getDist5(double x);
double getDist6(double x);
//================================

//===== Sensors ======
#define IRPin A0
#define IRPin2 A1
#define IRPin3 A2
#define IRPin4 A3
#define IRPin5 A4
#define IRPin6 A5
#define model GP2Y0A21YK0F
#define model1 GP2Y0A02YK0F
SharpIR mySensor(SharpIR:: model, A0);
SharpIR mySensor2(SharpIR:: model, A1);
SharpIR mySensor3(SharpIR:: model, A2);
SharpIR mySensor4(SharpIR:: model, A3);
SharpIR mySensor5(SharpIR:: model, A4);
SharpIR mySensor6(SharpIR:: model1, A5);

//===== Sensor Variables =====
double curFiltered1, curFiltered2, curFiltered3, curFiltered4, curFiltered5, curFiltered6;
double oldFiltered1 = -1, oldFiltered2 = -1, oldFiltered3 = -1, oldFiltered4 = -1, oldFiltered5 = -1, oldFiltered6 = -1; 
double V1, V2, V3, V4, V5, V6;

//===== Motors =====
DualVNH5019MotorShield md;
#define E1A 3
#define E1B 5
#define E2A 11
#define E2B 13

double speedR, speedL;

//===== Encoders =====
double curTickR = 0, curTickL = 0, oldTickR = 0, oldTickL = 0;
volatile long TickL = 0, TickR = 0;
double targetTick;


void EncoderInit(){
  pinMode(E1A, INPUT);
  pinMode(E1B, INPUT);
  pinMode(E2A, INPUT);
  pinMode(E2B, INPUT);
  enableInterrupt(E1A, countTickR, CHANGE);
  enableInterrupt(E2A, countTickL, CHANGE);
}

void countTickR(){
  TickR++;
}

void countTickL(){
  TickL++;
}

//===== Parameters =====
int delayms = 20;
double motorfactor = 0.99775;
boolean brakes = false;
double circumference = PI * 6;
double distance_cm; //distance in cm that the robot need to move
double dist_between_wheels = 17.4; // in cm

//===== PID =====
//10, 1, 0.25
double kp = 10, ki = 1, kd = 0.25;
PID PID1(&curTickR ,&speedR ,&curTickL ,kp ,ki ,kd ,DIRECT);
PID PID2(&curTickL ,&speedL ,&curTickR ,kp ,ki ,kd ,DIRECT);

void PIDInit(){
  PID1.SetOutputLimits(-400, 400);
  PID1.SetSampleTime(delayms);
  PID1.SetMode(AUTOMATIC);
  PID2.SetOutputLimits(-400, 400);
  PID2.SetSampleTime(delayms);
  PID2.SetMode(AUTOMATIC);
}

//==========================
//===== Main Functions =====
//==========================
void setup() {
  Serial.begin(9600);
  md.init();
  EncoderInit();
}

void loop() {
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

char readInput(){
  if(Serial.available()>0){
    int inByte = Serial.read();
      switch(inByte){
      case 'w': 
                state = 1;
                moveF(10);
                break;
      case 's': 
                state = 2;
                moveB(10);
                break;
      case 'a': delay(0); 
                state = 3;
                turnL(90);
                break;        
      case 'd': delay(0);
                state = 4;
                turnR(90);
                break;
      case 'b': brake();
                state = 5;
                break;
      // default: getSensorReading();
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

//===== Conversion Functions =====

double calcTickFromDist(double dist){
  return ((0.95*dist) * 1124.5)/circumference;
}

double getTicksFromAngle(double angle){
  return ((dist_between_wheels/6)*1124.5) * (angle/360);
}

//===== Movement =====
void moveF(){
  speedL = 350;
  speedR = speedL * motorfactor;
  md.setSpeeds(speedR,speedL);
  delay(delayms);
  oldTickR = (double)TickR;
  oldTickL = (double)TickL;
  PIDInit();
  brakes = false;
  
  while(!brakes){
    curTickR = TickR - oldTickR;
    curTickL = TickL - oldTickL;
    Serial.print(curTickR);Serial.print(" ");Serial.println(curTickL);
    PID1.Compute();
    PID2.Compute();
    md.setSpeeds(speedR*motorfactor, speedL);
    oldTickR += curTickR;
    oldTickL += curTickL;
    delay(delayms);
  }
}

void moveF(double dist){
  TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
  targetTick = calcTickFromDist(dist);
  speedL = 350;
  speedR = speedL * motorfactor;
  // for(int i = 0; i<350 ; i+=20){
  //   md.setSpeeds(i*motorfactor,i);
  //   delay(10);
  // }
  md.setSpeeds(speedR,speedL);
  delay(delayms);
  oldTickR = (double)TickR;
  oldTickL = (double)TickL;
//  PIDInit();
  brakes = false;
  
  // while(targetTick > TickR && targetTick > TickL){
  //   curTickR = TickR - oldTickR;
  //   curTickL = TickL - oldTickL;
  //   Serial.print(curTickR);Serial.print(" ");Serial.println(curTickL);
  //   PID1.Compute();
  //   PID2.Compute();
  //   md.setSpeeds(speedR*motorfactor, speedL);
  //   oldTickR += curTickR;
  //   oldTickL += curTickL;
  //   delay(delayms);
  // }
  setTickLoop();
  md.setBrakes(400,400);
}

void moveB(){
  speedL = 350;
  speedR = speedL * motorfactor;
  md.setSpeeds(-speedR,-speedL);
  delay(delayms);
  oldTickR = (double)TickR;
  oldTickL = (double)TickL;
  PIDInit();
  brakes = false;
  
  while(!brakes){
    curTickR = TickR - oldTickR;
    curTickL = TickL - oldTickL;
    Serial.print(curTickR);Serial.print(" ");Serial.println(curTickL);
    PID1.Compute();
    PID2.Compute();
    md.setSpeeds(-speedR*motorfactor, -speedL);
    oldTickR += curTickR;
    oldTickL += curTickL;
    delay(delayms);
  }
}

void moveB(double dist){
  TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
  targetTick = calcTickFromDist(dist);
  speedL = 350;
  speedR = speedL * motorfactor;
  md.setSpeeds(-speedR,-speedL);
  delay(delayms);
  oldTickR = (double)TickR;
  oldTickL = (double)TickL;
//  PIDInit();
  brakes = false;
  
  // while(targetTick > TickR && targetTick > TickL){
  //   curTickR = TickR - oldTickR;
  //   curTickL = TickL - oldTickL;
  //   Serial.print(curTickR);Serial.print(" ");Serial.println(curTickL);
  //   PID1.Compute();
  //   PID2.Compute();
  //   md.setSpeeds(-speedR*motorfactor, -speedL);
  //   oldTickR += curTickR;
  //   oldTickL += curTickL;
  //   delay(delayms);
  // }
  setTickLoop();
  md.setBrakes(400,400);
}

void turnL(){
  speedL = 350;
  speedR = speedL * motorfactor;
  md.setSpeeds(speedR,speedL);
  delay(delayms);
  oldTickR = (double)TickR;
  oldTickL = (double)TickL;
  PIDInit();
  brakes = false;
  
  while(!brakes){
    curTickR = TickR - oldTickR;
    curTickL = TickL - oldTickL;
    Serial.print(curTickR);Serial.print(" ");Serial.println(curTickL);
    PID1.Compute();
    PID2.Compute();
    md.setSpeeds(speedR*motorfactor, -speedL);
    oldTickR += curTickR;
    oldTickL += curTickL;
    delay(delayms);
  }
}

void turnL(double angle){
  TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
  targetTick = getTicksFromAngle(angle);
  //6600 - 720deg;
  speedL = 250;
  speedR = speedL * motorfactor;
  md.setSpeeds(speedR,-speedL);
  delay(delayms);
  oldTickR = (double)TickR;
  oldTickL = (double)TickL;
//  PIDInit();
  brakes = false;
  
  // while(targetTick > TickR && targetTick > TickL){
  //   curTickR = TickR - oldTickR;
  //   curTickL = TickL - oldTickL;
  //   Serial.print(curTickR);Serial.print(" ");Serial.println(curTickL);
  //   PID1.Compute();
  //   PID2.Compute();
  //   md.setSpeeds(speedR*motorfactor, -speedL);
  //   oldTickR += curTickR;
  //   oldTickL += curTickL;
  //   if(targetTick-TickR < 100)
  //     md.setSpeeds(100,-100);
  //   delay(delayms);
  // }
  setTickLoop();
  md.setBrakes(400,400);
}

void turnR(){
  speedL = 350;
  speedR = speedL * motorfactor;
  md.setSpeeds(speedR,speedL);
  delay(delayms);
  oldTickR = (double)TickR;
  oldTickL = (double)TickL;
  PIDInit();
  brakes = false;

  while(!brakes){
    curTickR = TickR - oldTickR;
    curTickL = TickL - oldTickL;
    Serial.print(curTickR);Serial.print(" ");Serial.println(curTickL);
    PID1.Compute();
    PID2.Compute();
    md.setSpeeds(-speedR*motorfactor, speedL);
    oldTickR += curTickR;
    oldTickL += curTickL;
    delay(delayms);
  }
}

void turnR(double angle){
  TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
  targetTick = getTicksFromAngle(angle);
  speedL = 250;
  speedR = speedL * motorfactor;
  md.setSpeeds(-speedR,speedL);
  delay(delayms);
  oldTickR = (double)TickR;
  oldTickL = (double)TickL;
//  PIDInit();
  brakes = false;
  
  // while(targetTick > TickR && targetTick > TickL){
  //   curTickR = TickR - oldTickR;
  //   curTickL = TickL - oldTickL;
  //   Serial.print(curTickR);Serial.print(" ");Serial.println(curTickL);
  //   PID1.Compute();
  //   PID2.Compute();
  //   md.setSpeeds(-speedR*motorfactor, speedL);
  //   oldTickR += curTickR;
  //   oldTickL += curTickL;
  //   if(targetTick-TickR < 100)
  //     md.setSpeeds(-100,100);
  //   delay(delayms);
  // }
  setTickLoop();
  md.setBrakes(400,400);
}

void brake(){
  md.setBrakes(400,400);
  brakes = true;
}

void setTickLoop(){
  PIDInit();
  while(targetTick > TickR && targetTick > TickL){
    curTickR = TickR - oldTickR;
    curTickL = TickL - oldTickL;
    Serial.print(curTickR);Serial.print(" ");Serial.println(curTickL);
    PID1.Compute();
    PID2.Compute();
    switch(state){
      case 1: md.setSpeeds(speedR*motorfactor, speedL);
      break;
      // case 2: md.setSpeeds(-speedR*motorfactor, -speedL);
      // break;
      // case 3: md.setSpeeds(speedR*motorfactor, -speedL);
      // break;
      // case 4: md.setSpeeds(-speedR*motorfactor, speedL);
      // break;
    }
    oldTickR += curTickR;
    oldTickL += curTickL;
    switch(state){
      case 1: if(targetTick-TickR < 100)
      md.setSpeeds(100,100);
      break;
      // case 2: if(targetTick-TickR < 100)
      // md.setSpeeds(-100,-100);
      // break;
      // case 3: if(targetTick-TickR < 100)
      // md.setSpeeds(100,-100);
      // break;
      // case 4: if(targetTick-TickR < 100)
      // md.setSpeeds(-100,100);
      // break;
    }
    getSensorReading();
    delay(delayms);
  }
}

//===== Sensors =====

void getSensorReading(){
  //Change according to pin (A0 = PS1, A1 = PS2, etc)
  V1 = analogRead(A0); // Read voltage
  V2 = analogRead(A1);
  V3 = analogRead(A2);
  V4 = analogRead(A3);
  V5 = analogRead(A4);
  V6 = analogRead(A5);

  if(oldFiltered1 == -1) // sanity check for t=0
    oldFiltered1 = V1;
  curFiltered1 = filter(V1, oldFiltered1); // Exponential filter
  oldFiltered1 = curFiltered1; // get old value

  if(oldFiltered2 == -1) // sanity check for t=0
    oldFiltered2 = V2;
  curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
  oldFiltered2 = curFiltered2; // get old value

  if(oldFiltered3 == -1) // sanity check for t=0
    oldFiltered3 = V3;
  curFiltered3 = filter(V3, oldFiltered3); // Exponential filter
  oldFiltered3 = curFiltered3; // get old value

  if(oldFiltered4 == -1) // sanity check for t=0
    oldFiltered4 = V4;
  curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
  oldFiltered4 = curFiltered4; // get old value

  if(oldFiltered5 == -1) // sanity check for t=0
    oldFiltered5 = V5;
  curFiltered5 = filter(V5, oldFiltered5); // Exponential filter
  oldFiltered5 = curFiltered5; // get old value

  if(oldFiltered6 == -1) // sanity check for t=0
    oldFiltered6 = V6;
  curFiltered6 = filter(V6, oldFiltered6); // Exponential filter
  oldFiltered6 = curFiltered6; // get old value
  
//  Serial.print(V*0.0049);
//  Serial.print("  ");
//  Serial.println(curFiltered*0.0049);

//  Serial.print(V);
//  Serial.print("  ");
//  Serial.println(curFiltered);

Serial.print("Sensor 1: ");
Serial.print(curFiltered1*0.0049);
Serial.print("  ");
Serial.println(getDist1(curFiltered1*0.0049));
Serial.print("  ");
Serial.print("Sensor 2: ");
Serial.print(curFiltered2*0.0049);
Serial.print("  ");
Serial.println(getDist2(curFiltered2*0.0049));
Serial.print("  ");
Serial.print("Sensor 3 ");
Serial.print(curFiltered3*0.0049);
Serial.print("  ");
Serial.println(getDist3(curFiltered3*0.0049));
Serial.print("  ");
Serial.print("Sensor 4 ");
Serial.print(curFiltered4*0.0049);
Serial.print("  ");
Serial.println(getDist4(curFiltered4*0.0049));
Serial.print("  ");
Serial.print("Sensor 5 ");
Serial.print(curFiltered5*0.0049);
Serial.print("  ");
Serial.println(getDist5(curFiltered5*0.0049));
Serial.print("  ");
Serial.print("Sensor 6 ");
Serial.print(curFiltered6*0.0049);
Serial.print("  ");
Serial.println(getDist6(curFiltered6*0.0049));
}

// Used for sensor 1 & 4
double getDist1(double x){
//  return -5.7108*pow(x,5) + 47.988*pow(x,4) - 159.85*pow(x,3) + 270.34*pow(x,2) - 247.46*x + 120.28;
  return -13.696*pow(x,5) + 101.4*pow(x,4) - 296.49*pow(x,3) + 438.4*pow(x,2) - 348.66*x + 144.17;
}

// Sensor 2
double getDist2(double x){
  return -11.577*pow(x,5) + 92.006*pow(x,4) - 288.75*pow(x,3) + 455*pow(x,2) - 377.91*x + 155.37;
}

double getDist3(double x){
  return 25.863*pow(x,-1.268);
}

double getDist4(double x){
  return -20.988*pow(x,5) + 143.17*pow(x,4) - 383.55*pow(x,3) + 520.2*pow(x,2) - 382.96*x + 148.66;
}
// 3 & 5
double getDist5(double x){ 
  return 26.353*pow(x,-1.056);
}

// Long distance sensor
double getDist6(double x){
  return -17.686*pow(x,5) + 143.29*pow(x,4) - 454.79*pow(x,3) + 718.36*pow(x,2) - 600.2*x + 265.99;
 // return 3.9597*pow(x,6) - 50.124*pow(x,5) + 247.27*pow(x,4) - 620.04*pow(x,3) + 854.23*pow(x,2) - 654.48*x + 274.19;
}

double alpha = 0.1; // Smoothing Factor
double filter(double volt, double oldVal){
  return (alpha*volt) + (1-alpha)*oldVal;
}