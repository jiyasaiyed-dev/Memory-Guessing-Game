package com.memorygame;

import com.memorygame.ui.GameFrame;

import javax.swing.*;

/** Application entry point. Only starts the app — no game logic here. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to default look and feel if system L&F is unavailable.
            }
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
