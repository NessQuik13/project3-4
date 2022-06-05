package com.example.projectgui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class LanguageScreenController{


    @FXML
    private Button submitEnglish;
    @FXML
    protected void submitEnglishAction(){
        SceneController controller = SceneController.getInstance();
        Singleton language = Singleton.getInstance();
        try {
            language.setEnglish(true);
            controller.setScene("PinScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button submitDutch;
    @FXML
    protected void submitDutchAction(){
        SceneController controller = SceneController.getInstance();
        Singleton language = Singleton.getInstance();
        try {
            language.setEnglish(false);
            controller.setScene("PinScreen.fxml");
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
            controller.setScene("StartScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
