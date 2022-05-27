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

    @Override
    public void execute(Board board) {
        rook.getField().removePiece(false);
        rook.getField().forceRemove();
        rookField.setRookPiece(rook);
        rook.moved();
        super.execute(board);
    }

    @Override
    public void undo(Board board) {
        rookField.removePiece(false);
        rookField.forceRemove();
        rookStartField.setRookPiece(rook);
        rook.unmove();
        super.undo(board);
    }

    @Override
    public String getNotation() {
        return notation;
    }

    @Override
    public String getPgnNotation() {
        return notation;
    }

    // ---------------------------------- PRE-CALCULATE BASE RATING ----------------------------------

    private void updateRating(Piece king) {
        super.rating += king.getRating();
    }

}
