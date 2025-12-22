package gui.frames;

import controller.StartupController;
import controller.GameController;
import gui.facades.ViewFacade;
import gui.interfaces.GameCompletionListener;
import model.Board;
import model.Difficulty;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class StartupFrame implements GameCompletionListener {
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    
    // Main frame and panels
    private final JFrame frame;
    private JPanel mainPanel;
    
    // Components
    private JLabel iconLabel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel statusLabel;
    
    private JButton resumeButton;
    private JButton easyButton;
    private JButton mediumButton;
    private JButton hardButton;
    private JButton uploadButton;
    private JButton exitButton;
    
    // Controllers and facades
    private final StartupController startupController;
    private final GameController gameController;
    private final ViewFacade viewFacade;
    
    // Reference to game frame (for navigation)
    private GameFrame gameFrame;
    
    /** Constructor - creates and initializes the startup frame */
    public StartupFrame(StartupController startupController, 
                       GameController gameController,
                       ViewFacade viewFacade) {
        this.startupController = startupController;
        this.gameController = gameController;
        this.viewFacade = viewFacade;
        
        // Create the JFrame
        frame = new JFrame("Sudoku Game Launcher");
        
        // Initialize UI
        initUI();
        
        // Setup window listener for clean exit
        setupWindowListener();
    }
    
    /** Initializes all UI components */
    private void initUI() {
        configureFrame();
        createMainPanel();
        createTitleSection();
        createButtonSection();
        createFooterSection();
        assembleUI();
        
        // Initial button states
        updateButtonVisibility(false, false);
        
        // Set initial status
        statusLabel.setText("Ready");
    }
    
    /** Configures the JFrame properties */
    private void configureFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setResizable(false);
    }
    
    /** Creates the main container panel */
    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
    }
    
    /** Creates the title section (top of the window) */
    private void createTitleSection() {
        JPanel titlePanel = new JPanel(new BorderLayout(0, 10));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Icon/emoji
        iconLabel = new JLabel("🔢", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        
        // Main title
        titleLabel = new JLabel("SUDOKU MASTER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        // Subtitle
        subtitleLabel = new JLabel("Choose your challenge", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_COLOR);
        
        // Layout
        titlePanel.add(iconLabel, BorderLayout.NORTH);
        
        JPanel textPanel = new JPanel(new BorderLayout(0, 5));
        textPanel.setBackground(BACKGROUND_COLOR);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        titlePanel.add(textPanel, BorderLayout.CENTER);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
    }
    
    /** Creates the button section (center of the window) */
    private void createButtonSection() {
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 0, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        
        // Create styled buttons
        resumeButton = createStyledButton("↻ Resume Unfinished Game", PRIMARY_COLOR);
        easyButton = createStyledButton("Easy", new Color(39, 174, 96));     // Green
        mediumButton = createStyledButton("Medium", new Color(230, 126, 34)); // Orange
        hardButton = createStyledButton("Hard", new Color(231, 76, 60));      // Red
        uploadButton = createStyledButton("📁 Upload Solved Puzzle", SECONDARY_COLOR);
        exitButton = createStyledButton("Exit", new Color(149, 165, 166));    // Gray
        
        // Add action listeners
        setupButtonListeners();
        
        // Add buttons to panel
        buttonPanel.add(resumeButton);
        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);
        buttonPanel.add(uploadButton);
        buttonPanel.add(exitButton);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
    }
    
    /** Creates a styled button with hover effects */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        
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
    
    /** Sets up button action listeners */
    private void setupButtonListeners() {
        resumeButton.addActionListener(e -> handleResumeGame());
        easyButton.addActionListener(e -> handleStartGame(Difficulty.EASY));
        mediumButton.addActionListener(e -> handleStartGame(Difficulty.MEDIUM));
        hardButton.addActionListener(e -> handleStartGame(Difficulty.HARD));
        uploadButton.addActionListener(e -> handleUploadPuzzle());
        exitButton.addActionListener(e -> handleExit());
    }
    
    /** Creates the footer section (bottom of the window) */
    private void createFooterSection() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_COLOR));
        
        statusLabel = new JLabel("Checking game library...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(127, 140, 141));
        statusLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        footerPanel.add(statusLabel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }
    
    /** Assembles all UI components */
    private void assembleUI() {
        frame.setContentPane(mainPanel);
    }
    
    /** Sets up window listener for clean shutdown */
    private void setupWindowListener() {
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleExit();
            }
        });
    }
    
    // ================= PUBLIC API METHODS =================
    
    /** Shows the startup frame */
    public void show() {
        frame.setVisible(true);
    }
    
    /** Hides the startup frame */
    public void hide() {
        frame.setVisible(false);
    }
    
    /** Disposes the startup frame */
    public void dispose() {
        frame.dispose();
    }
    
    /** Updates the UI based on catalog status */
    public void updateCatalogStatus(boolean hasUnfinishedGame, boolean hasAllDifficulties) {
        updateButtonVisibility(hasUnfinishedGame, hasAllDifficulties);
        updateStatusLabels(hasUnfinishedGame, hasAllDifficulties);
    }
    
    /** Shows a loading state */
    public void setLoading(boolean isLoading) {
        if (isLoading) {
            statusLabel.setText("Loading...");
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            setButtonsEnabled(false);
        } else {
            frame.setCursor(Cursor.getDefaultCursor());
            setButtonsEnabled(true);
        }
    }
    
    /** Shows a message dialog */
    public void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /** Shows an error dialog */
    public void showError(String title, String error) {
        JOptionPane.showMessageDialog(frame, error, title, JOptionPane.ERROR_MESSAGE);
    }
    
    // ================= PRIVATE HELPER METHODS =================
    
    /** Updates button visibility based on catalog state */
    private void updateButtonVisibility(boolean hasUnfinished, boolean hasAllDifficulties) {
        resumeButton.setVisible(hasUnfinished);
        
        if (hasAllDifficulties) {
            easyButton.setVisible(true);
            mediumButton.setVisible(true);
            hardButton.setVisible(true);
            uploadButton.setVisible(false);
        } else {
            easyButton.setVisible(false);
            mediumButton.setVisible(false);
            hardButton.setVisible(false);
            uploadButton.setVisible(true);
        }
        
        // Update subtitle based on state
        if (hasUnfinished) {
            subtitleLabel.setText("You have an unfinished game waiting!");
            subtitleLabel.setForeground(ACCENT_COLOR);
        } else if (hasAllDifficulties) {
            subtitleLabel.setText("Select a difficulty level to begin");
            subtitleLabel.setForeground(TEXT_COLOR);
        } else {
            subtitleLabel.setText("Please upload a solved Sudoku puzzle");
            subtitleLabel.setForeground(SECONDARY_COLOR);
        }
    }
    
    /** Updates status labels */
    private void updateStatusLabels(boolean hasUnfinished, boolean hasAllDifficulties) {
        if (hasUnfinished) {
            statusLabel.setText("✓ Unfinished game detected");
            statusLabel.setForeground(ACCENT_COLOR);
        } else if (hasAllDifficulties) {
            statusLabel.setText("✓ Game library ready - Easy, Medium, Hard puzzles available");
            statusLabel.setForeground(new Color(39, 174, 96));
        } else {
            statusLabel.setText("⚠ No games found - Please upload a solved puzzle");
            statusLabel.setForeground(new Color(230, 126, 34));
        }
    }
    
    /** Enables or disables all buttons */
    private void setButtonsEnabled(boolean enabled) {
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                setButtonsEnabledInPanel((JPanel) comp, enabled);
            }
        }
    }
    
    /** Recursively enables/disables buttons in a panel */
    private void setButtonsEnabledInPanel(JPanel panel, boolean enabled) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(enabled);
            } else if (comp instanceof JPanel) {
                setButtonsEnabledInPanel((JPanel) comp, enabled);
            }
        }
    }
    
    // ================= EVENT HANDLERS =================
    
    /** Handles Resume Game button click */
    private void handleResumeGame() {
        setLoading(true);
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Load unfinished game
                    Board unfinishedBoard = startupController.resumeUnfinished();
                    
                    // Switch to game frame on EDT
                    SwingUtilities.invokeLater(() -> {
                        hide();
                        
                        // Create and show game frame
                        gameFrame = new GameFrame(gameController, viewFacade, StartupFrame.this);
                        gameFrame.loadBoard(unfinishedBoard);
                        gameFrame.show();
                    });
                    
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        showError("Load Error", 
                            "Failed to load unfinished game: " + e.getMessage());
                        setLoading(false);
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        showError("Error", 
                            "Unexpected error: " + e.getMessage());
                        setLoading(false);
                    });
                }
                return null;
            }
        };
        
        worker.execute();
    }
    
    /** Handles starting a new game of specified difficulty */
    private void handleStartGame(Difficulty difficulty) {
        setLoading(true);
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Start new game
                    Board newBoard = startupController.startNewGame(difficulty);
                    
                    // Switch to game frame on EDT
                    SwingUtilities.invokeLater(() -> {
                        hide();
                        
                        // Create and show game frame
                        // In handleStartGame method:
gameFrame = new GameFrame(gameController, viewFacade, StartupFrame.this);
gameFrame.loadBoard(newBoard);
gameFrame.setDifficulty(difficulty.toString());

gameFrame.show();
                    });
                    
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        showError("Game Start Error", 
                            "Failed to start " + difficulty + " game: " + e.getMessage());
                        setLoading(false);
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        showError("Error", 
                            "Unexpected error: " + e.getMessage());
                        setLoading(false);
                    });
                }
                return null;
            }
        };
        
        worker.execute();
    }
    
    /** Handles Upload Puzzle button click */
    private void handleUploadPuzzle() {
        // Create file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Solved Sudoku CSV File");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".csv");
            }
            
            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });
        
        int result = fileChooser.showOpenDialog(frame);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Validate file
            if (!selectedFile.exists() || !selectedFile.canRead()) {
                showError("File Error", "Cannot read the selected file.");
                return;
            }
            
            setLoading(true);
            
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    try {
                        // Process uploaded file
                        Path filePath = selectedFile.toPath();
                        startupController.bootstrapFromSolvedSource(filePath, 1);
                        
                        // Success - update UI
                        SwingUtilities.invokeLater(() -> {
                            showMessage("Success", 
                                "Games generated successfully! You can now choose a difficulty.");
                            
                            // Update catalog status
                            updateCatalogStatus(false, true);
                            setLoading(false);
                        });
                        
                    } catch (IOException e) {
                        SwingUtilities.invokeLater(() -> {
                            showError("Upload Error", 
                                "Failed to process file: " + e.getMessage());
                            setLoading(false);
                        });
                    } catch (IllegalArgumentException e) {
                        SwingUtilities.invokeLater(() -> {
                            showError("Invalid Puzzle", 
                                "The file does not contain a valid solved Sudoku: " + e.getMessage());
                            setLoading(false);
                        });
                    } catch (Exception e) {
                        SwingUtilities.invokeLater(() -> {
                            showError("Error", 
                                "Unexpected error: " + e.getMessage());
                            setLoading(false);
                        });
                    }
                    return null;
                }
            };
            
            worker.execute();
        }
    }
    
    /** Handles Exit button click */
    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to exit Sudoku?",
            "Exit Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Clean up resources if needed
            if (gameFrame != null) {
                gameFrame.dispose();
            }
            System.exit(0);
        }
    }
    
    // ================= GameCompletionListener METHODS =================
    
   
    
@Override
public void onGameCompleted(boolean success, String difficulty) {
    SwingUtilities.invokeLater(() -> {
        if (success) {
            showMessage("Level Completed", 
                "You have successfully completed the " + difficulty + " level!");
        }
        
        // Refresh the catalog to update game availability
        refreshCatalog();
        
        // Show the frame again
        show();
    });
}

@Override
public void onReturnToMenu() {
    SwingUtilities.invokeLater(() -> {
        // Refresh catalog when returning to menu
        refreshCatalog();
        show();
    });
}

/**
 * Refreshes the catalog from the controller
 */
private void refreshCatalog() {
    setLoading(true);
    
    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        private boolean[] catalogInfo;
        
        @Override
        protected Void doInBackground() {
            try {
                catalogInfo = viewFacade.getCatalog();
            } catch (Exception e) {
                catalogInfo = new boolean[]{false, false};
                System.err.println("Error refreshing catalog: " + e.getMessage());
            }
            return null;
        }
        
        @Override
        protected void done() {
            setLoading(false);
            if (catalogInfo != null) {
                updateCatalogStatus(catalogInfo[0], catalogInfo[1]);
            }
        }
    };
    
    worker.execute();
}
}