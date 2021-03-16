#ifndef Sensor_H
#define Sensor_H

double distToBlocks(double dist);
void sensorInit();
void getSensorReading();
double get_curFiltered1();
double get_curFiltered2();
double get_curFiltered3();
double get_curFiltered4();
double get_curFiltered5();
double get_curFiltered6();
void printSensorReading();
void printSensorBlocks();
double filter(double volt, double oldVal);
double getDist1(double x);
double getDist2(double x);
double getDist3(double x);
double getDist4(double x);
double getDist5(double x);
double getDist6(double x);
double getAvg1();
double getAvg2();
double getAvg3();
double getAvg4();
double getAvg5();
double getAvg6();
int getBlocksSR(double dist);
int getBlocksLR(double dist);

#endif