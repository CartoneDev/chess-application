package com.chess.root.pieces;

import java.util.List;

import com.chess.Loader;
import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.moves.Move;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * abstract class for a figure
 */
public abstract class Piece {
    protected Board board;
    protected Field field;
    private final String descriptiveName; // internal use only
    private final String notation;
    private ImageView symbol;
    private Image image;
    protected boolean color;
    protected int rating;
    protected int posValue;

    private static final int IMGSIZE = 60;
    protected int[][] table;
    /**
     * abstract figure constructor
     * @param board where figure should be placed
     * @param field where figure should be placed
     * @param color of a figure that should be placed
     */
    public Piece(Board board, Field field, boolean color, String name, String notation, int rating, int[][] table, boolean simulation) {
        this.board = board;
        if (field == null) {
            throw new NullPointerException("no field set!");
        }
        this.field = field;
        this.descriptiveName = name;
        this.notation = notation;
        this.color = color;
        this.rating = rating;
        this.table = table;

        if (!simulation) {
            createSymbol();
            init();
        }
    }

    /**
     * adds reference to a piece to a board and board field
     */
    public void init() {
        field.setPiece(this, true);
        board.addPiece(this);
    }

    // ---------------------------------- GAMEPLAY HANDLING ----------------------------------

    /**
     * sets figure field
     * @param field where figure is placed
     */
    public void setField(Field field) {
        if (field == null) {
            throw new NullPointerException("null value was set" + this);
        }
        this.field = field;
        updatePos();
        board.endMove();
    }

    /**
     * sets figure field without any graphical updates
     * @param field where figure is placed
     */
    public void setFieldSilently(Field field) {
        this.field = field;
        updatePos();
    }

    private void updatePos() {
        posValue = table[field.getRow()][field.getColumn()];
    }

    // ---------------------------------- GENERIC GETTERS AND SETTERS ----------------------------------

    /**
     * retrives figure field
     * @return figure field
     */
    public Field getField() {
        if (field == null) {
            throw new NullPointerException("Field not found! " + this);
        }
        return field;
    }
    /**
     * retrives figure column
     * @return figure column
     */
    public int getColumn() {
        if (field == null) {
            throw new NullPointerException("Field column not found - out of range!" + this);
        }
        return field.getColumn();
    }
    /**
     * retrives figure row
     * @return figure row
     */
    public int getRow() {
        if (field == null) {
            throw new NullPointerException("Field row not found - out of range!");
        }
        return field.getRow();
    }

    public boolean isBlack() {
        return color;
    }

    public boolean getColor() {
        return color;
    }

    /**
     * getter for a visual representation of a figure
     * @return visual representation of a figure
     */
    public ImageView getSymbol() {
        return symbol;
    }

    /**
     * @return figure in text chess notation
     */
    public String getNotation() {
        return notation;
    }
    /**
     * @return figure in pgn notation
     */
    public String getPgnNotation() {
        return getNotation();
    }

    /**
     * @return figure rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * @return figure value
     */
    public int getValue() {
        return rating + posValue;
    }

    /**
     * @return if figure have moved
     */
    public boolean wasMoved() {
        return true;
    }

    /**
     * marks figure when moved
     */
    public void moved(){}

    /**
     * unmarks figure when undo moved
     */
    public void unmove() {

    }

    /**
     * @return is figure taken
     */
    public boolean isDead() {
        return false;
    }

    /** tells figure that game have stopped
     * @param end
     */
    public void setEndTable(boolean end) {

    }

    // ---------------------------------- HELPER METHODS ----------------------------------

    /**
     * initilises visual representation
     */
    public void createSymbol() {
        if (image == null || symbol == null) {
            String path = "com/chess/resources/img/";
            path += descriptiveName;
            path += this.isBlack() ? "_b.png" : "_w.png";
            final String filePath = path;
            ImageView img = new ImageView(Loader.load(filePath).toString());
            img.setFitWidth(IMGSIZE);
            img.setFitHeight(IMGSIZE);
            symbol = img;

            double imgSize = IMGSIZE;
            image = new Image(Loader.load(filePath).toString(), imgSize, imgSize, false, false);
        }
    }

    /**
     * @return figure as text
     */
    @Override
    public String toString() {
        return descriptiveName + "" + field.getNotation();
    }

    /**
     * equality check
     * @param obj to compare with this
     * @return obj == this
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            if (this instanceof PawnPiece || this instanceof RookPiece && obj instanceof RookPiece || this instanceof KnightPiece && obj instanceof KnightPiece) {
                return true;
            }
            return (this instanceof BishopPiece && obj instanceof BishopPiece) || (this instanceof QueenPiece && obj instanceof QueenPiece) || (this instanceof KingPiece && obj instanceof KingPiece);
        }
        return false;
    }

    /**
     * hash calculation for comparison
     * @return hash of a figure
     */
    @Override
    public int hashCode() {
        int hash = this.isBlack() ? 2 : 0;
        hash += this.getField().getColumn();
        hash += this.getField().getRow();
        hash += this.toString().hashCode();
        hash += 123;
        return hash;
    }

    // ---------------------------------- ABSTRACT METHODS ----------------------------------

    /**
     * calculate possible moves for a figure
     * @return possible moves for a figure
     */
    public abstract List<Move> getMoves();

}
