#include "Comms.h"
#include "message.pb.h"
#include "pb_encode.h"
#include "pb_decode.h"
#include "Arduino.h"
#include "stdint.h"
#include <ArduinoJson.h>


// resend = 0x00 echo = 0x01 forward = 0x02 left = 0x03 right = 0x04 getmetrics =0x05 getcalibration = 0x06 terminate = 0x07

size_t message_length;
uint8_t buffer[10];
uint8_t actualBuffer[1024];

bool status = true;

StaticJsonDocument<300> doc;




uint8_t messageBuffer(uint8_t* rpi_receive, int length){
  int start = 0;
  int counter = 0;
  int end = 0;
  uint8_t temp_buffer[1024];
  while(rpi_receive[counter]!=';'){
    counter++;
  }
  
}

uint8_t receiveMessage(uint8_t* rpi_receive, int length)
{
  Serial.println("");
  int start = 0;
  int end = 0;
  int n = 0;
  int m = 0;
  for (int i = 0; i < length; i++)
  {
    if (rpi_receive[i] == '\\')
    {
      start = i+1; 
    }
    if(rpi_receive[i]==';'){
      end = i;
    }
  }
   char command[start];
   char moveReqStr[12] = "MoveRequest";
   char turnReqStr[12] = "TurnRequest";

  uint8_t encodedData[end-start];
      Serial.print("JSON Data: ");
  for (int i = start; i < end; i++){
    encodedData[n] = rpi_receive[i];
    Serial.print((char)encodedData[n]);
    n++;
  }
  Serial.println("");
  
    Serial.print("Command: ");
  for(int i = 0; i < start-1; i++){
    command[m] = char(rpi_receive[i]);
    Serial.print(command[m]);
  }
      Serial.println("");
      Serial.println(command);
      if (strcmp(command, moveReqStr)==0)
  {//do stuff here because it was a match
    Serial.println("forward cmd");
  } else{
    Serial.println("other cmd");
  }


    // Deserialize the JSON document
  DeserializationError error = deserializeJson(doc, encodedData);

  // Test if parsing succeeds.
  if (error) {
    Serial.print(F("deserializeJson() failed: "));
    return;
  }


  int step = doc["step"];

  // Print values.
  
  Serial.println(step);



   return encodedData[n];


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

void decodeMessage(uint8_t rpi_receive[1024])
{
   Serial.println("Status 1");
  Serial.println(status);
  MoveRequest message = MoveRequest_init_zero;

  /* Create a stream that reads from the buffer. */
  // uint8_t buffer[] = {0x01, 0x00, 0x08, 0x01}; // command, session, data, end char
  pb_istream_t stream = pb_istream_from_buffer(rpi_receive, sizeof(rpi_receive));
  //pb_istream_t stream = pb_istream_from_buffer(buffer, sizeof(buffer));

  /* Now we are ready to decode the message. */
  status = pb_decode(&stream, MoveRequest_fields, &message);
  Serial.println("Status");
  Serial.println(status);
  Serial.println((int)message.step);
}