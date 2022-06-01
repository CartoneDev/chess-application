package com.chess.root;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.chess.application.GameController;
import com.chess.model.Mode;
import com.chess.model.Setting;
import com.chess.root.moves.Move;

/**
 * Main game class representing a chess game
 */
public class Game {

	private final GameController controller;
	private final Board board;
	private Player whitePlayer;
	private Player blackPlayer;
	private Player currentPlayer;
	private Double moveCounter = 1.0;
	private boolean gameEnded = false;
	private static final Logger LOG = Logger.getLogger(String.class.getName());
	private String pgnEvent;
	private String pgnSite;
	private String pgnDate;
	private static int pgnRoundCounter = 0;
	private String pgnRound;
	private final String pgnDifficulty;
	private String pgnWhite;
	private String pgnBlack;
	private String pgnResult = "*";

	/**
	 * Constructor for a game object
	 * @param controller which passes GUI calls - player controls from gui
	 * @param settings stores game settings
	 */
	public Game(GameController controller, Setting settings) {
		this.controller = controller;
		setUpPlayers(settings.getColor(), settings.getMode());
		setImportData(settings);
		holdThreads();
		pgnDifficulty = settings.getDifficultyName();
		this.board = new Board(this, settings);

		board.executePgn(settings);
		controller.renderDisplay();
		releaseThreads();
	}

	private void setImportData(Setting settings) {

		pgnEvent = PgnParser.getEvent(settings);
		pgnSite = PgnParser.getSite(settings);
		pgnDate = PgnParser.getDate(settings);
		setRoundCounter(settings);
		String round = PgnParser.getRound(settings);
		if (round == null) {
			round = Integer.toString(pgnRoundCounter);
		}
		pgnRound = round;
		pgnWhite = settings.getWhite();
		pgnBlack = settings.getBlack();
		pgnResult = PgnParser.getResult(settings);
	}

	private static void setRoundCounter(Setting settings) {
		String round = PgnParser.getRound(settings);
		if (round == null) {
			pgnRoundCounter++;
		} else {
			try {
				pgnRoundCounter = Integer.parseInt(round);
			} catch (Exception e) {
				pgnRoundCounter++;
			}
		}
	}

	// ---------------------------------- PLAYER HANDLING ----------------------------------

	/**
	 * Getter for a event name from pgn data
	 * @return event name
	 */
	public String getEvent() {
		if (pgnEvent == null) {
			return "";
		}
		return pgnEvent;
	}
	/**
	 * Getter for a site from pgn data
	 * @return site where event was hosted
	 */
	public String getSite() {
		if (pgnSite == null) {
			return "";
		}
		return pgnSite;
	}

	/**
	 * Getter for a date from pgn data
	 * @return date when an event was hosted
	 */
	public String getDate() {
		if (pgnDate == null) {
			return "";
		}
		return pgnDate;
	}

	/**
	 * Getter for a round from pgn data
	 * @return date when an event was hosted
	 */
	public String getRound() {
		if (pgnRound == null) {
			return "";
		}
		return pgnRound;
	}

	/**
	 * Getter for a difficulty from pgn data
	 * @return difficulty
	 */
	public String getDifficulty() {
		return pgnDifficulty;
	}
	/**
	 * Getter for a white player
	 * @return white player name from pgn data, empty string otherwise
	 */
	public String getWhite() {
		if (pgnWhite == null) {
			return "";
		}
		return pgnWhite;
	}
	/**
	 * Getter for a black player
	 * @return black player name from pgn data, empty string otherwise
	 */
	public String getBlack() {
		if (pgnBlack == null) {
			return "";
		}
		return pgnBlack;
	}

	/**
	 * Getter for a game result
	 * @return game result from pgn turn history
	 */
	public String getResult() {
		if (board.hasHistory()) {
			pgnResult = board.getHistory().get(getHistory().size()-1).getResult();
		} else if (pgnResult == null) {
			return "";
		}
		return pgnResult;
	}

	/**
	 * Getter for a list of moves
	 * @return list of moves done in this game
	 */
	public List<Move> getHistory() {
		return board.getHistory();
	}

	/**
	 * getter for current player
	 * @return current player
	 */
	public Player getPlayer() {
		return currentPlayer;
	}

	/**
	 * getter for an opponent of a current player
	 * @return an opponent of a current player
	 */
	public Player getOtherPlayer() {
		if (currentPlayer != null && currentPlayer.equals(blackPlayer)) {
			return whitePlayer;
		}
		return blackPlayer;
	}

	/**
	 * Getter for players played by AI
	 * @return list of player played by AI
	 */
	public List<Player> getAIPlayers() {
		List<Player> players = new ArrayList<>();
		if (blackPlayer.isAI()) {
			players.add(blackPlayer);
		}
		if (whitePlayer.isAI()) {
			players.add(whitePlayer);
		}
		return players;
	}

	/**
	 * method to pass a turn to another player
	 */
	public void switchPlayer() {
		switchPlayerSilently();
		board.validateBoard();
		notifyAI();
	}

	/**
	 * utility method to technically pass a turn without any clue for a player
	 */
	public void switchPlayerSilently() {
		currentPlayer = getOtherPlayer();

		if (controller != null) {
			controller.displayPlayer(this);
		}
	}

	/**
	 * sets display of possible moves to on / off
	 * @param on / off the display of possible moves
	 */
	public void setDummyMode(boolean on) {
		board.setDummyMode(on);
	}

	// ---------------------------------- EDIT MODE HANDLING ----------------------------------

	/**
	 * Reverts last move
	 */
	public void stepBack() {
		if (board.hasHistory()) {
			controller.setForwardBut(true);
			switchPlayerSilently();
			board.undoMove(null);
			decreaseMoveCounter();

			if (!board.hasHistory()) {
				controller.setBackBut(false);
				controller.requestFocusForward();
				controller.setDisplay(currentPlayer.toString() + " starts the game");
			}

			controller.setGoBut(true);
			//board.render();
			resetAI();

		}
	}

	/**
	 * transforms board state to next move
	 */
	public void stepForward() {
		if (board.hasFutureMoves()) {
			controller.setBackBut(true);
			board.redoMove(null);
		}

		if (!board.hasFutureMoves()) {
			controller.setForwardBut(false);
			controller.requestFocusBack();
			if (gameEnded) {
				controller.setGoBut(false);
			}

		}
	}

	/**
	 * blocks threads
	 */
	private void holdThreads() {
		if (blackPlayer.isAI() && blackPlayer.getThread() != null) {
			blackPlayer.getThread().block(true);
		}
		if (whitePlayer.isAI() && whitePlayer.getThread() != null) {
			whitePlayer.getThread().block(true);
		}
		pauseCurrent();
	}

	/**
	 * pauses the game
	 */
	public void pauseGame() {
		if (gameEnded) {
			controller.setGoBut(false);
		} else {
			controller.requestFocusGo();
		}
		if (board.hasHistory()) {
			controller.setBackBut(true);
		}
		board.setEditMode(true);
		holdThreads();
	}

	/**
	 * resumes the game
	 */
	public void resumeGame() {
		gameEnded = false;
		controller.requestFocusStop();
		board.validateBoard();
		board.cleanUpEdit();
		board.setEditMode(false);


		releaseThreads();
	}

	private void releaseThreads() {
		if (blackPlayer.isAI() && blackPlayer.getThread() != null) {
			blackPlayer.getThread().block(false);
		}
		if (whitePlayer.isAI() && whitePlayer.getThread() != null) {
			whitePlayer.getThread().block(false);
		}
		resumeCurrent();
		notifyAI();
	}

	/**
	 * sets speed delay in ms
	 * @param speed - delay in ms
	 */
	public void setSpeed(int speed) {
		board.setDelay(speed);
	}

	// ---------------------------------- THREAD HANDLING ----------------------------------

	private void notifyAI() {
		if (getOtherPlayer().isAI()) {
			getOtherPlayer().getThread().requestPause();
		}
		if (currentPlayer.isAI()) {
			currentPlayer.getThread().requestResume();
		}
	}

	private void resetAI() {
		if (whitePlayer.isAI() && whitePlayer.getThread().getState() == Thread.State.TERMINATED) {
			createThread(whitePlayer);
			whitePlayer.getThread().block(true);
		}
		if (blackPlayer.isAI() && blackPlayer.getThread().getState() == Thread.State.TERMINATED) {
			createThread(blackPlayer);
			blackPlayer.getThread().block(true);
		}
	}

	private void pauseCurrent() {
		if (currentPlayer.isAI()) {
			currentPlayer.getThread().requestPause();
		}
	}

	private void resumeCurrent() {
		if (currentPlayer.isAI()) {
			currentPlayer.getThread().requestResume();
		}
	}

	/**
	 * ends the game
	 * @param endType end game type
	 * @param hasWinner does game have a winner
	 */
	public void endGame(String endType, boolean hasWinner) {
		gameEnded = true;
		controller.setForwardBut(false);
		controller.setGoBut(false);
		if (blackPlayer.isAI() && blackPlayer.getThread() != null) {
			blackPlayer.getThread().requestStop();
		}
		if (whitePlayer.isAI() && whitePlayer.getThread() != null) {
			whitePlayer.getThread().requestStop();
		}
		LOG.log(Level.INFO, () -> "SYSTEM: ---------- game ends after " + moveCounter + " moves ----------");
		String end = endType;
		if (hasWinner) {
			end += " by " + getOtherPlayer().toString() + " player!";
		}
		controller.setDisplay(end);
	}

	// ---------------------------------- HELPER METHODS ----------------------------------

	/**
	 * Updates move counter on turn
	 */
	public void updateMoveCounter() {
 		moveCounter += 0.5;
 		controller.updateMoveCounter(Double.toString(moveCounter));
 	}

	private void decreaseMoveCounter() {
 		moveCounter -= 0.5;
 		controller.updateMoveCounter(Double.toString(moveCounter));
 	}

	private void setUpPlayers(boolean color, Mode mode) {
		blackPlayer = new Player(true);
   		whitePlayer = new Player(false);
   		currentPlayer = whitePlayer;
   		// checks mode value to set mode; for two manual players, no AI player has to be set up
   		if (mode == Mode.MANUAL_VS_AI) {
   			if (!color) {
   				createThread(whitePlayer);
   			} else {
   				createThread(blackPlayer);
   			}
   		}
   		LOG.log(Level.INFO, "SYSTEM: ---------- game starts ----------");
	}

	private void createThread(Player player) {
		player.setAI();
		player.setThread(new AIThread(this, player));
		player.getThread().start();
	}

	// ---------------------------------- GENERIC GETTERS ----------------------------------

	// getter for game controller
	public GameController getController() {
		return controller;
	}
    // getter for board
 	public Board getBoard() {
 		return board;
 	}


}
