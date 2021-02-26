#include "Comms.h"
#include "message.pb.h"
#include "pb_encode.h"
#include "pb_decode.h"
#include "Arduino.h"
#include "stdint.h"

size_t message_length;
uint8_t buffer[10];

bool status;

uint8_t receiveMessage(uint8_t rpi_receive[1024])
{
  int counter = 0;
  for (int i = 0; i < 1024; i++)
  {
    Serial.println(rpi_receive[i]);
    if (rpi_receive[i] == 0xFF)
    {
      Serial.print("FF detected at ");
      Serial.println(i);
      counter++;
      if (counter == 5)
      {
        Serial.println("ending sequence detected after ");
        Serial.println(rpi_receive[i - 5]);
        counter = 0;
        break;
      }
    }
    else
    {
      counter = 0;
    }
  }
  Serial.println("ENDING");
  return rpi_receive[0];
}

void encodeMessage()
{
  Serial.println("start");

  // Example mymessage = {42};
  // pb_ostream_t stream = pb_ostream_from_buffer(buffer, sizeof(buffer));
  // mymessage.value = 6;
  // status = pb_encode(&stream, Example_fields, &mymessage);
  // message_length = stream.bytes_written;

  // Serial.print("%x\r",buffer);

  Serial.println(status);

  Serial.write(buffer, sizeof(buffer));
}

void decodeMessage()
{
  MoveRequest message = MoveRequest_init_zero;

  /* Create a stream that reads from the buffer. */
  uint8_t buffer[] = {0x01, 0x00, 0x08, 0x01}; // command, session, data, end char
  pb_istream_t stream = pb_istream_from_buffer(buffer, sizeof(buffer));

  /* Now we are ready to decode the message. */
  status = pb_decode(&stream, MoveRequest_fields, &message);
  Serial.println(status);
  Serial.println((int)message.step);
}