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

public class WithdrawScreenController extends API{


    @FXML
    private Label T1;

    @FXML
    private Label Amount;

    private int Geld = 0;

    public void initialize(){

        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Kies een hoeveelheid om op te nemen");
            submitAbort.setText("Annuleren");
            submitReturn.setText("Terug");
            submitReset.setText("Bedrag resetten");
            submitAmount.setText("Indienen");
        }
    }

    @FXML
    private Button submitTen;
    @FXML
    protected void submitTenAction(){
        Geld = Geld + 10;
        Amount.setText("€" + String.valueOf(Geld));
    }
    @FXML
    private Button submitTwenty;
    @FXML
    protected void submitTwentyAction(){
        Geld = Geld + 20;
        Amount.setText("€" + String.valueOf(Geld));
    }
    @FXML
    private Button submitFifty;
    @FXML
    protected void submitFiftyAction(){
        Geld = Geld + 50;
        Amount.setText("€" + String.valueOf(Geld));
    }
    @FXML
    private Button submitTwohundred;
    @FXML
    protected void submitTwohundredAction(){
        Geld = Geld + 200;
        Amount.setText("€" + String.valueOf(Geld));
    }
    @FXML
    private Button submitFivehundred;
    @FXML
    protected void submitFivehundredAction(){
        Geld = Geld + 500;
        Amount.setText("€" + String.valueOf(Geld));
    }
    @FXML
    private Button submitReset;
    @FXML
    protected void submitResetAction(){
        Geld = 0;
        Amount.setText("€" + String.valueOf(Geld));
    }

    @FXML
    private Button submitAmount;
    @FXML
    protected void submitAmountAction(){
        try {
            API.withdraw(ArduinoControls.accCountry,ArduinoControls.accBank,ArduinoControls.accNumber,PinScreenController.pincodePinScreen,Geld);
        } catch (URISyntaxException | IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
        int Response = Integer.parseInt(API.withdrawResponse);
        System.out.println(Response);
        if(Response == 200) {
            SceneController controller = SceneController.getInstance();
            try {
                controller.setScene("ContinueScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            displayBalance = displayBalance - Geld;
        }
        else {
            System.out.println("HIER MOET NOG IETS");
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
        ArduinoControls.ejectCard();
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
