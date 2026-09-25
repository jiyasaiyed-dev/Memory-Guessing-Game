package com.memorygame.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/** Persists aggregate lifetime statistics as simple key=value pairs. */
public class StatisticsRepository {
    private static final Path FILE_PATH = Paths.get("data", "statistics.dat");

    public Map<String, String> load() {
        Map<String, String> map = new HashMap<>();
        if (!Files.exists(FILE_PATH)) return map;
        try {
            for (String line : Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8)) {
                int idx = line.indexOf('=');
                if (idx > 0) {
                    map.put(line.substring(0, idx), line.substring(idx + 1));
                }
            }
        } catch (IOException e) {
            // Return whatever was parsed; treat rest as unavailable.
        }
        return map;
    }

    public void save(Map<String, String> map) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> e : map.entrySet()) {
                sb.append(e.getKey()).append("=").append(e.getValue()).append(System.lineSeparator());
            }
            Files.write(FILE_PATH, sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // Non-fatal.
        }
    }
}
