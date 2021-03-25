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
// double curTickR = 0, curTickL = 0, oldTickR = 0, oldTickL = 0;
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
// int delayms = 20;
int delayms = 10;
double motorfactorL = 1; //6.2V 1.013
double motorfactorR = 1; //6.14V 0.993   6.13V 0.95       0.99025
double motorfactor = 1;
double motorfactorB = 1; // 0.99775
bool brakes = false;
double circumference = PI * 6;
double distance_cm;                  //distance in cm that the robot need to move
double dist_between_wheels = 17.315; // in cm

//===== PID =====
double kp = 14.5, ki = 20, kd = 0;
// double kp = 18, ki = 50, kd = 0;
// double kp2 = 15, ki2 = 50, kd2 = 0;
// PID PID1(&curTickL, &speedL, &curTickR, kp, ki, kd, DIRECT);

// double kp_l = 14.5, ki_l = 18, kd_l = 0;
// PID PIDL(&curTickL, &speedL, &curTickR, kp_l, ki_l, kd_l, DIRECT);

// double kp_r = 14.5, ki_r = 11, kd_r = 0;
// PID PIDR(&curTickL, &speedL, &curTickR, kp_r, ki_r, kd_r, DIRECT);
// double kp = 5, ki = 0, kd = 0;
double targetTickDiff = 0.4;
double tickDiff;

PID PIDZ(&tickDiff, &speedR, &targetTickDiff, kp, ki, kd, DIRECT);
// PID PIDZ2(&tickDiff, &speedL, &targetTickDiff, kp2, ki2, kd2, REVERSE);
// PID PIDZS(&tickDiff, &speedR, &targetTickDiff , kp, ki, kd, DIRECT);
PID PIDZL(&tickDiff, &speedR, &targetTickDiff, kp, ki, kd, DIRECT);
PID PIDZR(&tickDiff, &speedR, &targetTickDiff, kp, ki, kd, DIRECT);

void PIDInit()
{
    // PID1.SetOutputLimits(-400, 400);
    // PID1.SetSampleTime(delayms);
    // PID1.SetMode(AUTOMATIC);
    // PIDL.SetOutputLimits(-400, 400);
    // PIDL.SetSampleTime(delayms);
    // PIDL.SetMode(AUTOMATIC);
    // PIDR.SetOutputLimits(-400, 400);
    // PIDR.SetSampleTime(delayms);
    // PIDR.SetMode(AUTOMATIC);
    // PID PIDZ(&tickDiff, &speedR, &targetTickDiff, kp, ki, kd, DIRECT);
    // PID PIDZL(&tickDiff, &speedR, &targetTickDiff, kp, ki, kd, DIRECT);
    // PID PIDZR(&tickDiff, &speedR, &targetTickDiff, kp, ki, kd, DIRECT);
    PIDZ.SetOutputLimits(-400, 400);
    PIDZ.SetSampleTime(delayms);
    PIDZ.SetMode(AUTOMATIC);
    // PIDZ2.SetOutputLimits(-400, 400);
    // PIDZ2.SetSampleTime(delayms);
    // PIDZ2.SetMode(AUTOMATIC);
    PIDZL.SetOutputLimits(-400, 400);
    PIDZL.SetSampleTime(delayms);
    PIDZL.SetMode(AUTOMATIC);
    PIDZR.SetOutputLimits(-400, 400);
    PIDZR.SetSampleTime(delayms);
    PIDZR.SetMode(AUTOMATIC);
}

//===== Conversion Functions =====

double calcTickFromDist(double dist)
{
    return ((0.965 * dist) * 1124.5) / circumference;
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
int emergencyDistance = 8; //in cm

void moveF(double dist)
{
    dist = blocksToCm(dist);
    TickL = TickR = 0;
    // volatile long TickL = 0, TickR = 0;
    double curTickR = 0, curTickL = 0, oldTickR = 0, oldTickL = 0;
    // curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
    // speedL = 300;
    speedL = 300;
    speedR = speedL * motorfactor;
    md.setSpeeds(speedR, speedL);
    delay(delayms + 3);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    brakes = false;
    PIDInit();

    while (targetTick > TickR && targetTick > TickL && brakes == false)
    {
        //  unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        // PID1.Compute();
        PIDZ.Compute();
        // PIDZ2.Compute();
        // Serial.print(speedL);
        // Serial.print(" ");
        // Serial.println(speedR);
        md.setSpeeds(speedR * motorfactor, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        // Serial.print(curTickL);
        // Serial.print(" ");
        // Serial.println(curTickR);
        // tickDiff = curTickL - curTickR;
        tickDiff = curTickR - curTickL;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        // Remember to re-enable after fastest path
        if (((getDist2(get_curFiltered2()) < emergencyDistance) && getDist2(get_curFiltered2()) > 0)) //     || ((getDist1(get_curFiltered1()) < emergencyDistance) && getDist1(get_curFiltered1()) > 0) || ((getDist4(get_curFiltered4()) < emergencyDistance) && getDist4(get_curFiltered4()) > 0))
        {
            brakes = emergencyStop();
        }
        delay(delayms);
        // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
        // Serial.print("Time taken (ms): ");
        // Serial.println(pepe2);
    }
    md.setBrakes(400,400);
    // (getDist2(get_curFiltered2()) < emergencyDistance) && getDist2(get_curFiltered2()) > 0) &&
    if (((getDist2(get_curFiltered2()) < emergencyDistance) && getDist2(get_curFiltered2()) > 0) && ((getDist1(get_curFiltered1()) < emergencyDistance) && getDist1(get_curFiltered1()) > 0) && ((getDist4(get_curFiltered4()) < emergencyDistance) && getDist4(get_curFiltered4()) > 0))
    {
        calibrateProc();
    }
}

// void moveFslow(double dist)
// {
//     dist = blocksToCm(dist);
//     double curTickR = 0, curTickL = 0, oldTickR = 0, oldTickL = 0;
//     TickL = TickR = 0; //curTickL = curTickR = oldTickL = oldTickR = 0;
//     // volatile long TickL = 0, TickR = 0;
//     targetTick = calcTickFromDist(dist);
//     speedL = 100;
//     speedR = speedL;
//     md.setSpeeds(speedR, speedL);
//     Serial.print(speedL);
//     Serial.print(" ");
//     Serial.println(speedR);
//     delay(delayms + 3);
//     oldTickR = (double)TickR;
//     oldTickL = (double)TickL;
//     brakes = false;
//     PIDInit();

//     while (targetTick > TickR && targetTick > TickL && brakes == false)
//     {
//         curTickR = TickR - oldTickR;
//         curTickL = TickL - oldTickL;
//         tickDiff = curTickR - curTickL;
//         // Serial.print(TickL);
//         // Serial.print(" ");
//         // Serial.print(TickR);
//         // Serial.print(" ");
//         // Serial.print(oldTickL);
//         // Serial.print(" ");
//         // Serial.print(oldTickR);
//         // Serial.print(" ");
//         // Serial.print(curTickL);
//         // Serial.print(" ");
//         // Serial.println(curTickR);
//         // // PID1.Compute();
//         // // PIDZ.Compute();
//         // Serial.print(speedL);
//         // Serial.print(" ");
//         // Serial.println(speedR);
//         md.setSpeeds(speedR, speedL);
//         oldTickR += curTickR;
//         oldTickL += curTickL;
//         getSensorReading();
//         getSensorReading();
//         getSensorReading();
//         delay(delayms);
//     }
//     md.setBrakes(400, 400);
// }

void moveFstopWall(double distToStop)
{
    speedL = 100;
    speedR = speedL;
    md.setSpeeds(speedR, speedL);
    brakes = false;
    while (!brakes)
    {
        getSensorReading();
        if (getDist2(get_curFiltered2()) < distToStop)
        {
            brakes = true;
            break;
        }
    }
    md.setBrakes(400, 400);
}

void moveBstopWall(double distToStop)
{
    speedL = 100;
    speedR = speedL;
    md.setSpeeds(-speedR, -speedL);
    brakes = false;

    while (!brakes)
    {
        getSensorReading();
        if (getDist2(get_curFiltered2()) >= distToStop)
        {
            brakes = true;
            break;
        }
    }
    md.setBrakes(400, 400);
}

void moveB(double dist)
{
    dist = blocksToCm(dist);
    double curTickR = 0, curTickL = 0, oldTickR = 0, oldTickL = 0;
    // volatile long TickL = 0, TickR = 0;
    TickL = TickR = 0; //curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
    speedL = 300;
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
        tickDiff = curTickR - curTickL;
        // PID1.Compute();
        PIDZ.Compute();
        md.setSpeeds(-speedR * motorfactor, -speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        delay(delayms);
    }
    md.setBrakes(400, 400);
}

void turnL(double angle)
{
    double curTickR = 0, curTickL = 0, oldTickR = 0, oldTickL = 0;
    // volatile long TickL = 0, TickR = 0;
    TickL = TickR = 0;                     //curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = getTicksFromAngle(angle); // -1.8
    speedL = 300;
    speedR = speedL * motorfactorL;
    md.setSpeeds(speedR, -speedL);
    delay(delayms + 3);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL)
    {
        if (targetTick - TickR < 125)
            md.setSpeeds(100 * motorfactorL, -100);
        else
        {
            curTickR = TickR - oldTickR;
            curTickL = TickL - oldTickL;
            tickDiff = curTickR - curTickL;
            // PID1.Compute();
            PIDZL.Compute();
            // PIDL.Compute();
            md.setSpeeds(speedR, -speedL);
            oldTickR += curTickR;
            oldTickL += curTickL;
            delay(delayms);
        }
        getSensorReading();
        getSensorReading();
        getSensorReading();
    }
    md.setBrakes(400, 400);
}

void turnR(double angle)
{
    double curTickR = 0, curTickL = 0, oldTickR = 0, oldTickL = 0;
    // volatile long TickL = 0, TickR = 0;
    TickL = TickR = 0;                     //curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = getTicksFromAngle(angle); //-2.1
    speedL = 300;
    speedR = speedL * motorfactorR;
    md.setSpeeds(-speedR, speedL);
    delay(delayms + 3);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL)
    {
        if (targetTick - TickR < 125)
            md.setSpeeds(-100 * motorfactorR, 100);
        else
        {
            curTickR = TickR - oldTickR;
            curTickL = TickL - oldTickL;
            tickDiff = curTickR - curTickL;
            PIDZR.Compute();
            md.setSpeeds(-speedR, speedL);
            oldTickR += curTickR;
            oldTickL += curTickL;
            delay(delayms);
        }
        getSensorReading();
        getSensorReading();
        getSensorReading();
    }
    md.setBrakes(400, 400);
}

void brake()
{
    md.setBrakes(400, 400);
    brakes = true;
}

double calibrationTolerence;
double calibrationBase;
double calibrationToleranceCCW;
double calibrationBaseCCW;
int recursionCount = 10;

void wallCalibrate(int side) // side 0: Front, 1: Right
{
    double sensorL;
    double sensorR;
    if (side == 0)
    {
        sensorL = getDist1(getAvg1());
        sensorR = getDist4(getAvg4());
        calibrationTolerence = 0.3;
        calibrationBase = -0.2;
        calibrationToleranceCCW = 0.3;
        calibrationBaseCCW = -0.15;
    }
    else if (side == 1)
    {
        sensorL = getDist3(getAvg3());
        sensorR = getDist5(getAvg5());
        calibrationTolerence = 0.7;
        calibrationBase = -1.1;
        calibrationToleranceCCW = 0.7;
        calibrationBaseCCW = -0.8;
    }
    else
    {
        return;
    }

    double diff = sensorL - sensorR; //Sensor L = sensor 1, Sensor R = sensor 4

    if ((diff > calibrationBase) && (diff < (calibrationBase + calibrationTolerence)))
    {
        return;
    }
    else if (diff < 0) //if Sensor 2(L) nearer than Sensor 4(R)
    {
        // Serial.print("CCW");
        CCW_Calibrate(side);
    }
    else if (diff > 0) //if Sensor 4(R) nearer than Sensor 2(L)
    {
        // Serial.print("CW");
        CW_Calibrate(side);
    }
    md.setBrakes(400, 400);
    recursionCount--;
    if (recursionCount == 0)
    {
        return;
    }
    // delay(100);
    wallCalibrate(side);
    // wallCalibrate();
    recursionCount = 10;
}

void CCW_Calibrate(int side)
{
    int speed = 70;
    double readingsL;
    double readingsR;
    md.setSpeeds(speed, -speed);
    boolean calibrated = false;
    while (!calibrated)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        if (side == 0)
        {
            readingsL = getDist1(get_curFiltered1());
            readingsR = getDist4(get_curFiltered4());
        }
        else if (side == 1)
        {
            readingsL = getDist3(get_curFiltered3());
            readingsR = getDist5(get_curFiltered5());
        }
        else
        {
            return;
        }
        double diff = (readingsL - readingsR);
        // Serial.println(diff);
        if ((diff > calibrationBaseCCW) && (diff < (calibrationBaseCCW + calibrationToleranceCCW))) //if diff between readings is close to 0
        {
            calibrated = true;
        }
        if (readingsR < readingsL) //sanity check so the robot doesn't keep turning if it goes past the midpoint
        {
            return;
        }
    }
}

void CW_Calibrate(int side)
{
    int speed = 70;
    double readingsL;
    double readingsR;
    md.setSpeeds(-speed, speed);
    boolean calibrated = false;
    while (!calibrated)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        if (side == 0)
        {
            readingsL = getDist1(get_curFiltered1());
            readingsR = getDist4(get_curFiltered4());
        }
        else if (side == 1)
        {
            readingsL = getDist3(get_curFiltered3());
            readingsR = getDist5(get_curFiltered5());
        }
        else
        {
            return;
        }
        double diff = (readingsL - readingsR);
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

double distTol = 0.1;
double distTolBase = -0.1;
int calibrationDist = 6; //cm from wall

void wallDistCalibrate()
{
    for (int i = 0; i < 20; i++)
    {
        getSensorReading();
    }
    // double FC = getDist2(get_curFiltered2());
    double FL = getDist1(get_curFiltered1());
    double FR = getDist4(get_curFiltered4());

    if ((FL > calibrationDist + distTolBase && FL < calibrationDist + distTol) || (FL > calibrationDist + distTolBase && FL < calibrationDist + distTol))
    {
        return;
    }
    else if (FL > calibrationDist && FR > calibrationDist)
    {
        moveFstopWall(calibrationDist - 2);
    }
    else if (FL < calibrationDist && FR < calibrationDist)
    {
        moveBstopWall(calibrationDist - 2);
    }
    md.setBrakes(400, 400);
    recursionCount--;
    if (recursionCount == 0)
    {
        return;
    }
    delay(100);
    wallDistCalibrate();
    recursionCount = 10;
}

void calibrateProc()
{
    wallCalibrate(0);
    wallDistCalibrate();
    // moveFslow(0.45);
    wallCalibrate(0);
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