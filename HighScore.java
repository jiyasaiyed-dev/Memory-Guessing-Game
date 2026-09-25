package com.memorygame.model;

import com.memorygame.enums.Difficulty;
import com.memorygame.enums.GameMode;

/** Immutable record of a single completed game, used for the leaderboard. */
public class HighScore implements Comparable<HighScore> {
    private final String playerName;
    private final int score;
    private final GameMode mode;
    private final Difficulty difficulty;
    private final int level;

    public HighScore(String playerName, int score, GameMode mode, Difficulty difficulty, int level) {
        this.playerName = playerName;
        this.score = score;
        this.mode = mode;
        this.difficulty = difficulty;
        this.level = level;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public GameMode getMode() { return mode; }
    public Difficulty getDifficulty() { return difficulty; }
    public int getLevel() { return level; }

    /** Serialize to a single pipe-delimited line for file storage. */
    public String toFileLine() {
        return playerName + "|" + score + "|" + mode.name() + "|" + difficulty.name() + "|" + level;
    }

    public static HighScore fromFileLine(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 5) throw new IllegalArgumentException("Corrupted high score line: " + line);
        String name = parts[0];
        int score = Integer.parseInt(parts[1]);
        GameMode mode = GameMode.valueOf(parts[2]);
        Difficulty diff = Difficulty.valueOf(parts[3]);
        int level = Integer.parseInt(parts[4]);
        return new HighScore(name, score, mode, diff, level);
    }

    @Override
    public int compareTo(HighScore other) {
        return Integer.compare(other.score, this.score); // descending
    }
}
