package com.memorygame.memory;

import com.memorygame.enums.GameMode;

/** Factory that hides concrete generator classes behind the GameMode enum. */
public final class MemoryGeneratorFactory {

    private MemoryGeneratorFactory() { }

    public static MemoryGenerator create(GameMode mode) {
        switch (mode) {
            case NUMBERS: return new NumberMemoryGenerator();
            case ALPHABETS: return new AlphabetMemoryGenerator();
            case LETTERS_NUMBERS: return new LetterNumberMemoryGenerator();
            case SYMBOLS: return new SymbolMemoryGenerator();
            case MIXED: return new MixedMemoryGenerator();
            case CARDS: return new CardMemoryGenerator();
            default: throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
    }
}
