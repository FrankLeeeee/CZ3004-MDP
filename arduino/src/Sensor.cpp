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

double distToBlocksFront1(double dist)
{
    double blocks = ((dist + 8.5) / 10);
    return blocks;
}

double distToBlocksFront2(double dist)
{
    double blocks = ((dist + 8.5) / 10);
    return blocks;
}
double distToBlocksFront4(double dist)
{
    double blocks = ((dist + 8.5) / 10);
    return blocks;
}

double distToBlocksSide3(double dist)
{
    double blocks = ((dist + 8.5) / 10);
    return blocks;
}

double distToBlocksSide5(double dist)
{
    double blocks = ((dist + 8.5) / 10);
    return blocks;
}

double distToBlocksLR(double dist)
{
    double blocks = ((dist + 8.5) / 10);
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
    //Change according to pin (A0 = PS1, A1 = PS2, etc)
    V1 = analogRead(A0); // Read voltage
    V2 = analogRead(A1);
    V3 = analogRead(A2);
    V4 = analogRead(A3);
    V5 = analogRead(A4);
    V6 = analogRead(A5);

    curFiltered1 = filter(V1, oldFiltered1); // Exponential filter
    oldFiltered1 = curFiltered1;             // get old value

    curFiltered2 = filter(V2, oldFiltered2); // Exponential filter
    oldFiltered2 = curFiltered2;             // get old value

    curFiltered3 = filter(V3, oldFiltered3); // Exponential filter
    oldFiltered3 = curFiltered3;             // get old value

    curFiltered4 = filter(V4, oldFiltered4); // Exponential filter
    oldFiltered4 = curFiltered4;             // get old value

    curFiltered5 = filter(V5, oldFiltered5); // Exponential filter
    oldFiltered5 = curFiltered5;             // get old value

    curFiltered6 = filter(V6, oldFiltered6); // Exponential filter
    oldFiltered6 = curFiltered6;             // get old value
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
    Serial.print("Sensor 1: ");
    Serial.println(getDist1(getAvg1()));
    Serial.print("Sensor 2: ");
    Serial.println(getDist2(getAvg2()));
    Serial.print("Sensor 3: ");
    Serial.println(getDist3(getAvg3()));
    Serial.print("Sensor 4: ");
    Serial.println(getDist4(getAvg4()));
    Serial.print("Sensor 5: ");
    Serial.println(getDist5(getAvg5()));
    Serial.print("Sensor 6: ");
    Serial.println(getDist6(getAvg6()));
    Serial.println("");
}

void printSensorBlocks()
{
    Serial.print("Sensor 1: ");
    Serial.println(getBlocksSR_front1(getDist1(getAvg1())));
    Serial.print("  ");
    Serial.print(getBlocksSR_float_front1(getDist1(getAvg1())));
    Serial.println("    ");
    Serial.print("Sensor 2: ");
    Serial.println(getBlocksSR_front2(getDist2(getAvg2())));
    Serial.print("  ");
    Serial.print(getBlocksSR_float_front2(getDist2(getAvg2())));
    Serial.println("    ");
    Serial.print("Sensor 3: ");
    Serial.println(getBlocksSR_side3(getDist3(getAvg3())));
    Serial.print("  ");
    Serial.print(getBlocksSR_float_side3(getDist3(getAvg3())));
    Serial.println("    ");
    Serial.print("Sensor 4: ");
    Serial.println(getBlocksSR_front4(getDist4(getAvg4())));
    Serial.print("  ");
    Serial.print(getBlocksSR_float_front4(getDist4(getAvg4())));
    Serial.println("    ");
    Serial.print("Sensor 5: ");
    Serial.println(getBlocksSR_side5(getDist5(getAvg5())));
    Serial.print("  ");
    Serial.print(getBlocksSR_float_side5(getDist5(getAvg5())));
    Serial.println("    ");
    Serial.print("Sensor 6: ");
    Serial.println(getBlocksLR(getDist6(getAvg6())));
    Serial.print("  ");
    Serial.print(getBlocksLR_float(getDist6(getAvg6())));
    Serial.println("");
}

double getDist1(double x)
{
    return (1 / (0.00000008 * pow(x, 2) + 0.0002 * x - 0.0002) - 5) * 1.18;
}

double getDist2(double x)
{
    return (1 / (0.00000007 * pow(x, 2) + 0.0002 * x - 0.0007) - 2) * 1.15;
}

double getDist3(double x)
{
    return (1 / (0.0000001 * pow(x, 2) + 0.0002 * x - 0.00004) - 6) * 1.2;
}

double getDist4(double x)
{
    return (1 / (0.00000009 * pow(x, 2) + 0.0002 * x + 0.0003) - 5) * 1.32;
}

double getDist5(double x)
{
    return (1 / (0.0000001 * pow(x, 2) + 0.0001 * x + 0.0053) - 6) * 0.725;
}

// Long distance sensor
double getDist6(double x)
{
    return 1 / (0.00000009 * pow(x, 2) + 0.00002 * x + 0.0085) - 17 - 2;
}

double alpha = 0.1; // Smoothing Factor
double filter(double volt, double oldVal)
{
    return (alpha * volt) + (1 - alpha) * oldVal;
}

int sampleSize = 25;

int compare(const void *a, const void *b)
{
    return (*(int *)a - *(int *)b);
}

double getAvg1()
{
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i+=5)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i] = curFiltered1;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+1] = curFiltered1;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+2] = curFiltered1;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+3] = curFiltered1;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+4] = curFiltered1;
    }
    qsort(sensorReadings, readSample, sizeof(double), compare);
    double median = sensorReadings[readSample / 2];
    return median;
}

double getAvg2()
{
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i+=5)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i] = curFiltered2;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+1] = curFiltered2;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+2] = curFiltered2;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+3] = curFiltered2;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+4] = curFiltered2;
    }
    qsort(sensorReadings, readSample, sizeof(double), compare);
    double median = sensorReadings[readSample / 2];
    return median;
}

double getAvg3()
{
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i+=5)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i] = curFiltered3;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+1] = curFiltered3;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+2] = curFiltered3;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+3] = curFiltered3;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+4] = curFiltered3;
    }
    qsort(sensorReadings, readSample, sizeof(double), compare);
    double median = sensorReadings[readSample / 2];
    return median;
}

double getAvg4()
{
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i+=5)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i] = curFiltered4;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+1] = curFiltered4;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+2] = curFiltered4;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+3] = curFiltered4;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+4] = curFiltered4;
    }
    qsort(sensorReadings, readSample, sizeof(double), compare);
    double median = sensorReadings[readSample / 2];
    return median;
}

double getAvg5()
{
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i+=5)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i] = curFiltered5;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+1] = curFiltered5;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+2] = curFiltered5;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+3] = curFiltered5;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+4] = curFiltered5;
    }
    qsort(sensorReadings, readSample, sizeof(double), compare);
    double median = sensorReadings[readSample / 2];
    return median;
}

double getAvg6()
{
    int readSample = sampleSize;
    double sensorReadings[readSample];

    for (int i = 0; i < readSample; i+=5)
    {
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i] = curFiltered6;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+1] = curFiltered6;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+2] = curFiltered6;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+3] = curFiltered6;
        getSensorReading();
        getSensorReading();
        getSensorReading();
        sensorReadings[i+4] = curFiltered6;
    }
    qsort(sensorReadings, readSample, sizeof(double), compare);
    double median = sensorReadings[readSample / 2];
    return median;
}

float getBlocksSR_float_front1(double dist)
{
    float blocks = distToBlocksFront1(dist);
    if (blocks > 4 || blocks < 0)
    {
        return 0;
    }
    if (blocks > 0.5 && blocks < 1.0)
    {
        blocks = 1.0;
    }
    return blocks;
}

float getBlocksSR_float_front2(double dist)
{
    float blocks = distToBlocksFront2(dist);
    if (blocks > 4 || blocks < 0)
    {
        return 0;
    }
    if (blocks > 0.5 && blocks < 1.0)
    {
        blocks = 1.0;
    }
    return blocks;
}

float getBlocksSR_float_front4(double dist)
{
    float blocks = distToBlocksFront4(dist);
    if (blocks > 4 || blocks < 0)
    {
        return 0;
    }
    if (blocks > 0.5 && blocks < 1.0)
    {
        blocks = 1.0;
    }
    return blocks;
}

float getBlocksSR_float_side3(double dist)
{
    float blocks = distToBlocksSide3(dist);
    if (blocks > 4 || blocks < 0)
    {
        return 0;
    }
    if (blocks > 0.5 && blocks < 1.0)
    {
        blocks = 1.0;
    }
    return blocks;
}

float getBlocksSR_float_side5(double dist)
{
    float blocks = distToBlocksSide5(dist);
    if (blocks > 4 || blocks < 0)
    {
        return 0;
    }
    if (blocks > 0.5 && blocks < 1.0)
    {
        blocks = 1.0;
    }
    return blocks;
}

int getBlocksSR_front1(double dist)
{
    int blocks = distToBlocksFront1(dist);
    if (blocks > 4 || blocks < 0)
    {
        return 0;
    }

    return blocks;
}

int getBlocksSR_front2(double dist)
{
    int blocks = distToBlocksFront2(dist);
    if (blocks > 4 || blocks < 0)
    {
        return 0;
    }

    return blocks;
}

int getBlocksSR_front4(double dist)
{
    int blocks = distToBlocksFront4(dist);
    if (blocks > 4 || blocks < 0)
    {
        return 0;
    }

    return blocks;
}

int getBlocksSR_side3(double dist)
{
    int blocks = distToBlocksSide3(dist);
    if (blocks > 4 || blocks < 0)
    {
        return 0;
    }

    return blocks;
}

int getBlocksSR_side5(double dist)
{
    int blocks = distToBlocksSide5(dist);
    if (blocks > 4 || blocks < 0)
    {
        return 0;
    }

    return blocks;
}

float getBlocksLR_float(double dist)
{
    float blocks = distToBlocksLR(dist);
    if (blocks > 6 || blocks < 0)
    {
        return 0;
    }
    return blocks;
}

int getBlocksLR(double dist)
{
    int blocks = distToBlocksLR(dist);
    if (blocks > 5 || blocks < 0)
    {
        return 0;
    }
    return blocks;
}
