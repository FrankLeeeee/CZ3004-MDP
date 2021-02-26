#ifndef Sensor_H
#define Sensor_H

void getSensorReading();
void printSensorReading();
double filter(double volt, double oldVal);
double getDist1(double x);
double getDist2(double x);
double getDist3(double x);
double getDist4(double x);
double getDist5(double x);
double getDist6(double x);

#endif