package com.chess.application;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.chess.Loader;
import com.chess.model.Difficulty;
import com.chess.model.PieceValues;
import com.chess.model.Mode;
import com.chess.model.Setting;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SettingsController extends SceneController implements Initializable {


    // ---------------------------------- GUI ----------------------------------

    @FXML
    private BorderPane settingsPane;

    @FXML
    private Label welcomeLabel;

    // colors
    @FXML
    private Label colorLabel;

    @FXML
    private RadioButton whiteChoice;

    @FXML
    private RadioButton blackChoice;


    @FXML
    private Label modeLabel;

    @FXML
    private ComboBox<String> modeChoice;

    // difficulty
    @FXML
    private Label difficultyLabel;

    @FXML
    private Slider difficultyChoice;

    @FXML
    private Button loadButton;

    @FXML
    private Label loadLabel;

    @FXML
    private Button startButton;

    // ---------------------------------- INITIALIZATION ----------------------------------

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        super.settings = new Setting(true, Mode.MANUAL_VS_AI, PieceValues.RANDOM, Difficulty.RANDOM);
        selectWhite();
        initComboBox();
        initSlider();
        prepareCSS();
    }

    // ---------------------------------- COLOR CHOICE HANDLING ----------------------------------

    @FXML
    private void handleWhiteRadio() {
        if (whiteChoice.isSelected()) {
            blackChoice.setSelected(false);
            whiteChoice.setSelected(true);
            super.settings.setColor(true);
        } else {
            blackChoice.setSelected(true);
            whiteChoice.setSelected(false);
            super.settings.setColor(false);
        }
    }

    @FXML
    private void handleBlackRadio() {
        if (blackChoice.isSelected()) {
            blackChoice.setSelected(true);
            whiteChoice.setSelected(false);
            super.settings.setColor(false);
        } else {
            blackChoice.setSelected(false);
            whiteChoice.setSelected(true);
            super.settings.setColor(true);
        }
    }

    // ---------------------------------- MODE CHOICE HANDLING ----------------------------------

    @FXML
    private void handleModeChoice() {
        String inputMode = modeChoice.getSelectionModel().getSelectedItem();
        if (inputMode.contentEquals(Mode.MANUAL_ONLY.get())) {
            difficultyLabel.setVisible(false);
            difficultyChoice.setVisible(false);
            super.settings.setMode(Mode.MANUAL_ONLY);
        } else {
            difficultyLabel.setVisible(true);
            difficultyChoice.setVisible(true);
            super.settings.setMode(Mode.MANUAL_VS_AI);
        }
    }

    // ---------------------------------- DIFFICULTY CHOICE HANDLING ----------------------------------

    public void getSliderValue(Number newValue) {
        int value = newValue.intValue();
        PieceValues pieceVal = PieceValues.RANDOM;
        Difficulty difficultyVal = Difficulty.RANDOM;
        switch (value) {
            case 10 -> {
                pieceVal = PieceValues.EASY;
                difficultyVal = Difficulty.EASY;
            }
            case 20 -> {
                pieceVal = PieceValues.HARD;
                difficultyVal = Difficulty.HARD;
            }
            default -> {
            }
        }
        settings.setPieceValue(pieceVal);
        settings.setDifficulty(difficultyVal);
    }

    // ---------------------------------- LOAD DIALOG HANDLING ----------------------------------

    @FXML
    private void handleLoadButton() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Load game");
        dialog.setHeaderText("Load a game from PGN code / file.");
        dialog.setResizable(true);
        dialog.getDialogPane().setMinWidth(500);
        dialog.getDialogPane().getStylesheets().add(Loader.load("com/chess/resources/application.css").toExternalForm());

        GaussianBlur blurEffect = new GaussianBlur(2);
        settingsPane.setEffect(blurEffect);

        Label pgnLabel = new Label("PGN: ");
        TextArea pgnCode = new TextArea();
        Label pgnFileName = new Label("no file selected");

        GridPane grid = new GridPane();
        grid.add(pgnLabel, 1, 1);
        grid.add(pgnCode, 2, 1);
        grid.setVgap(10);
        dialog.getDialogPane().setContent(grid);

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Loader.load("com/chess/resources/img/go.png").toString()));

        pgnLabel.setMinWidth(70);
        pgnCode.setPrefWidth(410);
        pgnCode.setPrefHeight(200);

        pgnCode.setPromptText("[Event \"Casual waste of time\"]\r[Site \"Your cave, SWITZERLAND\"]\r[Date \"2019.12.08\"]\r[Round \"2\"]\r[White \"Thought, Deep\"]\r[Black \"Blue, Deep\"]\r[Result \"0:1\"]\r\r1. f4 d5 2. Nc3 d4 3. Nb5 a6 4. Na3 Bg4 5. h3 Bh5 6. d3 Nd7 \r7. Bd2 e6 8. Nc4 Qh4+ 9. g3 Qxg3# 0:1");
        dialog.getDialogPane().requestFocus();

        ButtonType buttonUpload = new ButtonType("Upload PGN file", ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().add(buttonUpload);
        ButtonType buttonOk = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonOk);
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonCancel);

        ButtonBar buttonBar = (ButtonBar) dialog.getDialogPane().lookup(".button-bar");
        buttonBar.getButtons().forEach(b -> b.getStyleClass().add("gui-control-default"));

        dialog.setResultConverter(b -> convertResult(b, buttonOk, buttonUpload, pgnFileName, pgnCode));

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(it -> {
            settingsPane.setEffect(null);
            if (it.contentEquals("")) {
                loadLabel.setText("NOPE! try again.");
                return;
            }

            if (it.contentEquals(pgnFileName.getText())) {
                final File uploadFile = pngUploadFile;
                super.settings.addPgn(uploadFile);
                loadLabel.setText(it);
            } else if (pgnCode.getText() != null && it.startsWith("[")) {
                super.settings.addPgn(it);
                loadLabel.setText("LOADED");
            } else {
                loadLabel.setText("NOPE! try again.");
            }
        });

        if (result.isEmpty()) {
            settingsPane.setEffect(null);
        }
    }

    private String convertResult(ButtonType b, ButtonType buttonOk, ButtonType buttonUpload, Label pgnFileName, TextArea pgnCode) {
        if (b == buttonOk) {
            if (!pgnFileName.getText().contentEquals("no file selected")) {
                return pgnFileName.getText();
            } else if (pgnCode.getText() != null && !pgnCode.getText().contentEquals("")) {
                return pgnCode.getText();
            }
            return "";
        } else if (b == buttonUpload) {
            FileChooser fileChooser = new FileChooser();
            pngUploadFile = fileChooser.showOpenDialog(stage);
            if (pngUploadFile != null) {
                pgnFileName.setText(pngUploadFile.getName());
                return pgnFileName.getText();
            }

        }
        return null;
    }

    // ---------------------------------- START GAME HANDLING ----------------------------------

    @FXML
    private void handleStartButton() {
        super.startNewGame(this, stage, settings);
    }

    // ---------------------------------- INITIALIZATION HELPERS ----------------------------------

    private void selectWhite() {
        whiteChoice.setSelected(true);
        whiteChoice.requestFocus();
    }

    private void initComboBox() {
        modeChoice.getItems().addAll(Mode.MANUAL_VS_AI.get(), Mode.MANUAL_ONLY.get());
        modeChoice.getSelectionModel().select(Mode.MANUAL_VS_AI.get());
    }

    private void initSlider() {
        difficultyChoice.setMin(0);
        difficultyChoice.setMax(20);
        difficultyChoice.setValue(10);
        difficultyChoice.setShowTickMarks(true);
        difficultyChoice.setMinorTickCount(4);
        difficultyChoice.setMajorTickUnit(50);
        difficultyChoice.setBlockIncrement(10);
        difficultyChoice.valueProperty().addListener((observable, oldValue, newValue) -> getSliderValue(newValue));
    }

    private void prepareCSS() {
        String settingsLabel = "settings-label";
        String playerRadios = "player-radios";
        String guiControl = "gui-control-default";
        welcomeLabel.getStyleClass().add("welcome-label");
        colorLabel.getStyleClass().add(settingsLabel);
        blackChoice.getStyleClass().add(playerRadios);
        whiteChoice.getStyleClass().add(playerRadios);
        modeLabel.getStyleClass().add(settingsLabel);
        difficultyLabel.getStyleClass().add(settingsLabel);
        startButton.getStyleClass().add("button-start");
        modeChoice.getStyleClass().add(guiControl);
        loadButton.getStyleClass().add(guiControl);
        startButton.getStyleClass().add(guiControl);
    }

}
