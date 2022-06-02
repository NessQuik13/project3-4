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

#define RST_PIN         5           // Configurable, see typical pin layout above
#define SS_PIN          53          // Configurable, see typical pin layout above
// variables used for timer
unsigned long previousMillis = 0;
unsigned long activity = 0;         // not used rn
unsigned long lastActivity = 0;     // not used rn
// input variables
boolean stringComplete = false;
String inputString = "";
boolean checkCard = false;
boolean getKeyInput = false;
boolean eatCard = false;
boolean ejectCard = false;
// setting up stepper
int distanceBills = 1000;
#define stopSwitch 7
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
#define motorInterfaceType 8
AccelStepper dispStepper1 = AccelStepper(motorInterfaceType, motorPin5, motorPin7, motorPin6, motorPin8);
#define motorPin9  36      // IN1 on the ULN2003 driver
#define motorPin10  37     // IN2 on the ULN2003 driver
#define motorPin11  38     // IN3 on the ULN2003 driver
#define motorPin12  39     // IN4 on the ULN2003 driver
#define motorInterfaceType 8
AccelStepper dispStepper2 = AccelStepper(motorInterfaceType, motorPin9, motorPin11, motorPin10, motorPin12);
#define motorPin13  42      // IN1 on the ULN2003 driver
#define motorPin14  43     // IN2 on the ULN2003 driver
#define motorPin15  44     // IN3 on the ULN2003 driver
#define motorPin16  45     // IN4 on the ULN2003 driver
#define motorInterfaceType 8
AccelStepper dispStepper3 = AccelStepper(motorInterfaceType, motorPin13, motorPin15, motorPin14, motorPin16);
// creating the card
MFRC522 mfrc522(SS_PIN, RST_PIN);   // Create MFRC522 instance.
byte block = 4;                     // determines the block that we will read from
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
// all the functions used
boolean readCardDetails();
String keypadInputs();
void eatingCard();
void processInputs(String);
void ejectingCard();
void dispense(int, int, int);
void dispenserHome();

// setup runs once to initiate the program
void setup() {
    Serial.begin(115200); // Initialize serial communications with the PC
    while (!Serial);    // Do nothing if no serial port is opened
    SPI.begin();        // Init SPI bus
    mfrc522.PCD_Init(); // Init MFRC522 card    
    // Prepare the key (used both as key A and as key B)
    // using FFFFFFFFFFFFh which is the default at chip delivery from the factory
    for (byte i = 0; i < 6; i++) {
        key.keyByte[i] = 0xFF;
    }
    pinMode(stopSwitch, INPUT_PULLUP);  // switch pin as input
    stepper.setMaxSpeed(1000);          // set max speed
    stepper.setAcceleration(500.0);      // set accel
    dispStepper1.setMaxSpeed(1000);          // set max speed
    dispStepper1.setAcceleration(500.0);      // set accel
    dispStepper2.setMaxSpeed(1000);          // set max speed
    dispStepper2.setAcceleration(500.0);      // set accel
    dispStepper3.setMaxSpeed(1000);          // set max speed
    dispStepper3.setAcceleration(500.0);      // set accel
}
// runs continuously to execute the program
void loop() {
    unsigned long currentMillis = millis();

    // runs the eatingCard function 
    // dispense(1,2,3);
    // dispenserHome(); 
    if (eatCard) {
        eatingCard();
    }
    // eject card
    if (ejectCard) {
        ejectingCard();
    }
    // as long as checkCard is true it will try to read a card
    if (checkCard) {
        currentMillis = millis();
        // if it succesfully reads a card it will set checkCard to false, ending the while loop
        if (readCardDetails()) {
            checkCard = false;
        }
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
    if (input.equals("")) { return;}
    if (input.equals("ping")) {
        Serial.println("pong"); 
        return;
    }
    if (input.equals("AuthoriseArduino")) {
        Serial.println("RMega2560Ready");
        return;
    }
    if (input.equals("Creset")) {
        checkCard = false;
        getKeyInput = false;
        eatCard = false;
        ejectCard = false;
        Serial.println("Rresetting");
        return;
        // add future functions
    }
    if (input.equals("CcardInfo")) {
        Serial.println("RgetCI");
        checkCard = true;
        return;
    }
    if (input.equals("CcardStop")) {
        Serial.println("RstopCI");
        checkCard = false;
        return;
    }
    if (input.equals("CgetKey")) {
        Serial.println("RgetKey");
        getKeyInput = true;
        return;
    }
    if (input.equals("CstopKey")) {
        Serial.println("RstopKey");
        getKeyInput = false;
        return;
    }
    if (input.equals("CeatCard")) {
        Serial.println("ReatingCard");
        eatCard = true;
        return;
    }
    if (input.equals("CejectCard")) {
        ejectCard = true;
        return;
    }
}
// reads the info on the card and sends it over the serial connection
// returns true if succesful, false if not
boolean readCardDetails() {
    mfrc522.PCD_Init(); // initiate the reader
    // if there is a new card and succesful read, execute the function
    if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) { 
    // Authenticate using key A
    String outputString = "";
    // tries to use the authentication key and other info to authenticate the card, if this fails it will send an error code
    status = (MFRC522::StatusCode) mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));
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
    boolean timeout = false;
    unsigned long startTime = startTime;
    unsigned long currentTime = millis();
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
// system might be changed, left in for now
void dispense(int ten, int twenty, int fifty) {
    // for 50 euro bills
    for(int i = 0; i < fifty; i++) {
        dispStepper1.moveTo(dispStepper1.currentPosition() - distanceBills);
        dispStepper1.runToPosition();
    }
    for(int i = 0;i < twenty; i++) {
        dispStepper2.moveTo(dispStepper2.currentPosition() + distanceBills);
        dispStepper2.runToPosition();
    }
    for(int i = 0;i < ten; i++) {
        dispStepper3.moveTo(dispStepper3.currentPosition() - distanceBills);
        dispStepper3.runToPosition();
    }
}
// same as above
void dispenserHome() {
    dispStepper1.moveTo(0);
    dispStepper1.runToPosition();
    dispStepper2.moveTo(0);
    dispStepper2.runToPosition();
    dispStepper3.moveTo(0);
    dispStepper3.runToPosition();   
}