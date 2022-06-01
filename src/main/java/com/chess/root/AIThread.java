package com.chess.root;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread for an AI player
 */
public class AIThread extends Thread implements Runnable {
    Game game;
    Player player;
    final Object lock;
    private volatile boolean running = true;
    private volatile boolean paused = true;
    private volatile boolean blocked = false;
    private static final Logger LOG = Logger.getLogger(String.class.getName());

    /**
     * thread constructor
     * @param game which is currently played
     * @param player associated with AI
     */
    public AIThread(Game game, Player player) {
        this.game = game;
        this.player = player;
        this.lock = player.getLock();
    }

    /**
     * body of a thread, handles AI moves when unlocked
     */
    @Override
    public void run() {
        while (running) {
            synchronized (lock) {
                if (!running) {
                    break;
                }
                if (paused || blocked) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        LOG.log(Level.SEVERE, e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (!blocked) {
                game.getBoard().performAIMove();
            }
        }
    }

    // ---------------------------------- HELPER METHODS ----------------------------------

    /**
     * Stops AI thread and sets lock to wait
     */
    public void requestStop() {
        running = false;
        requestResume();
    }

    /**
     * Pauses AI thread run
     */
    public void requestPause() {
        paused = true;
    }

    /**
     * Continues AI thread run
     */
    public void requestResume() {
        synchronized (lock) {
            paused = false;
            lock.notifyAll();
        }
    }

    /**
     * sets block flag to blockthis param
     * @param blockthis flag to set
     */
    public void block(boolean blockthis) {
        this.blocked = blockthis;
    }

}
