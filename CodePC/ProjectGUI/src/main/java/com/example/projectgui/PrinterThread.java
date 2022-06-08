package com.example.projectgui;

public class PrinterThread implements Runnable{
    @Override
    public void run() {
        ArduinoControls.printReceipt();
    }
}
