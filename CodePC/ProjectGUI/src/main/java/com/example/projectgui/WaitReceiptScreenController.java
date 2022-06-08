package com.example.projectgui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.IOException;

public class WaitReceiptScreenController {
    PrinterThread print = new PrinterThread();
    Thread printThread = new Thread(print);
    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label T1;
    @FXML
    private Label T2;
    public void initialize(){

        Singleton language = Singleton.getInstance();
        if (!language.getIsEnglish()) {
            T1.setText("Even geduld");
            T2.setText("Terwijl wij U bon printen");
        }
        printThread.start();

        AnimationTimer progressTimer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                double currentProgress = progressBar.getProgress();
                if (currentProgress + 0.0005f > 1.0f) {
                    this.stop();
                    SceneController controller = SceneController.getInstance();
                    try {
                        controller.setScene("FinishScreen.fxml");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    currentProgress += 0.0005;
                }
                progressBar.setProgress(currentProgress);
            }
        };
        progressTimer.start();

    }
}
