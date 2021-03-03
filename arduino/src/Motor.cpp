#include "EnableInterrupt.h"
#include "DualVNH5019MotorShield.h"
#include "PID_v1.h"
#include "Motor.h"
#include "Sensor.h"
#include "Comms.h"

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
// double motorfactor = 0.99775;
double motorfactor = 0.99775;
boolean brakes = false;
double circumference = PI * 6;
double distance_cm;                //distance in cm that the robot need to move
double dist_between_wheels = 17.3; // in cm

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

double blocksTo_cm(double blocks)
{
    return blocks * 10;
}

//===== Movement =====
void moveFstopWall(double distToStop)
{
    // distToStop = 17;

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
        unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
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
        Serial.println(getDist2(get_curFiltered2()));
        if (getDist2(get_curFiltered2()) < distToStop)
        {
            brakes = true;
            break;
        }
        delay(delayms / (5 + 1));
        getSensorReading();
        Serial.println(getDist2(get_curFiltered2()));
        if (getDist2(get_curFiltered2()) < distToStop)
        {
            brakes = true;
            break;
        }
        delay(delayms / (5 + 1));
        getSensorReading();
        Serial.println(getDist2(get_curFiltered2()));
        if (getDist2(get_curFiltered2()) < distToStop)
        {
            brakes = true;
            break;
        }
        delay(delayms / (5 + 1));
        getSensorReading();
        Serial.println(getDist2(get_curFiltered2()));
        if (getDist2(get_curFiltered2()) < distToStop)
        {
            brakes = true;
            break;
        }
        delay(delayms / (5 + 1));
        getSensorReading();
        Serial.println(getDist2(get_curFiltered2()));
        if (getDist2(get_curFiltered2()) < distToStop)
        {
            brakes = true;
            break;
        }
        delay(delayms / (5 + 1));
        // if((int)getDist2(get_curFiltered2()) <= 20){
        //     brakes = true;
        // }
        unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
        Serial.print("Time taken (ms): ");
        Serial.println(pepe2);
    }
    md.setBrakes(400, 400);
}

int emergencyDistance = 10; //in cm

void moveF(double dist)
{
    dist = blocksTo_cm(dist);
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
    speedL = 350;
    speedR = speedL * motorfactor;
    // for(int i = 0; i<350 ; i+=20){
    //   md.setSpeeds(i*motorfactor,i);
    //   delay(10);
    // }
    md.setSpeeds(speedR * motorfactor, speedL);
    delay(delayms);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL && brakes == false)
    {
        // unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        // Serial.print(curTickR);
        // Serial.print(" ");
        // Serial.println(curTickL);
        PID1.Compute();
        PID2.Compute();
        md.setSpeeds(speedR * motorfactor, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        getSensorReading();
        delay(delayms);
        if ((getDist2(get_curFiltered2()) < emergencyDistance) || (getDist1(get_curFiltered1()) < emergencyDistance) || (getDist4(get_curFiltered4()) < emergencyDistance))
        {
            brakes = emergencyStop();
        }
        // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
        // Serial.print("Time taken (ms): ");
        // Serial.println(pepe2);
    }
    //    setTickLoop();
    md.setBrakes(400, 400);
}

// void moveB()
// {
//     speedL = 350;
//     speedR = speedL * motorfactor;
//     md.setSpeeds(-speedR, -speedL);
//     delay(delayms);
//     oldTickR = (double)TickR;
//     oldTickL = (double)TickL;
//     PIDInit();
//     brakes = false;

//     while (!brakes)
//     {
//         curTickR = TickR - oldTickR;
//         curTickL = TickL - oldTickL;
//         Serial.print(curTickR);
//         Serial.print(" ");
//         Serial.println(curTickL);
//         PID1.Compute();
//         PID2.Compute();
//         md.setSpeeds(-speedR * motorfactor, -speedL);
//         oldTickR += curTickR;
//         oldTickL += curTickL;
//         delay(delayms);
//     }
// }

void moveB(double dist)
{
    dist = blocksTo_cm(dist);
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
    speedL = 350;
    speedR = speedL * motorfactor;
    md.setSpeeds(-speedR * motorfactor, -speedL);
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
        md.setSpeeds(-speedR * motorfactor, -speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        delay(delayms);
    }
    md.setBrakes(400, 400);
}

// void turnL()
// {
//     speedL = 350;
//     speedR = speedL * motorfactor;
//     md.setSpeeds(speedR, speedL);
//     delay(delayms);
//     oldTickR = (double)TickR;
//     oldTickL = (double)TickL;
//     PIDInit();
//     brakes = false;

//     while (!brakes)
//     {
//         curTickR = TickR - oldTickR;
//         curTickL = TickL - oldTickL;
//         Serial.print(curTickR);
//         Serial.print(" ");
//         Serial.println(curTickL);
//         PID1.Compute();
//         PID2.Compute();
//         md.setSpeeds(speedR * motorfactor, -speedL);
//         oldTickR += curTickR;
//         oldTickL += curTickL;
//         delay(delayms);
//     }
// }

void turnL(double angle)
{
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = getTicksFromAngle(angle);
    //6600 - 720deg;
    speedL = 250;
    speedR = speedL * motorfactor;
    md.setSpeeds(speedR * motorfactor, -speedL);
    delay(delayms);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL)
    {
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        // Serial.print(curTickR);
        // Serial.print(" ");
        // Serial.println(curTickL);
        PID1.Compute();
        PID2.Compute();
        md.setSpeeds(speedR * motorfactor, -speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        if (targetTick - TickR < 150)
            md.setSpeeds(100 * motorfactor, -100);
        delay(delayms);
    }
    md.setBrakes(400, 400);
}

// void turnR()
// {
//     speedL = 350;
//     speedR = speedL * motorfactor;
//     md.setSpeeds(speedR, speedL);
//     delay(delayms);
//     oldTickR = (double)TickR;
//     oldTickL = (double)TickL;
//     PIDInit();
//     brakes = false;

//     while (!brakes)
//     {
//         curTickR = TickR - oldTickR;
//         curTickL = TickL - oldTickL;
//         Serial.print(curTickR);
//         Serial.print(" ");
//         Serial.println(curTickL);
//         PID1.Compute();
//         PID2.Compute();
//         md.setSpeeds(-speedR * motorfactor, speedL);
//         oldTickR += curTickR;
//         oldTickL += curTickL;
//         delay(delayms);
//     }
// }

void turnR(double angle)
{
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = getTicksFromAngle(angle);
    speedL = 250;
    speedR = speedL * motorfactor;
    md.setSpeeds(-speedR * motorfactor, speedL);
    delay(delayms);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL)
    {
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        // Serial.print(curTickR);
        // Serial.print(" ");
        // Serial.println(curTickL);
        PID1.Compute();
        PID2.Compute();
        md.setSpeeds(-speedR * motorfactor, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        if (targetTick - TickR < 150)
            md.setSpeeds(-100 * motorfactor, 100);
        delay(delayms);
    }
    md.setBrakes(400, 400);
}

void brake()
{
    md.setBrakes(400, 400);
    brakes = true;
}

double calibrationTolerence = 0.1;
// double calibrationTolerence = 0.6;
// double calibrationTolerence = 0.75;

void wallCalibrate()
{
    int readSample = 20;
    double sensorL_Readings[readSample];
    double sensorR_Readings[readSample];

    for (int i = 0; i < readSample; i++)
    {
        getSensorReading();
        sensorL_Readings[i] = getDist2(get_curFiltered2());
        sensorR_Readings[i] = getDist4(get_curFiltered4());
    }

    double sumL = 0;
    double sumR = 0;

    for (int i = 0; i < readSample; i++)
    {
        sumL = sumL + sensorL_Readings[i];
        sumR = sumR + sensorR_Readings[i];
    }

    double sensorL = sumL / readSample;
    double sensorR = sumR / readSample;

    Serial.println(sensorL);
    Serial.println(sensorR);

    double diff = sensorL - sensorR;
    Serial.println(diff);

    if ((diff > -(calibrationTolerence)) && (diff < calibrationTolerence))
    {
        return;
    }

    if (sensorL < sensorR) //if Sensor 2(L) nearer than Sensor 4(R)
    {
        Serial.print("CCW");
        CCW_Calibrate();
    }
    else if (sensorR < sensorL) //if Sensor 4(R) nearer than Sensor 2(L)
    {
        Serial.print("CW");
        CW_Calibrate();
    }
    md.setSpeeds(0, 0);
}

void CCW_Calibrate()
{
    int speed = 100;
    double readingsL;
    double readingsR;
    md.setSpeeds(speed * motorfactor, -speed);
    boolean calibrated = false;
    while (!calibrated)
    {
        getSensorReading();
        readingsL = getDist2(get_curFiltered2());
        readingsR = getDist4(get_curFiltered4());
        double diff = (readingsL - readingsR);
        Serial.print(diff);
        if ((diff > -(calibrationTolerence)) && (diff < calibrationTolerence)) //if diff between readings is close to 0
        {
            calibrated = true;
        }
        if (readingsR < readingsL) //sanity check so the robot doesn't keep turning if it goes past the midpoint
        {
            return;
        }
    }
}

void CW_Calibrate()
{
    int speed = 100;
    double readingsL;
    double readingsR;
    md.setSpeeds(-speed * motorfactor, speed);
    boolean calibrated = false;
    while (!calibrated)
    {
        getSensorReading();
        readingsL = getDist2(get_curFiltered2());
        readingsR = getDist4(get_curFiltered4());

        double diff = (readingsL - readingsR);
        Serial.print(diff);
        if ((diff > -(calibrationTolerence)) && (diff < calibrationTolerence)) //if diff between readings is close to 0
        {
            calibrated = true;
        }
        if (readingsL < readingsR) //sanity check so the robot doesn't keep turning if it goes past the midpoint
        {
            return;
        }
    }
}

void avoidObstacle90()
{
    double distToStop = 7;
    double distOffset = 0.2 * distToStop + 3;
    moveFstopWall(distToStop + distOffset);

    delay(100);
    turnR(90);
    delay(100);
    moveF(20);
    delay(100);
    turnL(90);
    delay(100);
    moveF(45);
    delay(100);
    turnL(90);
    delay(100);
    moveF(20);
    delay(100);
    turnR(90);
    delay(100);
    moveFstopWall(10);
}

void avoidObstacleDiag()
{
    double distToStop = 15;
    double distOffset = 0.2 * distToStop + 3;
    moveFstopWall(distToStop + distOffset);

    turnR(45);
    delay(100);
    moveF(41.72 + 4.75);
    delay(100);
    turnL(90);
    delay(100);
    moveF(41.72 + 4.75);
    delay(100);
    turnR(45);
    delay(100);
    moveFstopWall(10);
}

bool emergencyStop()
{
    md.setBrakes(400, 400);
    return true;
}