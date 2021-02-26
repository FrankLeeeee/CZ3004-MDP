// #include "SharpIR.h"
// #include "math.h"

// // Define model and input pin:
// #define IRPin A0
// #define IRPin2 A1
// #define IRPin3 A2
// #define IRPin4 A3
// #define IRPin5 A4
// #define IRPin6 A5

// // ===== Function Declaration =====
// double filter(double volt, double oldVal);
// double getDist1(double x);
// double getDist2(double x);
// double getDist3(double x);
// double getDist4(double x);
// double getDist5(double x);
// double getDist6(double x);

// // ===== Variables =====
// double curFiltered;
// double oldFiltered = -1; 
// double V;


// //#define model 1080
// //#define model1 20150
// #define model GP2Y0A21YK0F
// #define model1 GP2Y0A02YK0F
// // Create variable to store the distance:
// // int distance_cm, distance_cm2 ,distance_cm3 ,distance_cm4 ,distance_cm5 ,distance_cm6;
  
// /* Model :
//   GP2Y0A02YK0F --> 20150
//   GP2Y0A21YK0F --> 1080
//   GP2Y0A710K0F --> 100500
//   GP2YA41SK0F --> 430
// */

// // Create a new instance of the SharpIR class:
// //SharpIR mySensor = SharpIR(IRPin, model);
// //SharpIR mySensor2 = SharpIR(IRPin2, model);
// //SharpIR mySensor3 = SharpIR(IRPin3, model);
// //SharpIR mySensor4 = SharpIR(IRPin4, model);
// //SharpIR mySensor5 = SharpIR(IRPin5, model);
// //SharpIR mySensor6 = SharpIR(IRPin6, model1);

// SharpIR mySensor(SharpIR:: model, A0);
// SharpIR mySensor2(SharpIR:: model, A1);
// SharpIR mySensor3(SharpIR:: model, A2);
// SharpIR mySensor4(SharpIR:: model, A3);
// SharpIR mySensor5(SharpIR:: model, A4);
// SharpIR mySensor6(SharpIR:: model1, A5);

// void setup() {
//   // Begin serial communication at a baudrate of 9600:
//  // Serial.begin(9600);
//   Serial.begin(9600);
 
// }

// void loop() {
//   // Get a distance measurement and store it as distance_cm:
// //  distance_cm = mySensor.getDistance();
// //  distance_cm2 = mySensor2.distance();
// //  distance_cm3 = mySensor3.distance();
// //  distance_cm4 = mySensor4.distance();
// //  distance_cm5 = mySensor5.distance();
// //  distance_cm6 = mySensor6.distance();
// //
// //  distance_cm = (distance_cm + 7.2857)/1.5179;
// //  distance_cm2 = (distance_cm2 + 4)/1.4142;
// //  distance_cm3 = (distance_cm3 + 3.2857)/1.2393;
// //  distance_cm4 = (distance_cm4 + 8.1429)/1.6643;
// //  distance_cm5 = (distance_cm5 + 16.143)/1.8964;
// //  distance_cm6 = (distance_cm6 + 7.1667)/1.2403;

//   // Print the measured distance to the serial monitor:
// //  Serial.print("Mean distance1: ");
// //  Serial.print(distance_cm);
// //  Serial.println(" cm");
// //  
// //  Serial.print("Mean distance2: ");
// //  Serial.print(distance_cm2);
// //  Serial.println(" cm");

// //  Serial.print("Mean distance3: ");
// //  Serial.print(distance_cm3);
// //  Serial.println(" cm");

// //  Serial.print("Mean distance4: ");
// //  Serial.print(distance_cm4);
// //  Serial.println(" cm");
// //
// //  Serial.print("Mean distance5: ");
// //  Serial.print(distance_cm5);
// //  Serial.println(" cm");
// //
// //  Serial.print("Mean distance6: ");
// //  Serial.print(distance_cm6);
// //  Serial.println(" cm");

// //===== change according to pin (A0 = PS1, A1 = PS2, etc) =====
//  V = analogRead(A0); // Read voltage
// //  V = analogRead(A1);
// //  V = analogRead(A2);
// //  V = analogRead(A3);
// //  V = analogRead(A4);
// //  V = analogRead(A5);
  
//   if(oldFiltered == -1) // sanity check for t=0
//     oldFiltered = V;
//   curFiltered = filter(V, oldFiltered); // Exponential filter
//   oldFiltered = curFiltered; // get old value
  
// //  Serial.print(V*0.0049);
// //  Serial.print("  ");
// //  Serial.println(curFiltered*0.0049);

// //  Serial.print(V);
// //  Serial.print("  ");
// //  Serial.println(curFiltered);

//   Serial.print(curFiltered*0.0049);
//   Serial.print("  ");
//   Serial.println(getDist5(curFiltered*0.0049));
  
//   delay(20);
// }

// // Used for sensor 1 & 4
// double getDist1(double x){
// //  return -5.7108*pow(x,5) + 47.988*pow(x,4) - 159.85*pow(x,3) + 270.34*pow(x,2) - 247.46*x + 120.28;
//   return -13.696*pow(x,5) + 101.4*pow(x,4) - 296.49*pow(x,3) + 438.4*pow(x,2) - 348.66*x + 144.17;
// }

// // Sensor 2
// double getDist2(double x){
//   return -11.577*pow(x,5) + 92.006*pow(x,4) - 288.75*pow(x,3) + 455*pow(x,2) - 377.91*x + 155.37;
// }

// double getDist3(double x){
//   return 25.863*pow(x,-1.268);
// }

// double getDist4(double x){
//   return -20.988*pow(x,5) + 143.17*pow(x,4) - 383.55*pow(x,3) + 520.2*pow(x,2) - 382.96*x + 148.66;
// }
// // 3 & 5
// double getDist5(double x){ 
//   return 26.353*pow(x,-1.056);
// }

// // Long distance sensor
// double getDist6(double x){
//   return -17.686*pow(x,5) + 143.29*pow(x,4) - 454.79*pow(x,3) + 718.36*pow(x,2) - 600.2*x + 265.99;
//  // return 3.9597*pow(x,6) - 50.124*pow(x,5) + 247.27*pow(x,4) - 620.04*pow(x,3) + 854.23*pow(x,2) - 654.48*x + 274.19;
// }

// double alpha = 0.1; // Smoothing Factor
// double filter(double volt, double oldVal){
//   return (alpha*volt) + (1-alpha)*oldVal;
// }