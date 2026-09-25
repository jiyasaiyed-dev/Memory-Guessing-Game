package com.memorygame.ui;

import com.memorygame.enums.Difficulty;
import com.memorygame.enums.GameMode;
import com.memorygame.enums.QuestionType;
import com.memorygame.memory.MemoryGenerator;
import com.memorygame.memory.MemoryGeneratorFactory;
import com.memorygame.memory.QuestionFactory;
import com.memorygame.model.GameConfiguration;
import com.memorygame.model.GameSession;
import com.memorygame.model.HighScore;
import com.memorygame.model.Player;
import com.memorygame.model.Question;
import com.memorygame.repository.HighScoreRepository;
import com.memorygame.repository.SettingsRepository;
import com.memorygame.repository.StatisticsRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Top-level window and screen controller. Owns the CardLayout that swaps
 * between menu/game/result/etc. screens. UI-only concerns live here;
 * persistence goes through the repository classes and game rules live in
 * the model/memory packages.
 */
public class GameFrame extends JFrame {

    private static final int TOTAL_ROUNDS = 10;
    private static final String SCREEN_MAIN_MENU = "MAIN_MENU";
    private static final String SCREEN_SETUP = "SETUP";
    private static final String SCREEN_HELP = "HELP";
    private static final String SCREEN_GAME = "GAME";
    private static final String SCREEN_RESULT = "RESULT";
    private static final String SCREEN_HIGH_SCORES = "HIGH_SCORES";
    private static final String SCREEN_STATISTICS = "STATISTICS";
    private static final String SCREEN_SETTINGS = "SETTINGS";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);

    private final HighScoreRepository highScoreRepository = new HighScoreRepository();
    private final StatisticsRepository statisticsRepository = new StatisticsRepository();
    private final SettingsRepository settingsRepository = new SettingsRepository();
    private Properties settings;

    // Setup selections
    private JTextField nameField;
    private JComboBox<GameMode> modeCombo;
    private JComboBox<Difficulty> difficultyCombo;

    // Active game state
    private GameSession session;
    private MemoryGenerator generator;
    private List<String> currentTokens;
    private Timer countdownTimer;
    private int countdownRemaining;

    // Game screen widgets (rebuilt/updated each round)
    private JPanel gameScreen;
    private JLabel hudPlayer, hudMode, hudDifficulty, hudLevel, hudRound, hudScore, hudLives, hudStreak;
    private JLabel sequenceLabel;
    private JLabel statusLabel;
    private JPanel questionArea;

    public GameFrame() {
        super("Memory Guessing Game");
        loadSettings();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 640);
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_DARK);

        root.setBackground(UITheme.BG_DARK);
        add(root);

        root.add(buildMainMenu(), SCREEN_MAIN_MENU);
        root.add(buildSetupScreen(), SCREEN_SETUP);
        root.add(buildHelpScreen(), SCREEN_HELP);
        root.add(buildHighScoreScreen(), SCREEN_HIGH_SCORES);
        root.add(buildStatisticsScreen(), SCREEN_STATISTICS);
        root.add(buildSettingsScreen(), SCREEN_SETTINGS);

        showScreen(SCREEN_MAIN_MENU);
    }

    private void loadSettings() {
        settings = settingsRepository.load();
        settings.putIfAbsent("defaultDifficulty", Difficulty.EASY.name());
        settings.putIfAbsent("countdownEnabled", "true");
        settings.putIfAbsent("soundEnabled", "false");
    }

    private void showScreen(String name) {
        cardLayout.show(root, name);
    }

    // ---------------------------------------------------------------
    // MAIN MENU
    // ---------------------------------------------------------------

    private JPanel buildMainMenu() {
        JPanel panel = Components.panel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(UITheme.BG_DARK);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(8, 0, 8, 0);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = Components.title("MEMORY GUESSING GAME");
        JLabel subtitle = Components.muted("Memorize fast. Answer faster.");
        c.gridy = 0; box.add(titleLabel, c);
        c.gridy = 1; box.add(subtitle, c);

        String[] labels = {"Start Game", "How To Play", "High Scores", "Statistics", "Settings", "Exit"};
        Runnable[] actions = {
                () -> { resetSetupForNewGame(); showScreen(SCREEN_SETUP); },
                () -> showScreen(SCREEN_HELP),
                () -> { refreshHighScoreTable(); showScreen(SCREEN_HIGH_SCORES); },
                () -> { refreshStatisticsScreen(); showScreen(SCREEN_STATISTICS); },
                () -> showScreen(SCREEN_SETTINGS),
                () -> System.exit(0)
        };

        int row = 2;
        for (int i = 0; i < labels.length; i++) {
            JButton btn = Components.primaryButton(labels[i]);
            btn.setPreferredSize(new Dimension(260, 44));
            final Runnable action = actions[i];
            btn.addActionListener(e -> action.run());
            c.gridy = row++;
            box.add(btn, c);
        }

        panel.add(box);
        return panel;
    }

    // ---------------------------------------------------------------
    // SETUP (name + mode + difficulty)
    // ---------------------------------------------------------------

    private JPanel buildSetupScreen() {
        JPanel panel = Components.panel();
        panel.setLayout(new GridBagLayout());

        JPanel card = Components.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(8, 4, 8, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;

        c.gridy = 0;
        card.add(Components.heading("Game Setup"), c);

        c.gridwidth = 1;
        c.gridy = 1; c.gridx = 0; card.add(Components.body("Player Name:"), c);
        nameField = new JTextField("Player", 16);
        nameField.setFont(UITheme.FONT_BODY);
        c.gridx = 1; card.add(nameField, c);

        c.gridy = 2; c.gridx = 0; card.add(Components.body("Memory Mode:"), c);
        modeCombo = new JComboBox<>(GameMode.values());
        c.gridx = 1; card.add(modeCombo, c);

        c.gridy = 3; c.gridx = 0; card.add(Components.body("Difficulty:"), c);
        difficultyCombo = new JComboBox<>(Difficulty.values());
        try {
            difficultyCombo.setSelectedItem(Difficulty.valueOf(settings.getProperty("defaultDifficulty", "EASY")));
        } catch (IllegalArgumentException ignored) { }
        c.gridx = 1; card.add(difficultyCombo, c);

        JLabel exampleLabel = Components.muted("Example: " + ((GameMode) modeCombo.getSelectedItem()).getExample());
        modeCombo.addActionListener(e -> exampleLabel.setText(
                "Example: " + ((GameMode) modeCombo.getSelectedItem()).getExample()));
        c.gridy = 4; c.gridx = 0; c.gridwidth = 2; card.add(exampleLabel, c);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        buttonRow.setBackground(UITheme.BG_PANEL);
        JButton backBtn = Components.secondaryButton("Back");
        backBtn.addActionListener(e -> showScreen(SCREEN_MAIN_MENU));
        JButton startBtn = Components.primaryButton("Start Game");
        startBtn.addActionListener(e -> onStartGameClicked());
        buttonRow.add(backBtn);
        buttonRow.add(startBtn);
        c.gridy = 5; card.add(buttonRow, c);

        panel.add(card);
        return panel;
    }

    private void resetSetupForNewGame() {
        // Keep prior selections; nothing to reset besides ensuring fields exist.
    }

    private void onStartGameClicked() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        GameMode mode = (GameMode) modeCombo.getSelectedItem();
        Difficulty difficulty = (Difficulty) difficultyCombo.getSelectedItem();

        Player player = new Player(name);
        GameConfiguration configuration = new GameConfiguration(mode, difficulty, TOTAL_ROUNDS);
        session = new GameSession(player, configuration);
        generator = MemoryGeneratorFactory.create(mode);

        buildAndShowGameScreen();
        startRound();
    }

    // ---------------------------------------------------------------
    // HELP
    // ---------------------------------------------------------------

    private JPanel buildHelpScreen() {
        JPanel panel = Components.panel();
        panel.setLayout(new BorderLayout(0, 16));
        panel.setBorder(new EmptyBorder(24, 40, 24, 40));

        panel.add(Components.title("How To Play"), BorderLayout.NORTH);

        String helpText =
                "1. Choose a memory mode (numbers, letters, symbols, mixed, or cards).\n\n" +
                "2. Choose a difficulty. Harder difficulties show longer sequences for less time.\n\n" +
                "3. Memorize the sequence shown on screen before the countdown reaches zero.\n\n" +
                "4. Once it's hidden, answer the question about what you saw:\n" +
                "     - Type the full sequence, OR\n" +
                "     - Pick what was at a certain position, OR\n" +
                "     - Pick which item appeared at all.\n\n" +
                "5. Correct answers earn points and build a streak. Wrong answers cost a life\n" +
                "   and reset your streak. Run out of lives and the game ends.\n\n" +
                "6. Survive all rounds for a final score, then see if you made the high score list!";

        JTextArea area = new JTextArea(helpText);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(UITheme.FONT_BODY);
        area.setForeground(UITheme.TEXT_LIGHT);
        area.setBackground(UITheme.BG_PANEL);
        area.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JScrollPane(area), BorderLayout.CENTER);

        JButton backBtn = Components.primaryButton("Back to Menu");
        backBtn.addActionListener(e -> showScreen(SCREEN_MAIN_MENU));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(UITheme.BG_DARK);
        bottom.add(backBtn);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // ---------------------------------------------------------------
    // GAME SCREEN
    // ---------------------------------------------------------------

    private void buildAndShowGameScreen() {
        if (gameScreen != null) {
            root.remove(gameScreen);
        }
        gameScreen = new JPanel(new BorderLayout(0, 12));
        gameScreen.setBackground(UITheme.BG_DARK);
        gameScreen.setBorder(new EmptyBorder(18, 28, 18, 28));

        gameScreen.add(buildHud(), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(UITheme.BG_PANEL);
        center.setBorder(new EmptyBorder(24, 24, 24, 24));

        sequenceLabel = new JLabel(" ", SwingConstants.CENTER);
        sequenceLabel.setFont(UITheme.FONT_SEQUENCE);
        sequenceLabel.setForeground(UITheme.GOLD);
        sequenceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(UITheme.FONT_HEADING);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        questionArea = new JPanel();
        questionArea.setBackground(UITheme.BG_PANEL);
        questionArea.setLayout(new BoxLayout(questionArea, BoxLayout.Y_AXIS));
        questionArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(Box.createVerticalStrut(10));
        center.add(sequenceLabel);
        center.add(Box.createVerticalStrut(14));
        center.add(statusLabel);
        center.add(Box.createVerticalStrut(20));
        center.add(questionArea);

        gameScreen.add(center, BorderLayout.CENTER);
        root.add(gameScreen, SCREEN_GAME);
        showScreen(SCREEN_GAME);
    }

    private JPanel buildHud() {
        JPanel hud = new JPanel(new GridLayout(2, 4, 12, 6));
        hud.setBackground(UITheme.BG_DARK);
        hudPlayer = Components.body("Player: -");
        hudMode = Components.body("Mode: -");
        hudDifficulty = Components.body("Difficulty: -");
        hudLevel = Components.body("Level: -");
        hudRound = Components.body("Round: -");
        hudScore = Components.body("Score: -");
        hudLives = Components.body("Lives: -");
        hudStreak = Components.body("Streak: -");
        for (JLabel l : new JLabel[]{hudPlayer, hudMode, hudDifficulty, hudLevel, hudRound, hudScore, hudLives, hudStreak}) {
            hud.add(l);
        }
        return hud;
    }

    private void refreshHud() {
        Difficulty d = session.getConfiguration().getDifficulty();
        hudPlayer.setText("Player: " + session.getPlayer().getName());
        hudMode.setText("Mode: " + session.getConfiguration().getMode().getLabel());
        hudDifficulty.setText("Difficulty: " + d.name());
        hudLevel.setText("Level: " + session.getCurrentLevel());
        hudRound.setText("Round: " + session.getCurrentRound() + "/" + session.getConfiguration().getTotalRounds());
        hudScore.setText("Score: " + session.getScore());

        StringBuilder hearts = new StringBuilder("Lives: ");
        for (int i = 0; i < d.getLives(); i++) {
            hearts.append(i < session.getLives() ? "\u2665 " : "\u2661 ");
        }
        hudLives.setText(hearts.toString().trim());
        hudLives.setForeground(session.getLives() <= 1 ? UITheme.DANGER : UITheme.TEXT_LIGHT);

        hudStreak.setText("Streak: " + session.getStreak());
    }

    private int sequenceLengthForCurrentRound() {
        Difficulty d = session.getConfiguration().getDifficulty();
        return d.getSequenceLength() + (session.getCurrentLevel() - 1);
    }

    private int memoryTimeForCurrentRound() {
        Difficulty d = session.getConfiguration().getDifficulty();
        return Math.max(1, d.getMemoryTimeSeconds() - (session.getCurrentLevel() - 1) / 2);
    }

    private void startRound() {
        refreshHud();
        questionArea.removeAll();
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setText("Memorize the sequence!");

        currentTokens = generator.generate(sequenceLengthForCurrentRound());
        session.setCurrentSequence(generator.toDisplayString(currentTokens));
        sequenceLabel.setText(session.getCurrentSequence());
        sequenceLabel.setForeground(UITheme.GOLD);

        questionArea.revalidate();
        questionArea.repaint();

        boolean countdownEnabled = Boolean.parseBoolean(settings.getProperty("countdownEnabled", "true"));
        countdownRemaining = memoryTimeForCurrentRound();

        if (!countdownEnabled) {
            hideSequenceAndAsk();
            return;
        }

        if (countdownTimer != null && countdownTimer.isRunning()) countdownTimer.stop();
        statusLabel.setText("Memorize! Hiding in " + countdownRemaining + "...");
        countdownTimer = new Timer(1000, e -> {
            countdownRemaining--;
            if (countdownRemaining > 0) {
                statusLabel.setText("Memorize! Hiding in " + countdownRemaining + "...");
            } else {
                countdownTimer.stop();
                hideSequenceAndAsk();
            }
        });
        countdownTimer.start();
    }

    private void hideSequenceAndAsk() {
        sequenceLabel.setText("? ? ? ? ?");
        sequenceLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setText("Sequence hidden — answer the question below:");

        QuestionType type = QuestionFactory.pickTypeFor(session.getConfiguration().getDifficulty());
        Question question = QuestionFactory.build(type, currentTokens, generator);
        session.setCurrentQuestion(question);

        renderQuestion(question);
    }

    private void renderQuestion(Question question) {
        questionArea.removeAll();

        JLabel prompt = Components.body(question.getPromptText());
        prompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionArea.add(prompt);
        questionArea.add(Box.createVerticalStrut(14));

        if (question.getType() == QuestionType.COMPLETE_SEQUENCE) {
            JTextField answerField = new JTextField(18);
            answerField.setFont(UITheme.FONT_MONO);
            answerField.setMaximumSize(new Dimension(280, 34));
            answerField.setAlignmentX(Component.CENTER_ALIGNMENT);
            questionArea.add(answerField);
            questionArea.add(Box.createVerticalStrut(14));

            JButton submit = Components.primaryButton("Submit Answer");
            submit.setAlignmentX(Component.CENTER_ALIGNMENT);
            Runnable submitAction = () -> {
                String given = answerField.getText().trim();
                if (given.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter an answer.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                handleAnswer(question.checkAnswer(given), question.getCorrectAnswer());
            };
            submit.addActionListener(e -> submitAction.run());
            answerField.addActionListener(e -> submitAction.run());
            questionArea.add(submit);
            SwingUtilities.invokeLater(answerField::requestFocusInWindow);
        } else {
            for (String option : question.getOptions()) {
                JButton optBtn = Components.secondaryButton(option);
                optBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                optBtn.setMaximumSize(new Dimension(240, 40));
                optBtn.addActionListener(e -> handleAnswer(question.checkAnswer(option), question.getCorrectAnswer()));
                questionArea.add(optBtn);
                questionArea.add(Box.createVerticalStrut(8));
            }
        }

        questionArea.revalidate();
        questionArea.repaint();
    }

    private void handleAnswer(boolean correct, String correctAnswer) {
        Difficulty d = session.getConfiguration().getDifficulty();
        questionArea.removeAll();
        questionArea.revalidate();
        questionArea.repaint();

        if (correct) {
            int points = d.getBasePoints();
            int streakBonus = session.getStreak() > 0 && (session.getStreak() + 1) % 3 == 0 ? 15 : 0;
            session.registerCorrectAnswer(points);
            if (streakBonus > 0) session.addScore(streakBonus);
            statusLabel.setForeground(UITheme.SUCCESS);
            String msg = "Correct! +" + points + (streakBonus > 0 ? " (+" + streakBonus + " streak bonus)" : "");
            if (session.getStreak() >= 3 && session.getStreak() % 3 == 0) {
                msg += "   *** " + session.getStreak() + " ANSWER STREAK! ***";
            }
            statusLabel.setText(msg);
        } else {
            session.registerWrongAnswer();
            statusLabel.setForeground(UITheme.DANGER);
            statusLabel.setText("Incorrect. The answer was: " + correctAnswer);
        }
        sequenceLabel.setText(session.getCurrentSequence());
        sequenceLabel.setForeground(correct ? UITheme.SUCCESS : UITheme.DANGER);
        refreshHud();

        Timer pause = new Timer(1400, e -> proceedAfterAnswer());
        pause.setRepeats(false);
        pause.start();
    }

    private void proceedAfterAnswer() {
        if (session.isGameOver()) {
            finishGame(false);
            return;
        }
        session.nextRound();
        if (session.isComplete()) {
            finishGame(true);
            return;
        }
        startRound();
    }

    private void finishGame(boolean completed) {
        GameSession finished = session;
        finished.getPlayer().recordGame(
                finished.getScore(), finished.getCorrectAnswers(), finished.getWrongAnswers(),
                finished.getBestStreak(), finished.getCurrentLevel());

        HighScore hs = new HighScore(
                finished.getPlayer().getName(), finished.getScore(),
                finished.getConfiguration().getMode(), finished.getConfiguration().getDifficulty(),
                finished.getCurrentLevel());
        highScoreRepository.save(hs);
        updateAggregateStatistics(finished, completed);

        showResultScreen(finished, completed);
    }

    private void updateAggregateStatistics(GameSession finished, boolean completed) {
        Map<String, String> stats = statisticsRepository.load();
        int totalGames = parseIntOr(stats.get("totalGames"), 0) + 1;
        int totalScore = parseIntOr(stats.get("totalScore"), 0) + finished.getScore();
        int highestScore = Math.max(parseIntOr(stats.get("highestScore"), 0), finished.getScore());
        int totalCorrect = parseIntOr(stats.get("totalCorrect"), 0) + finished.getCorrectAnswers();
        int totalWrong = parseIntOr(stats.get("totalWrong"), 0) + finished.getWrongAnswers();
        int bestStreak = Math.max(parseIntOr(stats.get("bestStreak"), 0), finished.getBestStreak());
        int highestLevel = Math.max(parseIntOr(stats.get("highestLevel"), 0), finished.getCurrentLevel());

        stats.put("totalGames", String.valueOf(totalGames));
        stats.put("totalScore", String.valueOf(totalScore));
        stats.put("highestScore", String.valueOf(highestScore));
        stats.put("totalCorrect", String.valueOf(totalCorrect));
        stats.put("totalWrong", String.valueOf(totalWrong));
        stats.put("bestStreak", String.valueOf(bestStreak));
        stats.put("highestLevel", String.valueOf(highestLevel));
        stats.put("favoriteMode", finished.getConfiguration().getMode().name());
        stats.put("bestDifficulty", finished.getConfiguration().getDifficulty().name());
        statisticsRepository.save(stats);
    }

    private int parseIntOr(String s, int fallback) {
        if (s == null) return fallback;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return fallback; }
    }

    // ---------------------------------------------------------------
    // RESULT SCREEN
    // ---------------------------------------------------------------

    private void showResultScreen(GameSession finished, boolean completed) {
        JPanel panel = Components.panel();
        panel.setLayout(new GridBagLayout());

        JPanel card = Components.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.insets = new Insets(6, 4, 6, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        card.add(Components.title(completed ? "GAME COMPLETE" : "GAME OVER"), c);

        double accuracy = (finished.getCorrectAnswers() + finished.getWrongAnswers()) == 0 ? 0.0 :
                (finished.getCorrectAnswers() * 100.0) / (finished.getCorrectAnswers() + finished.getWrongAnswers());
        String rating = accuracy >= 90 ? "EXCELLENT" : accuracy >= 80 ? "VERY GOOD" : accuracy >= 70 ? "GOOD"
                : accuracy >= 50 ? "NEEDS IMPROVEMENT" : "KEEP PRACTICING";

        String[] lines = {
                "Player: " + finished.getPlayer().getName(),
                "Final Score: " + finished.getScore(),
                "Correct Answers: " + finished.getCorrectAnswers(),
                "Wrong Answers: " + finished.getWrongAnswers(),
                String.format("Accuracy: %.2f%%", accuracy),
                "Best Streak: " + finished.getBestStreak(),
                "Highest Level: " + finished.getCurrentLevel(),
                "Mode: " + finished.getConfiguration().getMode().getLabel(),
                "Difficulty: " + finished.getConfiguration().getDifficulty().name(),
                "Performance: " + rating
        };
        int row = 1;
        for (String line : lines) {
            c.gridy = row++;
            JLabel l = Components.body(line);
            card.add(l, c);
        }

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        buttonRow.setBackground(UITheme.BG_PANEL);
        JButton playAgain = Components.primaryButton("Play Again");
        playAgain.addActionListener(e -> {
            root.remove(panel);
            showScreen(SCREEN_SETUP);
        });
        JButton mainMenu = Components.secondaryButton("Main Menu");
        mainMenu.addActionListener(e -> {
            root.remove(panel);
            showScreen(SCREEN_MAIN_MENU);
        });
        buttonRow.add(playAgain);
        buttonRow.add(mainMenu);
        c.gridy = row;
        card.add(buttonRow, c);

        panel.add(card);
        root.add(panel, SCREEN_RESULT);
        showScreen(SCREEN_RESULT);
    }

    // ---------------------------------------------------------------
    // HIGH SCORES
    // ---------------------------------------------------------------

    private JPanel highScorePanel;
    private DefaultTableModel highScoreTableModel;

    private JPanel buildHighScoreScreen() {
        highScorePanel = new JPanel(new BorderLayout(0, 16));
        highScorePanel.setBackground(UITheme.BG_DARK);
        highScorePanel.setBorder(new EmptyBorder(24, 40, 24, 40));

        highScorePanel.add(Components.title("High Scores"), BorderLayout.NORTH);

        highScoreTableModel = new DefaultTableModel(new Object[]{"Rank", "Player", "Score", "Mode", "Level"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(highScoreTableModel);
        table.setFont(UITheme.FONT_BODY);
        table.setRowHeight(26);
        table.getTableHeader().setFont(UITheme.FONT_BODY_BOLD);
        table.setBackground(UITheme.BG_PANEL);
        table.setForeground(UITheme.TEXT_LIGHT);
        table.setGridColor(UITheme.BG_DARK);
        table.setSelectionBackground(UITheme.ACCENT);

        highScorePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton backBtn = Components.primaryButton("Back to Menu");
        backBtn.addActionListener(e -> showScreen(SCREEN_MAIN_MENU));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(UITheme.BG_DARK);
        bottom.add(backBtn);
        highScorePanel.add(bottom, BorderLayout.SOUTH);

        return highScorePanel;
    }

    private void refreshHighScoreTable() {
        highScoreTableModel.setRowCount(0);
        List<HighScore> scores = highScoreRepository.loadAll();
        int rank = 1;
        for (HighScore hs : scores) {
            highScoreTableModel.addRow(new Object[]{rank++, hs.getPlayerName(), hs.getScore(),
                    hs.getMode().getLabel(), hs.getLevel()});
        }
        if (scores.isEmpty()) {
            highScoreTableModel.addRow(new Object[]{"-", "No scores yet", "-", "-", "-"});
        }
    }

    // ---------------------------------------------------------------
    // STATISTICS
    // ---------------------------------------------------------------

    private JPanel statisticsContent;

    private JPanel buildStatisticsScreen() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(24, 40, 24, 40));
        panel.add(Components.title("Statistics"), BorderLayout.NORTH);

        statisticsContent = new JPanel(new GridLayout(0, 1, 4, 8));
        statisticsContent.setBackground(UITheme.BG_PANEL);
        statisticsContent.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(statisticsContent, BorderLayout.CENTER);

        JButton backBtn = Components.primaryButton("Back to Menu");
        backBtn.addActionListener(e -> showScreen(SCREEN_MAIN_MENU));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setBackground(UITheme.BG_DARK);
        bottom.add(backBtn);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshStatisticsScreen() {
        statisticsContent.removeAll();
        Map<String, String> stats = statisticsRepository.load();
        int totalGames = parseIntOr(stats.get("totalGames"), 0);
        int totalScore = parseIntOr(stats.get("totalScore"), 0);
        int highestScore = parseIntOr(stats.get("highestScore"), 0);
        int totalCorrect = parseIntOr(stats.get("totalCorrect"), 0);
        int totalWrong = parseIntOr(stats.get("totalWrong"), 0);
        int bestStreak = parseIntOr(stats.get("bestStreak"), 0);
        int highestLevel = parseIntOr(stats.get("highestLevel"), 0);
        String favMode = stats.getOrDefault("favoriteMode", "-");
        String bestDiff = stats.getOrDefault("bestDifficulty", "-");
        double avgScore = totalGames == 0 ? 0.0 : (double) totalScore / totalGames;
        double accuracy = (totalCorrect + totalWrong) == 0 ? 0.0 : (totalCorrect * 100.0) / (totalCorrect + totalWrong);

        String[] lines = {
                "Total Games          : " + totalGames,
                "Highest Score        : " + highestScore,
                String.format("Average Score        : %.0f", avgScore),
                "Correct Answers      : " + totalCorrect,
                "Wrong Answers        : " + totalWrong,
                String.format("Overall Accuracy     : %.2f%%", accuracy),
                "Best Streak          : " + bestStreak,
                "Highest Level        : " + highestLevel,
                "Favorite Mode        : " + favMode,
                "Best Difficulty      : " + bestDiff
        };
        for (String line : lines) {
            JLabel l = new JLabel(line);
            l.setFont(UITheme.FONT_MONO);
            l.setForeground(UITheme.TEXT_LIGHT);
            statisticsContent.add(l);
        }
        statisticsContent.revalidate();
        statisticsContent.repaint();
    }

    // ---------------------------------------------------------------
    // SETTINGS
    // ---------------------------------------------------------------

    private JPanel buildSettingsScreen() {
        JPanel panel = Components.panel();
        panel.setLayout(new GridBagLayout());

        JPanel card = Components.card();
        card.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridwidth = 2;
        c.insets = new Insets(8, 4, 8, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridy = 0;
        card.add(Components.heading("Settings"), c);

        c.gridwidth = 1;
        c.gridy = 1; c.gridx = 0; card.add(Components.body("Default Difficulty:"), c);
        JComboBox<Difficulty> defaultDifficultyCombo = new JComboBox<>(Difficulty.values());
        try {
            defaultDifficultyCombo.setSelectedItem(Difficulty.valueOf(settings.getProperty("defaultDifficulty", "EASY")));
        } catch (IllegalArgumentException ignored) { }
        c.gridx = 1; card.add(defaultDifficultyCombo, c);

        c.gridy = 2; c.gridx = 0; card.add(Components.body("Memory Countdown:"), c);
        JCheckBox countdownCheck = new JCheckBox("Enabled", Boolean.parseBoolean(settings.getProperty("countdownEnabled", "true")));
        countdownCheck.setBackground(UITheme.BG_PANEL);
        countdownCheck.setForeground(UITheme.TEXT_LIGHT);
        c.gridx = 1; card.add(countdownCheck, c);

        c.gridy = 3; c.gridx = 0; card.add(Components.body("Sound (placeholder):"), c);
        JCheckBox soundCheck = new JCheckBox("Enabled", Boolean.parseBoolean(settings.getProperty("soundEnabled", "false")));
        soundCheck.setBackground(UITheme.BG_PANEL);
        soundCheck.setForeground(UITheme.TEXT_LIGHT);
        c.gridx = 1; card.add(soundCheck, c);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        buttonRow.setBackground(UITheme.BG_PANEL);
        JButton saveBtn = Components.primaryButton("Save");
        saveBtn.addActionListener(e -> {
            settings.setProperty("defaultDifficulty", ((Difficulty) defaultDifficultyCombo.getSelectedItem()).name());
            settings.setProperty("countdownEnabled", String.valueOf(countdownCheck.isSelected()));
            settings.setProperty("soundEnabled", String.valueOf(soundCheck.isSelected()));
            settingsRepository.save(settings);
            JOptionPane.showMessageDialog(this, "Settings saved.", "Settings", JOptionPane.INFORMATION_MESSAGE);
        });
        JButton backBtn = Components.secondaryButton("Back");
        backBtn.addActionListener(e -> showScreen(SCREEN_MAIN_MENU));
        buttonRow.add(saveBtn);
        buttonRow.add(backBtn);
        c.gridy = 4; c.gridx = 0; c.gridwidth = 2;
        card.add(buttonRow, c);

        panel.add(card);
        return panel;
    }

    /** Minimal table model wrapper to avoid importing javax.swing.table.* at top for clarity. */
    private static class DefaultTableModel extends javax.swing.table.DefaultTableModel {
        DefaultTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }
    }
}
