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
void moveF(double dist);
// void moveFslow(double dist);
void moveFstopWall(double distToStop);
void moveBstopWall(double distToStop);
void moveB(double dist);
void turnL(double angle);
void turnR(double angle);
void brake();
void wallCalibrate(int side);
void CW_Calibrate(int side);
void CCW_Calibrate(int side);
void calibrateProc();
void avoidObstacle90();
void avoidObstacleDiag();
bool emergencyStop();

#endif