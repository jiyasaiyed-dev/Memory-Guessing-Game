package com.memorygame.model;

import com.memorygame.enums.Difficulty;
import com.memorygame.enums.GameMode;

public class GameResult {
    private final String playerName;
    private final int finalScore;
    private final int correctAnswers;
    private final int wrongAnswers;
    private final int bestStreak;
    private final int highestLevel;
    private final GameMode mode;
    private final Difficulty difficulty;
    private final boolean completed;

    public GameResult(String playerName, int finalScore, int correctAnswers, int wrongAnswers,
                       int bestStreak, int highestLevel, GameMode mode, Difficulty difficulty, boolean completed) {
        this.playerName = playerName;
        this.finalScore = finalScore;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.bestStreak = bestStreak;
        this.highestLevel = highestLevel;
        this.mode = mode;
        this.difficulty = difficulty;
        this.completed = completed;
    }

    public double getAccuracy() {
        int total = correctAnswers + wrongAnswers;
        return total == 0 ? 0.0 : (correctAnswers * 100.0) / total;
    }

    public String getPerformanceRating() {
        double acc = getAccuracy();
        if (acc >= 90) return "EXCELLENT";
        if (acc >= 80) return "VERY GOOD";
        if (acc >= 70) return "GOOD";
        if (acc >= 50) return "NEEDS IMPROVEMENT";
        return "KEEP PRACTICING";
    }

    public String getPlayerName() { return playerName; }
    public int getFinalScore() { return finalScore; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getWrongAnswers() { return wrongAnswers; }
    public int getBestStreak() { return bestStreak; }
    public int getHighestLevel() { return highestLevel; }
    public GameMode getMode() { return mode; }
    public Difficulty getDifficulty() { return difficulty; }
    public boolean isCompleted() { return completed; }
}
