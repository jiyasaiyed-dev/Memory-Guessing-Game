package com.memorygame.memory;

import java.util.List;

/**
 * Abstraction over how a memorizable sequence is produced.
 * Each GameMode has a concrete implementation, and GameService depends
 * only on this interface (polymorphism).
 */
public interface MemoryGenerator {

    /** Generates a sequence of the given length as a list of displayable tokens. */
    List<String> generate(int length);

    /** Joins tokens for on-screen display (e.g. "A 7 K 2 9" vs "A7K29"). */
    String toDisplayString(List<String> tokens);
}
