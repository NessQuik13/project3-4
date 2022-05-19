package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class WithdrawScreenController {
    private Timer timer;


    @FXML
    private Label T1;
    public void initialize(){

        Singleton language = Singleton.getInstance();
        if (language.getIsEnglish() == false) {
            T1.setText("Kies een hoeveelheid om op te nemen");
            submitAbort.setText("Anuleren");
            submitReturn.setText("Terug");
            submitCustom.setText("Aangepast");
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
    private Button submitTen;
    @FXML
    protected void submitTenAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("ContinueScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitTwenty;
    @FXML
    protected void submitTwentyAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("ContinueScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitFifty;
    @FXML
    protected void submitFiftyAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("ContinueScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitTwohundred;
    @FXML
    protected void submitTwohundredAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("ContinueScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitFivehundred;
    @FXML
    protected void submitFivehundredAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("ContinueScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitCustom;
    @FXML
    protected void submitCustomAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("CustomScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitReturn;
    @FXML
    protected void submitReturnAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("TransactionScreenEngels.fxml");
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
