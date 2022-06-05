package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FinishScreenController {


    @FXML
    private Label T1;
    @FXML
    private Label T2;
    public void initialize(){

        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Dankuwel");
            T2.setText("Voor het gebruiken van onze ATM");
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    SceneController controller = SceneController.getInstance();
                    try {
                        controller.setScene("LanguageScreen.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        },8000);
    }
}
