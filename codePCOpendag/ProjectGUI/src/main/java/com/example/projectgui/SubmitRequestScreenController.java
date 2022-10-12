package com.example.projectgui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.example.projectgui.BillTypeScreenController.billTypeSelected;

public class SubmitRequestScreenController extends WithdrawScreenController implements Runnable {

    private static int bills10Dispense = 0;
    private static int bills20Dispense = 0;
    private static int bills50Dispense = 0;

    private static int receiptAmount = 0;
    public static int getReceiptAmount() {return receiptAmount;}


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
            API.withdraw(Geld);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int Response = API.withdrawResponse;
        System.out.println(Response);

        if (Response == 200) {
            if (!billTypeSelected) {
                BillTypeScreenController.calculateBills(50);
            }
            SceneController controller = SceneController.getInstance();
            try {
                controller.setScene("WaitMoneyScreen.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
            displayBalance = displayBalance - Geld;
            receiptAmount = Geld;
            Thread dispenseThread = new Thread(this);
            dispenseThread.start();
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

    @Override
    public void run() {
        ArduinoControls.dispense(bills10Dispense, bills20Dispense, bills50Dispense);
        if (billTypeSelected) {
            billTypeSelected = false;
        }

    }
}
