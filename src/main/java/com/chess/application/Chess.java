package com.chess.application;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chess.Loader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

/**
 * The main class of the chess application.
 */
public class Chess extends Application {

    private SettingsController controller;
    private static final Logger LOG = Logger.getLogger(String.class.getName());

    /**
     * Method to get the settings screen.
     * @param stage The stage to be used.
     */
    public void getSettingsScreen(Stage stage) {
        try {
            FXMLLoader loadFrame = new FXMLLoader(Loader.load("/com/chess/resources/SettingsFrame.fxml"));
            Parent mainRoot = loadFrame.load();
            Scene scene = new Scene(mainRoot, 550, 630);

            scene.getStylesheets().add(Loader.load("/com/chess/resources/application.css").toString());
            if (stage == null) {
                stage = new Stage();
                stage.setMinWidth(566);
                stage.setMinHeight(669);
            }
            stage.setScene(scene);
            stage.setTitle("Chess.com");
            controller = loadFrame.getController();
            controller.setStage(stage);
            controller.setMainAccess(this);
            scene.setFill(Color.TRANSPARENT);
            stage.show();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void start(Stage args) {
        getSettingsScreen(null);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (controller != null) {
            controller.handleExit(null);
        }
        Platform.exit();
        System.exit(0);
    }

    /**
     * Main method.
     * @param args The arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
