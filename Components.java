package com.memorygame.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Small reusable Swing component builders to keep panel code clean. */
public final class Components {

    private Components() { }

    /**
     * A JButton that paints its own background instead of delegating to the
     * platform Look & Feel. Native Windows/Mac themes otherwise ignore
     * setBackground()/setForeground() on JButton and render a plain white
     * button, which is what causes invisible text on some systems.
     */
    private static class FlatButton extends JButton {
        private Color baseColor;
        private Color hoverColor;

        FlatButton(String text, Color baseColor, Color hoverColor, Color textColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            setForeground(textColor);
            setFont(UITheme.FONT_BODY_BOLD);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorderPainted(false);
            setBorder(new EmptyBorder(10, 22, 10, 22));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { repaint(); }
                @Override public void mouseExited(MouseEvent e) { repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean hovered = getModel().isRollover() || getModel().isPressed();
            g2.setColor(hovered ? hoverColor : baseColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JButton primaryButton(String text) {
        return new FlatButton(text, UITheme.ACCENT, UITheme.ACCENT_HOVER, Color.WHITE);
    }

    public static JButton secondaryButton(String text) {
        FlatButton b = new FlatButton(text, UITheme.BG_PANEL, new Color(48, 52, 64), UITheme.TEXT_LIGHT);
        b.setBorderPainted(false);
        return b;
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(UITheme.FONT_TITLE);
        l.setForeground(UITheme.TEXT_LIGHT);
        return l;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(UITheme.FONT_HEADING);
        l.setForeground(UITheme.TEXT_LIGHT);
        return l;
    }

    public static JLabel body(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_LIGHT);
        return l;
    }

    public static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_MUTED);
        return l;
    }

    public static JPanel panel() {
        JPanel p = new JPanel();
        p.setBackground(UITheme.BG_DARK);
        return p;
    }

    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(UITheme.BG_PANEL);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));
        return p;
    }
}
