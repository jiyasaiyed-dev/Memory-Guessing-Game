package com.memorygame.memory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents cards using text symbols instead of images. Designed so that
 * a future ImageCardMemoryGenerator could implement the same interface
 * and swap in real images without touching game logic.
 */
public class CardMemoryGenerator implements MemoryGenerator {
    private static final String[] CARD_NAMES = {
            "APPLE", "CAR", "STAR", "DOG", "GUITAR", "MOON", "FISH",
            "TREE", "CLOCK", "BOOK", "SHIP", "CROWN", "KEY", "CUP"
    };
    private static final Random RANDOM = new SecureRandom();

    @Override
    public List<String> generate(int length) {
        List<String> pool = new ArrayList<>(List.of(CARD_NAMES));
        Collections.shuffle(pool, RANDOM);
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            tokens.add(pool.get(i % pool.size()));
        }
        return tokens;
    }

    @Override
    public String toDisplayString(List<String> tokens) {
        StringBuilder sb = new StringBuilder();
        for (String t : tokens) {
            sb.append("[").append(t).append("] ");
        }
        return sb.toString().trim();
    }
}
