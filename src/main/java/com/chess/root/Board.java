package com.chess.root;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.chess.application.FieldButton;
import com.chess.model.Difficulty;
import com.chess.model.PieceValues;
import com.chess.model.Setting;
import com.chess.root.moves.Move;
import com.chess.root.pieces.KingPiece;
import com.chess.root.pieces.PawnPiece;
import com.chess.root.pieces.Piece;
import javafx.scene.input.MouseEvent;

public class Board {

    private final Game game;
    private boolean editMode = false;
    private final PieceValues pieceValues;
    private final Difficulty difficulty;
    private final Field[][] fields;
    private final List<Piece> blackPieces;
    private final List<Piece> whitePieces;
    private Piece activePiece;
    private boolean blackPlays = false;
    private boolean check = false;
    private boolean isNextMoveUnlocked = true;
    private List<Move> currentMoves;
    private int recursionDepth;
    private int delayControl = 300;
    private static final Logger LOG = Logger.getLogger(String.class.getName());
    private Piece enPassantPiece;

    private boolean initializing = false;
    private boolean endGame = false;
    private final List<Move> moveHistory = new LinkedList<>();
    private final List<Move> moveFuture = new LinkedList<>();
    private int countdown;
    private static final Random random = new Random();
    private boolean dummyMode;

    /**
     * construcotor for game Board
     * @param game currently being played
     * @param settings game settings
     */
    public Board(Game game, Setting settings) {
        this.game = game;
        this.difficulty = settings.getDifficulty();
        recursionDepth = difficulty.tree();
        this.pieceValues = settings.getPieceValue();
        this.blackPieces = new LinkedList<>();
        this.whitePieces = new LinkedList<>();
        this.fields = new Field[8][8];
        initializeFields(settings);
        initializePieces();

        if (!settings.hasPgn()) {
            validateBoard();
        }

    }

    // ---------------------------------- MANUAL GAMEPLAY ----------------------------------

    /**
     * executes game move of a player
     * @param event
     */
    public void performManualMove(MouseEvent event) {
        if (!getPlayer().isAI()) {
            FieldButton button = (FieldButton) event.getSource();
            Field field = button.getField();
            if (isNextMoveUnlocked) {
                // start move
                activePiece = field.getPiece();
                if (canPieceMove(activePiece)) {
                    showHintsForDummies(activePiece, true);
                    isNextMoveUnlocked = false;
                    field.removePiece(false);
                }
            } else {
                // finish move
                Move move = getMove(activePiece, field);
                if (move != null) {
                    showHintsForDummies(activePiece, false);
                    executeMove(move);
                } else {
                    abortMove();
                }
            }
        }
    }


    // ---------------------------------- MANUAL GAMEPLAY LOGIC HANDLING ONLY ----------------------------------

    private boolean canPieceMove(Piece piece) {
        if (piece == null) {
            LOG.log(Level.INFO, "SYSTEM: (no piece)");
            return false;
        }
        if (piece.isBlack() != blackPlays) {
            LOG.log(Level.INFO, "SYSTEM: (wrong color)");
            return false;
        }
        boolean found = false;
        if (!currentMoves.isEmpty()) {
            for (Move m : currentMoves) {
                if (m.getPiece().equals(piece)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            LOG.log(Level.INFO, "SYSTEM: (not allowed to move)");
        }
        return found;
    }

    private Move getMove(Piece piece, Field field) {
        if (piece == null || field == null) {
            return null;
        }
        if (!currentMoves.isEmpty()) {
            for (Move m : currentMoves) {
                if (m.getPiece().equals(piece) && m.getField().equals(field)) {
                    return m;
                }
            }
        }
        return null;
    }

    private void abortMove() {
        showHintsForDummies(activePiece, false);
        LOG.log(Level.INFO, "SYSTEM: (can't move here)");
        isNextMoveUnlocked = true;
        activePiece.getField().setPiece(activePiece, true);
    }

    // ---------------------------------- AI GAMEPLAY ----------------------------------

    /**
     * executes game move of an AI player
     */
    public void performAIMove() {
        if (getPlayer() != null) {
            Move bestMove = getBestMove();
            if (bestMove != null && isNextMoveUnlocked) {
                isNextMoveUnlocked = false;
                shortDelay();
                bestMove.getPiece().getField().removePiece(false);
                shortDelay();
                executeMove(bestMove);
            }
        }
    }

    // ---------------------------------- AI GAMEPLAY LOGIC HANDLING ONLY ----------------------------------

    private Move getBestMove() {
        if (currentMoves.isEmpty()) {
            return null;
        }

        List<Move> moves = currentMoves;

        rateMovesAlphaBeta(moves);

        List<Move> bestMoves = new LinkedList<>();
        // spasm generator will - depending on difficulty setting - trigger completely random moves. Otherwise, best rated move is chosen
        int spasm = random.nextInt(difficulty.spasm());
        if (spasm == 0) {
            LOG.log(Level.INFO, "SYSTEM: (oopsie...)");
            bestMoves.addAll(moves);
        } else if (!moves.isEmpty()) {
            Optional<Move> maxMove = moves.stream().max(Comparator.comparing(Move::getRating));
            int maxValue;
            maxValue = maxMove.get().getRating();
            for (Move m : moves) {
                if (m.getRating() == maxValue) {
                    bestMoves.add(m);
                }
            }
        }

        // if multiple moves with same rating are given, take random move
        if (!bestMoves.isEmpty()) {
            int best = random.nextInt(bestMoves.size());
            return bestMoves.get(best);
        }
        return null;
    }

    // ---------------------------------- MOVE RATING ----------------------------------

    /**
     * Implementation of <a href="https://www.chessprogramming.org/Alpha-Beta">Alpha-Beta</a>
     *
     * @param moves list of moves to be rated
     */
    private void rateMovesAlphaBeta(List<Move> moves) {
        if (!moves.isEmpty()) {
            List<Piece> myPieces = new LinkedList<>(getPieces(blackPlays));
            List<Piece> otherPieces = new LinkedList<>(getPieces(getOtherPlayer().isBlack()));
            for (Move c : moves) {
                List<Move> startMove = new LinkedList<>();
                startMove.add(c);
                int rating = rateMovesAlphaBeta(startMove, myPieces, otherPieces, true, blackPlays, -1000000, 1000000, recursionDepth);
                c.incRating(rating);
            }
        }
    }

    private int rateMovesAlphaBeta(List<Move> moves, List<Piece> myPieces, List<Piece> otherPieces, boolean itsMe, boolean isBlack, int alpha, int beta, int tree) {
        List<Integer> numlist = new LinkedList<>();
        int vala = -1000000;
        int valb = 1000000;
        for (Move thisMove : moves) {
            int rating;

            // ----------------- before move -----------------
            thisMove.executeSimulation(this, otherPieces, myPieces, thisMove);
            // ----------------- after move start -----------------

            List<Move> otherMoves = getMoves(otherPieces, !isBlack);

            if (tree > 0 && !otherMoves.isEmpty()) {
                rating = rateMovesAlphaBeta(otherMoves, otherPieces, myPieces, !itsMe, !isBlack, alpha, beta, tree - 1);
                if (tree == recursionDepth) {

                    rating = avoidDrawCheck(thisMove, tree, rating);
                    if (rating > 600000 && tree == recursionDepth && avoidStalemateCheck(isBlack, myPieces) && !(thisMove.getVictim() instanceof KingPiece)) {
                        rating = rating / 320;
                        LOG.log(Level.INFO, "SYSTEM: avoiding stalemate");
                    }
                }
            } else {
                rating = itsMe ? getMaterial(myPieces) - getMaterial(otherPieces) : getMaterial(otherPieces) - getMaterial(myPieces);

                if (otherMoves.isEmpty() && thisMove.getVictim() instanceof KingPiece) {
                    LOG.log(Level.INFO, "SYSTEM: plotting about direct attack of king");
                    rating = rating * 2;
                    if (tree != 0) {
                        rating = rating * (tree + 1);
                    }
                }
            }
            numlist.add(rating + thisMove.getRating());
            // ----------------- after move end -----------------
            thisMove.undoSimulation(this, otherPieces, myPieces, thisMove);
            // ----------------- after move restore -----------------

            if (tree != 0) {
                if (itsMe) {
                    vala = Math.max(vala, rating);
                    alpha = Math.max(alpha, vala);
                } else {
                    valb = Math.min(valb, rating);
                    beta = Math.min(beta, valb);
                }
                if (alpha >= beta) {
                    break;
                }
            }
        }
        return itsMe ? numlist.stream().mapToInt(v -> v).max().orElseThrow(NoSuchElementException::new) : numlist.stream().mapToInt(v -> v).min().orElseThrow(NoSuchElementException::new);
    }

    private boolean avoidStalemateCheck(boolean otherColor, List<Piece> myPieces) {
        Piece king = getKing(!otherColor);
        if (difficulty.draw() && !isPieceEndangered(king, myPieces)) {

            List<Move> kingMoves = new LinkedList<>(king.getMoves());
            int size;
            if (!kingMoves.isEmpty()) {
                size = kingMoves.size();
                for (Move m : kingMoves) {
                    m.executeSimulation(this, myPieces);
                    if (isPieceEndangered(king, myPieces)) {
                        size--;
                    }
                    m.undoSimulation(this, myPieces);
                }
                return size == 0;
            }
        }
        return false;
    }

    private int avoidDrawCheck(Move thisMove, int tree, int rating) {
        if (difficulty.draw()) {
            // avoid draw by threefold repetition
            if (tree == recursionDepth && getBoardOccurrences(moveHistory) >= 1) {
                LOG.log(Level.INFO, "SYSTEM: avoiding threefold rule draw");
                rating = rating / 6;
            }
            // avoid draw by 50 moves rule
            if ((tree == recursionDepth && countdown > 80) && (thisMove.getPiece() instanceof PawnPiece || thisMove.getVictim() != null)) {
                LOG.log(Level.INFO, "SYSTEM: avoiding 50 moves rule draw");
                rating = rating * 2;
            }
        }
        return rating;
    }

    private int getMaterial(List<Piece> pieces) {
        int value = 0;
        int count = pieces.size();
        if (count > 0) {
            for (Piece p : pieces) {
                value += p.getValue();
            }
        }
        return value;
    }

    // ---------------------------------- MOVE EXECUTION ----------------------------------

    private void executeMove(Move move) {
        countdown++;
        moveHistory.add(move);
        move.execute(this);
        isNextMoveUnlocked = true;
    }

    /**
     * redoes a move
     * @param move to redone
     */
    public void redoMove(Move move) {
        if (move == null && !moveFuture.isEmpty()) {
            move = moveFuture.get(moveFuture.size() - 1);
        }

        if (move != null) {
            moveFuture.remove(move);
            move.getPiece().getField().removePiece(true);
            executeMove(move);
        }
    }

    /**
     * undoes a move
     * @param move to undone
     */
    public void undoMove(Move move) {
        if (move == null && !moveHistory.isEmpty()) {
            move = moveHistory.get(moveHistory.size() - 1);
        }

        if (move != null) {
            moveHistory.remove(move);
            moveFuture.add(move);
            move.undo(this);
            validateBoard();
        }
    }

    /**
     * finishes move switches player
     */
    public void endMove() {
        game.updateMoveCounter();
        game.switchPlayer();
    }

    // ---------------------------------- MOVE GENERATION AND VALIDATION ----------------------------------

    private List<Move> getValidMoves(boolean isblack, List<Move> moves) {
        List<Move> validMoves = new LinkedList<>();

        if (moves.isEmpty() || getPlayer() == null) {
            return validMoves;
        }

        boolean checked = false;
        Piece king = getKing(isblack);
        List<Piece> otherPieces = getPieces(!isblack);
        boolean endangered;

        // check if king is currently in check
        if (isPieceEndangered(king, otherPieces)) {
            checked = true;
        }

        for (Move thisMove : moves) {
            thisMove.executeSimulation(this, otherPieces);
            endangered = isPieceEndangered(king, otherPieces);
            if (!endangered) {
                validMoves.add(thisMove);
            }
            thisMove.undoSimulation(this, otherPieces);
        }

        check = checked;

        if (!check) {
            // get castling moves
            KingPiece k = (KingPiece) king;
            if (king != null) {
                Optional.ofNullable(k.getCastlingMoves()).ifPresent(validMoves::addAll);
            }
        }

        if (!validMoves.isEmpty()) {
            ambiguousCheck(validMoves);
        }
        return validMoves;
    }

    private void ambiguousCheck(List<Move> moves) {
        for (Move a : moves) {
            String p = a.getPiece().getNotation();
            String f = a.getField().getNotation();
            String sCol = a.getStartField().getColNotation();
            String sRow = Integer.toString(a.getStartField().getRowNotation());
            for (Move b : moves) {
                if (b.getPiece().getNotation().contentEquals(p) && !(b.getPiece() instanceof PawnPiece) && b.getField().getNotation().contentEquals(f) && !b.getStartField().getNotation().contentEquals(f)) {
                    boolean breaknow = false;
                    if (!sCol.contentEquals(b.getStartField().getColNotation())) {
                        a.ambiguousCol();
                        breaknow = true;
                    }
                    if (!sRow.contentEquals(Integer.toString(b.getStartField().getRowNotation()))) {
                        a.ambiguousRow();
                        breaknow = true;
                    }
                    if (breaknow) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * getter for a king
     * @param blackPlayer is a black player
     * @return a king
     */
    public Piece getKing(boolean blackPlayer) {
        List<Piece> pieces = getPieces(blackPlayer);
        Piece king = null;
        for (Piece p : pieces) {
            if (p instanceof KingPiece) {
                king = p;
            }
        }
        return king;
    }

    /**
     * checks if piece is under attack
     * @param piece to be checked
     * @param otherPieces in game
     * @return is piece under attack
     */
    public boolean isPieceEndangered(Piece piece, List<Piece> otherPieces) {
        List<Move> otherMoves = getMoves(otherPieces, getOtherPlayer().isBlack());
        if (!otherMoves.isEmpty()) {
            for (Move m : otherMoves) {
                if (m.getVictim() != null && m.getVictim().equals(piece)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Move> getMoves(List<Piece> p, boolean isBlack) {
        // get all pieces from specific player
        if (p == null) {
            p = getPieces(isBlack);
        }
        List<Piece> pieces = new LinkedList<>(p);
        List<Move> moves = new LinkedList<>();
        for (Piece piece : pieces) {
            Optional.ofNullable(piece.getMoves()).ifPresent(moves::addAll);
        }
        return moves;
    }

    // ---------------------------------- BOARD STATE HANDLING ----------------------------------

    /**
     * Checks if board is still playble, no stalemate or checks after move, finishes game if so
     */
    public void validateBoard() {
        if (getPlayer() == null) {
            return;
        }

        blackPlays = getPlayer().isBlack();

        gameStateCheck();
        currentMoves = getValidMoves(blackPlays, getMoves(null, blackPlays));
        Move lastMove = getLastMove();

        if (currentMoves.isEmpty() && (!editMode || !hasFutureMoves())) {
            if (lastMove == null) {
                endGame("no playable situation", false);
                return;
            }
            endGame();
            return;
        }

        if (check) {
            if (hasHistory()) {
                getLastMove().setCheckSuffix("+");
            }
            if (!initializing) {
                game.getController().setDisplay("CHECK by " + getOtherPlayer().toString() + " player");
            }
        }
        if (lastMove != null) {
            updateLog(getLastMove().getNotation());
        }
    }

    private void gameStateCheck() {
        int minPieces = 4;
        if (!endGame && difficulty.recursion()) {
            if (blackPieces.size() <= minPieces) {
                endGame = true;
                for (Piece p : blackPieces) {
                    p.setEndTable(true);
                }
            }
            if (whitePieces.size() <= minPieces) {
                endGame = true;
                for (Piece p : whitePieces) {
                    p.setEndTable(true);
                }
            }
            if (endGame) {
                recursionDepth++;
                LOG.log(Level.INFO, "SYSTEM: recursive search horizon increased to: {0}", recursionDepth);
            }
        } else if (endGame && (getPieces(blackPlays).size() > minPieces && getPieces(!blackPlays).size() > minPieces) && difficulty.recursion()) {
            endGame = false;
            for (Piece p : blackPieces) {
                p.setEndTable(false);
            }
            for (Piece p : whitePieces) {
                p.setEndTable(false);
            }
            recursionDepth = difficulty.tree();
        }
    }

    private int getBoardOccurrences(List<Move> posList) {
        return posList.size();
    }

    private void updateLog(String moveNotation) {
        LOG.log(Level.INFO, moveNotation);
    }

    // ---------------------------------- END OF GAME HANDLING ----------------------------------

    private void endGame() {
        if (check) {
            if (hasHistory()) {
                getLastMove().setCheckSuffix("#");
            }
            if (blackPlays) {
                if (hasHistory()) {
                    getLastMove().setResult("1:0");
                }
            } else {
                if (hasHistory()) {
                    getLastMove().setResult("0:1");
                }
            }
            endGame("CHECKMATE", true);
        } else {
            if (hasHistory()) {
                getLastMove().setResult("1/2:1/2");
            }
            endGame("STALEMATE!", false);
        }
    }

    private void endGame(String end, boolean hasWinner) {
        game.endGame(end, hasWinner);
    }

    // ---------------------------------- EDIT MODE HANDLING ----------------------------------

    /**
     * sets control delay so graphics not that intensly updated, better for a player eye perception
     * @param delay
     */
    public void setDelay(int delay) {
        delayControl = delay;
    }

    /**
     * Check if board is in edit mode
     * @return board is in play mode
     */
    public boolean isPlayMode() {
        return !editMode;
    }

    /**
     * Clean up and validate board on edit mode exit
     */
    public void cleanUpEdit() {
        validateBoard();
        if (hasFutureMoves()) {
            moveFuture.clear();
        }
    }

    /**
     * self documentary
     */
    public boolean hasFutureMoves() {
        return !moveFuture.isEmpty();
    }

    // ---------------------------------- PIECE AND FIELD HANDLING ----------------------------------

    /**
     * getter for figures
     * @param isBlack
     * @return figures
     */
    public List<Piece> getPieces(boolean isBlack) {
        return isBlack ? blackPieces : whitePieces;
    }

    /**
     * Adds piece to a player
     * @param piece to add in game
     */
    public void addPiece(Piece piece) {
        if (piece.isBlack()) {
            blackPieces.add(piece);
        } else {
            whitePieces.add(piece);
        }
    }

    /**
     * removes piece from a board
     * @param piece to remove
     */
    public void removePiece(Piece piece) {
        if (piece.isBlack()) {
            blackPieces.remove(piece);
        } else {
            whitePieces.remove(piece);
        }
    }

    /**
     * @param column
     * @param row
     * @return field on col and row
     */
    public Field getField(int column, int row) {
        return fields[row][column];
    }

    // ---------------------------------- GENERIC GETTERS AND SETTERS ----------------------------------


    // difficulty

    /**
     * @return piece value
     */
    public PieceValues getPieceValue() {
        return pieceValues;
    }

    // rules

    /**
     * @return piece that've done passant
     */
    public Piece getEnPassantPiece() {
        return enPassantPiece;
    }

    /**
     * sets piece to passant
     * @param piece pawn to set as one that 've done passant
     */
    public void setEnPassantPiece(Piece piece) {
        enPassantPiece = piece;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int c) {
        countdown = c;
    }

    // edit mode
    public void setEditMode(boolean editing) {
        editMode = editing;
    }

    public boolean hasHistory() {
        return !moveHistory.isEmpty();
    }

    public List<Move> getHistory() {
        return moveHistory;
    }

    public Move getLastMove() {
        if (hasHistory()) {
            return moveHistory.get(moveHistory.size() - 1);
        }
        return null;
    }


    private Player getPlayer() {
        return game.getPlayer();
    }

    private Player getOtherPlayer() {
        return game.getOtherPlayer();
    }

    // ---------------------------------- GUI HELPER METHODS ----------------------------------

    private void shortDelay() {
        try {
            TimeUnit.MILLISECONDS.sleep(delayControl);
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * displays hint of possible move for field on which figure standing and where figure can go
     * @param piece that is activated to take turn
     * @param on is hint to be shown of disabled after being shown
     */
    public void showHintsForDummies(Piece piece, boolean on) {
        if (dummyMode) {
            Double op = on ? 0.7 : 1.0;
            if (piece != null) {
                for (Move m : currentMoves) {
                    if (m.getPiece() == piece) {
                        m.getField().setOpacity(op);
                    }
                }
            }
        }
    }

    /**
     * sets figure hint mode on / off
     * @param on / off figure hint mode
     */
    public void setDummyMode(boolean on) {
        dummyMode = on;
    }

    /**
     * triggers render for each field on a board
     */
    public void render() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                fields[i][j].render();
            }
        }
    }

    // ---------------------------------- INITIALIZATION ----------------------------------

    private void initializeFields(Setting settings) {
        boolean black = false;
        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 8; column++) {
                // create field and button
                Field field = new Field(column, row, black);
                FieldButton button = new FieldButton(this, field);
                field.setButton(button);

                fields[row][column] = field;

                // handle appearance on gui
                if (black) {
                    button.getStyleClass().add("button-black");
                } else {
                    button.getStyleClass().add("button-white");
                }

                settings.getGrid().add(button, column, row);
                black = !black;
            }
            black = !black;
        }
    }

    private void initializePieces() {
        PieceInitializer.initialize(this);
    }


    /**
     * simulates game by PGN save file
     * @param settings pgn data
     */
    public void executePgn(Setting settings) {
        if (settings.hasPgn()) {
            initializing = true;
            List<Move> pgnMoves;
            boolean turn = blackPlays;
            List<String> moveString = settings.getPgnMoves();
            if (!moveString.isEmpty()) {
                for (String step : moveString) {

                    pgnMoves = getValidMoves(turn, getMoves(null, turn));
                    Move preMove = PgnParser.parseMove(step, pgnMoves);

                    if (preMove == null) {
                        LOG.log(Level.INFO, "SYSTEM: pgn execution break at: {0}", step);
                        break;
                    }
                    preMove.getPiece().getField().removePiece(false);
                    executeMove(preMove);
                    turn = !turn;
                }
                initializing = false;
                validateBoard();
                render(); // not needed when validateboard renders
            }
        }
    }

}
