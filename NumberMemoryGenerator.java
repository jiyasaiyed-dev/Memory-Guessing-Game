package com.memorygame.memory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberMemoryGenerator implements MemoryGenerator {
    private static final Random RANDOM = new SecureRandom();

    @Override
    public List<String> generate(int length) {
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            tokens.add(String.valueOf(RANDOM.nextInt(10)));
        }
        return tokens;
    }

    @Override
    public String toDisplayString(List<String> tokens) {
        return String.join(" ", tokens);
    }
}
