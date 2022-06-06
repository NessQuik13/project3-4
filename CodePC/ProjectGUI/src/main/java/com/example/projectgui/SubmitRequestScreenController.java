package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;

public class SubmitRequestScreenController extends WithdrawScreenController {
    private static int bills10Dispense = 0;
    private static int bills20Dispense = 0;
    private static int bills50Dispense = 0;

    public static void setBills10Dispense(int bills10Dispense) {
        SubmitRequestScreenController.bills10Dispense = bills10Dispense;
    }
    public static void setBills20Dispense(int bills20Dispense) {
        SubmitRequestScreenController.bills20Dispense = bills20Dispense;
    }
    public static void setBills50Dispense(int bills50Dispense) {
        SubmitRequestScreenController.bills50Dispense = bills50Dispense;
    }

    @FXML
    private Label T1;

    @FXML
    private Label T2;

    public void initialize(){
        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Wilt U uw betalingsverzoek indienen?");
            T2.setText("");
            submitAbort.setText("Annuleren");
            submitYes.setText("Ja");
            submitNo.setText("Nee");
    }
    }

    @FXML
    private Button submitYes;
    @FXML
    protected void submitYesAction(){
        try {
            API.withdraw(ArduinoControls.accCountry, ArduinoControls.accBank, ArduinoControls.accNumber, PinScreenController.pincodePinScreen, Geld);
        } catch (URISyntaxException | IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
        int Response = Integer.parseInt(API.withdrawResponse);
        System.out.println(Response);

        if (Response == 200) {

            SceneController controller = SceneController.getInstance();
            try {
                controller.setScene("ContinueScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            displayBalance = displayBalance - Geld;
            Platform.runLater(() -> {ArduinoControls.dispense(bills10Dispense, bills20Dispense, bills50Dispense);
                bills10Dispense = 0; bills20Dispense = 0; bills50Dispense = 0;});
        }
    }

    @FXML
    private Button submitNo;
    @FXML
    protected void submitNoAction(){
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
