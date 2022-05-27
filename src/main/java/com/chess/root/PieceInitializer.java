package com.chess.root;

import com.chess.root.pieces.BishopPiece;
import com.chess.root.pieces.KingPiece;
import com.chess.root.pieces.KnightPiece;
import com.chess.root.pieces.PawnPiece;
import com.chess.root.pieces.QueenPiece;
import com.chess.root.pieces.RookPiece;

public class PieceInitializer {

    private PieceInitializer() {
    }

    public static void initialize(Board board) {
        new PawnPiece(board, board.getField(0, 1), true);
        new PawnPiece(board, board.getField(1, 1), true);
        new PawnPiece(board, board.getField(2, 1), true);
        new PawnPiece(board, board.getField(3, 1), true);
        new PawnPiece(board, board.getField(4, 1), true);
        new PawnPiece(board, board.getField(5, 1), true);
        new PawnPiece(board, board.getField(6, 1), true);
        new PawnPiece(board, board.getField(7, 1), true);
        new RookPiece(board, board.getField(0, 0), true);
        new RookPiece(board, board.getField(7, 0), true);
        new KnightPiece(board, board.getField(1, 0), true);
        new KnightPiece(board, board.getField(6, 0), true);
        new BishopPiece(board, board.getField(2, 0), true);
        new BishopPiece(board, board.getField(5, 0), true);
        new QueenPiece(board, board.getField(3, 0), true, false);
        new KingPiece(board, board.getField(4, 0), true);

        new PawnPiece(board, board.getField(0, 6), false);
        new PawnPiece(board, board.getField(1, 6), false);
        new PawnPiece(board, board.getField(2, 6), false);
        new PawnPiece(board, board.getField(3, 6), false);
        new PawnPiece(board, board.getField(4, 6), false);
        new PawnPiece(board, board.getField(5, 6), false);
        new PawnPiece(board, board.getField(6, 6), false);
        new PawnPiece(board, board.getField(7, 6), false);
        new RookPiece(board, board.getField(0, 7), false);
        new RookPiece(board, board.getField(7, 7), false);
        new KnightPiece(board, board.getField(1, 7), false);
        new KnightPiece(board, board.getField(6, 7), false);
        new BishopPiece(board, board.getField(2, 7), false);
        new BishopPiece(board, board.getField(5, 7), false);
        new QueenPiece(board, board.getField(3, 7), false, false);
        new KingPiece(board, board.getField(4, 7), false);
    }
	
}
