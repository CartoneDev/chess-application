package com.chess.root;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chess.application.FieldButton;
import com.chess.root.pieces.Piece;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class Field {
    private FieldButton button;
    private final int column;
    private final int row;
    private final boolean isBlack;
    private Piece piece = null;
    private final String notation;
    private static final Logger LOG = Logger.getLogger(String.class.getName());

    public Field(int column, int row, boolean isBlack) {
        this.column = column;
        this.row = row;
        this.isBlack = isBlack;
        this.notation = getColNotation() + getRowNotation();
    }

    // ---------------------------------- GAMEPLAY HANDLING ----------------------------------

    public void setPiece(Piece piece) {
        if (piece == null) {
            throw new NullPointerException("piece is null" + this);
        }
        setPiece(piece, false);
    }

    public void setPiece(Piece piece, boolean init) {
        if (piece == null) {
            throw new NullPointerException("piece is null" + this);
        }
        if (this.piece == null || this.piece.equals(piece)) {
            this.piece = piece;
            if (button != null) {
                updateButton();
            }
            // set current field as property in the Piece class
            setField(init);
        }
    }

    public void setRookPiece(Piece piece) {
        this.piece = piece;
        if (button != null) {
            updateButton();
        }
        this.piece.setFieldSilently(this);
    }


    private void updateButton() {
        if (Platform.isFxApplicationThread()) {
            setButtonGraphic();

            // seems to be necessary...
            Platform.runLater(() -> button.setText(""));
        } else {

            try {
                updateUI();

            } catch (InterruptedException | ExecutionException e) {
                LOG.log(Level.SEVERE, e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public void setOpacity(Double d) {
        button.setOpacity(d);
        Platform.runLater(() -> button.setOpacity(d));
    }

    private void setField(boolean init) {
        if (piece == null) {
            return;
        }
        if (init) {
            this.piece.setFieldSilently(this);
        } else {

            this.piece.setField(this);
        }
    }

    public void restorePiece(Piece piece) {
        if (piece == null) {
            throw new NullPointerException("no piece found");
        }

        this.piece = piece;
        final ImageView x = piece.getSymbol();
        Platform.runLater(() -> button.setText("o"));

        if (button != null) {

            updateButton();
            Platform.runLater(() -> button.setGraphic(x));

            Platform.runLater(() -> button.getGraphic().setOpacity(1.0));
        }
        this.piece.setFieldSilently(this);
    }

    public void render() {
        if (piece == null) {
            Platform.runLater(() -> button.setGraphic(null));
        } else {

            final ImageView x = piece.getSymbol();
            Platform.runLater(() -> button.setText("o"));

            updateButton();
            Platform.runLater(() -> button.setGraphic(x));
            Platform.runLater(() -> button.getGraphic().setOpacity(1.0));
        }
    }

    public void removePiece(boolean isVictim) {
        if (this.piece != null) {
            this.piece = null;

            if (!isVictim && button != null && button.getGraphic() != null) {
                button.getGraphic().setOpacity(0.3);
            }
        }
    }

    public void forceRemove() {
        if (button != null) {
            Platform.runLater(this::clearButton);
        }
    }


    private void clearButton() {
        button.setGraphic(null);
        button.setText("");
    }

    public void setPieceSilently(Piece piece) {
        this.piece = piece;
    }

    public void removePieceSilently() {
        piece = null;
    }

    // ---------------------------------- GENERIC SETTERS AND GETTERS ----------------------------------

    public Piece getPiece() {
        return piece;
    }

    public int getColumn() {
        return column;
    }

    public String getColNotation() {
        String s = "";
        switch (column) {
            case 0 -> s = "a";
            case 1 -> s = "b";
            case 2 -> s = "c";
            case 3 -> s = "d";
            case 4 -> s = "e";
            case 5 -> s = "f";
            case 6 -> s = "g";
            case 7 -> s = "h";
            default -> {
            }
        }
        return s;
    }

    public int getRowNotation() {
        int s = 0;
        switch (row) {
            case 0 -> s = 8;
            case 1 -> s = 7;
            case 2 -> s = 6;
            case 3 -> s = 5;
            case 4 -> s = 4;
            case 5 -> s = 3;
            case 6 -> s = 2;
            case 7 -> s = 1;
            default -> {
            }
        }
        return s;
    }

    public String getNotation() {
        return notation;
    }

    public int getRow() {
        return row;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setButton(FieldButton button) {
        this.button = button;
    }

    // ---------------------------------- GENERIC HELPER METHODS ----------------------------------

    private void updateUI() throws InterruptedException, ExecutionException {
        FutureTask<Void> updateUITask = new FutureTask<>(this::setButtonGraphic, null);
        Platform.runLater(updateUITask);
        updateUITask.get();
    }

    private void setButtonGraphic() {
        button.setGraphic(piece.getSymbol());
        button.getGraphic().setOpacity(1.0);
    }

    @Override
    public String toString() {
        return "field " + getColumn() + getRow();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        int hash = this.isBlack() ? 3 : 4;
        hash += this.toString().hashCode();
        hash += 123;
        return hash;
    }

}
