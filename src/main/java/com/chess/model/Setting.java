package com.chess.model;

import java.io.File;
import java.util.List;

import com.chess.root.PgnParser;
import javafx.scene.layout.GridPane;

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

    public boolean hasPgn() {
        return hasPgn;
    }

    public void addPgn(File file) {
        String s = PgnParser.getFileContent(file);
        addPgn(s);
    }

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

    public String getDifficultyName() {
        return difficulty.get();
    }

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

    public List<String> getPgnMoves() {
        return pgnMoves;
    }

    // ---------------------------------- GENERIC SETTERS AND GETTERS ----------------------------------

    public void setGrid(GridPane boardGrid) {
        this.boardGrid = boardGrid;
    }

    public GridPane getGrid() {
        return boardGrid;
    }

    public void setColor(boolean whiteUp) {
        this.whiteUp = whiteUp;
    }

    public boolean getColor() {
        return whiteUp;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public void setPieceValue(PieceValues pieceValue) {
        this.pieceValue = pieceValue;
    }

    public PieceValues getPieceValue() {
        return pieceValue;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }


}
