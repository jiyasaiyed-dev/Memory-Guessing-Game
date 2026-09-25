package com.memorygame.repository;

import com.memorygame.model.HighScore;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Handles all persistence for the leaderboard. Never mixed into game logic. */
public class HighScoreRepository {
    private static final Path FILE_PATH = Paths.get("data", "highscores.txt");
    private static final int MAX_ENTRIES = 20;

    public List<HighScore> loadAll() {
        List<HighScore> scores = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return scores;
        }
        try {
            List<String> lines = Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                try {
                    scores.add(HighScore.fromFileLine(line));
                } catch (IllegalArgumentException corrupted) {
                    // Skip corrupted lines rather than crashing the app.
                }
            }
        } catch (IOException e) {
            // Treat unreadable file as an empty leaderboard.
        }
        Collections.sort(scores);
        return scores;
    }

    public void save(HighScore newScore) {
        List<HighScore> scores = loadAll();
        scores.add(newScore);
        Collections.sort(scores);
        if (scores.size() > MAX_ENTRIES) {
            scores = scores.subList(0, MAX_ENTRIES);
        }
        try {
            Files.createDirectories(FILE_PATH.getParent());
            List<String> lines = new ArrayList<>();
            for (HighScore hs : scores) lines.add(hs.toFileLine());
            Files.write(FILE_PATH, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Silently ignore; not fatal to gameplay.
        }
    }
}
