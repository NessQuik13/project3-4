package com.example.projectgui;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.Timer;


public class ContinueScreenController {
    private Timer timer;




    public void initialize(){

        Singleton language = Singleton.getInstance();
        if (language.getIsEnglish() == false) {
            submitAbort.setText("Annuleren");
            submitFinish.setText("Transactie beeindigen");
            submitContinue.setText("Transactie voortzetten");
        }
    }

    @FXML
    private Button submitContinue;
    @FXML
    protected void submitContinueAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("TransactionScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private Button submitFinish;
    @FXML
    protected void submitFinishAction(){
        SceneController controller = SceneController.getInstance();
        try {
            controller.setScene("ReceiptScreen.fxml");
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
