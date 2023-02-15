package com.example.projectgui;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {


    @Override
            public void start (Stage stage) throws IOException {
                try {
                    Image image = new Image(new File("insertcard.gif").toURI().toString());
                    ImageView imageview = new ImageView(image);
                    SceneController controller = SceneController.getInstance();
                    if (!ArduinoControls.setupCommunication()) {
                        System.exit(1); // if setup fails, exit program
                    }
                    controller.setStage(stage);
                    controller.setScene("LanguageScreen.fxml");
                    stage.setTitle("ATM");
                    stage.setFullScreen(true);
                    stage.setFullScreenExitHint("");
                    stage.show();
            }
                catch(Exception e){
                    e.printStackTrace();
                }
            }


        }
