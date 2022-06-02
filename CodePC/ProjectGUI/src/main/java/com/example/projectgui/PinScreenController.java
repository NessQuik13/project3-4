package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class PinScreenController  implements Runnable {
    private ArrayList<String> passwords = new ArrayList<>();
    private char[] wachtwoord;
    public static String pincodePinScreen;

    @FXML
    public PasswordField pinField;


    @FXML
    private Label T1;

    public void initialize() {
        Singleton language = Singleton.getInstance();
        if (language.getIsEnglish() == false) {
            T1.setText("Pin invoeren");
            submitAbort.setText("Annuleren");
            submitReturn.setText("Terug");
            submitPin.setText("Indienen");
        }
        Thread keyThread = new Thread(this);
        keyThread.start();
    }

    @FXML
    private Button submitPin;

    @FXML
    private Label Warning;

    @FXML
    protected void submitPinAction() {

        pincodePinScreen = pinField.getText();

        try {
            API.balance(ArduinoControls.accCountry,ArduinoControls.accBank,ArduinoControls.accNumber, pincodePinScreen);
        } catch (URISyntaxException | IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }

        int Response = Integer.parseInt(API.balanceResponse);
        System.out.println(Response);
        int Attempts = API.loginAttemptsLeft;
        System.out.println(Attempts);

        Singleton language = new Singleton();

        if (Response == 200) {
            SceneController controller = SceneController.getInstance();
            try {
                controller.setScene("TransactionScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if(Attempts == 0){
            if (language.getIsEnglish() == false) {
                Warning.setText("Kaart uitwerpen.....");
                pinField.setText("");
            }
            else {
                //Eject card
                Warning.setText("Ejecting card......");
                pinField.setText("");
            }
        }
        else {
            if (Attempts >= 2) {
                if (language.getIsEnglish() == false) {
                    Warning.setText("Foute pin, " + Attempts + " pogingen over");
                    pinField.setText("");
                    initialize();
                }
                else {
                    Warning.setText("Wrong pin, " + Attempts + " attempts left");
                    pinField.setText("");
                    initialize();
                }
            }
            else {
                if (language.getIsEnglish() == false) {
                    Warning.setText("Foute pin, " + Attempts + " poging over");
                    pinField.setText("");
                    initialize();
                }
                else {
                    Warning.setText("Wrong pin, " + Attempts + " attempt left");
                    pinField.setText("");
                    initialize();
                }
            }
        }
    }


    @FXML
    private Button submitReturn;

    @FXML
    protected void submitReturnAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button submitAbort;

    @FXML
    protected void submitAbortAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        StringBuilder password = new StringBuilder();
        boolean pinConfirm = false;
        Character keyInput;
        ArduinoControls.sendData("CgetKey\n");
        System.out.println("get key pin");
        while (!pinConfirm) {
            keyInput = ArduinoControls.getKeypad();
            switch (keyInput) {
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    if (password.length() >= 4) {
                        password.append(keyInput);
                    }
                }
                case '*' -> { // removes last character
                    if (password.length() >= 1) {
                        password = new StringBuilder(password.substring(0, password.length() - 1));
                    }
                }
                case '#' -> pinConfirm = true; // confirm pin
                // if any other character, ignore
                default -> System.out.println("invalid character / input, ignored");
            }
            try{
                Thread.sleep(10);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        ArduinoControls.sendData("CstopKey\n");
        final String ww = password.toString();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pinField.setText(ww);
            }
        });
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println(pinField.getText());
            }
        });
    }
}