package com.memorygame.model;

import com.memorygame.enums.Difficulty;
import com.memorygame.enums.GameMode;

/** Holds the immutable settings chosen for a single game run. */
public class GameConfiguration {
    private final GameMode mode;
    private final Difficulty difficulty;
    private final int totalRounds;

    public GameConfiguration(GameMode mode, Difficulty difficulty, int totalRounds) {
        this.mode = mode;
        this.difficulty = difficulty;
        this.totalRounds = totalRounds;
    }

    public GameMode getMode() { return mode; }
    public Difficulty getDifficulty() { return difficulty; }
    public int getTotalRounds() { return totalRounds; }
}
