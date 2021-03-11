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
void moveFslow(double dist);
void moveFstopWall(double distToStop);
void moveBstopWall(double distToStop);
void moveB(double dist);
void turnL(double angle);
void turnR(double angle);
void brake();
void wallCalibrate();
void CW_Calibrate();
void CCW_Calibrate();
void calibrateProc();
void avoidObstacle90();
void avoidObstacleDiag();
bool emergencyStop();

#endif