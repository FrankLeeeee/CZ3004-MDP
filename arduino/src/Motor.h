#ifndef Motor_H
#define Motor_H

void EncoderInit();
void countTickR();
void countTickL();
void PIDInit();
void PIDTInit();
double calcTickFromDist(double dist);
double getTicksFromAngle(double angle);
double blocksToCm(double blocks);
void moveFstopWall();
void moveF(double dist);
// void moveB();
void moveB(double dist);
//void turnL();
void turnL(double angle);
//void turnR();
void turnR(double angle);
void brake();
void wallCalibrate();
void CW_Calibrate();
void CCW_Calibrate();
void avoidObstacle90();
void avoidObstacleDiag();
bool emergencyStop();

#endif