#define ConfigParam 0x10
#define SetLeftSpeed 0x11
#define SetRightSpeed 0x12
#define Up 'u'
#define Down 'd'
#define Left 'l'
#define Right 'r'

#define NUM_PID_PARAM 6
#define DELAY_MS 20

String message;
const char c_separator = ';';
const char c_separator_sequence[] = {';', ';', ';', ';', ';'};
const uint8_t sequence_length = 5;
uint8_t counter = 0;

// ===== Encoders =====
double curTickR = 0, curTickL = 0, oldTickR = 0, oldTickL = 0;
volatile uint32_t TickL = 0, TickR = 0;
double targetTick;

double Kp_l = 0, Ki_l = 0, Kd_l = 0;
double Kp_r = 0, Ki_r = 0, Kd_r = 0;

void parameter_request_deserializer(const char *request, float *value_ptr, int value_len);
void speed_request_deserializer(const char *request, int32_t speed_value, int32_t time_value);
void config_pid_parameters_handler(const char* request);
void speed_left_handler(char *request);
void speed_right_handler(char *request);

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
    const char* arguments = message.c_str() + 1;

    switch (command) {
        case ConfigParam:
            config_pid_parameters_handler(arguments);
            break;
        case SetLeftSpeed:
            speed_left_handler(arguments);
            break;
        case SetRightSpeed:
            speed_right_handler(arguments);
            break;
        case Up:
            break;
        case Down:
            break;
        case Left:
            break;
        case Right:
            break;
        default:
            break;
  }
}

void speed_left_handler(const char *request) {
    int32_t speed_left = 0, running_time = 0;
    speed_request_deserializer(request, &speed_left, &running_time);
    // Run left motor
    // md.setSpeeds((double) speed_left, 0);
    while (1) {
        delay(DELAY_MS);
        running_time -= DELAY_MS;
        if (running_time < 0)
            break;

        // Read feedback
        Serial.print(0.);
        Serial.print(" ");
        Serial.println(0.);
        delay(DELAY_MS);
    }
    // Rest left motor
    // md.setSpeeds(0, 0);
}

void speed_right_handler(const char *request) {
    int32_t speed_right = 0, running_time = 0;
    speed_request_deserializer(request, &speed_right, &running_time);
    // Run left motor
    // md.setSpeeds(0, (double) speed_right);
    while (1) {
        delay(DELAY_MS);
        running_time -= DELAY_MS;
        if (running_time < 0)
            break;

        // Read feedback
        Serial.print(0.);
        Serial.print(" ");
        Serial.println(0.);
        delay(DELAY_MS);
    }
    // Rest right motor
    // md.setSpeeds(0, 0);
}

/**
 * Configure PID hyper-parameters in the order of:
 *     Kp_l, Ki_l, Kd_l, Kp_r, Ki_r, Kd_r
 */
void config_pid_parameters_handler(const char* request) {
    // Parse arg string
    float values[NUM_PID_PARAM];
    parameter_request_deserializer(request, values, NUM_PID_PARAM);

    // Assign values
    Kp_l = (double) values[0];
    Ki_l = (double) values[1];
    Kd_l = (double) values[2];
    Kp_r = (double) values[3];
    Ki_r = (double) values[4];
    Kd_r = (double) values[5];
}

void parameter_request_deserializer(const char *request, float *value_ptr, int value_len) {
    for (int i = 0; i < value_len; i++)
        memcpy(value_ptr + i, (uint8_t *) (request + i * 4), 4);
}

void speed_request_deserializer(const char *request, int32_t* speed_value_ptr, int32_t* time_value_ptr) {
    memcpy((uint8_t *) speed_value_ptr, (uint8_t *) request, 4);
    memcpy((uint8_t *) time_value_ptr, (uint8_t *) request + 4, 4);
}
