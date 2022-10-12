/*             MFRC522      Arduino       Arduino   Arduino    Arduino          Arduino
 *             Reader/PCD   Uno/101       Mega      Nano v3    Leonardo/Micro   Pro Micro
 * Signal      Pin          Pin           Pin       Pin        Pin              Pin
 * -----------------------------------------------------------------------------------------
 * RST/Reset   RST          9             5         D9         RESET/ICSP-5     RST
 * SPI SS      SDA(SS)      10            53        D10        10               10
 * SPI MOSI    MOSI         11 / ICSP-4   51        D11        ICSP-4           16
 * SPI MISO    MISO         12 / ICSP-1   50        D12        ICSP-1           14
 * SPI SCK     SCK          13 / ICSP-3   52        D13        ICSP-3           15
*/

#include <SPI.h>
#include <MFRC522.h>
#include <AccelStepper.h>
#include <Keypad.h>
#include "Adafruit_Thermal.h"
#include <SoftwareSerial.h>
#include <stdio.h>      /* printf, fgets */
#include <stdlib.h>
#include <string.h>
#define TX_PIN 16 // Arduino transmit labeled RX on printer
#define RX_PIN 17 // Arduino receive labeled TX on printer

SoftwareSerial mySerial(RX_PIN, TX_PIN); // Declare SoftwareSerial obj first
Adafruit_Thermal printer(&mySerial);     // Pass addr to printer constructor


#define RST_PIN         5           // Configurable, see typical pin layout above
#define SS_PIN          53          // Configurable, see typical pin layout above
// variables used for timer
unsigned long activity = 0;         // not used rn
unsigned long lastActivity = 0;     // not used rn
// input variables
boolean stringComplete = false;
String inputString = "";
boolean checkCard = false;
boolean getKeyInput = false;
boolean eatCard = false;
boolean ejectCard = false;
boolean dipsenseBills = false;
boolean print = false;
boolean refillStatus = false;
// refill pin
#define refillButton 4
// setting up stepper
int distanceBills = 1000;
#define stopSwitch 7      // switch in the card reader
#define motorPin1  8      // IN1 on the ULN2003 driver
#define motorPin2  9      // IN2 on the ULN2003 driver
#define motorPin3  10     // IN3 on the ULN2003 driver
#define motorPin4  11     // IN4 on the ULN2003 driver
#define motorInterfaceType 8
AccelStepper stepper = AccelStepper(motorInterfaceType, motorPin1, motorPin3, motorPin2, motorPin4);
#define motorPin5  30      // IN1 on the ULN2003 driver
#define motorPin6  31      // IN2 on the ULN2003 driver
#define motorPin7  32     // IN3 on the ULN2003 driver
#define motorPin8  33     // IN4 on the ULN2003 driver
AccelStepper dispStepper1 = AccelStepper(motorInterfaceType, motorPin5, motorPin7, motorPin6, motorPin8);
#define motorPin9  36      // IN1 on the ULN2003 driver
#define motorPin10  37     // IN2 on the ULN2003 driver
#define motorPin11  38     // IN3 on the ULN2003 driver
#define motorPin12  39     // IN4 on the ULN2003 driver
AccelStepper dispStepper2 = AccelStepper(motorInterfaceType, motorPin9, motorPin11, motorPin10, motorPin12);
#define motorPin13  42      // IN1 on the ULN2003 driver
#define motorPin14  43     // IN2 on the ULN2003 driver
#define motorPin15  44     // IN3 on the ULN2003 driver
#define motorPin16  45     // IN4 on the ULN2003 driver
AccelStepper dispStepper3 = AccelStepper(motorInterfaceType, motorPin13, motorPin15, motorPin14, motorPin16);
int stepper1Steps[] = {-50, 750, 1100, 1500, 1900, 2300, 2600, 2900, 3200, 3500, 3900};
int stepper2Steps[] = {-50, 800, 1100, 1400, 1900, 2300, 2600, 2900, 3200, 3500, 3900};
int stepper3Steps[] = {50, -750, -1100, -1500, -1800, -2200, -2450, -2800, -3100, -3400, -3800};
// creating the card
MFRC522 mfrc522(SS_PIN, RST_PIN);   // Create MFRC522 instance.
int block = 1;                      // determines the block that we will read from
byte len = 18;                      // determines the length of the array
int bytesToRead = 16;               // the amount of bytes to read
byte trailerBlock = 7;              // sets the length of the trailer block
MFRC522::StatusCode status;         // will return status codes
MFRC522::MIFARE_Key key;            // key used for authentication
// keypad stuff
const int rowNum = 4;               // number of rows on the keypad
const int columnNum = 4;            // number of columns on the keypad
char keys[rowNum][columnNum] = {    // layout of the keypad
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};
// pins connected to the keypad
byte pinRows[rowNum] = {23, 25, 27, 29}; 
byte pinColumn[columnNum] = {22, 24, 26, 28};
Keypad keypad = Keypad( makeKeymap(keys), pinRows, pinColumn, rowNum, columnNum); // setup of the keypad object
// the amount of bills that need to be dispensed

int bills10 = 0;
int bills20 = 0;
int bills50 = 0;
// time
String printTime = "";
String printAccount = "";
String printAmount = "";
// all the functions used
boolean readCardDetails();
String keypadInputs();
void eatingCard();
void processInputs(String);
void ejectingCard();
void dispense(int, int, int);
void dispenserHome();
void receiptPrinter(String , String ,String);

// setup runs once to initiate the program
void setup() {
    Serial.begin(115200); // Initialize serial communications with the PC
    while (!Serial);    // Do nothing if no serial port is opened
    SPI.begin();        // Init SPI bus
    mySerial.begin(9600);
    printer.begin();
    mfrc522.PCD_Init(); // Init MFRC522 card    
    // Prepare the key (used both as key A and as key B)
    // using FFFFFFFFFFFFh which is the default at chip delivery from the factory
    for (byte i = 0; i < 6; i++) {
        key.keyByte[i] = 0xFF;
    }
    pinMode(stopSwitch, INPUT_PULLUP);  // switch pin as input
    pinMode(refillButton, INPUT_PULLUP);
    stepper.setMaxSpeed(1000);          // set max speed
    stepper.setAcceleration(500.0);      // set accel
    dispStepper1.setMaxSpeed(1000);          // set max speed
    dispStepper1.setAcceleration(5000.0);      // set accel
    dispStepper2.setMaxSpeed(1000);          // set max speed
    dispStepper2.setAcceleration(5000.0);      // set accel
    dispStepper3.setMaxSpeed(1000);          // set max speed
    dispStepper3.setAcceleration(500.0);      // set accel
}
// runs continuously to execute the program
void loop() {
    unsigned long currentMillis = millis();
    unsigned long previousMillis = currentMillis;
    while (refillStatus) {
        if (!digitalRead(refillButton)) {
            Serial.println("Rrefilled");
            refillStatus = false;
        }
    }
    // if print is true it will print a receipt
    if (print) {
        receiptPrinter(printTime, printAccount, printAmount);
        print = false;
    }
    // if eatCard is true it will eat the card
    if (eatCard) {
        eatingCard();
    }
    // if ejectCard is true it will eject the card
    if (ejectCard) {
        ejectingCard();
    }
    // as long as checkCard is true it will try to read a card
    while (checkCard) {
        currentMillis = millis();
        // if it succesfully reads a card it will set checkCard to false, ending the while loop
        if (readCardDetails()) {
            checkCard = false;
        }
        // timeout 
        if (currentMillis - previousMillis >= 3500) {
            previousMillis = currentMillis;
            Serial.println("CItimeout");
            checkCard = false;
        }
    }
    // as long as getKeyInput is true it will read the chars that the keypad sends it
    if (getKeyInput) {
        char key = keypad.getKey();
        if (key != NO_KEY) {        // checks if key has a key stored, if so it sends it over the serial connection
            Serial.println("KP" + (String)key);
        }
    }
    // if dispenesBill is true it will dispense the set bills
    if (dipsenseBills) {
        dispense(bills10, bills20, bills50);
    }
}
/*==================== functions =====================================================================================*/
// this function gets triggered when there is an event on the serial line
// it reads all the inputs and converts them to a string that gets processed
void serialEvent(){
  while (Serial.available()){
    char inChar = (char)Serial.read();
    if (inChar == '\n'){
      processInputs(inputString);
      inputString = "";
      return;
    }
    inputString += inChar;
  }
}
// processes the input string by reading it and comparing it to know inputs
// if it finds one that fits it will execute that response
void processInputs(String input) {
    if (input.equals("")) { // empty string does nothing
        return;
        }
    if (input.equals("ping")) { // pong
        Serial.println("pong"); 
        return;
    }
    if (input.equals("AuthoriseArduino")) { // authorisation check
        Serial.println("RMega2560Ready");
        return;
    }
    if (input.equals("Creset")) { // resets the state of all possible functions
        stringComplete = false;
        inputString = "";
        checkCard = false;
        getKeyInput = false;
        eatCard = false;
        ejectCard = false;
        dipsenseBills = false;
        print = false;
        refillStatus = false;
        Serial.println("Rresetting");
        return;
    }
    if (input.equals("CcardInfo")) { // enables the checkCard 
        Serial.println("RgetCI");
        checkCard = true;
        return;
    }
    if (input.equals("CcardStop")) { // disables the checkCard
        Serial.println("RstopCI");
        checkCard = false;
        return;
    }
    if (input.equals("CgetKey")) { // enables the get keypad input
        Serial.println("RgetKey");
        getKeyInput = true;
        return;
    }
    if (input.equals("CstopKey")) { // disables getting the keypad inputs
        Serial.println("RstopKey");
        getKeyInput = false;
        return;
    }
    if (input.equals("CeatCard")) { // enables eatCard
        Serial.println("ReatingCard");
        eatCard = true;
        return;
    }
    if (input.equals("CejectCard")) { // enables ejectCard
        ejectCard = true;
        return;
    }
    if (input.startsWith("Cdis")) { // convert the input to bills to dispense
        Serial.println("R" + input);
        String b10 = input.substring(4,6);
        bills10 = b10.toInt();
        String b20 = input.substring(6,8);
        bills20 = b20.toInt();
        String b50 = input.substring(8,10);
        bills50 = b50.toInt();
        dipsenseBills = true;
        return;
    }
    if (input.startsWith("Cprint")) { // converts in the input to printable text
        Serial.println("R" + input);
        printTime = input.substring(6,25);
        printAccount = input.substring(26, 42);
        printAmount = input.substring(41);
        print = true;
        return;
    }
    if (input.equals("Crefill")) {
        refillStatus = true;
        return;
    }
}
// reads the info on the card and sends it over the serial connection
// returns true if succesful, false if not
boolean readCardDetails() {
    String outputString = "";
    mfrc522.PCD_Init(); // initiate the reader
    // if there is a new card and succesful read, execute the function
    if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) { 
    // Authenticate using key A
    
    // tries to use the authentication key and other info to authenticate the card, if this fails it will send an error code
    status = (MFRC522::StatusCode) mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, block, &key, &(mfrc522.uid));
    if (status != MFRC522::STATUS_OK) {
        Serial.println("CIerCard1");
        return false;
    }
    byte buffer[len]; // setting up the buffer array 
    // reads the card and stores data in the buffer, error code if fails
    status = mfrc522.MIFARE_Read(block, buffer, &len); 
    if (status != MFRC522::STATUS_OK) {
        Serial.println("CIerCard2");
        return false;
    }
    // converts the bytes in the array to a string
    for (uint8_t i = 0; i < bytesToRead; i++) {
        outputString = outputString + (char)buffer[i];
    }
    mfrc522.PICC_HaltA();   // sets the card to halt, effectively putting it to sleep
    Serial.println("CI" + outputString); // sends output over the serial connection
    return true;
    }
    return false;
}

void eatingCard() {
    // setting up a millis timer
    unsigned long startTime = millis();
    unsigned long currentTime = startTime;
    // reading the stop switch and checking the timeout
    // while they are both false it will keep running the stepper till either the timeout gets reached or the stopswitch activates
    while(digitalRead(stopSwitch)) {
        stepper.setSpeed(1000);
        stepper.runSpeed();
        currentTime = millis();
        // determines of the timeout is reached
        // if the time out is reached it will send an error code over the serial connection and exit the function
        if (currentTime - startTime >= 20000) {
            Serial.println("ReatCardTime");
            eatCard = false;
            return;
        }
    }
    // function was succesful in eating the card and will now send a confirmation over the serial connection
    Serial.println("RcardEaten");
    eatCard = false;    
}
// ejects the card
void ejectingCard() {
    stepper.setCurrentPosition(0);
    stepper.moveTo(-3000); // 3000 brings the card back far enough to take it out but it won't fall out on it's own
    stepper.runToPosition();
    Serial.println("RcardEjected");
    ejectCard = false;
}
// moves the steppers to dispense the wanted amount of money
void dispense(int ten, int twenty, int fifty) {
    // uses the stepper steps arrays to determine what distance to move
    dispStepper1.moveTo(stepper1Steps[ten]);
    dispStepper1.runToPosition();
    
    dispStepper2.moveTo(stepper2Steps[twenty]);
    dispStepper2.runToPosition();
    
    dispStepper3.moveTo(stepper3Steps[fifty]);
    dispStepper3.runToPosition();
    // homes the steppers 
    dispenserHome();
    // resets the bills 
    bills10 = 0;
    bills20 = 0;
    bills50 = 0;
    Serial.println("RdTrue");
    dipsenseBills = false;
}
// moves the steppers back home
void dispenserHome() {
    dispStepper1.moveTo(-50);
    dispStepper1.runToPosition();
    delay(1000);
    dispStepper1.setCurrentPosition(0);
    dispStepper2.moveTo(-50);
    dispStepper2.runToPosition();
    delay(1000);
    dispStepper2.setCurrentPosition(0);
    dispStepper3.moveTo(50);
    dispStepper3.runToPosition();
    delay(1000);
    dispStepper3.setCurrentPosition(0);
}
// prints the receipt
void receiptPrinter(String dateInput , String account, String amountPinned) {
    String acc = account.substring(9,15);
    printer.wake();
    printer.setDefault();

    printer.setSize('M');
    printer.justify('C');
    printer.boldOn();

    printer.println(F("Kopie Kaarthouder"));
  
    printer.setLineHeight(50);
    printer.setSize('L');
  
    printer.println(F("KR-IV"));

    printer.setSize('S');
    printer.setLineHeight(); //default
    printer.boldOff();
    printer.println(F("Wijnhaven 107"));
    printer.println(F("3011 WN ROTTERDAM"));

    printer.justify('L');
    printer.setLineHeight(50);

    printer.println(F("ATM: WHR01"));

    printer.setLineHeight(); //default
  
    printer.println(F("Geldopname"));
    printer.println(F("Kabinet Rutte IV"));
    printer.println("Kaart: xxxxxxxxxx" + acc);

    printer.setSize('L');
    printer.boldOn();
    printer.setLineHeight(50);

    printer.println(F("TRANSACTIE"));

    printer.setSize('S');
    printer.boldOff();
    printer.setLineHeight(); //default

    printer.println(dateInput);

    printer.setLineHeight(50);
    printer.setSize('M');
    printer.boldOn();

    printer.print(F("Totaal: "));
    printer.print(amountPinned);
    printer.print(F(" EUR\n"));

    printer.justify('C');
     printer.setSize('L');

    printer.println(F("AKKOORD"));

    printer.feed(4);

    printer.sleep();
    Serial.println("RreceiptPrinted");
}
