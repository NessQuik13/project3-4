/**
 * ----------------------------------------------------------------------------
 * This is a MFRC522 library example; see https://github.com/miguelbalboa/rfid
 * for further details and other examples.
 *
 * NOTE: The library file MFRC522.h has a lot of useful info. Please read it.
 *
 * Released into the public domain.
 * ----------------------------------------------------------------------------
 * This sample shows how to read and write data blocks on a MIFARE Classic PICC
 * (= card/tag).
 *
 * BEWARE: Data will be written to the PICC, in sector #1 (blocks #4 to #7).
 *
 *
 * Typical pin layout used:
 * -----------------------------------------------------------------------------------------
 *             MFRC522      Arduino       Arduino   Arduino    Arduino          Arduino
 *             Reader/PCD   Uno/101       Mega      Nano v3    Leonardo/Micro   Pro Micro
 * Signal      Pin          Pin           Pin       Pin        Pin              Pin
 * -----------------------------------------------------------------------------------------
 * RST/Reset   RST          9             5         D9         RESET/ICSP-5     RST
 * SPI SS      SDA(SS)      10            53        D10        10               10
 * SPI MOSI    MOSI         11 / ICSP-4   51        D11        ICSP-4           16
 * SPI MISO    MISO         12 / ICSP-1   50        D12        ICSP-1           14
 * SPI SCK     SCK          13 / ICSP-3   52        D13        ICSP-3           15
 *
 * More pin layouts for other boards can be found here: https://github.com/miguelbalboa/rfid#pin-layout
 *
 */

#include <SPI.h>
#include <MFRC522.h>
#include <AccelStepper.h>

#define cardSensor      2           // ir sensor for detecting cards
#define cardSwitch      3           // switch at the back of the card reader, triggers when card reaches the back
#define RST_PIN         9           // Configurable, see typical pin layout above
#define SS_PIN          10          // Configurable, see typical pin layout above
#define motorInterface  8
//  variables used for timer
unsigned long currentMillis = 0;
unsigned long previousMillis = 0;
unsigned long activity = 0;
unsigned long lastActivity = 0;

// variables used for the status of card 
bool cardSeen = false;
bool ejecting = false;
bool cardInside = false;

// creating the motor and setting up it's values
AccelStepper stepper(motorInterface, 4, 6, 5, 7);
int maxSpeed = 500;
int accel = 100;
int pd = 0;

// creating the card
MFRC522 mfrc522(SS_PIN, RST_PIN);   // Create MFRC522 instance.
byte block = 4;
byte len = 18;
byte trailerBlock = 7;
MFRC522::StatusCode status;
MFRC522::MIFARE_Key key;

// interrupt protocol that gets called when the ir detects something
void cardDetected() {
    cardSeen = true;
}
// interrupt protocal that gets called when the card reached the switch
void cardConsumed() {
    cardSeen = false;
    cardInside = true;
    stepper.stop();
}
// function that is used to control the motors. if 1 is given it will eat the card, if 2 is given it will spit it out
void moveCard(int d) {
    Serial.println("");
    // setup the stepper
    switch (d){
        case 1: // eat card
            while(cardSeen){
                stepper.setMaxSpeed(maxSpeed);
                stepper.setAcceleration(accel);
                stepper.moveTo(8192);// to be determined
                stepper.runToPosition();         
                //Serial.println("eating card");
            }
            stepper.setCurrentPosition(0);
            break;
        case 2: // spit out card
            stepper.setMaxSpeed(maxSpeed);
            stepper.setAcceleration(accel);
            stepper.moveTo(-8192); // distance to be determined
            stepper.runToPosition();
            Serial.println("ejecting card");
            break;    
        default:
            Serial.println("Something went wrong during moveCard");
            break;
    }    
}
// function that is called when the card needs to get ejected
void ejectCard() {
    Serial.println("Ejecting card");
    ejecting = true;
    cardInside = false;
    moveCard(2);
    ejecting = false;
}

int readCardDetails();

void setup() {

    pinMode(cardSensor, INPUT);
    pinMode(cardSwitch, INPUT);

    attachInterrupt(digitalPinToInterrupt(cardSensor), cardDetected, RISING);
    attachInterrupt(digitalPinToInterrupt(cardSwitch), cardConsumed, CHANGE);
    
    Serial.begin(9600); // Initialize serial communications with the PC
    while (!Serial);    // Do nothing if no serial port is opened (added for Arduinos based on ATMEGA32U4)
    SPI.begin();        // Init SPI bus
    mfrc522.PCD_Init(); // Init MFRC522 card
    // Prepare the key (used both as key A and as key B)
    //using FFFFFFFFFFFFh which is the default at chip delivery from the factory
    
    for (byte i = 0; i < 6; i++) {
        key.keyByte[i] = 0xFF;
    }// might get used, not sure yet

}

void loop() {
    currentMillis = millis();
    activity = millis();
    // when a card is detected it will start the motor to eat the card
    while (cardSeen && ejecting == false) {
        Serial.println("cardseen");      
        moveCard(1);
        lastActivity = activity;
    }
    // checks for a card
    if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {
        readCardDetails();
        lastActivity = activity;
    }
    // if the card is still inside after 10 seconds it will eject
    if (activity - lastActivity > 10000 && cardInside) {
        ejectCard();
        lastActivity = activity;
    }  
    // 
}

int readCardDetails() {
    // Authenticate using key A
    Serial.println(F("Authenticating using key A..."));
    status = (MFRC522::StatusCode) mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));
    if (status != MFRC522::STATUS_OK) {
        Serial.print(F("PCD_Authenticate() failed: "));
        Serial.println(mfrc522.GetStatusCodeName(status));
        return 1;
    }
    byte buffer[18];
    status = mfrc522.MIFARE_Read(block, buffer, &len);
    if (status != MFRC522::STATUS_OK) {
        Serial.print(F("reading failed: "));
        Serial.println(mfrc522.GetStatusCodeName(status));
        return 1;
    }
    for (uint8_t i = 0; i < 16; i++) {
        if (buffer[i] != 32 ) {
            Serial.write(buffer[i]);
        }
    }
    mfrc522.PICC_HaltA();
    return 0;
}
