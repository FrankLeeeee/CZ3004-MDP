#ifndef Motor_H
#define Motor_H

void EncoderInit();
void countTickR();
void countTickL();
void PIDInit();
double calcTickFromDist(double dist);
double getTicksFromAngle(double angle);
double blocksTo_cm(double blocks);
void moveFstopWall();
void moveF(double dist);
// void moveB();
void moveB(double dist);
//void turnL();
void turnL(double angle);
//void turnR();
void turnR(double angle);
void brake();
void setTickLoop();
void wallCalibrate();
void CW_Calibrate();
void CCW_Calibrate();
void avoidObstacle90();
void avoidObstacleDiag();

#endif