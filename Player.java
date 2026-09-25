package com.memorygame.model;

/** Represents a player and their lifetime statistics. */
public class Player {
    private String name;
    private int totalScore;
    private int gamesPlayed;
    private int correctAnswers;
    private int wrongAnswers;
    private int bestStreak;
    private int highestLevel;

    public Player(String name) {
        this.name = name;
    }

    public void recordGame(int score, int correct, int wrong, int streak, int level) {
        gamesPlayed++;
        totalScore += score;
        correctAnswers += correct;
        wrongAnswers += wrong;
        if (streak > bestStreak) bestStreak = streak;
        if (level > highestLevel) highestLevel = level;
    }

    public String getName() { return name; }
    public int getTotalScore() { return totalScore; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getWrongAnswers() { return wrongAnswers; }
    public int getBestStreak() { return bestStreak; }
    public int getHighestLevel() { return highestLevel; }
}
