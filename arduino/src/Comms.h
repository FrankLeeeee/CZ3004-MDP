#ifndef Comms_H
#define Comms_H
#include "stdint.h"
uint8_t receiveMessage(uint8_t* rpi_receive, int length);

void encodeMessage();
void decodeMessage(uint8_t rpi_receive[1024]);
void jsonTest();
void parseMessage();
#endif