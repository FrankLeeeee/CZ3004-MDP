#ifndef Comms_H
#define Comms_H
#include "stdint.h"
uint8_t receiveMessage(uint8_t rpi_receive[1024]);
void encodeMessage();
void decodeMessage();
#endif