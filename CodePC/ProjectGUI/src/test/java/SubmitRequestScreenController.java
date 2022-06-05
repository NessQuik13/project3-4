package com.example.projectgui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;

public class SubmitRequestScreenController extends WithdrawScreenController {

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
            API.withdraw("GR", "KRIV", "GRKRIV0000123401", PinScreenController.pincodePinScreen, Geld);
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
        try {
            controller.setScene("LanguageScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
