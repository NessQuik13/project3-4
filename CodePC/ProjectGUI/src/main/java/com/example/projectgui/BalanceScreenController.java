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

public class BalanceScreenController {

    private Timer timer;

    @FXML
    private TableView table;

    @FXML
    private Label T1;

    @FXML
    private TableColumn Balance;

    @FXML
    private TableColumn Transaction;

    public void initialize() {

        Singleton language = Singleton.getInstance();
        if (language.getIsEnglish() == false) {
            T1.setText("Balans");
            submitAbort.setText("Anuleren");
            submitReturn.setText("Terug");
            submitContinue.setText("Doorgaan");
        }

        final ObservableList<FileData> data = FXCollections.observableArrayList(
                new FileData("test", "test"),
                new FileData("test", "test")
        );


        Balance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        Transaction.setCellValueFactory(new PropertyValueFactory("transaction"));

        ObservableList<String> list = FXCollections.observableArrayList();
        table.setItems(data);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getColumns().addAll(Balance,Transaction);


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {


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
        }, 60000);
    }

    @FXML
    private Button submitReturn;

    @FXML
    protected void submitReturnAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("TransactionScreenEngels.fxml");
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
            controller.setScene("ContinueScreenEngels.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button submitAbort;

    @FXML
    protected void submitAbortAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
