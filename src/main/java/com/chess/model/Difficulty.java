package com.chess.model;

/**
 * various AI difficulty settings
 */

public enum Difficulty {

    RANDOM("random", 1, 0, 1, false, true),
    EASY("easy", 4, 1, 8, true, true),
    HARD("hard", 7, 2, 30, true, true);

    private final String name;
    private final int level;
    private final int tree;
    private final int spasm;
    private final boolean recursionDepthChanges;
    private final boolean drawCheck;

    Difficulty(String name, int level, int tree, int spasm, boolean recursionDepthChanges, boolean drawCheck) {
        this.name = name;
        this.level = level;
        this.tree = tree;
        this.spasm = spasm;
        this.recursionDepthChanges = recursionDepthChanges;
        this.drawCheck = drawCheck;
    }

    // ---------------------------------- GENERIC GETTERS ----------------------------------

    public String get() {
        return name;
    }

    public int level() {
        return level;
    }

    /**
     * @return depth of recursive move checks
     */
    public int tree() {
        return tree;
    }

    /**
     * @return random factor of a difficulty
     */
    public int spasm() {
        return spasm;
    }

    /**
     * gets flag setting for a difficulty representing if depth(of a move search) should be checked recursively in depth
     * @return
     */
    public boolean recursion() {
        return recursionDepthChanges;
    }

    /**
     * @return is draw checkers set for this difficulty
     */
    public boolean draw() {
        return drawCheck;
    }


}
