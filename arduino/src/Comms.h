#ifndef Comms_H
#define Comms_H
#include "stdint.h"

void parseMessage();
void loopSeq();
// Handlers
int echoHandler(int arguement, uint8_t *response);
int forwardHandler(int arguement, uint8_t *response);
int turnLeftHandler(int arguement, uint8_t *response);
int turnRightHandler(int arguement, uint8_t *response);
int getMetricsHandler(int arguement, uint8_t *response);
int calibrationHandler(int argument, uint8_t *response);
int terminateHandler(int argument, uint8_t *response);

// Serializers
int echo_response_serializer(char message, bool status, uint8_t *response);
int metric_response_serializer(float *value_ptr, int value_len, bool status, uint8_t *response);
int status_serializer(bool status, uint8_t *response);


#endif