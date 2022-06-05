package com.example.projectgui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;


public class BillTypeScreenController {

    @FXML
    private Label T1;
    public void initialize() {
        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Geef een biljet voorkeur op");
        }
    }

    @FXML
    private Button submitTen;

    @FXML
    protected void submitTenAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("SubmitRequestScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button submitTwenty;

    @FXML
    protected void submitTwentyAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("SubmitRequestScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button submitFifty;

    @FXML
    protected void submitFiftyAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("SubmitRequestScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button submitReturn;

    @FXML
    protected void submitReturnAction() {
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("WithdrawScreen.fxml");
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
