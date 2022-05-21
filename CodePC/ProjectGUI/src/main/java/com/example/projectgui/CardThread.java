package com.example.projectgui;

import javafx.application.Platform;

import java.io.IOException;

public class CardThread implements Runnable{
    public void run() {
        SceneController controller = SceneController.getInstance();
        System.out.println("Thread started");
        if (!ArduinoControls.eatCard()) {
            Platform.runLater(() -> {try {
                controller.setScene("LanguageScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }});
            ArduinoControls.reset();
            return;
        }
        String temp = ArduinoControls.getCardInfo();
        if (temp.startsWith("ER")) {
            Platform.runLater(() -> {try {
                controller.setScene("LanguageScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }});
            ArduinoControls.reset();
            return;
        }
        Platform.runLater(() -> {try {
            controller.setScene("PinScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }});
    }
}