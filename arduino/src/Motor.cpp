#include "EnableInterrupt.h"
#include "DualVNH5019MotorShield.h"
#include "PID_v1.h"
#include "Motor.h"
#include "Sensor.h"

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

void EncoderInit()
{
    md.init();
    pinMode(E1A, INPUT);
    pinMode(E1B, INPUT);
    pinMode(E2A, INPUT);
    pinMode(E2B, INPUT);
    enableInterrupt(E1A, countTickR, CHANGE);
    enableInterrupt(E2A, countTickL, CHANGE);
}

void countTickR()
{
    TickR++;
}

void countTickL()
{
    TickL++;
}

//===== Parameters =====
int delayms = 20;
double motorfactor = 0.99775;
boolean brakes = false;
double circumference = PI * 6;
double distance_cm;                //distance in cm that the robot need to move
double dist_between_wheels = 17.4; // in cm

//===== PID =====
//10, 1, 0.25
double kp = 10, ki = 1, kd = 0.25;
PID PID1(&curTickR, &speedR, &curTickL, kp, ki, kd, DIRECT);
PID PID2(&curTickL, &speedL, &curTickR, kp, ki, kd, DIRECT);

void PIDInit()
{
    PID1.SetOutputLimits(-400, 400);
    PID1.SetSampleTime(delayms);
    PID1.SetMode(AUTOMATIC);
    PID2.SetOutputLimits(-400, 400);
    PID2.SetSampleTime(delayms);
    PID2.SetMode(AUTOMATIC);
}

//===== Conversion Functions =====

double calcTickFromDist(double dist)
{
    return ((0.95 * dist) * 1124.5) / circumference;
}

double getTicksFromAngle(double angle)
{
    return ((dist_between_wheels / 6) * 1124.5) * (angle / 360);
}

//===== Movement =====
void moveF()
{
    speedL = 350;
    speedR = speedL * motorfactor;
    md.setSpeeds(speedR, speedL);
    delay(delayms);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (!brakes)
    {
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        Serial.print(curTickR);
        Serial.print(" ");
        Serial.println(curTickL);
        PID1.Compute();
        PID2.Compute();
        md.setSpeeds(speedR * motorfactor, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        delay(delayms);
    }
}

void moveF(double dist)
{
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
    speedL = 350;
    speedR = speedL * motorfactor;
    // for(int i = 0; i<350 ; i+=20){
    //   md.setSpeeds(i*motorfactor,i);
    //   delay(10);
    // }
    md.setSpeeds(speedR, speedL);
    delay(delayms);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL)
    {
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        Serial.print(curTickR);
        Serial.print(" ");
        Serial.println(curTickL);
        PID1.Compute();
        PID2.Compute();
        md.setSpeeds(speedR * motorfactor, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        getSensorReading();
        delay(delayms);
    }
    //    setTickLoop();
    md.setBrakes(400, 400);
}

void moveB()
{
    speedL = 350;
    speedR = speedL * motorfactor;
    md.setSpeeds(-speedR, -speedL);
    delay(delayms);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (!brakes)
    {
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        Serial.print(curTickR);
        Serial.print(" ");
        Serial.println(curTickL);
        PID1.Compute();
        PID2.Compute();
        md.setSpeeds(-speedR * motorfactor, -speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        delay(delayms);
    }
}

void moveB(double dist)
{
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
    speedL = 350;
    speedR = speedL * motorfactor;
    md.setSpeeds(-speedR, -speedL);
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
    md.setBrakes(400, 400);
}

void turnL()
{
    speedL = 350;
    speedR = speedL * motorfactor;
    md.setSpeeds(speedR, speedL);
    delay(delayms);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (!brakes)
    {
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        Serial.print(curTickR);
        Serial.print(" ");
        Serial.println(curTickL);
        PID1.Compute();
        PID2.Compute();
        md.setSpeeds(speedR * motorfactor, -speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        delay(delayms);
    }
}

void turnL(double angle)
{
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = getTicksFromAngle(angle);
    //6600 - 720deg;
    speedL = 250;
    speedR = speedL * motorfactor;
    md.setSpeeds(speedR, -speedL);
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
    md.setBrakes(400, 400);
}

void turnR()
{
    speedL = 350;
    speedR = speedL * motorfactor;
    md.setSpeeds(speedR, speedL);
    delay(delayms);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (!brakes)
    {
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        Serial.print(curTickR);
        Serial.print(" ");
        Serial.println(curTickL);
        PID1.Compute();
        PID2.Compute();
        md.setSpeeds(-speedR * motorfactor, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        delay(delayms);
    }
}

void turnR(double angle)
{
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = getTicksFromAngle(angle);
    speedL = 250;
    speedR = speedL * motorfactor;
    md.setSpeeds(-speedR, speedL);
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
    md.setBrakes(400, 400);
}

void brake()
{
    md.setBrakes(400, 400);
    brakes = true;
}

void setTickLoop()
{
    // PIDInit();
    // while (targetTick > TickR && targetTick > TickL)
    // {
    //     curTickR = TickR - oldTickR;
    //     curTickL = TickL - oldTickL;
    //     Serial.print(curTickR);
    //     Serial.print(" ");
    //     Serial.println(curTickL);
    //     PID1.Compute();
    //     PID2.Compute();
    //     switch (state)
    //     {
    //     case 1:
    //         md.setSpeeds(speedR * motorfactor, speedL);
    //         break;
    //         // case 2: md.setSpeeds(-speedR*motorfactor, -speedL);
    //         // break;
    //         // case 3: md.setSpeeds(speedR*motorfactor, -speedL);
    //         // break;
    //         // case 4: md.setSpeeds(-speedR*motorfactor, speedL);
    //         // break;
    //     }
    //     oldTickR += curTickR;
    //     oldTickL += curTickL;
    //     switch (state)
    //     {
    //     case 1:
    //         if (targetTick - TickR < 100)
    //             md.setSpeeds(100, 100);
    //         break;
    //         // case 2: if(targetTick-TickR < 100)
    //         // md.setSpeeds(-100,-100);
    //         // break;
    //         // case 3: if(targetTick-TickR < 100)
    //         // md.setSpeeds(100,-100);
    //         // break;
    //         // case 4: if(targetTick-TickR < 100)
    //         // md.setSpeeds(-100,100);
    //         // break;
    //     }
    //     //getSensorReading();
    //     delay(delayms);
    // }
    return;
}