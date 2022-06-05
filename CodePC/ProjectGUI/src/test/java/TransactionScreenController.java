package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public class TransactionScreenController {
    private Timer timer;

    public String pincode;
    public String balanceTransaction;
    @FXML
    private Label T1;


    public void initialize() {
        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Kies een transactie");
            submitAbort.setText("Annuleren");
            submitBalance.setText("Balans");
            submitWithdraw.setText("Opnemen");
            submitReturn.setText("Terug");
        }
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
            controller.setScene("PinScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void submitWithdrawAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("WithdrawScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void submitBalanceAction(){

        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("BalanceScreen.fxml");
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
