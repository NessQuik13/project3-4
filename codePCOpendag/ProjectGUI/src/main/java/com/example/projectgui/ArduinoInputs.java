package com.example.projectgui;

import com.fazecast.jSerialComm.SerialPort;
import java.util.Scanner;

public class ArduinoInputs extends Thread {
    private SerialPort arduinoPort;
    private String recData;
    private String cardInfo = "";
    private Character KPinput;
    private Boolean disConfirm = false;
    public boolean KPnew = false;

    ArduinoInputs(SerialPort ap) {
        this.arduinoPort = ap;
    }

    public Boolean getDisConfirm() {
        return disConfirm;
    }

    public void setDisConfirm(Boolean disConfirm) {
        this.disConfirm = disConfirm;
    }

    public String getRecData() {
        return this.recData;
    }
    public String getCardInfo() {
        return this.cardInfo;
    }
    public Character getKPinput() {
        KPnew = false;
        return this.KPinput;
    }
    public void resetCardInfo() {this.cardInfo = "";}

    private void dataProcessing() {
        System.out.println("processing");
        // data filtration
        // if the received data starts with CI it saves it in the cardInfo variable.
        if (!recData.equals("")) {
            if (recData.startsWith("R")) {
                System.out.println(recData);
            }
            if (recData.startsWith("CI") && !recData.equals(cardInfo)) {
                cardInfo = recData.substring(2);
                recData = "";
                return;
            }
            // inputs keypad
            if (recData.startsWith("KP")) {
                KPinput = recData.charAt(2);
                KPnew = true;
                recData = "";
            }
            if (recData.startsWith("Rd")) {
                if (recData.contains("dTrue")) {
                    disConfirm = true;
                }
            }
        }
    }

    public void run() {
        Scanner scanner = new Scanner(arduinoPort.getInputStream());
        while (arduinoPort.isOpen()) {
            while (scanner.hasNextLine()) {
                try {
                    recData = scanner.nextLine();
                    dataProcessing();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        scanner.close();
    }


}

