package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.PrimitiveIterator;
import java.util.Timer;
import java.util.TimerTask;

public class PinScreenController implements Runnable {
    private ArrayList<String> passwords = new ArrayList<>();
    private char[] wachtwoord;
    private int Attempts = 3;

    @FXML
    private Label T1;

    public void initialize() {
        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Pin invoeren");
            submitAbort.setText("Anuleren");
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
        Singleton language = Singleton.getInstance();

        if (pinField.getText().equals("1234")) {
            Attempts = 3;
            SceneController controller = SceneController.getInstance();
            try {
                controller.setScene("TransactionScreenEngels.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(Attempts == 1){
            if (!language.getIsEnglish()) {
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
            Attempts = Attempts - 1;
            if (Attempts >= 2) {
                if (!language.getIsEnglish()) {
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
                if (!language.getIsEnglish()) {
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

    @FXML
    private PasswordField pinField;

    public void connect() {
        String url = "jdbc:mysql://145.24.222.137:3306/banklocal";
        String username = "timo";
        String password = "Welkom02!";

        System.out.println("Connecting database...");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected!");

            var result = connection.createStatement().executeQuery("SELECT * FROM pas ");

            while (result.next()) {
                passwords.add(result.getString("pincode"));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
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
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        pinField.setText(ww);
                    }
                });
            }

            try{
                Thread.sleep(1);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        ArduinoControls.sendData("CstopKey\n");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println(pinField.getText());
            }
        });


//        if (wachtwoord.equals("1234")) {
//            Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                    SceneController controller = SceneController.getInstance();
//                    try {
//                        controller.setScene("TransactionScreenEngels.fxml");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }

    }
}