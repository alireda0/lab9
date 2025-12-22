package gui.components;

import model.Board;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SudokuBoardPanel extends JPanel {
    // Colors
    private static final Color CELL_BACKGROUND = Color.WHITE;
    private static final Color FIXED_CELL_BACKGROUND = new Color(240, 240, 240);
    private static final Color FIXED_CELL_TEXT = new Color(60, 60, 60);
    private static final Color EDITABLE_CELL_TEXT = Color.BLACK;
    private static final Color CELL_BORDER_COLOR = new Color(200, 200, 200);
    private static final Color THICK_BORDER_COLOR = new Color(120, 120, 120);
    private static final Color HIGHLIGHT_BORDER_COLOR = new Color(41, 128, 185);
    private static final Color ERROR_BORDER_COLOR = new Color(231, 76, 60);
    
    // Cell dimensions
    private static final int CELL_SIZE = 50;
    private static final int PANEL_PADDING = 10;
    
    // Components
    private SudokuCell[][] cells = new SudokuCell[9][9];
    private int highlightedRow = -1;
    private int highlightedCol = -1;
    
    // Listeners
    private CellChangeListener cellChangeListener;
    
    /**
     * Interface for cell change events
     */
    public interface CellChangeListener {
        void onCellChange(int row, int col, int newValue);
    }
    
    /**
     * Constructor
     */
    public SudokuBoardPanel() {
        initComponents();
        setupLayout();
    }
    
    /**
     * Initializes all components
     */
    private void initComponents() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));
        
        // Create all cells
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = createCell(row, col);
            }
        }
    }
    
    /**
     * Sets up the layout
     */
    private void setupLayout() {
        setLayout(new GridLayout(3, 3, 2, 2)); // 3x3 grid of 3x3 boxes
        
        // Create 9 sub-panels for 3x3 boxes
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                JPanel boxPanel = createBoxPanel(boxRow, boxCol);
                add(boxPanel);
            }
        }
    }
    
    /**
     * Creates a single Sudoku cell
     */
    private SudokuCell createCell(int row, int col) {
        SudokuCell cell = new SudokuCell(row, col);
        
        // Determine border based on position
        Border border = createCellBorder(row, col);
        cell.setBorder(border);
        
        // Set cell size
        cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        cell.setHorizontalAlignment(JTextField.CENTER);
        cell.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Set initial state
        cell.setEditable(true);
        cell.setBackground(CELL_BACKGROUND);
        cell.setForeground(EDITABLE_CELL_TEXT);
        
        // Add focus listener for highlighting
        cell.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                highlightRowAndColumn(row, col);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                clearHighlights();
            }
        });
        
        // Add key listener for input validation
        cell.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                handleCellInput(cell, e);
            }
        });
        
        return cell;
    }
    
   private Border createCellBorder(int row, int col) {
    // Create borders for each side
    Border topBorder = new MatteBorder(
        (row % 3 == 0) ? 2 : 1, 
        0, 0, 0, 
        (row % 3 == 0) ? THICK_BORDER_COLOR : CELL_BORDER_COLOR
    );
    
    Border leftBorder = new MatteBorder(
        0, 
        (col % 3 == 0) ? 2 : 1, 
        0, 0, 
        (col % 3 == 0) ? THICK_BORDER_COLOR : CELL_BORDER_COLOR
    );
    
    Border bottomBorder = new MatteBorder(
        0, 0, 
        (row == 8) ? 2 : 1, 
        0, 
        (row == 8) ? THICK_BORDER_COLOR : CELL_BORDER_COLOR
    );
    
    Border rightBorder = new MatteBorder(
        0, 0, 0, 
        (col == 8) ? 2 : 1, 
        (col == 8) ? THICK_BORDER_COLOR : CELL_BORDER_COLOR
    );
    
    // Combine them: top + (left + (bottom + right))
    Border combined = new CompoundBorder(topBorder, 
                        new CompoundBorder(leftBorder,
                            new CompoundBorder(bottomBorder, rightBorder)));
    
    return combined;
}
    /**
     * Creates a 3x3 box panel
     */
    private JPanel createBoxPanel(int boxRow, int boxCol) {
        JPanel boxPanel = new JPanel(new GridLayout(3, 3, 1, 1));
        boxPanel.setBackground(THICK_BORDER_COLOR);
        boxPanel.setBorder(new LineBorder(THICK_BORDER_COLOR, 1));
        
        // Add cells to this box
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int actualRow = boxRow * 3 + r;
                int actualCol = boxCol * 3 + c;
                boxPanel.add(cells[actualRow][actualCol]);
            }
        }
        
        return boxPanel;
    }
    
    /**
     * Handles cell input
     */
    private void handleCellInput(SudokuCell cell, KeyEvent e) {
        String text = cell.getText().trim();
        
        // Handle Enter key
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            moveToNextCell(cell.getRow(), cell.getCol());
            return;
        }
        
        // Handle number input
        if (text.length() == 1 && Character.isDigit(text.charAt(0))) {
            int digit = Character.getNumericValue(text.charAt(0));
            
            // Validate input (1-9 or 0 for clear)
            if (digit >= 0 && digit <= 9) {
                // Update cell
                cell.setText(digit == 0 ? "" : text);
                
                // Notify listener
                if (cellChangeListener != null) {
                    cellChangeListener.onCellChange(cell.getRow(), cell.getCol(), digit);
                }
            } else {
                // Invalid digit, clear the cell
                cell.setText("");
            }
        } else if (text.isEmpty()) {
            // Empty cell means 0
            if (cellChangeListener != null) {
                cellChangeListener.onCellChange(cell.getRow(), cell.getCol(), 0);
            }
        } else {
            // Invalid input, clear the cell
            cell.setText("");
        }
    }
    
    /**
     * Moves focus to the next cell
     */
    private void moveToNextCell(int currentRow, int currentCol) {
        int nextCol = (currentCol + 1) % 9;
        int nextRow = currentRow;
        
        if (nextCol == 0) {
            nextRow = (currentRow + 1) % 9;
        }
        
        cells[nextRow][nextCol].requestFocusInWindow();
    }
    
    /**
     * Highlights the row and column of the focused cell
     */
    private void highlightRowAndColumn(int row, int col) {
        clearHighlights();
        
        highlightedRow = row;
        highlightedCol = col;
        
        // Highlight row
        for (int c = 0; c < 9; c++) {
            if (c != col) {
                highlightCell(row, c);
            }
        }
        
        // Highlight column
        for (int r = 0; r < 9; r++) {
            if (r != row) {
                highlightCell(r, col);
            }
        }
        
        // Highlight 3x3 box
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (r != row && c != col) {
                    highlightCell(r, c);
                }
            }
        }
    }
    
    /**
     * Highlights a single cell
     */
    private void highlightCell(int row, int col) {
        SudokuCell cell = cells[row][col];
        Border originalBorder = cell.getBorder();
        
        // Create highlighted border
        Border highlightBorder = new CompoundBorder(
            new LineBorder(HIGHLIGHT_BORDER_COLOR, 2),
            originalBorder
        );
        
        cell.setBorder(highlightBorder);
        cell.setBackground(new Color(240, 248, 255)); // Light blue
    }
    
    /**
     * Clears all highlights
     */
    private void clearHighlights() {
        if (highlightedRow == -1 || highlightedCol == -1) return;
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                SudokuCell cell = cells[row][col];
                
                // Restore original border
                Border originalBorder = createCellBorder(row, col);
                cell.setBorder(originalBorder);
                
                // Restore background
                if (cell.isFixed()) {
                    cell.setBackground(FIXED_CELL_BACKGROUND);
                } else {
                    cell.setBackground(CELL_BACKGROUND);
                }
            }
        }
        
        highlightedRow = -1;
        highlightedCol = -1;
    }
    
    /**
     * Marks a cell as an error
     */
    public void markCellError(int row, int col) {
        SudokuCell cell = cells[row][col];
        Border originalBorder = cell.getBorder();
        
        Border errorBorder = new CompoundBorder(
            new LineBorder(ERROR_BORDER_COLOR, 2),
            originalBorder
        );
        
        cell.setBorder(errorBorder);
    }
    
    /**
     * Clears error marks from all cells
     */
    public void clearErrorMarks() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                SudokuCell cell = cells[row][col];
                Border originalBorder = createCellBorder(row, col);
                cell.setBorder(originalBorder);
            }
        }
    }
    
    // ================= PUBLIC API METHODS =================
    
    /**
     * Loads a board into the panel
     */
    public void loadBoard(Board board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board.get(row, col);
                SudokuCell cell = cells[row][col];
                
                if (value == 0) {
                    cell.setText("");
                    cell.setEditable(true);
                    cell.setFixed(false);
                    cell.setBackground(CELL_BACKGROUND);
                    cell.setForeground(EDITABLE_CELL_TEXT);
                } else {
                    cell.setText(String.valueOf(value));
                    cell.setEditable(false);
                    cell.setFixed(true);
                    cell.setBackground(FIXED_CELL_BACKGROUND);
                    cell.setForeground(FIXED_CELL_TEXT);
                }
            }
        }
    }
    
    /**
     * Sets the cell change listener
     */
    public void setCellChangeListener(CellChangeListener listener) {
        this.cellChangeListener = listener;
    }
    
    /**
     * Enables or disables the entire board
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                SudokuCell cell = cells[row][col];
                
                // Only disable editable cells
                if (!cell.isFixed()) {
                    cell.setEditable(enabled);
                    if (!enabled) {
                        cell.setBackground(FIXED_CELL_BACKGROUND);
                    } else {
                        cell.setBackground(CELL_BACKGROUND);
                    }
                }
            }
        }
    }
    
    /**
     * Reverts a cell to a specific value
     */
    public void revertCell(int row, int col, int value) {
        SudokuCell cell = cells[row][col];
        
        if (value == 0) {
            cell.setText("");
        } else {
            cell.setText(String.valueOf(value));
        }
    }
    
    /**
     * Gets the current board state
     */
    public Board getCurrentBoard() {
        Board board = new Board();
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                SudokuCell cell = cells[row][col];
                String text = cell.getText().trim();
                
                if (text.isEmpty()) {
                    board.set(row, col, 0);
                } else {
                    try {
                        int value = Integer.parseInt(text);
                        board.set(row, col, value);
                    } catch (NumberFormatException e) {
                        board.set(row, col, 0);
                    }
                }
            }
        }
        
        return board;
    }
    
    /**
     * Custom JTextField for Sudoku cells
     */
    private static class SudokuCell extends JTextField {
        private final int row;
        private final int col;
        private boolean isFixed = false;
        
        public SudokuCell(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        public int getRow() { return row; }
        public int getCol() { return col; }
        public boolean isFixed() { return isFixed; }
        public void setFixed(boolean fixed) { this.isFixed = fixed; }
        
        @Override
        protected void processKeyEvent(KeyEvent e) {
            // Only allow digits, backspace, delete, and navigation keys
            char c = e.getKeyChar();
            
            if (Character.isDigit(c) || 
                c == KeyEvent.VK_BACK_SPACE || 
                c == KeyEvent.VK_DELETE ||
                c == KeyEvent.VK_ENTER ||
                c == KeyEvent.VK_TAB ||
                e.isActionKey()) {
                
                super.processKeyEvent(e);
            }
            
            // Consume other key events
            e.consume();
        }
    }
}