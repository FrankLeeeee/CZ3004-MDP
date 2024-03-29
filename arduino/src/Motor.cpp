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
double kp_l = 14.5, ki_l = 20, kd_l = 0;
double kp_r = 14.5, ki_r = 20, kd_r = 0;

double targetTickDiff = 0.35; // R minus L
double targetTickDiffL = 1.0; // R minus L
double targetTickDiffR = 1.0; // R minus L
double tickDiff;
double tickDiffL;
double tickDiffR;

PID PIDZ(&tickDiff, &speedR, &targetTickDiff, kp, ki, kd, DIRECT);
PID PIDZL(&tickDiffL, &speedR, &targetTickDiffL, kp_l, ki_l, kd_l, DIRECT);
PID PIDZR(&tickDiffR, &speedR, &targetTickDiffR, kp_r, ki_r, kd_r, DIRECT);

void PIDInit()
{

    PIDZ.SetOutputLimits(-400, 400);
    PIDZ.SetSampleTime(delayms);
    PIDZ.SetMode(AUTOMATIC);

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
    return ((0.955 * dist) * 1124.5) / circumference;
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
int emergencyDistance = 6; //in cm

void moveF(double dist)
{
    dist = blocksToCm(dist);
    TickL = TickR = 0;
    double curTickR = 0, curTickL = 0, oldTickR = 0, oldTickL = 0;
    // curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
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
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        PIDZ.Compute();
        md.setSpeeds(speedR * motorfactor, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;

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
    }
    md.setBrakes(400, 400);
}

void moveFstopWall(double distToStop)
{
    speedL = 100;
    speedR = speedL;
    md.setSpeeds(speedR, speedL);
    brakes = false;
    while (!brakes)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        getSensorReading();
        getSensorReading();
        if (getDist1(get_curFiltered1()) < distToStop || getDist4(get_curFiltered4()) < distToStop)
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
        getSensorReading();
        getSensorReading();
        getSensorReading();
        getSensorReading();
        if (getDist1(get_curFiltered1()) > distToStop || getDist4(get_curFiltered4()) > distToStop)
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
    TickL = TickR = 0;                            //curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = getTicksFromAngle(angle - 1.55); // -1.8
    speedL = -300;
    speedR = 300;
    md.setSpeeds(speedR, speedL);
    delay(delayms + 3);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL)
    {
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        tickDiffL = curTickR - curTickL;
        PIDZL.Compute();
        md.setSpeeds(speedR, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        delay(delayms);
        getSensorReading();
        getSensorReading();
        getSensorReading();
    }
    md.setBrakes(400, 400);
}

void turnR(double angle)
{
    double curTickR = 0, curTickL = 0, oldTickR = 0, oldTickL = 0;
    TickL = TickR = 0;                            //curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = getTicksFromAngle(angle - 1.35); //-2.1
    speedL = 300;
    speedR = 300;
    md.setSpeeds(-speedR, speedL);
    delay(delayms + 3);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL)
    {
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        tickDiffR = curTickR - curTickL;
        PIDZR.Compute();
        md.setSpeeds(-speedR, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        delay(delayms);
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
double calibrationTolerenceCW;
double calibrationBaseCW;
int recursionCount = 10;

void wallCalibrate(int side) // side 0: Front, 1: Right
{
    double sensorL;
    double sensorR;
    if (side == 0)
    {
        sensorL = getDist1(getAvg1());
        sensorR = getDist4(getAvg4());
        calibrationTolerence = 0.6;
        calibrationBase = -0.2;
        calibrationToleranceCCW = 0.3;
        calibrationBaseCCW = -0.1;
        calibrationTolerenceCW = 0.35;
        calibrationBaseCW = -0.1;
    }
    else if (side == 1)
    {
        sensorL = getDist3(getAvg3());
        sensorR = getDist5(getAvg5());
        calibrationTolerence = 1.1;
        calibrationBase = -1.5;
        calibrationToleranceCCW = 0.45;
        calibrationBaseCCW = -1.3;
        calibrationTolerenceCW = 0.35;
        calibrationBaseCW = -1.3;
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
        CCW_Calibrate(side);
    }
    else if (diff > 0) //if Sensor 4(R) nearer than Sensor 2(L)
    {
        CW_Calibrate(side);
    }
    md.setBrakes(400, 400);
    recursionCount--;
    if (recursionCount == 0)
    {
        return;
    }
    wallCalibrate(side);
    recursionCount = 10;
}

void CCW_Calibrate(int side)
{
    int speed = 75;
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
        if ((diff > calibrationBaseCCW) && (diff < (calibrationBaseCCW + calibrationToleranceCCW))) //if diff between readings is close to 0
        {
            calibrated = true;
        }
        if (readingsR + 5 < readingsL) //sanity check so the robot doesn't keep turning if it goes past the midpoint
        {
            return;
        }
    }
}

void CW_Calibrate(int side)
{
    int speed = 75;
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
        if ((diff > calibrationBaseCW) && (diff < (calibrationBaseCW + calibrationTolerenceCW))) //if diff between readings is close to 0
        {
            calibrated = true;
        }
        if (readingsL + 5 < readingsR) //sanity check so the robot doesn't keep turning if it goes past the midpoint
        {
            return;
        }
    }
}

double distTol = 0.3;
double distTolBase = -0.3;
int calibrationDist = 6; //cm from wall

void wallDistCalibrate()
{
    for (int i = 0; i < 20; i++)
    {
        getSensorReading();
    }
    double FL = getDist1(getAvg1());
    double FR = getDist4(getAvg4());
    if ((FL > calibrationDist + distTolBase && FL < calibrationDist + distTol) || (FR > calibrationDist + distTolBase && FR < calibrationDist + distTol))
    {
        return;
    }
    else if (FL > calibrationDist && FR > calibrationDist)
    {
        moveFstopWall(calibrationDist);
    }
    else if (FL < calibrationDist && FR < calibrationDist)
    {
        moveBstopWall(calibrationDist);
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