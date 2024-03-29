#ifndef Sensor_H
#define Sensor_H

double distToBlocksFront1(double dist);
double distToBlocksFront2(double dist);
double distToBlocksFront4(double dist);
double distToBlocksSide3(double dist);
double distToBlocksSide5(double dist);
double distToBlocksLR(double dist);
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
double medianOfMedians(double a[], int size);
void partialSort(int a[], int min, int max);
float getBlocksSR_float_front1(double dist);
float getBlocksSR_float_front2(double dist);
float getBlocksSR_float_front4(double dist);
float getBlocksSR_float_side3(double dist);
float getBlocksSR_float_side5(double dist);
int getBlocksSR_front1(double dist);
int getBlocksSR_front2(double dist);
int getBlocksSR_front4(double dist);
int getBlocksSR_side3(double dist);
int getBlocksSR_side5(double dist);
float getBlocksLR_float(double dist);
int getBlocksLR(double dist);

#endif