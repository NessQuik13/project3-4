package com.example.projectgui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;


public class WithdrawScreenController extends API{


    @FXML
    private Label T1;

    @FXML
    private Label Limit;

    @FXML
    private Label Amount;

    protected static int Geld = 0;

    public static int getGeld() {
        return Geld;
    }

    public void initialize(){

        Geld = 0;

        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Kies een hoeveelheid om op te nemen");
            submitAbort.setText("Annuleren");
            submitReturn.setText("Terug");
            submitReset.setText("Bedrag resetten");
            submitAmount.setText("Indienen");
            submitBillType.setText("Biljet voorkeur");
        }
    }

    @FXML
    private Button submitTen;
    @FXML
    protected void submitTenAction(){
        Geld = Geld + 10;
        Amount.setText("€" + Geld);
    }
    @FXML
    private Button submitTwenty;
    @FXML
    protected void submitTwentyAction(){
        Geld = Geld + 20;
        Amount.setText("€" + Geld);
    }
    @FXML
    private Button submitFifty;
    @FXML
    protected void submitFiftyAction(){
        Geld = Geld + 50;
        Amount.setText("€" + Geld);
    }
    @FXML
    private Button submitTwohundred;
    @FXML
    protected void submitTwohundredAction(){
        Geld = Geld + 200;
        Amount.setText("€" + Geld);
    }
    @FXML
    private Button submitFivehundred;
    @FXML
    protected void submitFivehundredAction(){
        Geld = Geld + 500;
        Amount.setText("€" + Geld);
    }
    @FXML
    private Button submitReset;
    @FXML
    protected void submitResetAction(){
        Geld = 0;
        Amount.setText("€" + Geld);
        Limit.setText("");
        }

    protected int currentlyLoaded() {
        return ((10 - ArduinoControls.getDispensed10()) * 10) + ((10 - ArduinoControls.getDispensed20()) * 20) + ((10 - ArduinoControls.getDispensed50()) * 50);
    }
    @FXML
    private Button submitAmount;
    @FXML
    protected void submitAmountAction(){
        if (Geld > currentlyLoaded()) {
            Limit.setText("Amount exceeds current money available in atm: " + currentlyLoaded());
            Singleton language = Singleton.getInstance();
            if (!language.getIsEnglish()) {
                Limit.setText("Bedrag is hoger dan de automaat kan leveren: " + currentlyLoaded());

            }
            return;
        }
        if (Geld <= displayBalance) {
            SceneController controller = SceneController.getInstance();
            try {
                controller.setScene("SubmitRequestScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Limit.setText("Amount exceeds limit");
            Singleton language = Singleton.getInstance();
            if (!language.getIsEnglish()) {
             Limit.setText("Bedrag overschrijdt limiet");
            }
        }
    }

    @FXML
    private Button submitBillType;
    @FXML
    protected void submitBillTypeAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("BillTypeScreen.fxml");
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
            controller.setScene("TransactionScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitAbort;
    @FXML
    protected void submitAbortAction(){
        SceneController controller = SceneController.getInstance();
        ArduinoControls.abort();
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
