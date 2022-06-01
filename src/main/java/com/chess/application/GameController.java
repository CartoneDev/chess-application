package com.chess.application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;

import com.chess.Loader;
import com.chess.model.Setting;
import com.chess.root.Game;
import com.chess.root.PgnParser;
import com.chess.root.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


/**
 * Class representing the controller of the game.
 */
public final class GameController extends SceneController implements Initializable {

    private SettingsController settingsController;
    private long startTime;
    private String gameStatusText;
    private final String[] alpha = {"a", "b", "c", "d", "e", "f", "g", "h"};

    // ---------------------------------- MENU ----------------------------------

    @FXML
    private MenuItem editModeStartItem;

    @FXML
    private MenuItem editModeStopItem;


    // ---------------------------------- GUI ----------------------------------

    // board
    @FXML
    private GridPane boardGrid;

    @FXML
    private VBox leftLabels;    // 1-8

    @FXML
    private VBox rightLabels;    // 1-8

    @FXML
    private HBox topLabels;        // a-h

    @FXML
    private HBox bottomLabels;    // a-h

    // edit mode bar
    @FXML
    private final CheckBox manageEditHandler = new CheckBox();

    @FXML
    private HBox editBar;

    @FXML
    private Button goButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button stepBackButton;

    @FXML
    private Button stepForwardButton;

    @FXML
    private Label statusTextLabel;

    @FXML
    private Label moveCounter;

    // ---------------------------------- INITIALIZATION ----------------------------------

    /**
     * Initializes the controller class. This method is automatically called when game starts
     * @param init The settings controller
     * @param settings The initial settings
     */
    public void loadGame(SettingsController init, Setting settings) {
        this.settings = settings;
        settings.setGrid(boardGrid);
        this.stage = init.getStage();
        this.chess = init.getMainAccess();
        this.settingsController = init;
        moveCounter.setText("1.0");
        setDisplay("white starts the game");
        setUpEditMode();
        setEditMode(true);
        this.game = new Game(this, settings);
        game.setDummyMode(true);

    }

    // ---------------------------------- INITIALIZATION HELPERS ----------------------------------


    private ImageView createSymbol(String suffix) {
        String path = "com/chess/resources/img/";
        path += suffix;
        final String filePath = path;
        ImageView img = new ImageView(Loader.load(filePath).toString());
        img.setFitWidth(16);
        img.setFitHeight(16);
        return img;
    }

    private void setUpEditMode() {
        stopButton.setGraphic(createSymbol("stop.png"));
        goButton.setGraphic(createSymbol("go.png"));
        stepBackButton.setGraphic(createSymbol("backward.png"));
        stepForwardButton.setGraphic(createSymbol("forward.png"));
        setGoBut(false);
        setBackBut(false);
        setForwardBut(false);
        manageEditHandler.setSelected(false);
        editBar.managedProperty().bind(manageEditHandler.selectedProperty());

        goButton.setVisible(false);
        stopButton.setVisible(false);
        stepBackButton.setVisible(false);
        stepForwardButton.setVisible(false);

        String guiClass = "gui-control-default";
        goButton.getStyleClass().add(guiClass);
        stopButton.getStyleClass().add(guiClass);
        stepBackButton.getStyleClass().add(guiClass);
        stepForwardButton.getStyleClass().add(guiClass);

    }

    // ---------------------------------- GAME HANDLING ----------------------------------

    @FXML
    private void handleSettings() {
        resetStage();
        cleanup();
        chess.getSettingsScreen(stage);
    }

    @FXML
    private void handleRestart() {
        resetStage();
        cleanup();
        super.startNewGame(settingsController, stage, settings);
    }

    /**
     * exit button
     * @param event click on button event
     */
    @FXML
    @Override
    public void handleExit(ActionEvent event) {
        cleanup();
        super.handleExit(event);
    }

    private void cleanup() {
        List<Player> players = game.getAIPlayers();
        for (Player player : players) {
            player.getThread().requestStop();
        }
    }

    // ---------------------------------- EXPORT HANDLING ----------------------------------

    @FXML
    private void handlePgnCopy() {
        String pgn = PgnParser.getFullPgn(game);
        ClipboardContent cc = new ClipboardContent();
        cc.putString(pgn);
        Clipboard.getSystemClipboard().setContent(cc);
    }

    @FXML
    private void handlePgnExport() {
        try {
            pgnString = PgnParser.getFullPgn(game);
            InputStream htmlFile = new ByteArrayInputStream(pgnString.getBytes());
            if (pgnFile == null || pgnPath == null) {
                pgnFile = File.createTempFile("chess_game", ".pgn");
                pgnPath = pgnFile.toPath();
            }
            Files.copy(htmlFile, pgnPath, StandardCopyOption.REPLACE_EXISTING);
            pgnFile.deleteOnExit();
            URI url = pgnFile.toURI();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export PGN");
                alert.setHeaderText("Export PGN");
                alert.setContentText("The PGN file has been exported to " + url);
                alert.showAndWait();
            });
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
    }

    @FXML
    private void handleBoardCopy() {
        WritableImage image = boardGrid.snapshot(new SnapshotParameters(), null);
        ClipboardContent cc = new ClipboardContent();
        cc.putImage(image);
        Clipboard.getSystemClipboard().setContent(cc);
    }

    // ---------------------------------- EDIT MODE HANDLING ----------------------------------

    /**
     * Function to rotate the board.
     */
    @FXML
    void handleRotate() {
        if (boardGrid.getRotate() == 180.0) {
            boardGrid.setRotate(0.0);
            for (Node node : boardGrid.getChildren()) {
                node.setRotate(0.0);
            }
            resetBoard();
        } else {
            boardGrid.setRotate(180.0);
            for (Node node : boardGrid.getChildren()) {
                node.setRotate(180.0);
            }
            switchBoard();
        }
    }

    private void switchBoard() {
        for (int i = 0; i < 8; i++) {
            ((Labeled) leftLabels.getChildren().get(i)).setText(Integer.toString(i + 1));
            ((Labeled) rightLabels.getChildren().get(i)).setText(Integer.toString(i + 1));
            ((Labeled) topLabels.getChildren().get(i)).setText(alpha[7 - i]);
            ((Labeled) bottomLabels.getChildren().get(i)).setText(alpha[7 - i]);
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 8; i++) {
            ((Labeled) leftLabels.getChildren().get(i)).setText(Integer.toString(8 - i));
            ((Labeled) rightLabels.getChildren().get(i)).setText(Integer.toString(8 - i));
            ((Labeled) topLabels.getChildren().get(i)).setText(alpha[i]);
            ((Labeled) bottomLabels.getChildren().get(i)).setText(alpha[i]);
        }
    }

    @FXML
    private void handleStartEditMode() {
        pauseGame();
        setEditMode(true);
        getStage().setMinHeight(692);
        editModeStartItem.setDisable(true);
        editModeStopItem.setDisable(false);
        setEditableGui(false);
    }

    @FXML
    private void handleLeaveEditMode() {
        setStopBut(true);
        setGoBut(false);
        setBackBut(false);
        setForwardBut(false);
        setEditMode(false);
        game.resumeGame();
        resetStage();
        setEditableGui(true);
    }

    private void setEditMode(boolean on) {
        manageEditHandler.setSelected(on);
        stopButton.setVisible(on);
        goButton.setVisible(on);
        stepBackButton.setVisible(on);
        stepForwardButton.setVisible(on);
    }

    /**
     * getter for a difficulty
     * @param newValue
     */
    public void getSliderValue(Number newValue) {
        int value = newValue.intValue();
        game.setSpeed(300 - value);
    }

    private void resetStage() {
        getStage().setMinHeight(669);
        getStage().setHeight(669);
    }

    // ---------------------------------- EDIT BAR HANDLING ----------------------------------

    @FXML
    private void handleGo() {
        setStopBut(true);
        setGoBut(false);
        setBackBut(false);
        setForwardBut(false);
        game.resumeGame();
        setEditableGui(true);
    }

    @FXML
    private void handleStop() {
        pauseGame();
        setEditableGui(false);
    }

    private void pauseGame() {
        setStopBut(false);
        setGoBut(true);
        setForwardBut(false);
        game.pauseGame();
        setEditableGui(false);
    }

    @FXML
    private void handleStepBack() {
        game.stepBack();
    }

    @FXML
    private void handleStepForward() {
        game.stepForward();
    }

    private void setEditableGui(boolean yes) {
        Cursor c;
        if (yes) {
            c = Cursor.HAND;
        } else {
            c = Cursor.DEFAULT;
        }
        for (Node node : boardGrid.getChildren()) {
            node.setCursor(c);
        }
    }

    // ---------------------------------- EDIT PUBLIC CONTROLS ----------------------------------

    /**
     * sets start game button active
     * @param activate game button?
     */
    public void setGoBut(boolean activate) {
        goButton.setDisable(!activate);
    }

    /**
     * sets stop game button active
     * @param activate stop button?
     */
    public void setStopBut(boolean activate) {
        stopButton.setDisable(!activate);
    }

    /**
     * sets back button active
     * @param activate back button?
     */
    public void setBackBut(boolean activate) {
        stepBackButton.setDisable(!activate);
    }


    /**
     * sets forward button active
     * @param activate forward button?
     */
    public void setForwardBut(boolean activate) {
        stepForwardButton.setDisable(!activate);
    }

    /**
     * sets focus on start button
     */
    public void requestFocusGo() {
        goButton.requestFocus();
    }

    /**
     * sets focus on a stop button
     */
    public void requestFocusStop() {
        stopButton.requestFocus();
    }

    /**
     * sets focus on a back button
     */
    public void requestFocusBack() {
        stepBackButton.requestFocus();
    }

    /**
     * sets focus on a forward button
     */
    public void requestFocusForward() {
        stepForwardButton.requestFocus();
    }

    // ---------------------------------- STATUS BAR HANDLING ----------------------------------

    /**
     * displays which player turn
     * @param game
     */
    public void displayPlayer(Game game) {
        if (game != null && game.getPlayer() != null) {
            setDisplay(game.getPlayer().toString() + "'s turn");
        }
    }

    /**
     * sets game status
     * @param display text to be displayed
     */
    public void setDisplay(String display) {
        gameStatusText = display;
        renderDisplay();
    }

    /**
     * redraws display
     */
    public void renderDisplay() {
        if (Platform.isFxApplicationThread()) {
            statusTextLabel.setText(gameStatusText);
        } else {
            Platform.runLater(() -> statusTextLabel.setText(gameStatusText));
        }
    }

    /**
     * updates move counter display
     * @param counter moves count as a string
     */
    public void updateMoveCounter(String counter) {
        Platform.runLater(() -> moveCounter.setText(counter));
    }

}
