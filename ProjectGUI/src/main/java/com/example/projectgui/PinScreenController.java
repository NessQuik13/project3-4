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
    public static String pincodePinScreen;

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

        pincodePinScreen = pinField.getText();

        try {
            API.balance("GR","KRIV","GRKRIV0000123401", pincodePinScreen);
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
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        ArduinoControls.setupCommunication();
        ArduinoControls.inputs.resetKPinput();
        String wachtwoord = "";
        ArduinoControls.sendData("CgetKey\n");
        System.out.println("get key pin");
        while (wachtwoord.length() < 4) {
            String keyInput = ArduinoControls.getKeypadInputs();
            if(keyInput == ""){
            }
            else{
                ArduinoControls.inputs.resetKPinput();
                wachtwoord += keyInput;
                final String ww = wachtwoord;
                Platform.runLater(() -> pinField.setText(ww));
            }

            try{
                Thread.sleep(1);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        Platform.runLater(() -> System.out.println(pinField.getText()));

    }
}