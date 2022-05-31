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
            }
            else {
                //Eject card
                Warning.setText("Ejecting card......");
            }
            pinField.setText("");
        }
        else {
            Attempts = Attempts - 1;
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