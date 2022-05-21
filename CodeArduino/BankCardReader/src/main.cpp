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
//#include <Stepper.h>
#include <Keypad.h>

#define RST_PIN         5           // Configurable, see typical pin layout above
#define SS_PIN          53          // Configurable, see typical pin layout above


// variables used for timer
unsigned long currentMillis = 0;
unsigned long previousMillis = 0;
unsigned long activity = 0;
unsigned long lastActivity = 0;
// input variables
boolean stringComplete = false;
String inputString = "";
boolean checkCard = false;
boolean getkeyInput = false;
boolean eatCard = false;
boolean ejectCard = false;
// setting up stepper
#define stopSwitch 7
#define motorPin1  8      // IN1 on the ULN2003 driver
#define motorPin2  9      // IN2 on the ULN2003 driver
#define motorPin3  10     // IN3 on the ULN2003 driver
#define motorPin4  11     // IN4 on the ULN2003 driver
#define motorInterfaceType 8
// int steps = 4096;
AccelStepper stepper = AccelStepper(motorInterfaceType, motorPin1, motorPin3, motorPin2, motorPin4);
// Stepper stepper(steps, motorPin1, motorPin3, motorPin2, motorPin4);

// creating the card
MFRC522 mfrc522(SS_PIN, RST_PIN);   // Create MFRC522 instance.
byte block = 4;
byte len = 18;
byte trailerBlock = 7;
MFRC522::StatusCode status;
MFRC522::MIFARE_Key key;
// keypad stuff
const int rowNum = 4;
const int columnNum = 4;
char keys[rowNum][columnNum] = {
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};
byte pinRows[rowNum] = {23, 25, 27, 29}; 
byte pinColumn[columnNum] = {22, 24, 26, 28};
Keypad keypad = Keypad( makeKeymap(keys), pinRows, pinColumn, rowNum, columnNum);
// functions
String readCardDetails();
String keypadInputs();
void eatingCard();
void processInputs(String);


void setup() {
    Serial.begin(115200); // Initialize serial communications with the PC
    while (!Serial);    // Do nothing if no serial port is opened (added for Arduinos based on ATMEGA32U4)
    SPI.begin();        // Init SPI bus
    mfrc522.PCD_Init(); // Init MFRC522 card    
    // Prepare the key (used both as key A and as key B)
    // using FFFFFFFFFFFFh which is the default at chip delivery from the factory
    for (byte i = 0; i < 6; i++) {
        key.keyByte[i] = 0xFF;
    }
    pinMode(stopSwitch, INPUT_PULLUP); // switch pin as input
    stepper.setMaxSpeed(1000); // set max speed
    stepper.setAcceleration(50.0); // set accel
}

void loop() {
    // if (stringComplete) {
    //     processInputs(inputString);
    //     inputString = "";
    //     stringComplete = false;
    // }
    
    if (eatCard) {
        eatingCard();
    }

    while (checkCard) {
        // checks for a card
        if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
            String temp = readCardDetails();
            if (temp.startsWith("ER")) {
                Serial.println("CIerror");
                checkCard = false;
                return;
            } else {
                Serial.println("CI" + temp);
                checkCard = false;
            }
        }   
    }

    while (getkeyInput) {
        char key = keypad.getKey();
        if (key != NO_KEY) {
            Serial.println("KP" + (String)key);
        }
    }
}
// functie om info op de kaart te lezen en door te geven.
// 
String readCardDetails() {
    // Authenticate using key A
    String outputString = "";
    status = (MFRC522::StatusCode) mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));
    if (status != MFRC522::STATUS_OK) {
        return "ERcard1";
    }
    byte buffer[18];
    status = mfrc522.MIFARE_Read(block, buffer, &len);
    if (status != MFRC522::STATUS_OK) {
        return "ERcard2";
    }
    for (uint8_t i = 0; i < 16; i++) {
        outputString = outputString + (char)buffer[i];
    }
    mfrc522.PICC_HaltA();
    return outputString;
}

void eatingCard() {
    boolean timeout = false;
    unsigned long startTime = millis();
    unsigned long currentTime = startTime;
    while(digitalRead(stopSwitch) && !timeout) {
        stepper.setSpeed(5000);
        stepper.runSpeed();
        currentTime = millis();
        if (currentTime - startTime >= 20000) {
            timeout = true;
        }
    }

    if (timeout) {
        //stepper.stop();
        //stepper.runToPosition();
        Serial.println("ReatCardTime");
        eatCard = false;
        return;
    }
    //stepper.stop();
    //stepper.runToPosition();
    //stepper.setCurrentPosition(0);
    Serial.println("RcardEaten");
    eatCard = false;    
}

void serialEvent(){
  while (Serial.available()){
    char inChar = (char)Serial.read();
    if (inChar == '\n'){
      //stringComplete = true;
      processInputs(inputString);
      inputString = "";
      return;
    }
    inputString += inChar;
  }
}

void processInputs(String input) {
    if (input.equals("")) { return;}
    if (input.equals("AuthoriseArduino")) {
        Serial.println("RMega2560Ready");
        return;
    }
    if (input.equals("Creset")) {
        checkCard = false;
        getkeyInput = false;
        eatCard = false;
        Serial.println("Rresetting");
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
        getkeyInput = true;
        return;
    }
    if (input.equals("CstopKey")) {
        Serial.println("RstopKey");
        getkeyInput = false;
        return;
    }
    if (input.equals("CeatCard")) {
        Serial.println("ReatingCard");
        eatCard = true;
        return;
    }
    if (input.equals("Cejectcard")) {
        Serial.println("RejectingCard");
        return;
    }
}