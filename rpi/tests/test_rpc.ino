#define RESPONSE_MSG_LEN 40

#define Echo 0x01
#define Forward 0x02
#define TurnLeft 0x03
#define TurnRight 0x04
#define GetMetrics 0x05
#define Calibration 0x06
#define Terminate 0x07

void parseMessage();

// Handlers
int echoHandler(int arguement, uint8_t * const response);
int forwardHandler(int arguement, uint8_t * const response);
int trunLeftHandler(int arguement, uint8_t * const response);
int turnRightHandler(int arguement, uint8_t * const response);
int getMetricsHandler(int arguement, uint8_t * const response);
int calibrationHandler(int argument, uint8_t * const response);
int terminateHandler(int argument, uint8_t * const response);

// Serializers
int echo_response_serilizer(char message, bool status, uint8_t * const response);
int metric_response_serializer(float *value_ptr, int value_len, bool status, uint8_t * const response);
int status_serializer(bool status, uint8_t * const response);


String message;
const char c_separator = ';';
const char c_separator_sequence[] = {';', ';', ';', ';', ';'};
const uint8_t sequence_length = 5;
uint8_t counter = 0;


void setup() {
  // put your setup code here, to run once:

  //Initialize serial and wait for port to open:
  Serial.begin(115200);
  while (!Serial); // wait for serial port to connect

  // Clear out flush
  while (Serial.available() > 0)
    Serial.read();
}

void loop() {
  char temp;
  while (Serial.available() > 0) {
    temp = Serial.read();
    message.concat((char)temp);

    if (temp == c_separator)
        counter++;

    if (counter == sequence_length) {  // find the end of message
      counter = 0;
      message.remove(message.length() - sequence_length, sequence_length);
      parseMessage();
      message = String("");
    }
  }
}

void parseMessage() {
  char command = message.charAt(0);
  int argument = 0;
  uint8_t msg[RESPONSE_MSG_LEN];
  int msg_len = 0;

  if (message.length() > 1)
    argument = (int) message.charAt(1);

  switch (command) {
    case Echo:
      msg_len = echoHandler(argument, msg);
      break;
    case Forward:
      msg_len = forwardHandler(argument, msg);
      break;
    case TurnLeft:
      msg_len = turnLeftHandler(argument, msg);
      break;
    case TurnRight:
      msg_len = turnRightHandler(argument, msg);
      break;
    case GetMetrics:
      msg_len = getMetricsHandler(argument, msg);
      break;
    case Calibration:
      msg_len = calibrationHandler(argument, msg);
      break;
    case Terminate:
      msg_len = terminateHandler(argument, msg);
      break;
    default:
      break;
  }

  // update the ending saperator
  memcpy(msg + msg_len, c_separator_sequence, sequence_length);
  Serial.write(msg, msg_len + sequence_length);
}

int echoHandler(int argument, uint8_t * const response) {
  bool status = true;

  return echo_response_serilizer((char) argument, status, response);
}

int forwardHandler(int argument, uint8_t * const response) {
  bool status = true;
  float data_values[] = {0.01, 0.01, 0.01, 0.01, 0.01, 0.01};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int turnLeftHandler(int argument, uint8_t * const response) {
  bool status = true;
  float data_values[] = {0.01, 0.01, 0.01, 0.01, 0.01, 0.01};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int turnRightHandler(int argument, uint8_t * const response) {
  bool status = true;
  float data_values[] = {0.01, 0.01, 0.01, 0.01, 0.01, 0.01};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int getMetricsHandler(int argument, uint8_t * const response) {
  bool status = true;
  float data_values[] = {0.01, 0.01, 0.01, 0.01, 0.01, 0.01};

  return metric_response_serializer(data_values, sizeof(data_values) / sizeof(float), status, response);
}

int calibrationHandler(int argument, uint8_t * const response) {
  bool status = true;

  return status_serializer(status, response);
}

int terminateHandler(int argument, uint8_t * const response) {
  bool status = true;

  return status_serializer(status, response);
}

int status_serializer(bool status, uint8_t * const response) {
  *response = (uint8_t)status;
  return 1;
}

int echo_response_serilizer(char message, bool status, uint8_t * const response) {
  *response = (uint8_t)(message + 1);
  *(response + 1) = (uint8_t)status;
  return 2;
}

int metric_response_serializer(float *value_ptr, int value_len, bool status, uint8_t * const response) {
  int float_len = sizeof(float);
  int data_values_size = float_len * value_len;

  for (auto i = 0; i < value_len; i++)
      memcpy(response + i * float_len, (uint8_t*)(value_ptr + i), float_len);

  *(response + data_values_size++) = (uint8_t)status;

  return data_values_size;
}
