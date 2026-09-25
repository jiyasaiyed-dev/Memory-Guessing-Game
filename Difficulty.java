package com.memorygame.enums;

/**
 * Centralized difficulty configuration. No difficulty values are
 * hardcoded anywhere else in the project.
 */
public enum Difficulty {
    EASY(5, 5, 3, 10),
    MEDIUM(7, 4, 3, 20),
    HARD(9, 3, 3, 30),
    EXPERT(12, 2, 3, 50);

    private final int sequenceLength;
    private final int memoryTimeSeconds;
    private final int lives;
    private final int basePoints;

    Difficulty(int sequenceLength, int memoryTimeSeconds, int lives, int basePoints) {
        this.sequenceLength = sequenceLength;
        this.memoryTimeSeconds = memoryTimeSeconds;
        this.lives = lives;
        this.basePoints = basePoints;
    }

    public int getSequenceLength() { return sequenceLength; }
    public int getMemoryTimeSeconds() { return memoryTimeSeconds; }
    public int getLives() { return lives; }
    public int getBasePoints() { return basePoints; }
}
