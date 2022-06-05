package com.example.projectgui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneController extends Parent {
    private static SceneController Instance;
    private Stage stage;

    public synchronized static SceneController getInstance() {
        if (Instance == null) {
            Instance = new SceneController();
        }
        return Instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean setScene(String sceneName) throws IOException {
        Parent scene = (FXMLLoader.load(Objects.requireNonNull(getClass().getResource(sceneName))));

        if (stage.getScene() != null) {
            if (scene != null) {
                stage.getScene().setRoot(scene);
                stage.setFullScreen(true);
                stage.setFullScreenExitHint("");
            } else {
                return false;
            }
        }
        else{
            stage.setScene(new Scene(scene));
        }
        return true;
    }
}

