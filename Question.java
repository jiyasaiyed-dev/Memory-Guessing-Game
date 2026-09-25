package com.memorygame.model;

import com.memorygame.enums.QuestionType;
import java.util.List;

/** Represents a single question generated from a memorized sequence. */
public class Question {
    private final QuestionType type;
    private final String promptText;
    private final List<String> options;   // null for COMPLETE_SEQUENCE
    private final String correctAnswer;

    public Question(QuestionType type, String promptText, List<String> options, String correctAnswer) {
        this.type = type;
        this.promptText = promptText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public QuestionType getType() { return type; }
    public String getPromptText() { return promptText; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }

    public boolean checkAnswer(String givenAnswer) {
        if (givenAnswer == null) return false;
        return correctAnswer.trim().equalsIgnoreCase(givenAnswer.trim());
    }
}
