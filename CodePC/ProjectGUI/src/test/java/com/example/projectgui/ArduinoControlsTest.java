package com.example.projectgui;

import com.fazecast.jSerialComm.SerialPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArduinoControlsTest {
    @Test
    void analyzeSetupCommunication() throws Exception{
        ArduinoControls.setupCommunication();
        // tests that the port is actually open and that it is connected to the port with arduin
        Assertions.assertTrue(ArduinoControls.arduinoPort.isOpen() && ArduinoControls.arduinoPort.getPortDescription().contains("Arduino Mega 2560"));
    }
    @Test
    void analyzeSendData() throws Exception{
        String input = "test\n";
        ArduinoControls.sendData(input);

    }
    @Test
    void analyzeGetKeypadInputs() throws Exception {

    }





    @Test
    void getCardInfo() {
    }

    @Test
    void eatCard() {
    }

    @Test
    void getKeypad() {
    }

    @Test
    void reset() {
    }
}