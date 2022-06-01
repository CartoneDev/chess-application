package com.chess.model;

import java.io.File;
import java.util.List;

import com.chess.root.PgnParser;
import javafx.scene.layout.GridPane;

/**
 * Contains game setting
 */
public class Setting {

    private GridPane boardGrid;
    private boolean whiteUp;
    private Mode mode;
    private PieceValues pieceValue;
    private Difficulty difficulty;
    private String[] pgnMeta;
    private List<String> pgnMoves;
    private boolean hasPgn;

    public Setting(boolean whiteUp, Mode mode, PieceValues pieceValue, Difficulty difficulty) {
        this.whiteUp = whiteUp;
        this.mode = mode;
        this.pieceValue = pieceValue;
        this.difficulty = difficulty;
    }

    // ---------------------------------- PGN HANDLING ----------------------------------

    /**
     * checks if pgn was imported
     * @return is pgn imported
     */
    public boolean hasPgn() {
        return hasPgn;
    }

    /**
     * parses pgn file
     * @param file to parse
     */
    public void addPgn(File file) {
        String s = PgnParser.getFileContent(file);
        addPgn(s);
    }

    /**
     * parses pgn string
     * @param s string to parse
     */
    public void addPgn(String s) {
        hasPgn = true;
        setPgnMeta(s);
        setPgnMoves();
    }

    private void setPgnMeta(String s) {
        if (s != null) {
            pgnMeta = s.split("]");
        }
    }

    /**
     * gets pgn meta of a setting
     * @return pgnMeta
     */
    public String[] getPgnMeta() {
        return pgnMeta;
    }

    private void setPgnMoves() {
        String pgnMoveData = null;
        if (pgnMeta != null) {
            pgnMoveData = pgnMeta[pgnMeta.length - 1];
        }
        if (pgnMoveData != null) {
            pgnMoves = PgnParser.parseMoves(pgnMoveData);
        }
    }

    /**
     * gets setting difficulty as string
     * @return setting difficulty as string
     */
    public String getDifficultyName() {
        return difficulty.get();
    }

    /**
     * gets white player name as a string
     * @return white player name as a string
     */
    public String getWhite() {
        String player = PgnParser.getWhite(this);
        if (player == null) {
            if (mode == Mode.MANUAL_VS_AI && !whiteUp) {
                player = "Thought, Deep";
            } else {
                player = "Doe, Jane";
            }
        }
        return player;
    }

    /**
     * @return black player name as a string
     */
    public String getBlack() {
        String player = PgnParser.getBlack(this);
        if (player == null) {
            if (mode == Mode.MANUAL_VS_AI && whiteUp){
                player = "Blue, Deep";
            } else {
                player = "Doe, John";
            }
        }
        return player;
    }

    /**
     * @return list of pgn notation moves
     */
    public List<String> getPgnMoves() {
        return pgnMoves;
    }

    // ---------------------------------- GENERIC SETTERS AND GETTERS ----------------------------------

    /**
     * links board grid to setting
     * @param boardGrid to be linked
     */
    public void setGrid(GridPane boardGrid) {
        this.boardGrid = boardGrid;
    }

    /**
     * @return linked board grid
     */
    public GridPane getGrid() {
        return boardGrid;
    }

    /**
     * sets player selected color
     * @param whiteUp
     */
    public void setColor(boolean whiteUp) {
        this.whiteUp = whiteUp;
    }

    /**
     * @return is player selected white to play
     */
    public boolean getColor() {
        return whiteUp;
    }

    /**
     * sets game mode
     * @param mode to set
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * @return game mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * sets piece value
     * @param pieceValue to set
     */
    public void setPieceValue(PieceValues pieceValue) {
        this.pieceValue = pieceValue;
    }

    /**
     * @return value of a figure
     */
    public PieceValues getPieceValue() {
        return pieceValue;
    }

    /**
     * links up difficulty
     * @param difficulty to link up
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * @return difficulty of a game set in settings
     */
    public Difficulty getDifficulty() {
        return this.difficulty;
    }


}
