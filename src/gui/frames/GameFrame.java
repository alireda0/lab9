package gui.frames;

import controller.GameController;
import gui.facades.ViewFacade;
import gui.components.SudokuBoardPanel;
import gui.interfaces.GameCompletionListener;
import model.Board;
import model.VerificationResult;
import model.VerificationStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class GameFrame {
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BACKGROUND = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    
    // Main frame and panels
    private final JFrame frame;
    private JPanel mainPanel;
    
    // Game components
    private SudokuBoardPanel boardPanel;
    
    // Control buttons
    private JButton verifyButton;
    private JButton solveButton;
    private JButton undoButton;
    private JButton newGameButton;
    private JButton menuButton;
    
    // Status display
    private JLabel statusLabel;
    private JLabel emptyCellsLabel;
    private JLabel difficultyLabel;
    
    // Menu components
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenu helpMenu;
    
    // Controllers and state
    private final GameController gameController;
    private final ViewFacade viewFacade;
    private final GameCompletionListener completionListener;
    private Board currentBoard;
    private String currentDifficulty;
    private boolean gameCompleted = false;
    
    /**
     * Constructor - creates the main game frame
     */
    public GameFrame(GameController gameController, ViewFacade viewFacade) {
        this(gameController, viewFacade, null);
    }
    
    public GameFrame(GameController gameController, ViewFacade viewFacade, 
                     GameCompletionListener completionListener) {
        this.gameController = gameController;
        this.viewFacade = viewFacade;
        this.completionListener = completionListener;
        
        // Create the JFrame
        frame = new JFrame("Sudoku Game");
        
        // Initialize UI
        initUI();
        
        // Setup keyboard shortcuts
        setupKeyboardShortcuts();
        
        // Setup window listener for clean return
        setupWindowListener();
    }
    
    /**
     * Initializes all UI components
     */
    private void initUI() {
        configureFrame();
        createMenuBar();
        createMainPanel();
        createBoardSection();
        createControlSection();
        createStatusSection();
        assembleUI();
        
        // Initial state
        updateEmptyCellsCount(81); // Full board empty initially
        setStatus("Ready to play");
    }
    
    /**
     * Configures the JFrame properties
     */
    private void configureFrame() {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(700, 800);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setMinimumSize(new Dimension(600, 700));
    }
    
    /**
     * Sets up window listener for clean return
     */
    private void setupWindowListener() {
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleReturnToMenu();
            }
        });
    }
    
    /**
     * Creates the menu bar
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Game Menu
        gameMenu = new JMenu("Game");
        gameMenu.setMnemonic(KeyEvent.VK_G);
        
        JMenuItem verifyMenuItem = new JMenuItem("Verify", KeyEvent.VK_V);
        verifyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        verifyMenuItem.addActionListener(e -> handleVerify());
        
        JMenuItem solveMenuItem = new JMenuItem("Solve", KeyEvent.VK_S);
        solveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        solveMenuItem.addActionListener(e -> handleSolve());
        
        JMenuItem undoMenuItem = new JMenuItem("Undo", KeyEvent.VK_Z);
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        undoMenuItem.addActionListener(e -> handleUndo());
        
        gameMenu.add(verifyMenuItem);
        gameMenu.add(solveMenuItem);
        gameMenu.add(undoMenuItem);
        gameMenu.addSeparator();
        
        JMenuItem newGameMenuItem = new JMenuItem("New Game", KeyEvent.VK_N);
        newGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        newGameMenuItem.addActionListener(e -> handleNewGame());
        
        JMenuItem menuMenuItem = new JMenuItem("Return to Menu", KeyEvent.VK_M);
        menuMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK));
        menuMenuItem.addActionListener(e -> handleReturnToMenu());
        
        gameMenu.add(newGameMenuItem);
        gameMenu.add(menuMenuItem);
        gameMenu.addSeparator();
        
        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_Q);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        exitMenuItem.addActionListener(e -> handleExit());
        
        gameMenu.add(exitMenuItem);
        
        // Help Menu
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem rulesMenuItem = new JMenuItem("Game Rules", KeyEvent.VK_R);
        rulesMenuItem.addActionListener(e -> showGameRules());
        
        JMenuItem aboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutMenuItem.addActionListener(e -> showAbout());
        
        helpMenu.add(rulesMenuItem);
        helpMenu.add(aboutMenuItem);
        
        // Add menus to menu bar
        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        
        frame.setJMenuBar(menuBar);
    }
    
    /**
     * Creates the main container panel
     */
    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
    }
    
    /**
     * Creates the Sudoku board section
     */
    private void createBoardSection() {
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBackground(PANEL_BACKGROUND);
        boardContainer.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 1, 1, 1, BORDER_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Create the Sudoku board panel
        boardPanel = new SudokuBoardPanel();
        boardPanel.setCellChangeListener(this::handleCellChange);
        
        boardContainer.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(boardContainer, BorderLayout.CENTER);
    }
    
    /**
     * Creates the control button section
     */
    private void createControlSection() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBackground(PANEL_BACKGROUND);
        controlPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        // Create styled buttons
        verifyButton = createControlButton("✓ Verify", PRIMARY_COLOR, KeyEvent.VK_V);
        solveButton = createControlButton("⚡ Solve", new Color(39, 174, 96), KeyEvent.VK_S);
        undoButton = createControlButton("↩️ Undo", new Color(230, 126, 34), KeyEvent.VK_Z);
        newGameButton = createControlButton("🔄 New Game", new Color(52, 152, 219), KeyEvent.VK_N);
        menuButton = createControlButton("🏠 Main Menu", new Color(149, 165, 166), KeyEvent.VK_M);
        
        // Initially disable Solve button (needs 5 empty cells)
        solveButton.setEnabled(false);
        
        // Add action listeners
        verifyButton.addActionListener(e -> handleVerify());
        solveButton.addActionListener(e -> handleSolve());
        undoButton.addActionListener(e -> handleUndo());
        newGameButton.addActionListener(e -> handleNewGame());
        menuButton.addActionListener(e -> handleReturnToMenu());
        
        // Add buttons to panel
        controlPanel.add(verifyButton);
        controlPanel.add(solveButton);
        controlPanel.add(undoButton);
        controlPanel.add(newGameButton);
        controlPanel.add(menuButton);
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates a styled control button
     */
    private JButton createControlButton(String text, Color bgColor, int mnemonic) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setMnemonic(mnemonic);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setPreferredSize(new Dimension(120, 40));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalColor = bgColor;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                originalColor = button.getBackground();
                button.setBackground(originalColor.darker());
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    /**
     * Creates the status display section
     */
    private void createStatusSection() {
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.setBackground(PANEL_BACKGROUND);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Main status label
        statusLabel = new JLabel("Game in progress", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(PRIMARY_COLOR);
        
        // Difficulty label
        difficultyLabel = new JLabel("Difficulty: N/A", SwingConstants.CENTER);
        difficultyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        difficultyLabel.setForeground(new Color(127, 140, 141));
        
        // Empty cells label
        emptyCellsLabel = new JLabel("Empty cells: 81", SwingConstants.RIGHT);
        emptyCellsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        emptyCellsLabel.setForeground(new Color(127, 140, 141));
        
        // Layout
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(difficultyLabel, BorderLayout.CENTER);
        statusPanel.add(emptyCellsLabel, BorderLayout.EAST);
        
        mainPanel.add(statusPanel, BorderLayout.NORTH);
    }
    
    /**
     * Assembles all UI components
     */
    private void assembleUI() {
        frame.setContentPane(mainPanel);
    }
    
    /**
     * Sets up keyboard shortcuts
     */
    private void setupKeyboardShortcuts() {
        // Add keyboard shortcuts
        InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = mainPanel.getActionMap();
        
        // Ctrl+V for Verify
        inputMap.put(KeyStroke.getKeyStroke("control V"), "verify");
        actionMap.put("verify", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handleVerify();
            }
        });
        
        // Ctrl+Z for Undo
        inputMap.put(KeyStroke.getKeyStroke("control Z"), "undo");
        actionMap.put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handleUndo();
            }
        });
    }
    
    // ================= PUBLIC API METHODS =================
    
    /**
     * Shows the game frame
     */
    public void show() {
        frame.setVisible(true);
    }
    
    /**
     * Hides the game frame
     */
    public void hide() {
        frame.setVisible(false);
    }
    
    /**
     * Disposes the game frame
     */
    public void dispose() {
        frame.dispose();
    }
    
    /**
     * Loads a board into the game
     */
    public void loadBoard(Board board) {
        this.currentBoard = board;
        boardPanel.loadBoard(board);
        
        // Update empty cells count
        int emptyCells = board.countZeros();
        updateEmptyCellsCount(emptyCells);
        
        // Update status
        setStatus("Game loaded - Start playing!");
        
        // Enable all controls
        setButtonsEnabled(true);
        gameCompleted = false;
    }
    
    /**
     * Sets the game difficulty display
     */
    public void setDifficulty(String difficulty) {
        this.currentDifficulty = difficulty;
        difficultyLabel.setText("Difficulty: " + difficulty);
    }
    
    /**
     * Updates the empty cells count
     */
    public void updateEmptyCellsCount(int count) {
        emptyCellsLabel.setText("Empty cells: " + count);
        
        // Enable/disable Solve button based on count
        boolean canSolve = (count == 5);
        solveButton.setEnabled(canSolve);
        
        // Update menu item
        for (Component comp : gameMenu.getMenuComponents()) {
            if (comp instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) comp;
                if ("Solve".equals(item.getText())) {
                    item.setEnabled(canSolve);
                    break;
                }
            }
        }
    }
    
    /**
     * Sets the status message
     */
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    /**
     * Shows a message dialog
     */
    public void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows an error dialog
     */
    public void showError(String title, String error) {
        JOptionPane.showMessageDialog(frame, error, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows a confirmation dialog
     */
    public boolean showConfirmDialog(String title, String message) {
        int result = JOptionPane.showConfirmDialog(
            frame, 
            message, 
            title, 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    // ================= EVENT HANDLERS =================
    
    /**
     * Handles cell changes in the Sudoku board
     */
    private void handleCellChange(int row, int col, int newValue) {
        if (currentBoard == null || gameCompleted) return;
        
        // Check if cell is fixed
        if (gameController.isFixedCell(row + 1, col + 1)) {
            showError("Invalid Move", "This cell is fixed and cannot be edited.");
            boardPanel.revertCell(row, col, currentBoard.get(row, col));
            return;
        }
        
        try {
            // Apply the move through game controller
            gameController.applyMove(currentBoard, row + 1, col + 1, newValue);
            
            // Update empty cells count
            int emptyCells = currentBoard.countZeros();
            updateEmptyCellsCount(emptyCells);
            
            setStatus("Move applied - " + emptyCells + " cells remaining");
            
            // Check if board is complete after this move
            if (emptyCells == 0) {
                checkBoardCompletion();
            }
            
        } catch (IOException e) {
            showError("Save Error", "Failed to save move: " + e.getMessage());
            boardPanel.revertCell(row, col, currentBoard.get(row, col));
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError("Invalid Move", e.getMessage());
            boardPanel.revertCell(row, col, currentBoard.get(row, col));
        }
    }
    
    /**
     * Checks if board is complete and valid
     */
    private void checkBoardCompletion() {
        if (gameCompleted) return;
        
        setLoading(true);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Verify the board
                    VerificationResult result = gameController.verify(currentBoard);
                    
                    // Update UI on EDT
                    SwingUtilities.invokeLater(() -> {
                        setLoading(false);
                        
                        if (result.getStatus() == VerificationStatus.VALID) {
                            // Board is complete and valid
                            handleLevelCompleted();
                        } else {
                            // Board is complete but invalid
                            setStatus("✗ Board complete but has errors");
                            showError("Invalid Solution", 
                                "The completed board contains errors. Please check your solution.");
                        }
                    });
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        setLoading(false);
                        showError("Verification Error", 
                            "Failed to verify board: " + e.getMessage());
                    });
                }
                return null;
            }
        };
        
        worker.execute();
    }
    
    /**
     * Handles successful completion of a level
     */
    private void handleLevelCompleted() {
        gameCompleted = true;
        setStatus("🎉 Level Completed Successfully!");
        
        // Disable editing and buttons
        boardPanel.setEnabled(false);
        setButtonsEnabled(false);
        
        // Show success message
        int choice = JOptionPane.showOptionDialog(
            frame,
            "🎊 Congratulations!\nYou have successfully completed the " + currentDifficulty + " level!\n\n" +
            "What would you like to do next?",
            "Level Completed",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Play Another Level", "Return to Main Menu", "Stay Here"},
            "Play Another Level"
        );
        
        switch (choice) {
            case 0: // Play Another Level
                returnToMainMenuForNewGame();
                break;
            case 1: // Return to Main Menu
                returnToMainMenu();
                break;
            case 2: // Stay Here
                // Keep the board visible but disabled
                boardPanel.setEnabled(false);
                setButtonsEnabled(false);
                setStatus("🎉 Level Completed!");
                break;
            default:
                // Dialog closed - return to main menu
                returnToMainMenu();
                break;
        }
    }
    
    /**
     * Returns to main menu to choose a new game
     */
    private void returnToMainMenuForNewGame() {
        // Notify listener that game is completed and we want a new game
        if (completionListener != null) {
            completionListener.onGameCompleted(true, currentDifficulty);
        }
        
        // Hide game frame
        hide();
        
        // Dispose after a short delay to allow transition
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // Ignore
            }
            dispose();
        });
    }
    
    /**
     * Returns to main menu
     */
    private void returnToMainMenu() {
        // Notify listener
        if (completionListener != null) {
            completionListener.onReturnToMenu();
        }
        
        // Hide game frame
        hide();
        
        // Dispose after a short delay
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                // Ignore
            }
            dispose();
        });
    }
    
    /**
     * Handles Verify button click
     */
    private void handleVerify() {
        if (currentBoard == null || gameCompleted) {
            showError("No Game", "No active game to verify.");
            return;
        }
        
        setLoading(true);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Verify the board
                    VerificationResult result = gameController.verify(currentBoard);
                    
                    // Update UI on EDT
                    SwingUtilities.invokeLater(() -> {
                        setLoading(false);
                        
                        switch (result.getStatus()) {
                            case VALID:
                                if (currentBoard.countZeros() == 0) {
                                    // Board is complete and valid
                                    handleLevelCompleted();
                                } else {
                                    setStatus("✓ Board is valid (incomplete)");
                                    showMessage("Verification", 
                                        "Board is valid but incomplete. Keep going!");
                                }
                                break;
                                
                            case INCOMPLETE:
                                setStatus("✓ Board is valid (incomplete)");
                                showMessage("Verification", 
                                    "Board is valid but incomplete. Keep going!");
                                break;
                                
                            case INVALID:
                                setStatus("✗ Board has errors");
                                StringBuilder errorMsg = new StringBuilder("Board has errors:\n");
                                result.getDuplicates().forEach(dup -> 
                                    errorMsg.append("- ").append(dup).append("\n")
                                );
                                showError("Verification Failed", errorMsg.toString());
                                break;
                        }
                    });
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        setLoading(false);
                        showError("Verification Error", 
                            "Failed to verify board: " + e.getMessage());
                    });
                }
                return null;
            }
        };
        
        worker.execute();
    }
    
    /**
     * Handles Solve button click
     */
    private void handleSolve() {
        if (currentBoard == null || gameCompleted) {
            showError("No Game", "No active game to solve.");
            return;
        }
        
        if (!gameController.canSolve(currentBoard)) {
            showError("Cannot Solve", 
                "Solve is only available when exactly 5 cells are empty.");
            return;
        }
        
        // Confirm with user
        boolean confirm = showConfirmDialog("Solve Puzzle", 
            "Are you sure you want to solve the puzzle? This will fill in all empty cells.");
        
        if (!confirm) return;
        
        setLoading(true);
        
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Solve the puzzle
                    Board solvedBoard = gameController.solve(currentBoard);
                    
                    SwingUtilities.invokeLater(() -> {
                        setLoading(false);
                        
                        if (solvedBoard != null) {
                            // Update the board
                            loadBoard(solvedBoard);
                            setStatus("✓ Puzzle solved!");
                            
                            // Automatically check if solved board is complete
                            if (solvedBoard.countZeros() == 0) {
                                checkBoardCompletion();
                            }
                        } else {
                            showError("Solve Failed", 
                                "Could not find a solution for this puzzle.");
                        }
                    });
                    
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        setLoading(false);
                        showError("Solve Error", 
                            "Failed to save solved puzzle: " + e.getMessage());
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        setLoading(false);
                        showError("Solve Error", 
                            "Unexpected error: " + e.getMessage());
                    });
                }
                return null;
            }
        };
        
        worker.execute();
    }
    
    /**
     * Handles Undo button click
     */
    private void handleUndo() {
        if (currentBoard == null || gameCompleted) {
            showError("No Game", "No active game to undo.");
            return;
        }
        
        try {
            // Perform undo
            gameController.undo(currentBoard);
            
            // Refresh the board display
            boardPanel.loadBoard(currentBoard);
            
            // Update empty cells count
            int emptyCells = currentBoard.countZeros();
            updateEmptyCellsCount(emptyCells);
            
            setStatus("Last move undone - " + emptyCells + " cells remaining");
            
        } catch (IOException e) {
            showError("Undo Error", "Failed to undo move: " + e.getMessage());
        } catch (IllegalStateException e) {
            showError("Cannot Undo", e.getMessage());
        }
    }
    
    /**
     * Handles New Game button click
     */
    private void handleNewGame() {
        boolean confirm = showConfirmDialog("New Game", 
            "Are you sure you want to start a new game? Current progress will be lost.");
        
        if (confirm) {
            returnToMainMenuForNewGame();
        }
    }
    
    /**
     * Handles Return to Main Menu button click
     */
    private void handleReturnToMenu() {
        boolean confirm = showConfirmDialog("Return to Menu", 
            "Are you sure you want to return to the main menu?");
        
        if (confirm) {
            returnToMainMenu();
        }
    }
    
    /**
     * Handles Exit button click
     */
    private void handleExit() {
        boolean confirm = showConfirmDialog("Exit Game", 
            "Are you sure you want to exit the Sudoku game?");
        
        if (confirm) {
            if (completionListener != null) {
                completionListener.onReturnToMenu();
            }
            System.exit(0);
        }
    }
    
    /**
     * Shows game rules
     */
    private void showGameRules() {
        String rules = """
            Sudoku Rules:
            
            1. Each row must contain the numbers 1-9 exactly once
            2. Each column must contain the numbers 1-9 exactly once
            3. Each 3x3 box must contain the numbers 1-9 exactly once
            4. The puzzle starts with some cells pre-filled
            5. Fill in the empty cells following the rules
            
            Game Features:
            • Verify: Check if your current solution is valid
            • Solve: Auto-solve when only 5 cells are empty
            • Undo: Revert your last move
            • New Game: Start a fresh puzzle
            
            Keyboard Shortcuts:
            • Ctrl+V: Verify
            • Ctrl+S: Solve
            • Ctrl+Z: Undo
            • Ctrl+N: New Game
            • Ctrl+M: Return to Menu
            • Ctrl+Q: Exit
            """;
        
        showMessage("Game Rules", rules);
    }
    
    /**
     * Shows about information
     */
    private void showAbout() {
        String about = """
            Sudoku Game
            
            Version: 1.0
            Course: Lab 10 Project
            
            Features:
            • Three difficulty levels (Easy, Medium, Hard)
            • Save/load game state
            • Verification and solving
            • Clean MVC architecture
            
            © 2024 Sudoku Game Project
            """;
        
        showMessage("About", about);
    }
    
    /**
     * Shows/hides loading state
     */
    private void setLoading(boolean isLoading) {
        if (isLoading) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            setButtonsEnabled(false);
            setStatus("Processing...");
        } else {
            frame.setCursor(Cursor.getDefaultCursor());
            setButtonsEnabled(true);
        }
    }
    
    /**
     * Enables or disables all buttons
     */
    private void setButtonsEnabled(boolean enabled) {
        verifyButton.setEnabled(enabled && !gameCompleted);
        solveButton.setEnabled(enabled && !gameCompleted && (currentBoard != null && currentBoard.countZeros() == 5));
        undoButton.setEnabled(enabled && !gameCompleted);
        newGameButton.setEnabled(enabled);
        menuButton.setEnabled(enabled);
    }
}