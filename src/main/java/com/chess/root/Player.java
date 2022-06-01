package com.chess.root;


/**
 * Chess player
 */
public class Player {

    private boolean color;
    private boolean isAI = false;
    private AIThread thread;

    private final Object lock = new Object();

    /**
     * constructs player object
     * @param color of a player
     */
    public Player(boolean color) {
        this.color = color;
    }

    // ---------------------------------- AI HANDLING ----------------------------------

    /**
     * sets player to be as an AI
     */
    public void setAI() {
        isAI = true;
    }

    /**
     * checks if player is AI
     * @return is player an AI
     */
    public boolean isAI() {
        return isAI;
    }

    /**
     * returns player lock object for AI thread
     * @return player lock object
     */
    public Object getLock() {
        return lock;
    }

    /**
     * sets AI thread to player
     * @param thread to set
     */
    public void setThread(AIThread thread) {
        this.thread = thread;
    }

    /**
     * getter for an AI Thread
     * @return AI thread
     */
    public AIThread getThread() {
        return thread;
    }

    // ---------------------------------- GENERIC GETTER ----------------------------------

    /**
     * getter method for color
     * @return is black player
     */
    public boolean isBlack() {
        return color;
    }

    // ---------------------------------- HELPER METHODS ----------------------------------

    /**
     * represents player with correlating color as a string
     * @return player color as text
     */
    @Override
    public String toString() {
        return color ? "black" : "white";
    }

    /**
     * checks if two players are the same player
     * @param obj second player to check
     * @return is two players are the same player
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * helper hash function for equal check
     * @return hash of player
     */
    @Override
    public int hashCode() {
        int hash = this.isBlack() ? 3 : 4;
        hash += this.toString().hashCode();
        hash += 123;
        return hash;
    }

}
