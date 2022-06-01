package com.chess.root.moves;

import java.util.List;

import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.pieces.Piece;
import com.chess.root.pieces.QueenPiece;
import com.chess.root.pieces.RookPiece;

/**
 * a move with a pawn promotion
 */
public class PromotionMove extends Move {

    private final Piece pawn;
    private Piece queen;

    /**
     * Constructor for a promotional move
     * @param piece pawn that moved
     * @param queen figure to promote a pawn, queen in our implementation
     * @param field where figure is moved
     * @param victim taken figure by move
     */
    public PromotionMove(Piece piece, Piece queen, Field field, Piece victim) {
        super(queen, field, victim);
        this.pawn = piece;
        rating = queen.getRating() * 2;
    }

    /**
     * get a promoted pawn
     * @return a promoted pawn
     */
    @Override
    public Piece getPawn() {
        return pawn;
    }

    private void setQueenReally(Piece q) {
        this.piece = q;
        queen = q;
        q.getField().setPiece(q);
    }

    /**
     * @param board on which move is to be executed
     */
    @Override
    public void execute(Board board) {
        boolean countdownReset = false;
        if (victim != null) {
            victimField.removePiece(true);
            board.removePiece(victim);

            if (victim instanceof RookPiece) {
                ((RookPiece) victim).kill();
            }
            countdownReset = true;
        }
        updateBoard(board, countdownReset);

        boolean c = pawn.getColor();
        board.removePiece(pawn);

        startField.removePiece(false);
        super.startField.forceRemove();
        if (queen == null) {
            this.setQueenReally(new QueenPiece(board, field, c, false));
        } else {
            queen.createSymbol();
            board.addPiece(queen);
            field.setPiece(queen);
        }
    }

    /**
     * undoes promotional move
     * @param board to be reconfigured as promotional move has been undone
     */
    @Override
    public void undo(Board board) {
        startField.restorePiece(pawn);
        piece = pawn;
        board.addPiece(pawn); // new
        board.removePiece(queen);
        if (victim != null) {
            victimField.restorePiece(victim);
            board.addPiece(victim);

            if (victim instanceof RookPiece) {
                ((RookPiece) victim).revive();
            }
        } else {
            field.removePiece(false);
            field.forceRemove();
        }

        resetBoard(board);
    }

    /**
     * processes a move as with no graphical update happen
     * @param board to be processed
     * @param otherPieces list of pieces on board
     * @param myPieces pieces of a player
     * @param thisMove promotional move that to be silently happen
     */
    @Override
    public void executeSimulation(Board board, List<Piece> otherPieces, List<Piece> myPieces, Move thisMove) {
        super.executeSimulation(board, otherPieces);
        Piece piece = thisMove.getPiece();
        myPieces.remove(piece);
        piece = new QueenPiece(board, thisMove.getField(), piece.getColor(), true);
        queen = piece;
        myPieces.add(piece);
    }

    /**
     * undo a move as with no graphical update happen
     * @param board to be processed
     * @param otherPieces list of pieces on board
     * @param myPieces pieces of a player
     * @param thisMove promotional move that to be silently happen
     */
    @Override
    public void undoSimulation(Board board, List<Piece> otherPieces, List<Piece> myPieces, Move thisMove) {
        super.undoSimulation(board, otherPieces);
        myPieces.remove(queen);
        myPieces.add(thisMove.getPiece());
    }

}
