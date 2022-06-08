package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class RestockScreenController implements Runnable {

    @FXML
    private Label T1;

    public void initialize() {
        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("De ATM wordt momenteel hervuld");
        }
        Thread restock = new Thread(this);
        restock.start();
    }
    public void refillDone() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
}
    @Override
    public void run() {

        ArduinoControls.sendData("Crefill\n");
        while (!ArduinoControls.inputs.getRecData().equals("Rrefilled")) {
            System.out.println("waiting for refill");
        }

        ArduinoControls.setDispensed10(0);
        ArduinoControls.setDispensed20(0);
        ArduinoControls.setDispensed50(0);
        Platform.runLater(this::refillDone);
    }
}
