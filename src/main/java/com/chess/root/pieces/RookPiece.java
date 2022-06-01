package com.chess.root.pieces;

import java.util.ArrayList;
import com.chess.model.Direction;
import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.moves.Move;

/**
 * Rook class
 */
public class RookPiece extends Piece {
	
	private static String name = "rook";
	private static String notation = "R";
	private static Direction[] dirs = { Direction.TOP, Direction.BOTTOM, Direction.LEFT, Direction.RIGHT };
	private static final int[][] ROOK_UP = {
			{0,0,0,0,0,0,0,0},
			{5,10,10,10,10,10,10,5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0, 0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{0,0,0,5,5,0,0,0}
			};
	private static final int[][] ROOK_DOWN = {
			{0,0,0,5,5,0,0,0},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{5,10,10,10,10,10,10,5},
			{0,0,0,0,0,0,0,0}
			};
	private boolean moved = false;
	private int movecounter = 0;
	private boolean dead = false;

	/**
	 * rook constructor
	 * @param board where rook should be placed
	 * @param field where rook should be placed
	 * @param color of a rook that should be placed
	 */
	public RookPiece(Board board, Field field, boolean color) {
		super(board, field, color, name, notation, board.getPieceValue().rook(), !color ? ROOK_UP : ROOK_DOWN, false);
		moved = color ? !(field.getNotation().contentEquals("a8") || field.getNotation().contentEquals("h8")) : !(field.getNotation().contentEquals("a1") || field.getNotation().contentEquals("h1"));
	}

	/**
	 * checks if rook has moved in a game
	 * @return rook is moved
	 */
	@Override
	public boolean wasMoved() {
		return moved;
	}

	/**
	 * method to remember that rook moved
	 */
	@Override
	public void moved() {
		movecounter++;
		moved = true;
	}

	/**
	 * method to reckon undo of rook move
	 */
	@Override
	public void unmove() {
		movecounter--;
		if (movecounter == 0) {
			moved = false;
		}
	}

	/**
	 * marks figure as taken
	 */
	public void kill() {
		dead = true;
	}

	/**
	 * marks figure as not taken, when undo move
	 */
	public void revive() {
		dead = false;
	}

	/**
	 * checks if figure is taken
	 * @return is figure taken
	 */
	@Override
	public boolean isDead() {
		return dead;
	}
	
	// ---------------------------------- ABSTRACT METHODS ----------------------------------

	/**
	 * calculate a list of possible moves for a rook
	 * @return list of possible moves
	 */
	@Override
	public ArrayList<Move> getMoves() {
		ArrayList<Move> moves = new ArrayList<>();
		
		// find moves
		for (Direction direction : dirs) {
			int col = this.getField().getColumn();
			int row = this.getField().getRow();
			for (int i = 0; i < 8; i++) {
				col += direction.col();
				row += direction.row();
				// check if next field coordinates are valid
				if (col >= 0 && col < 8 && row >= 0 && row < 8) {				
					Field next = board.getField(col, row);
					Piece victim = next.getPiece();
					// check if enemy piece is next
					if (victim != null) {
						if (victim.getColor() != this.getColor()) {
								moves.add(new Move(this, next, victim));
						}
						break;
					} else {
						moves.add(new Move(this, next, null));
					}
				}
			}
		}
		return moves;
	}


}
