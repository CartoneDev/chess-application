package com.chess.root.moves;

import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.pieces.Piece;

/**
 * Class for castling move.
 */
public class CastlingMove extends Move {

    private final Piece rook;
    private final Field rookField;
    private final Field rookStartField;

    /**
     * castling move constructor
     * @param king to move
     * @param rook to be jumped around
     * @param kingField where king goes
     * @param rookField where rook goes
     */
    public CastlingMove(Piece king, Piece rook, Field kingField, Field rookField) {
        super(king, kingField, null);
        this.rook = rook;
        this.rookField = rookField;
        this.rookStartField = rook.getField();
        if (Math.abs(king.getField().getColumn() - rook.getField().getColumn()) > 3) {
            notation = "O-O-O";
        } else {
            notation = "O-O";
        }
        updateRating(king);
    }

    /**
     * @param board on which move is to be executed
     */
    @Override
    public void execute(Board board) {
        rook.getField().removePiece(false);
        rook.getField().forceRemove();
        rookField.setRookPiece(rook);
        rook.moved();
        super.execute(board);
    }

    /**
     * undoes castling
     * @param board to be reconfigured as move has been undone
     */
    @Override
    public void undo(Board board) {
        rookField.removePiece(false);
        rookField.forceRemove();
        rookStartField.setRookPiece(rook);
        rook.unmove();
        super.undo(board);
    }

    /**
     * @return move in chess notation
     */
    @Override
    public String getNotation() {
        return notation;
    }

    /**
     * @return move in pgn notation
     */
    @Override
    public String getPgnNotation() {
        return notation;
    }

    // ---------------------------------- PRE-CALCULATE BASE RATING ----------------------------------

    private void updateRating(Piece king) {
        super.rating += king.getRating();
    }

}
