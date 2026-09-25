package com.memorygame.model;

/** Mutable state tracked while a single game is in progress. */
public class GameSession {
    private final Player player;
    private final GameConfiguration configuration;

    private int currentLevel = 1;
    private int currentRound = 1;
    private int score = 0;
    private int lives;
    private int streak = 0;
    private int bestStreak = 0;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private String currentSequence = "";
    private Question currentQuestion;

    public GameSession(Player player, GameConfiguration configuration) {
        this.player = player;
        this.configuration = configuration;
        this.lives = configuration.getDifficulty().getLives();
    }

    public void registerCorrectAnswer(int points) {
        score += points;
        correctAnswers++;
        streak++;
        if (streak > bestStreak) bestStreak = streak;
    }

    public void registerWrongAnswer() {
        wrongAnswers++;
        streak = 0;
        lives--;
    }

    public boolean isGameOver() { return lives <= 0; }
    public boolean isComplete() { return currentRound > configuration.getTotalRounds(); }

    public void nextRound() {
        currentRound++;
        // Level up every 3 rounds
        if ((currentRound - 1) % 3 == 0) {
            currentLevel++;
        }
    }

    public void addScore(int amount) {
        score = Math.max(0, score + amount);
    }

    public Player getPlayer() { return player; }
    public GameConfiguration getConfiguration() { return configuration; }
    public int getCurrentLevel() { return currentLevel; }
    public int getCurrentRound() { return currentRound; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getStreak() { return streak; }
    public int getBestStreak() { return bestStreak; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getWrongAnswers() { return wrongAnswers; }
    public String getCurrentSequence() { return currentSequence; }
    public void setCurrentSequence(String s) { this.currentSequence = s; }
    public Question getCurrentQuestion() { return currentQuestion; }
    public void setCurrentQuestion(Question q) { this.currentQuestion = q; }
}
