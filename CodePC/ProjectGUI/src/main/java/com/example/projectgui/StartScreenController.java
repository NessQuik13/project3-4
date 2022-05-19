package com.example.projectgui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class StartScreenController {
    private Timer timer;
    @FXML
    private Label T1;

    public void initialize(){
        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Voer Uw pas in");
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        SceneController controller = SceneController.getInstance();
                        if (!ArduinoControls.eatCard()) {
                            try {
                                controller.setScene("LanguageScreen.fxml");
                            } catch (IOException e) {e.printStackTrace();}
                        }
                        if (ArduinoControls.getCardInfo().startsWith("ER")) {
                            try {
                                controller.setScene("LanguageScreen.fxml");
                            } catch (IOException e) {e.printStackTrace();}
                        }

                        try {
                            controller.setScene("PinScreenEngels.FXML");
                        } catch (IOException e) {e.printStackTrace();}
                    }
                });
            }
        },50);
    }
}
