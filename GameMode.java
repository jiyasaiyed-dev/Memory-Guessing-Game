package com.memorygame.enums;

public enum GameMode {
    NUMBERS("Numbers", "7 4 9 2 6 1"),
    ALPHABETS("Alphabets", "A K R P T M"),
    LETTERS_NUMBERS("Letters + Numbers", "A7K2P9M4"),
    SYMBOLS("Symbols", "@ # $ % & *"),
    MIXED("Mixed", "A7#k@29"),
    CARDS("Cards", "[APPLE] [CAR] [STAR]");

    private final String label;
    private final String example;

    GameMode(String label, String example) {
        this.label = label;
        this.example = example;
    }

    public String getLabel() { return label; }
    public String getExample() { return example; }

    @Override
    public String toString() { return label; }
}
