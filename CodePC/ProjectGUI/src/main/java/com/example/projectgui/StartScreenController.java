package com.example.projectgui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.Timer;


public class StartScreenController{
    Runnable runnable = new CardThread();
    Thread arduino = new Thread(runnable);
    @FXML
    private Label T1;

    public void initialize(){
        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Voer Uw pas in");
        }
        arduino.start();
    }
}
