package com.memorygame.memory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LetterNumberMemoryGenerator implements MemoryGenerator {
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final Random RANDOM = new SecureRandom();

    @Override
    public List<String> generate(int length) {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (i % 2 == 0) {
                tokens.add(String.valueOf(LETTERS.charAt(RANDOM.nextInt(LETTERS.length()))));
            } else {
                tokens.add(String.valueOf(DIGITS.charAt(RANDOM.nextInt(DIGITS.length()))));
            }
        }
        return tokens;
    }

    @Override
    public String toDisplayString(List<String> tokens) {
        return String.join("", tokens);
    }
}
