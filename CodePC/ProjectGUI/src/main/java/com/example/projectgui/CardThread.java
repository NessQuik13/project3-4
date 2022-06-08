package com.example.projectgui;

import javafx.application.Platform;

import java.io.IOException;

public class CardThread implements Runnable{
    // eats the card till it hits the switch, checks it, if it reads a proper card it goes to the pinscreen
    public void run() {
        SceneController controller = SceneController.getInstance();
        System.out.println("Thread started");
        if (!ArduinoControls.eatCard()) {
            Platform.runLater(() -> {try {
                controller.setScene("LanguageScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }});
            return;
        }
        ;
        if (!ArduinoControls.getCardInfo()) {
            Platform.runLater(() -> {try {
                controller.setScene("LanguageScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }});
            return;
        }
        Platform.runLater(() -> {try {
            controller.setScene("PinScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }});
    }
}
