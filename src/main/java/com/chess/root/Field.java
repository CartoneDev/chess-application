package com.chess.root;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chess.application.FieldButton;
import com.chess.root.pieces.Piece;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

/**
 * represents individual field on a chess field
 */
public class Field {
    private FieldButton button;
    private final int column;
    private final int row;
    private final boolean isBlack;
    private Piece piece = null;
    private final String notation;
    private static final Logger LOG = Logger.getLogger(String.class.getName());

    /**
     * constructs a game field with given coordinates and color
     * @param column Y coordinate of a field
     * @param row X coordinate of a field
     * @param isBlack field color
     */
    public Field(int column, int row, boolean isBlack) {
        this.column = column;
        this.row = row;
        this.isBlack = isBlack;
        this.notation = getColNotation() + getRowNotation();
    }

    // ---------------------------------- GAMEPLAY HANDLING ----------------------------------

    /**
     * set piece on a field
     * @param piece
     */
    public void setPiece(Piece piece) {
        if (piece == null) {
            throw new NullPointerException("piece is null" + this);
        }
        setPiece(piece, false);
    }

    /**
     * sets piece on the field
     * @param piece to be placed here
     * @param init should piece be placed silently(without calling of a GUI  rerender)
     */
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

    /**
     * sets rook on a filed
     * @param piece rook to be placed here
     */
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

    /**
     * sets opacity of a GUI button representing this field
     * @param d opacity value
     */
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

    /**
     * restores piece emplacement on a field when undone
     * @param piece to be placed back
     */
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

    /**
     * renders field, which figure or empty
     */
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

    /**
     * removes piece from a field
     * @param isVictim is piece taken by opponent
     */
    public void removePiece(boolean isVictim) {
        if (this.piece != null) {
            this.piece = null;

            if (!isVictim && button != null && button.getGraphic() != null) {
                button.getGraphic().setOpacity(0.3);
            }
        }
    }

    /**
     * clears button
     */
    public void forceRemove() {
        if (button != null) {
            Platform.runLater(this::clearButton);
        }
    }


    private void clearButton() {
        button.setGraphic(null);
        button.setText("");
    }

    /**
     * places figure silently, without any GUI changes
     * @param piece to place
     */
    public void setPieceSilently(Piece piece) {
        this.piece = piece;
    }
    /**
     * removes figure from the field silently, without any GUI changes
     */
    public void removePieceSilently() {
        piece = null;
    }

    // ---------------------------------- GENERIC SETTERS AND GETTERS ----------------------------------

    /**
     * gets piece on the field
     * @return piece on the field
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * gets x coordinate
     * @return x coordinate
     */
    public int getColumn() {
        return column;
    }

    /**
     * converts x coordinate to column literal
     * @return column literal
     */
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

    /**
     * gets row coordinate rulewise
     * @return row coordinate for board
     */
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

    /**
     * getter field coordinate in chess notation
     * @return field coordinate in chess notation
     */
    public String getNotation() {
        return notation;
    }

    /**
     * getter y coordinate
     * @return getter y coordinate
     */
    public int getRow() {
        return row;
    }

    /**
     * getter for field gui color
     * @return is field black
     */
    public boolean isBlack() {
        return isBlack;
    }

    /**
     * links gui button to the field
     * @param button to link
     */
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

    /**
     * represents field as string
     * @return field as string
     */
    @Override
    public String toString() {
        return "field " + getColumn() + getRow();
    }

    /**
     * checks field equality with other field
     * @param obj to compare
     * @return if objects are same
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * hash function for field comparison
     * @return hash code of a field
     */
    @Override
    public int hashCode() {
        int hash = this.isBlack() ? 3 : 4;
        hash += this.toString().hashCode();
        hash += 123;
        return hash;
    }

}
