package com.memorygame.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/** Loads and saves application settings using java.util.Properties. */
public class SettingsRepository {
    private static final Path FILE_PATH = Paths.get("data", "settings.properties");

    public Properties load() {
        Properties props = new Properties();
        if (Files.exists(FILE_PATH)) {
            try (InputStream in = Files.newInputStream(FILE_PATH)) {
                props.load(in);
            } catch (IOException e) {
                // Fall back to defaults handled by caller.
            }
        }
        return props;
    }

    public void save(Properties props) {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            try (OutputStream out = Files.newOutputStream(FILE_PATH)) {
                props.store(out, "Memory Guessing Game Settings");
            }
        } catch (IOException e) {
            // Non-fatal.
        }
    }
}
