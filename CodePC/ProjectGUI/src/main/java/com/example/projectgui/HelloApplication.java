package com.example.projectgui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {

    @Override
            public void start (Stage stage) throws IOException {
                try {
                    Image image = new Image(new File("insertcard.gif").toURI().toString());
                    ImageView imageview = new ImageView(image);
                    SceneController controller = SceneController.getInstance();
                    if(!ArduinoControls.setupCommunication()) {
                        System.exit(1);
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

            public static void main (String[]args){
                launch();
            }
        }
