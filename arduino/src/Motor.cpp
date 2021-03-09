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
double motorfactorL = 1.01;    //6.2V 1.013
//double motorfactorL = 1.03; //1.018, 6.19V not bad 
double motorfactorR = 0.99025; //6.14V 0.993   6.13V 0.95       0.99025
//double motorfactorR = 1; //0.990008 6.19V not bad
double motorfactor = 0.9975;    // 0.99775
//0.99655 6.05V
// double motorfactor = 0.998;
// double motorfactor = 1;
bool brakes = false;
double circumference = PI * 6;
double distance_cm;                  //distance in cm that the robot need to move
double dist_between_wheels = 17.315; // in cm
// double dist_between_wheels = 17.1; // in cm

//dist_between_wheels = 17.315, 6.18V, motorfactor = 0.9975;

//===== PID =====
//10, 1, 0.25
double kp = 10, ki = 1, kd = 0.25;
PID PID1(&curTickR, &speedR, &curTickL, kp, ki, kd, DIRECT);
PID PID2(&curTickL, &speedL, &curTickR, kp, ki, kd, DIRECT);

double kp_t = 1, ki_t = 0.1, kd_t = 0.25;
PID PIDT1(&curTickR, &speedR, &curTickL, kp_t, ki_t, kd_t, DIRECT);
PID PIDT2(&curTickL, &speedL, &curTickR, kp_t, ki_t, kd_t, DIRECT);

void PIDInit()
{
    PID1.SetOutputLimits(-400, 400);
    PID1.SetSampleTime(delayms);
    PID1.SetMode(AUTOMATIC);
    PID2.SetOutputLimits(-400, 400);
    PID2.SetSampleTime(delayms);
    PID2.SetMode(AUTOMATIC);
}

void PIDTInit()
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
    return ((1 * dist) * 1124.5) / circumference;
}

double getTicksFromAngle(double angle)
{
    return ((dist_between_wheels / 6) * 1124.5) * (angle / 360);
}

double blocksToCm(double blocks)
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
        md.setSpeeds(speedR, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        getSensorReading();
        // Serial.println(getDist2(get_curFiltered2()));
        if (getDist2(get_curFiltered2()) < distToStop)
        {
            brakes = true;
            break;
        }
        delay(delayms / (5 + 1));
        getSensorReading();
        // Serial.println(getDist2(get_curFiltered2()));
        if (getDist2(get_curFiltered2()) < distToStop)
        {
            brakes = true;
            break;
        }
        delay(delayms / (5 + 1));
        getSensorReading();
        // Serial.println(getDist2(get_curFiltered2()));
        if (getDist2(get_curFiltered2()) < distToStop)
        {
            brakes = true;
            break;
        }
        delay(delayms / (5 + 1));
        getSensorReading();
        // Serial.println(getDist2(get_curFiltered2()));
        if (getDist2(get_curFiltered2()) < distToStop)
        {
            brakes = true;
            break;
        }
        delay(delayms / (5 + 1));
        getSensorReading();
        // Serial.println(getDist2(get_curFiltered2()));
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

int emergencyDistance = 11; //in cm

void moveF(double dist)
{
    dist = blocksToCm(dist);
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
    speedL = 380;
    speedR = speedL * motorfactor;
    // for(int i = 0; i<350 ; i+=20){
    //   md.setSpeeds(i*motorfactor,i);
    //   delay(10);
    // }
    md.setSpeeds(speedR, speedL);
    delay(delayms+3);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL && brakes == false)
    {
        //  unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        // Serial.print(curTickR);
        // Serial.print(" ");
        // Serial.println(curTickL);
        PID1.Compute();
        PID2.Compute();
        md.setSpeeds(speedR, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        if (((getDist2(get_curFiltered2()) < emergencyDistance) && getDist2(get_curFiltered2()) > 0) || ((getDist1(get_curFiltered1()) < emergencyDistance) && getDist1(get_curFiltered1()) > 0) || ((getDist4(get_curFiltered4()) < emergencyDistance) && getDist4(get_curFiltered4()) > 0))
        {
            brakes = emergencyStop();
        }
        delay(delayms);
        // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
        // Serial.print("Time taken (ms): ");
        // Serial.println(pepe2);
    }
    md.setBrakes(400, 400);
    if(((getDist2(get_curFiltered2()) < emergencyDistance+5) && getDist2(get_curFiltered2()) > 0) && ((getDist1(get_curFiltered1()) < emergencyDistance+5) && getDist1(get_curFiltered1()) > 0) && ((getDist4(get_curFiltered4()) < emergencyDistance+5) && getDist4(get_curFiltered4()) > 0))
    {
        wallCalibrate();
    }
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
    dist = blocksToCm(dist);
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
    speedL = 350;
    speedR = speedL * motorfactor;
    md.setSpeeds(-speedR, -speedL);
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
    // delayms = delayms / 4;
    // TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    TickL = TickR = 0;
    targetTick = getTicksFromAngle(angle+3.5);
    speedL = 250;
    speedR = 250 * motorfactorL;
    md.setSpeeds(speedR, -speedL);
    // delay(delayms);
    // oldTickR = (double)TickR;
    // oldTickL = (double)TickL;
    // PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL)
    {
        // curTickR = TickR - oldTickR;
        // curTickL = TickL - oldTickL;
        // Serial.print(curTickR);
        // Serial.print(" ");
        // Serial.println(curTickL);
        // PID1.Compute();
        // PID2.Compute();
        // md.setSpeeds(speedR * motorfactor, -speedL);
        // oldTickR += curTickR;
        // oldTickL += curTickL;
        if (targetTick - TickR < 125)
            md.setSpeeds(100 * motorfactorL, -100);
        // delay(delayms);
        getSensorReading();
    }
    md.setBrakes(400, 400);
    // delayms = delayms * 4;
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
    // // TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    TickL = TickR = 0;
    targetTick = getTicksFromAngle(angle+5.1);
    speedL = 250;
    speedR = speedL * motorfactorR;
    md.setSpeeds(-speedR, speedL);
    // delay(delayms);
    // oldTickR = (double)TickR;
    // oldTickL = (double)TickL;
    // PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL)
    {
        // curTickR = TickR - oldTickR;
        // curTickL = TickL - oldTickL;
        // Serial.print(curTickR);
        // Serial.print(" ");
        // Serial.println(curTickL);
        // PID1.Compute();
        // PID2.Compute();
        // md.setSpeeds(-speedR, speedL);
        // oldTickR += curTickR;
        // oldTickL += curTickL;
        if (targetTick - TickR < 125)
            md.setSpeeds(-100 * motorfactorR, 100);
        // delay(delayms);
        getSensorReading();
    }
    md.setBrakes(400, 400);
    // delayms = delayms / 4;


    //TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;


    // TickL = TickR = 0;
    // targetTick = getTicksFromAngle(angle);
    // speedL = 250 * motorfactorR;
    // speedR = 250;
    // md.setSpeeds(-speedL, speedR);
    // // delay(delayms);
    // // oldTickR = (double)TickR;
    // // oldTickL = (double)TickL;
    // // PIDInit();
    // brakes = false;

    // while (targetTick > TickR && targetTick > TickL)
    // {
    //     // curTickR = TickR - oldTickR;
    //     // curTickL = TickL - oldTickL;
    //     // Serial.print(curTickR);
    //     // Serial.print(" ");
    //     // Serial.println(curTickL);
    //     // PID1.Compute();
    //     // PID2.Compute();
    //     // md.setSpeeds(speedR * motorfactor, -speedL);
    //     // oldTickR += curTickR;
    //     // oldTickL += curTickL;
    //     if (targetTick - TickL < 150)
    //         md.setSpeeds(100 * motorfactorR, -100);
    //     // delay(delayms);
    //     getSensorReading();
    // }
    // md.setBrakes(400, 400);
    // // delayms = delayms * 4;
}

void brake()
{
    md.setBrakes(400, 400);
    brakes = true;
}

double calibrationTolerence = 0.4;
// double calibrationTolerence = 0.6;
// double calibrationTolerence = 0.75;
// double sensorDiff = 0;
double calibrationBase = -0.2;
int recursionCount = 5;

void wallCalibrate()
{
    double sensorL = getDist1(getAvg1());
    double sensorR = getDist4(getAvg4());

    // Serial.println(sensorL);
    // Serial.println(sensorR);

    double diff = sensorL - sensorR; //Sensor L = sensor 1, Sensor R = sensor 4
    // Serial.println(diff);

    if ((diff > calibrationBase) && (diff < (calibrationBase + calibrationTolerence)))
    {
        // Serial.print(getDist1(getAvg1()));
        // Serial.print(" ");
        // Serial.println(getDist4(getAvg4()));
        return;
    }

    if (sensorL < sensorR) //if Sensor 2(L) nearer than Sensor 4(R)
    {
        // Serial.print("CCW");
        CCW_Calibrate();
    }
    else if (sensorR < sensorL) //if Sensor 4(R) nearer than Sensor 2(L)
    {
        // Serial.print("CW");
        CW_Calibrate();
    }
    md.setBrakes(400, 400);
    recursionCount--;
    if(recursionCount == 0){
        return;
    }
    wallCalibrate();
    wallCalibrate();
    recursionCount = 5;
}

void CCW_Calibrate()
{
    int speed = 80;
    double readingsL;
    double readingsR;
    md.setSpeeds(speed * motorfactor, -speed);
    boolean calibrated = false;
    while (!calibrated)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        readingsL = getDist1(get_curFiltered1());
        readingsR = getDist4(get_curFiltered4());
        double diff = (readingsL - readingsR);
        // Serial.print(readingsL);
        // Serial.print(" ");
        // Serial.print(readingsR);
        // Serial.print(" ");
        // Serial.println(diff);
        if ((diff > calibrationBase) && (diff < (calibrationBase + calibrationTolerence))) //if diff between readings is close to 0
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
    int speed = 80;
    double readingsL;
    double readingsR;
    md.setSpeeds(-speed * motorfactor, speed);
    boolean calibrated = false;
    while (!calibrated)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        readingsL = getDist1(get_curFiltered1());
        readingsR = getDist4(get_curFiltered4());

        double diff = (readingsL - readingsR);
        // Serial.print(readingsL);
        // Serial.print(" ");
        // Serial.print(readingsR);
        // Serial.print(" ");
        // Serial.println(diff);
        if ((diff > calibrationBase) && (diff < (calibrationBase + calibrationTolerence))) //if diff between readings is close to 0
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