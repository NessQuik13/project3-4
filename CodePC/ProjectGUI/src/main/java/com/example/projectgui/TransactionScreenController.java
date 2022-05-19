package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TransactionScreenController {
    private Timer timer;

    @FXML
    private Label T1;

    public void initialize() {
        Singleton language = Singleton.getInstance();
        if (language.getIsEnglish() == false) {
            T1.setText("Kies een transactie");
            submitAbort.setText("Anuleren");
            submitBalance.setText("Balans");
            submitWithdraw.setText("Opnemen");
            submitReturn.setText("Terug");
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
    private Button submitReturn;
    @FXML
    private Button submitWithdraw;
    @FXML
    private Button submitBalance;
    @FXML
    protected void submitReturnAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("PinScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void submitWithdrawAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("WithdrawScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void submitBalanceAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("BalanceScreenEngels.fxml");
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
