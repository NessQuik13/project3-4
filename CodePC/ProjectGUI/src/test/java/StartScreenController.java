package com.example.projectgui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Timer;
import java.util.TimerTask;

public class StartScreenController{
    Runnable runnable = new CardThread();
    Thread arduino = new Thread(runnable);
    Timer timer = new Timer();
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
