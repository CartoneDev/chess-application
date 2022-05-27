package com.chess.application;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chess.Loader;
import com.chess.model.Setting;
import com.chess.root.Game;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public abstract class SceneController implements Initializable {

    protected Chess chess;
    protected Game game;
    protected Stage stage;
    protected Setting settings;
    protected String header = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>Chess: moves of this game</title><style>body { font-family: \"Courier New\"; padding: 20px; }li { padding: 0.1em; }</style></head><body><h1>Following moves were done:</h1><ul>";
    protected String footer = "</ul></body></html>";
    protected String movesString = "";
    protected String out = header + movesString + footer;
    protected File pgnFile;
    protected Path pgnPath;
    protected File pngUploadFile;
    protected String pgnString = "";
    protected static final Logger LOG = Logger.getLogger(String.class.getName());

    @FXML
    protected MenuItem exitItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    // ---------------------------------- START NEW GAME ----------------------------------

    /**
     * Function to load new game controller
     * @param init
     * @param stage
     * @param settings
     */
    protected void startNewGame(SettingsController init, Stage stage, Setting settings) {
        try {
            FXMLLoader loadFrame = new FXMLLoader(Loader.load("com/chess/resources/GameFrame.fxml"));
            Parent mainRoot = loadFrame.load();
            Scene scene = new Scene(mainRoot, 550, 630);
            scene.getStylesheets().add(Loader.load("com/chess/resources/application.css").toExternalForm());
            stage.setScene(scene);
            GameController controller = loadFrame.getController();
            controller.loadGame(init, settings);
            if (!settings.getColor()) {
                controller.handleRotate();
            }
            stage.show();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
        }
    }

    // ---------------------------------- MENU HANDLING ----------------------------------

    @FXML
    protected void handleExit(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    // ---------------------------------- GENERIC METHODS ----------------------------------

    protected void setStage(Stage stage) {
        this.stage = stage;
    }

    protected void setMainAccess(Chess chess) {
        this.chess = chess;
    }

    protected Chess getMainAccess() {
        return chess;
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return stage.getScene();
    }

    public abstract void getSliderValue(Number newValue);
}
