package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.net.URISyntaxException;

public class PinScreenController  implements Runnable {
    public static String pincodePinScreen = "";

    @FXML
    public PasswordField pinField;


    @FXML
    private Label T1;

    public void initialize() {
        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
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
            if (!language.getIsEnglish()) {
                Warning.setText("Kaart uitwerpen.....");
            }
            else {
                //Eject card
                Warning.setText("Ejecting card......");
            }
            pinField.setText("");
        }
        else {
            if (Attempts >= 2) {
                if (!language.getIsEnglish()) {
                    Warning.setText("Foute pin, " + Attempts + " pogingen over");
                }
                else {
                    Warning.setText("Wrong pin, " + Attempts + " attempts left");
                }
            }
            else {
                if (!language.getIsEnglish()) {
                    Warning.setText("Foute pin, " + Attempts + " poging over");
                }
                else {
                    Warning.setText("Wrong pin, " + Attempts + " attempt left");
                }
            }
            pinField.setText("");
            initialize();
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
        ArduinoControls.ejectCard();
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        String password = "";
        String dots = "";
        boolean pinConfirm = false;
        Character keyInput;
        ArduinoControls.sendData("CgetKey\n");
        System.out.println("get key pin");
        while (!pinConfirm) {
            keyInput = ArduinoControls.getKeypad();
            switch (keyInput) {
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                    if (password.length() < 4) {
                        password = password.concat(String.valueOf(keyInput));
                        dots = dots.concat(String.valueOf('*'));
                        pinField.setText(dots);
                    }
                }
                case '*' -> { // removes last character
                    if (password.length() >= 1) {
                        password = password.substring(0, password.length() - 1);
                        if (dots.length() >= 1) {
                        dots = dots.substring(0, dots.length() - 1);
                        pinField.setText(dots);
                        }
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
        pincodePinScreen= password.toString();
        Platform.runLater(this::submitPinAction);
            }
}