package com.memorygame.memory;

import com.memorygame.enums.Difficulty;
import com.memorygame.enums.QuestionType;
import com.memorygame.model.Question;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Builds a Question from a memorized token sequence. Question type is
 * selected intelligently based on difficulty.
 */
public final class QuestionFactory {
    private static final Random RANDOM = new SecureRandom();

    private QuestionFactory() { }

    public static QuestionType pickTypeFor(Difficulty difficulty) {
        int roll = RANDOM.nextInt(100);
        switch (difficulty) {
            case EASY:
                return QuestionType.COMPLETE_SEQUENCE; // mostly complete sequence
            case MEDIUM:
                return roll < 60 ? QuestionType.COMPLETE_SEQUENCE : QuestionType.POSITION;
            case HARD:
                return roll < 50 ? QuestionType.POSITION : QuestionType.MULTIPLE_CHOICE;
            case EXPERT:
                if (roll < 34) return QuestionType.COMPLETE_SEQUENCE;
                if (roll < 67) return QuestionType.POSITION;
                return QuestionType.MULTIPLE_CHOICE;
            default:
                return QuestionType.COMPLETE_SEQUENCE;
        }
    }

    public static Question build(QuestionType type, List<String> tokens, MemoryGenerator generator) {
        switch (type) {
            case COMPLETE_SEQUENCE:
                return buildSequenceQuestion(tokens, generator);
            case POSITION:
                return buildPositionQuestion(tokens);
            case MULTIPLE_CHOICE:
                return buildMultipleChoiceQuestion(tokens);
            default:
                throw new IllegalArgumentException("Unsupported question type: " + type);
        }
    }

    private static Question buildSequenceQuestion(List<String> tokens, MemoryGenerator generator) {
        String correct = generator.toDisplayString(tokens).replace(" ", "");
        return new Question(QuestionType.COMPLETE_SEQUENCE,
                "Type the complete sequence you memorized (no spaces):", null, correct);
    }

    private static Question buildPositionQuestion(List<String> tokens) {
        int position = RANDOM.nextInt(tokens.size());
        String correct = tokens.get(position);
        List<String> options = buildOptionsAround(tokens, correct);
        return new Question(QuestionType.POSITION,
                "What was at position " + (position + 1) + "?", options, correct);
    }

    private static Question buildMultipleChoiceQuestion(List<String> tokens) {
        int index = RANDOM.nextInt(tokens.size());
        String correct = tokens.get(index);
        List<String> options = buildOptionsAround(tokens, correct);
        return new Question(QuestionType.MULTIPLE_CHOICE,
                "Which of these appeared in the sequence?", options, correct);
    }

    private static List<String> buildOptionsAround(List<String> tokens, String correct) {
        List<String> options = new ArrayList<>();
        options.add(correct);
        List<String> distractPool = new ArrayList<>();
        // Prefer distractors from the same alphabet/character space where possible
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*";
        for (char c : chars.toCharArray()) {
            String s = String.valueOf(c);
            if (!s.equals(correct) && !tokens.contains(s)) distractPool.add(s);
        }
        Collections.shuffle(distractPool, RANDOM);
        int need = 3;
        for (String d : distractPool) {
            if (options.size() >= need + 1) break;
            options.add(d);
        }
        Collections.shuffle(options, RANDOM);
        return options;
    }
}
