package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ContinueScreenController {
    private Timer timer;




    public void initialize(){

        Singleton language = Singleton.getInstance();
        if (language.getIsEnglish() == false) {
            submitAbort.setText("Anuleren");
            submitFinish.setText("Transactie beeindigen");
            submitContinue.setText("Transactie voortzetten");
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
    private Button submitContinue;
    @FXML
    protected void submitContinueAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("TransactionScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitFinish;
    @FXML
    protected void submitFinishAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("ReceiptScreenEngels.fxml");
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
}
