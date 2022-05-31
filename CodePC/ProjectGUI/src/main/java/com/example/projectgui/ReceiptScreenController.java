package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ReceiptScreenController {
    private Timer timer;
    @FXML
    private Label T1;
    public void initialize(){
        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Wilt U een bon?");
            submitNo.setText("Nee");
            submitYes.setText("Ja");
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        SceneController controller = SceneController.getInstance();
                        try {
                            controller.setScene("StartScreen.fxml");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        },60000);
    }
    @FXML
    private Button submitNo;
    @FXML
    protected void submitNoAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("FinishScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitYes;
    @FXML
    protected void submitYesAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("WaitReceiptScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
