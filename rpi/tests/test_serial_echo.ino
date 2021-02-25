void setup() {
  // Open serial communications and wait for port to open:
  Serial.begin(115200);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for Native USB only
  }

//  Serial.println("Goodnight moon!");
}

char temp;
void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available()) {
    temp = Serial.read();
    while (!Serial.availableForWrite())
      delay(10);
    Serial.write(temp);
  }
}
