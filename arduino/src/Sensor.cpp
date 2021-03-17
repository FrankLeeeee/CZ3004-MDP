#include "SharpIR.h"
#include "Motor.h"
#include "Sensor.h"
#include "Comms.h"
#include "math.h"
#include "string.h"

//===== Sensors ======
#define IRPin A0
#define IRPin2 A1
#define IRPin3 A2
#define IRPin4 A3
#define IRPin5 A4
#define IRPin6 A5

//===== Sensor Variables =====

double curFiltered1, curFiltered2, curFiltered3, curFiltered4, curFiltered5, curFiltered6;
double oldFiltered1 = -1, oldFiltered2 = -1, oldFiltered3 = -1, oldFiltered4 = -1, oldFiltered5 = -1, oldFiltered6 = -1;
double V1, V2, V3, V4, V5, V6;

//=====Conversion Functions=====

// int distToBlocks(double dist)
// {
//     return (int)((dist + 5) / 10);
// }

double distToBlocks(double dist)
{
    double blocks = ((dist + 5) / 10);
    if ((int)blocks == 2)
    {
        blocks = ((dist + 7.5) / 10);
    }
    return blocks;
}

//===== Sensors =====

void sensorInit()
{
    V1 = analogRead(A0); // Read voltage
    V2 = analogRead(A1);
    V3 = analogRead(A2);
    V4 = analogRead(A3);
    V5 = analogRead(A4);
    V6 = analogRead(A5);

    if (oldFiltered1 == -1) // sanity check for t=0
        oldFiltered1 = V1;
    curFiltered1 = filter(V1, oldFiltered1); // Exponential filter
    oldFiltered1 = curFiltered1;             // get old value

    if (oldFiltered2 == -1) // sanity check for t=0
        oldFiltered2 = V2;
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2;             // get old value

    if (oldFiltered3 == -1) // sanity check for t=0
        oldFiltered3 = V3;
    curFiltered3 = filter(V3, oldFiltered3); // Exponential filter
    oldFiltered3 = curFiltered3;             // get old value

    if (oldFiltered4 == -1) // sanity check for t=0
        oldFiltered4 = V4;
    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
    oldFiltered4 = curFiltered4;             // get old value

    if (oldFiltered5 == -1) // sanity check for t=0
        oldFiltered5 = V5;
    curFiltered5 = filter(V5, oldFiltered5); // Exponential filter
    oldFiltered5 = curFiltered5;             // get old value

    if (oldFiltered6 == -1) // sanity check for t=0
        oldFiltered6 = V6;
    curFiltered6 = filter(V6, oldFiltered6); // Exponential filter
    oldFiltered6 = curFiltered6;             // get old value
}

void getSensorReading()
{
    // unsigned long pepe1 = millis(); // takes the time before the loop on the library begins

    //Change according to pin (A0 = PS1, A1 = PS2, etc)
    V1 = analogRead(A0); // Read voltage
    V2 = analogRead(A1);
    V3 = analogRead(A2);
    V4 = analogRead(A3);
    V5 = analogRead(A4);
    V6 = analogRead(A5);

    // if (oldFiltered1 == -1) // sanity check for t=0
    //     oldFiltered1 = V1;
    curFiltered1 = filter(V1, oldFiltered1); // Exponential filter
    oldFiltered1 = curFiltered1;             // get old value

    // if (oldFiltered2 == -1) // sanity check for t=0
    //     oldFiltered2 = V2;
    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2;             // get old value

    // if (oldFiltered3 == -1) // sanity check for t=0
    //     oldFiltered3 = V3;
    curFiltered3 = filter(V3, oldFiltered3); // Exponential filter
    oldFiltered3 = curFiltered3;             // get old value

    // if (oldFiltered4 == -1) // sanity check for t=0
    //     oldFiltered4 = V4;
    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
    oldFiltered4 = curFiltered4;             // get old value

    // if (oldFiltered5 == -1) // sanity check for t=0
    //     oldFiltered5 = V5;
    curFiltered5 = filter(V5, oldFiltered5); // Exponential filter
    oldFiltered5 = curFiltered5;             // get old value

    // if (oldFiltered6 == -1) // sanity check for t=0
    //     oldFiltered6 = V6;
    curFiltered6 = filter(V6, oldFiltered6); // Exponential filter
    oldFiltered6 = curFiltered6;             // get old value

    // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
    // Serial.print("Time taken (ms): ");
    // Serial.println(pepe2);
}

double get_curFiltered1()
{
    return curFiltered1;
}

double get_curFiltered2()
{
    return curFiltered2;
}

double get_curFiltered3()
{
    return curFiltered3;
}

double get_curFiltered4()
{
    return curFiltered4;
}

double get_curFiltered5()
{
    return curFiltered5;
}

double get_curFiltered6()
{
    return curFiltered6;
}

void printSensorReading()
{
    // unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
    Serial.print("Sensor 1: ");
    Serial.print(curFiltered1);
    Serial.print("  ");
    Serial.print(getDist1(curFiltered1));
    Serial.print("  ");
    Serial.println(getBlocksSR(getDist1(curFiltered1)));
    Serial.print("Sensor 2: ");
    Serial.print(curFiltered2);
    Serial.print("  ");
    Serial.print(getDist2(curFiltered2));
    Serial.print("  ");
    Serial.println(getBlocksSR(getDist2(curFiltered2)));
    Serial.print("Sensor 3 ");
    Serial.print(curFiltered3);
    Serial.print("  ");
    Serial.print(getDist3(curFiltered3));
    Serial.print("  ");
    Serial.println(getBlocksSR(getDist3(curFiltered3)));
    Serial.print("Sensor 4 ");
    Serial.print(curFiltered4);
    Serial.print("  ");
    Serial.print(getDist4(curFiltered4));
    Serial.print("  ");
    Serial.println(getBlocksSR(getDist4(curFiltered4)));
    Serial.print("Sensor 5 ");
    Serial.print(curFiltered5);
    Serial.print("  ");
    Serial.print(getDist5(curFiltered5));
    Serial.print("  ");
    Serial.println(getBlocksSR(getDist5(curFiltered5)));
    Serial.print("Sensor 6 ");
    Serial.print(curFiltered6);
    Serial.print("  ");
    Serial.print(getDist6(curFiltered6));
    Serial.print("  ");
    Serial.println(getBlocksLR(getDist6(curFiltered6)));

    // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
    // Serial.print("Time taken (ms): ");
    // Serial.println(pepe2);
}

void printSensorBlocks()
{
    // unsigned long pepe1 = millis(); // takes the time before the loop on the library begins

    // Serial.print("Sensor 1: ");
    // Serial.println(getBlocksSR(getDist1(getAvg1())));
    // Serial.print("Sensor 2: ");
    // Serial.println(getBlocksSR(getDist2(getAvg2())));
    // Serial.print("Sensor 3: ");
    // Serial.println(getBlocksSR(getDist3(getAvg3())));
    // Serial.print("Sensor 4: ");
    // Serial.println(getBlocksSR(getDist4(getAvg4())));
    // Serial.print("Sensor 5: ");
    // Serial.println(getBlocksSR(getDist5(getAvg5())));
    // Serial.print("Sensor 6: ");
    // Serial.println(getBlocksLR(getDist6(getAvg6())));
    // Serial.println("");

    Serial.print("Sensor 1: ");
    Serial.println(getBlocksSR(getDist1(getAvg1())));
    Serial.print("Sensor 2: ");
    Serial.println(getBlocksSR(getDist2(getAvg2())));
    Serial.print("Sensor 3: ");
    Serial.println(getBlocksSR(getDist3(getAvg3())));
    Serial.print("Sensor 4: ");
    Serial.println(getBlocksSR(getDist4(getAvg4())));
    Serial.print("Sensor 5: ");
    Serial.println(getBlocksSR(getDist5(getAvg5())));
    Serial.print("Sensor 6: ");
    Serial.println(getBlocksLR(getDist6(getAvg6())));
    Serial.println("");

    // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement
    // Serial.print("Time taken (ms): ");
    // Serial.println(pepe2);
}

// Used for sensor 1 & 4
double getDist1(double x)
{
    //  return -5.7108*pow(x,5) + 47.988*pow(x,4) - 159.85*pow(x,3) + 270.34*pow(x,2) - 247.46*x + 120.28;
    // return -13.696 * pow(x, 5) + 101.4 * pow(x, 4) - 296.49 * pow(x, 3) + 438.4 * pow(x, 2) - 348.66 * x + 144.17;
    //return 204.0816327 / (0.0418 * x + 0.00007);
    //return 1 / (0.0002 * x - 0.0052);
    // return 1 / (0.0000005 * pow(x, 2) + 0.00005 * x + 0.0081);
    // return 1 / (0.0000001 * pow(x, 2) + 0.0001 * x + 0.0043);
    //   return 1 / (0.0002 * x - 0.0003);
    return 1 / (0.00000008 * pow(x, 2) + 0.0002 * x - 0.0002) - 5;
}

// Sensor 2
double getDist2(double x)
{
    // return -11.577 * pow(x, 5) + 92.006 * pow(x, 4) - 288.75 * pow(x, 3) + 455 * pow(x, 2) - 377.91 * x + 155.37;
    //  return 204.0816327 / (0.0448 * x - 0.0022);
    //return 1 / (0.0002 * x - 0.0065);
    // return 1 / (0.0002 * x - 0.0054);
    return 1 / (0.00000007 * pow(x, 2) + 0.0002 * x - 0.0007);
}

double getDist3(double x)
{
    // return 25.863 * pow(x, -1.268);
    //return 204.0816327 / (0.0483 * x - 0.0082);
    // return 1 / (0.0000004 * pow(x, 2) + 0.0001 * x + 0.0014);
    return 1 / (0.0002 * x - 0.0063);
}

double getDist4(double x)
{
    // return -20.988 * pow(x, 5) + 143.17 * pow(x, 4) - 383.55 * pow(x, 3) + 520.2 * pow(x, 2) - 382.96 * x + 148.66;
    //return 204.0816327 / (0.0429 * x - 0.00008);
    //return 1 / (0.0002 * x - 0.0035);
    // return 1 / (0.0000005 * pow(x, 2) + 0.00006 * x + 0.0062);
    // return 1 / (0.0000001 * pow(x, 2) + 0.0001 * x + 0.0012);
    // return 1 / (0.0002 * x - 0.0037);
    return 1 / (0.00000009 * pow(x, 2) + 0.0002 * x + 0.0003) - 5;
}
// 3 & 5
double getDist5(double x)
{
    // return 26.353 * pow(x, -1.056);
    //return 204.0816327 / (0.0413 * x - 0.0027);
    // return 1 / (0.0000003 * pow(x, 2) + 0.0001 * x + 0.006);
    return 1 / (0.0002 * x - 0.0016);
}

// Long distance sensor
double getDist6(double x)
{
    //return -17.686 * pow(x, 5) + 143.29 * pow(x, 4) - 454.79 * pow(x, 3) + 718.36 * pow(x, 2) - 600.2 * x + 265.99;
    // return 3.9597*pow(x,6) - 50.124*pow(x,5) + 247.27*pow(x,4) - 620.04*pow(x,3) + 854.23*pow(x,2) - 654.48*x + 274.19;
    //return 204.0816327 / (0.0181 * x + 0.0008);
    return 1 / (0.00000009 * pow(x, 2) + 0.00002 * x + 0.0085);
}

double alpha = 0.1; // Smoothing Factor
double filter(double volt, double oldVal)
{
    return (alpha * volt) + (1 - alpha) * oldVal;
}

int sampleSize = 20;
double getAvg1()
{
    // unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i++)
    {
        getSensorReading();
        sensorReadings[i] = get_curFiltered1();
    }

    double sumL = 0;

    for (int i = 0; i < readSample; i++)
    {
        sumL = sumL + sensorReadings[i];
    }

    double sensorAvg = sumL / readSample;

    // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement

    return (sensorAvg);
}

double getAvg2()
{
    // unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i++)
    {
        getSensorReading();
        sensorReadings[i] = get_curFiltered2();
    }

    double sumL = 0;

    for (int i = 0; i < readSample; i++)
    {
        sumL = sumL + sensorReadings[i];
    }

    double sensorAvg = sumL / readSample;

    // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement

    return (sensorAvg);
}

double getAvg3()
{
    // unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i++)
    {
        getSensorReading();
        sensorReadings[i] = get_curFiltered3();
    }

    double sumL = 0;

    for (int i = 0; i < readSample; i++)
    {
        sumL = sumL + sensorReadings[i];
    }

    double sensorAvg = sumL / readSample;

    // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement

    return (sensorAvg);
}

double getAvg4()
{
    //   unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i++)
    {
        getSensorReading();
        sensorReadings[i] = get_curFiltered4();
    }

    double sumL = 0;

    for (int i = 0; i < readSample; i++)
    {
        sumL = sumL + sensorReadings[i];
    }

    double sensorAvg = sumL / readSample;

    //   unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement

    return (sensorAvg);
}

double getAvg5()
{
    //   unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i++)
    {
        getSensorReading();
        sensorReadings[i] = get_curFiltered5();
    }

    double sumL = 0;

    for (int i = 0; i < readSample; i++)
    {
        sumL = sumL + sensorReadings[i];
    }

    double sensorAvg = sumL / readSample;

    //   unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement

    return (sensorAvg);
}

double getAvg6()
{
    // unsigned long pepe1 = millis(); // takes the time before the loop on the library begins
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i++)
    {
        getSensorReading();
        sensorReadings[i] = get_curFiltered6();
    }

    double sumL = 0;

    for (int i = 0; i < readSample; i++)
    {
        sumL = sumL + sensorReadings[i];
    }

    double sensorAvg = sumL / readSample;

    // unsigned long pepe2 = millis() - pepe1; // the following gives you the time taken to get the measurement

    return (sensorAvg);
}

int getBlocksSR(double dist)
{
    int blocks = distToBlocks(dist);
    if (blocks > 3 || blocks < 0)
    {
        return 0;
    }
    return blocks;
}
int getBlocksLR(double dist)
{
    int blocks = distToBlocks(dist);
    if (blocks > 5 || blocks < 0)
    {
        return 0;
    }
    return blocks;
}