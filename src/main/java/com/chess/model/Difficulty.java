package com.chess.model;

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

    public int tree() {
        return tree;
    }

    public int spasm() {
        return spasm;
    }

    public boolean recursion() {
        return recursionDepthChanges;
    }

    public boolean draw() {
        return drawCheck;
    }


}
