package com.example.projectgui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class BalanceScreenController extends API {

    private Timer timer;

    @FXML
    private TableView table;

    @FXML
    private Label T1;

    @FXML
    private Label Balance;

    @FXML
    private Label Money;

    @FXML
    private TableColumn Transaction;

    public void initialize() {

        String balance;

        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Balans");
            submitAbort.setText("Annuleren");
            submitReturn.setText("Terug");
            submitContinue.setText("Doorgaan");
        }

        if (language.getIsEnglish()) {
            Balance.setText("You currently have");
        } else {
            Balance.setText("U bezit op dit moment");
        }

        balance = "€ " + String.valueOf(displayBalance);
        Money.setText(balance);
    }

    @FXML
    private Button submitReturn;

    @FXML
    protected void submitReturnAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("TransactionScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button submitContinue;

    @FXML
    protected void submitContinueAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("ContinueScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button submitAbort;

    @FXML
    protected void submitAbortAction() {
        SceneController controller = SceneController.getInstance();
        ArduinoControls.ejectCard();
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
