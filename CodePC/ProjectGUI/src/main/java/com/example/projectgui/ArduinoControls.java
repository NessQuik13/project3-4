package com.example.projectgui;

import com.fazecast.jSerialComm.SerialPort;

import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class ArduinoControls {
    static SerialPort[] ports;                  // list of all ports found
    static SerialPort arduinoPort = null;       // port with arduino attached to it
    static String recDataArduino;               // storage for the received data
    static ArduinoInputs inputs;



    // setting up communication by looking at all the ports and picking the one that has the right name.
    // after that it tries to set it up, if this succeeds there will be a thread that listens to arduino inputs.
    static boolean setupCommunication() {
//      if (ArduinoControls.arduinoPort != null && ArduinoControls.arduinoPort.isOpen()) {
//          ArduinoControls.arduinoPort.closePort();
//      }
        if (arduinoPort != null && arduinoPort.isOpen()) {
            System.out.println("port is already open");
            return true;
        }
        System.out.println("Getting arduino port");
        ports = SerialPort.getCommPorts();
        // checks all the ports descriptions till it finds the one with the mega
        for (SerialPort temp : ports) {
            if (temp.getPortDescription().contains("Arduino Mega 2560")) {
                arduinoPort = temp;
                System.out.println(temp.getSystemPortName() + " has arduino attached to it, set as arduino port.");
            }
        }
        // if no port was found it will leave the function
        if (arduinoPort == null) {
            System.out.println("No suitable port found, angry");
            return false;
        }
        // setting up the parameters for the serial connection
        arduinoPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0,0);
        // if the port successfully opens it will also start a new thread that scans the inputs. For more details check ArduinoInputs
        if (arduinoPort.openPort()){
            try {Thread.sleep(4000);} catch (Exception e) {e.printStackTrace();} // delay letting the arduino reboot
            System.out.println("Port successfully opened");
            inputs = new ArduinoInputs(arduinoPort);
            inputs.start();
        } else {
            arduinoPort.closePort();
            System.out.println("Something went wrong opening port");
            return false;
        }
        // initiate the authorisation of the arduino
        sendData("AuthoriseArduino\n");
        long startTime = System.currentTimeMillis();
        long interval = 4000;
        while (inputs.getRecData() == null) { // waits till a response gets
            System.out.println("waiting");
            if (System.currentTimeMillis() - startTime >= interval) {
                System.out.println("Authorisation timed out");
                return false;
            }
            try {Thread.sleep(100);} catch (Exception e) {e.printStackTrace();}
        }
        recDataArduino = inputs.getRecData();
        System.out.println(recDataArduino);
        if (recDataArduino.equals("RMega2560Ready")) {
            System.out.println("Verified arduino");
        } else {
            System.out.println("This is not the right arduino. Closing port");
            arduinoPort.closePort();
            return false;
        }
        // shuts down the port when the program is closed, so it prevents the port getting stuck
        //Runtime.getRuntime().addShutdownHook(new Thread(() -> arduinoPort.closePort()));
        return true;
    }
    // sends data
    static void sendData(String data) {
        byte[] buffer= data.getBytes(StandardCharsets.US_ASCII);
        if (arduinoPort != null && arduinoPort.isOpen()) {
            arduinoPort.writeBytes(buffer, buffer.length);
        } else {
            System.out.println("no port to send data too");
        }
    }
    // get card info from the arduino
    static String getCardInfo() {
        System.out.println("Getting card info");
        sendData("CcardInfo\n");
        long startTime = System.currentTimeMillis();
        long interval = 4000;
        // loop that runs till it gets the account info or it times out.
        while (inputs.getCardInfo().equals("") || inputs.getCardInfo() == null) {
            System.out.println("waiting");
            // if its over 10 seconds since a card got requested it will time out.
            if (System.currentTimeMillis() - startTime >= interval) {
                System.out.println("Time out error");
                sendData("CcardStop\n");
                return "ERTimeOut";
            }
            try {Thread.sleep(100);} catch (Exception e) {e.printStackTrace();}
        }
        String temp = inputs.getCardInfo();
        if (!temp.equals("") && !temp.equals("error")) {
            System.out.println(temp);
            inputs.resetCardInfo();
            sendData("CcardStop\n");
            return temp;
        }
        else {
            inputs.resetCardInfo();
            sendData("CcardStop\n");
            return "ERcardInfo";
        }
    }
    // eats the pincard
    static boolean eatCard() {
        sendData("CeatCard\n");
        while (!inputs.getRecData().equals("RcardEaten")) {
            if (inputs.getRecData().equals("ReatCardTime")) {
                System.out.println("eating card timed out");
                return false;
            }
            System.out.println("Waiting for card to be eaten");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
        return true;
    }
    // test method for keypad
    static Character getKeypad() {
        System.out.println("getting keypad inputs");
        while (!inputs.KPnew) {
            try{ Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        }
        return inputs.getKPinput();
    }
    // reset all commands
    static void reset() {
        sendData("Creset\n");
        while (!inputs.getRecData().equals("Rresetting")) {
            try{
                System.out.println("waiting for confirmation");
                Thread.sleep(100);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
        System.out.println("All commands have been reset");
    }

}