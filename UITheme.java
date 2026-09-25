package com.memorygame.ui;

import java.awt.Color;
import java.awt.Font;

/** Centralized visual constants so styling isn't scattered/hardcoded everywhere. */
public final class UITheme {
    public static final Color BG_DARK = new Color(24, 26, 33);
    public static final Color BG_PANEL = new Color(32, 35, 44);
    public static final Color ACCENT = new Color(94, 129, 244);
    public static final Color ACCENT_HOVER = new Color(120, 150, 250);
    public static final Color TEXT_LIGHT = new Color(232, 234, 240);
    public static final Color TEXT_MUTED = new Color(150, 155, 170);
    public static final Color SUCCESS = new Color(88, 199, 120);
    public static final Color DANGER = new Color(226, 87, 87);
    public static final Color WARNING = new Color(240, 180, 60);
    public static final Color GOLD = new Color(230, 190, 80);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SEQUENCE = new Font("Consolas", Font.BOLD, 40);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 15);

    private UITheme() { }
}
