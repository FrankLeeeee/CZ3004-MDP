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
// int delayms = 20;
int delayms = 10;
double motorfactorL = 1; //6.2V 1.013
double motorfactorR = 1; //6.14V 0.993   6.13V 0.95       0.99025
double motorfactor = 1.05;
double motorfactorB = 1; // 0.99775
bool brakes = false;
double circumference = PI * 6;
double distance_cm;                  //distance in cm that the robot need to move
double dist_between_wheels = 17.315; // in cm

//===== PID =====
//10, 1, 0.25
// double kp = 10, ki = 1, kd = 0.15;
double kp = 14.5, ki = 20, kd = 0;
PID PID1(&curTickL, &speedL, &curTickR, kp, ki, kd, DIRECT);
// PID PID2(&curTickL, &speedL, &curTickR, kp, ki, kd, DIRECT);

double kp_l = 14.5, ki_l = 18, kd_l = 0;
PID PIDL(&curTickL, &speedL, &curTickR, kp_l, ki_l, kd_l, DIRECT);

double kp_r = 14.5, ki_r = 11, kd_r = 0;
PID PIDR(&curTickL, &speedL, &curTickR, kp_r, ki_r, kd_r, DIRECT);

void PIDInit()
{
    PID1.SetOutputLimits(-400, 400);
    PID1.SetSampleTime(delayms);
    PID1.SetMode(AUTOMATIC);
    // PID2.SetOutputLimits(-400, 400);
    // PID2.SetSampleTime(delayms);
    // PID2.SetMode(AUTOMATIC);
    PIDL.SetOutputLimits(-400, 400);
    PIDL.SetSampleTime(delayms);
    PIDL.SetMode(AUTOMATIC);
    PIDR.SetOutputLimits(-400, 400);
    PIDR.SetSampleTime(delayms);
    PIDR.SetMode(AUTOMATIC);
}

//===== Conversion Functions =====

double calcTickFromDist(double dist)
{
    return ((0.94 * dist) * 1124.5) / circumference;
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
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
    // speedL = 300;
    speedL = 300;
    speedR = speedL * motorfactor;
    md.setSpeeds(speedR, speedL);
    delay(delayms + 3);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL && brakes == false)
    {
        //  unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        PID1.Compute();
        md.setSpeeds(speedR, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        // Serial.print(curTickL);
        // Serial.print(" ");
        // Serial.println(curTickR);
        getSensorReading();
        getSensorReading();
        getSensorReading();
        // Remember to re-enable after fastest path
        if (((getDist2(get_curFiltered2()) < emergencyDistance) && getDist2(get_curFiltered2()) > 0)        )//     || ((getDist1(get_curFiltered1()) < emergencyDistance) && getDist1(get_curFiltered1()) > 0) || ((getDist4(get_curFiltered4()) < emergencyDistance) && getDist4(get_curFiltered4()) > 0))
        {
            brakes = emergencyStop();
        }
        delay(delayms);
        // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
        // Serial.print("Time taken (ms): ");
        // Serial.println(pepe2);
    }
    md.setBrakes(400, 400);
    // (getDist2(get_curFiltered2()) < emergencyDistance) && getDist2(get_curFiltered2()) > 0) && 
    if (((getDist1(get_curFiltered1()) < emergencyDistance+3) && getDist1(get_curFiltered1()) > 0) && ((getDist4(get_curFiltered4()) < emergencyDistance+3) && getDist4(get_curFiltered4()) > 0))
    {
        calibrateProc();
    }
}

void moveFslow(double dist)
{
    dist = blocksToCm(dist);
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
    targetTick = calcTickFromDist(dist);
    speedL = 100;
    speedR = speedL;
    md.setSpeeds(speedR, speedL);
    delay(delayms + 3);
    oldTickR = (double)TickR;
    oldTickL = (double)TickL;
    PIDInit();
    brakes = false;

    while (targetTick > TickR && targetTick > TickL && brakes == false)
    {
        curTickR = TickR - oldTickR;
        curTickL = TickL - oldTickL;
        md.setSpeeds(speedR, speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        getSensorReading();
        getSensorReading();
        getSensorReading();
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
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
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
        PID1.Compute();
        md.setSpeeds(-speedR * motorfactor, -speedL);
        oldTickR += curTickR;
        oldTickL += curTickL;
        delay(delayms);
    }
    md.setBrakes(400, 400);
}

void turnL(double angle)
{
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
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
            PIDL.Compute();
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
    TickL = TickR = curTickL = curTickR = oldTickL = oldTickR = 0;
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
            PIDR.Compute();
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

double calibrationTolerence = 0.3;
double calibrationBase = -0.15;
double calibrationToleranceCCW = 0.3;
double calibrationBaseCCW = -0.15;
int recursionCount = 5;

void wallCalibrate()
{
    double sensorL = getDist1(getAvg1());
    double sensorR = getDist4(getAvg4());

    double diff = sensorL - sensorR; //Sensor L = sensor 1, Sensor R = sensor 4

    if ((diff > calibrationBase) && (diff < (calibrationBase + calibrationTolerence)))
    {
        return;
    }
    else if (diff < 0) //if Sensor 2(L) nearer than Sensor 4(R)
    {
        // Serial.print("CCW");
        CCW_Calibrate();
    }
    else if (diff > 0) //if Sensor 4(R) nearer than Sensor 2(L)
    {
        // Serial.print("CW");
        CW_Calibrate();
    }
    md.setBrakes(400, 400);
    recursionCount--;
    if (recursionCount == 0)
    {
        return;
    }
    wallCalibrate();
    // wallCalibrate();
    recursionCount = 5;
}

void CCW_Calibrate()
{
    int speed = 70;
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

void CW_Calibrate()
{
    int speed = 70;
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

double distTol = 0.4;
double distTolBase = -0.4;
int calibrationDist = 10; //cm from wall

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
        moveFstopWall(calibrationDist-2);
    }
    else if (FL < calibrationDist && FR < calibrationDist)
    {
        moveBstopWall(calibrationDist-2);
    }
    md.setBrakes(400, 400);
    recursionCount--;
    if (recursionCount == 0)
    {
        return;
    }
    delay(100);
    wallDistCalibrate();
    recursionCount = 5;
}

void calibrateProc()
{
    wallCalibrate();
    wallDistCalibrate();
    moveFslow(0.45);
    wallCalibrate();
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