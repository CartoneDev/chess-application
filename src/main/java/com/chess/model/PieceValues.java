package com.chess.model;

/**
 * collections of piece values for different difficulties of an AI
 */
public enum PieceValues {

    RANDOM(200, 200, 200, 200, 200, 200),
    EASY(2000, 500, 200, 200, 200, 200),
    HARD(10000, 1500, 500, 500, 600, 200);

    private final int king;
    private final int queen;
    private final int bishop;
    private final int knight;
    private final int rook;
    private final int pawn;

    PieceValues(int king, int queen, int bishop, int knight, int rook, int pawn) {
        this.king = king;
        this.queen = queen;
        this.bishop = bishop;
        this.knight = knight;
        this.rook = rook;
        this.pawn = pawn;
    }

    // ---------------------------------- GENERIC GETTERS ----------------------------------
    // -- gets a particular figure value
    public int king() {
        return king;
    }

    public int queen() {
        return queen;
    }

    public int bishop() {
        return bishop;
    }

    public int knight() {
        return knight;
    }

    public int rook() {
        return rook;
    }

    public int pawn() {
        return pawn;
    }

    }
