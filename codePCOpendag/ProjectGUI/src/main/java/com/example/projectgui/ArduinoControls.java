package com.example.projectgui;

import com.fazecast.jSerialComm.SerialPort;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ArduinoControls {
    static SerialPort[] ports;                  // list of all ports found
    static SerialPort arduinoPort = null;       // port with arduino attached to it
    static String recDataArduino;               // storage for the received data
    static ArduinoInputs inputs;
    static String accCountry;
    static String accBank;
    static String accNumber;
    protected static int dispensed10 = 0;
    protected static int dispensed20 = 0;
    protected static int dispensed50 = 0;

    public static int getDispensed10() {
        return dispensed10;
    }
    public static int getDispensed20() {
        return dispensed20;
    }
    public static int getDispensed50() {
        return dispensed50;
    }

    public static void setDispensed10(int dispensed10) {
        ArduinoControls.dispensed10 = dispensed10;
    }
    public static void setDispensed20(int dispensed20) {
        ArduinoControls.dispensed20 = dispensed20;
    }
    public static void setDispensed50(int dispensed50) {
        ArduinoControls.dispensed50 = dispensed50;
    }

    // setting up communication by looking at all the ports and picking the one that has the right name.
    // after that it tries to set it up, if this succeeds there will be a thread that listens to arduino inputs.
    static boolean setupCommunication() {
        if (arduinoPort != null && arduinoPort.isOpen()) {
            System.out.println("port is already open");
            return true;
        }
        if (arduinoPort == null) {
            System.out.println("Getting arduino port");
            ports = SerialPort.getCommPorts();
            // checks all the ports descriptions till it finds the one with the mega
            for (SerialPort temp : ports) {
                if (temp.getPortDescription().contains("Arduino Mega 2560")) {
                    arduinoPort = temp;
                    System.out.println(temp.getSystemPortName() + " has arduino attached to it, set as arduino port.");
                }
            }
        }
        // if no port was found it will leave the function
        if (arduinoPort == null) {
            System.out.println("No suitable port found, angry");
            return false;
        }
        // setting up the parameters for the serial connection
        arduinoPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0,0);
        // if the port successfully opens it will also start a new thread that scans the inputs. For more details check com.example.projectgui.ArduinoInputs
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
    static boolean getCardInfo() {
        System.out.println("Getting card info");
        sendData("CcardInfo\n");
        // loop that runs till it gets the account info or it times out.
        while (inputs.getCardInfo().equals("") || inputs.getCardInfo() == null) {
            System.out.println("waiting");
            // if its over 10 seconds since a card got requested it will time out.
            try {Thread.sleep(100);} catch (Exception e) {e.printStackTrace();}
        }
        String temp = inputs.getCardInfo();
        if (temp.equals("") || temp.equals("erCard1") || temp.equals("erCard2") || temp.equals("timeout")) {
            sendData("CcardStop\n");
            System.out.println(temp);
            inputs.resetCardInfo();
            ejectCard();
            return false;
        }
        System.out.println(temp);
        accNumber = temp;
        accCountry = temp.substring(0,2);
        accBank = temp.substring(2,6);
        inputs.resetCardInfo();
        sendData("CcardStop\n");
        return true;
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
                Thread.sleep(100);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
        return true;
    }
    // eject the pincard
    static void ejectCard() {
        System.out.println("Ejecting card");
        sendData("CejectCard\n");
        while (!inputs.getRecData().equals("RcardEjected")) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
    }
    // test method for keypad
    static Character getKeypad() {
        System.out.println("getting keypad inputs");
        while (!inputs.KPnew) {
            try{ Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        }
        return inputs.getKPinput();
    }
    //dispense bills
    static void dispense(int b10, int b20, int b50) {
        System.out.println("Dispensing bills");
        String sb10;
        String sb20;
        String sb50;
        if (b10 < 10) {
            sb10 = "0" + b10;
        } else {sb10 = String.valueOf(b10);}
        if (b20 < 10) {
            sb20 = "0" + b20;
        } else {sb20 = String.valueOf(b20);}
        if (b50 < 10) {
            sb50 = "0" + b50;
        } else {sb50 = String.valueOf(b50);}
        sendData("Cdis" + sb10 + sb20 + sb50 +"\n");
        while (!inputs.getDisConfirm()) {
            try{
                Thread.sleep(10);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("money has been dispensed");
        dispensed10 = b10;
        dispensed20 = b20;
        dispensed50 = b50;
        inputs.setDisConfirm(false);
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
    static void printReceipt() {
        System.out.println("Printing receipt");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String time = dtf.format(now);
        sendData("Cprint" + time + ArduinoControls.accNumber + SubmitRequestScreenController.getReceiptAmount() +"\n");
    }
    static void abort() {
        ArduinoControls.ejectCard();
        ArduinoControls.reset();
    }

}