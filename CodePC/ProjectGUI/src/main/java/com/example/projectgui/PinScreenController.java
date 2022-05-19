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

public class PinScreenController{
    private ArrayList<String> passwords = new ArrayList<>();
    private char[] wachtwoord;



    @FXML
    private Label T1;

    public void initialize() {
        Singleton language = Singleton.getInstance();
        if (language.getIsEnglish() == false) {
            T1.setText("Pin invoeren");
            submitAbort.setText("Anuleren");
            submitReturn.setText("Terug");
            submitPin.setText("Indienen");
        }
    }

    @FXML
    private Button submitPin;
    @FXML
    protected void submitPinAction(){
        connect();
        pinFieldRead();
        }



    @FXML
    private Button submitReturn;
    @FXML
    protected void submitReturnAction(){
        System.out.println(pinField.getText());
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
    protected void submitAbortAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private PasswordField pinField;

    protected void pinFieldRead() {
        String wachtwoord = "";
        ArduinoControls.sendData("CgetKey\n");
        System.out.println("get key pin");
        while (wachtwoord.length() < 4) {
            wachtwoord = ArduinoControls.getKeypadInputs();
            pinField.setText(pinField.getText() + "*");
        }
        System.out.println(wachtwoord);
        pinField.setText("");
        if (true/*passwords.contains(String.valueOf(wachtwoord))*/) {
            SceneController controller = SceneController.getInstance();
            try {
                controller.setScene("TransactionScreenEngels.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void connect(){
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
}
