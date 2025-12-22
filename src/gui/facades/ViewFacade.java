package gui.facades;

import gui.interfaces.Controllable;
import gui.interfaces.NotFoundException;
import gui.interfaces.SolutionInvalidException;
import gui.interfaces.InvalidGameException;
import model.Board;
import model.Difficulty;
import model.VerificationResult;
import model.VerificationStatus;
import controller.StartupController;
import controller.GameController;
import catalog.GameCatalogue;
import driver.GameDriver;
import storage.GameStorage;
import verifier.BoardVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ViewFacade implements Controllable {
    
    private final StartupController startupController;
    private final GameController gameController;
    private final GameCatalogue gameCatalogue;
    private final GameDriver gameDriver;
    private final GameStorage storage;
    private final BoardVerifier verifier;
    
    // Reference to the main game board (maintained by GUI)
    private Board currentBoard;
    
    public ViewFacade(StartupController startupController,
                     GameController gameController,
                     GameCatalogue gameCatalogue,
                     GameDriver gameDriver,
                     GameStorage storage,
                     BoardVerifier verifier) {
        this.startupController = startupController;
        this.gameController = gameController;
        this.gameCatalogue = gameCatalogue;
        this.gameDriver = gameDriver;
        this.storage = storage;
        this.verifier = verifier;
        this.currentBoard = null;
    }
    
    @Override
    public boolean[] getCatalog() {
        try {
            boolean hasUnfinished = gameCatalogue.hasUnfinishedGame();
            boolean hasAllDifficulties = gameCatalogue.hasAtLeastOneGameInEachDifficulty();
            return new boolean[]{hasUnfinished, hasAllDifficulties};
        } catch (IOException e) {
            // Log error and return false for both
            System.err.println("Error checking catalog: " + e.getMessage());
            return new boolean[]{false, false};
        }
    }
    
    @Override
    public int[][] getGame(char level) throws NotFoundException {
        try {
            Difficulty difficulty = charToDifficulty(level);
            Board board = gameCatalogue.getGame(difficulty);
            
            // Store reference to current board
            this.currentBoard = board;
            
            // Start tracking this new game
            gameController.startTrackingNewOrLoadedGame(board);
            
            // Convert Board to 2D int array
            return boardTo2DArray(board);
            
        } catch (IOException e) {
            throw new NotFoundException("Failed to load game for level '" + level + "': " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Invalid difficulty level '" + level + "': " + e.getMessage());
        }
    }
    
    @Override
    public void driveGames(String sourcePath) throws SolutionInvalidException {
        try {
            Path path = Paths.get(sourcePath);
            
            // Load the board from the source file
            Board sourceBoard = Board.fromCSV(sourcePath);
            
            // Verify it's a valid complete Sudoku
            VerificationResult result = verifier.verify(sourceBoard);
            if (result.getStatus() != VerificationStatus.VALID) {
                throw new SolutionInvalidException(
                    "Provided source is not a valid complete Sudoku. Status: " + result.getStatus()
                );
            }
            
            // Use the driver to generate games
            gameDriver.generateDifficultyGamesFromSolved(path, 1);
            
        } catch (IOException e) {
            throw new SolutionInvalidException("IO error processing source file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new SolutionInvalidException("Invalid source file format: " + e.getMessage());
        }
    }
    
    @Override
    public boolean[][] verifyGame(int[][] board) {
        // Convert 2D array to Board
        Board gameBoard = arrayToBoard(board);
        
        // Update current board reference
        this.currentBoard = gameBoard;
        
        // Verify the board
        VerificationResult result = verifier.verify(gameBoard);
        
        // Create 9x9 boolean array
        boolean[][] correctness = new boolean[9][9];
        
        if (result.getStatus() == VerificationStatus.VALID) {
            // All cells are correct
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    correctness[r][c] = true;
                }
            }
        } else if (result.getStatus() == VerificationStatus.INVALID) {
            // Mark all cells as correct initially
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    correctness[r][c] = true;
                }
            }
            
            // Mark duplicate cells as incorrect
            // Note: This is a simplified approach - you might need more complex logic
            // to identify exactly which cells are incorrect
            for (var duplicate : result.getDuplicates()) {
                // For simplicity, mark all positions with the duplicate value as incorrect
                // You may want to implement more precise logic here
                for (int r = 0; r < 9; r++) {
                    for (int c = 0; c < 9; c++) {
                        if (gameBoard.get(r, c) == duplicate.getValue()) {
                            correctness[r][c] = false;
                        }
                    }
                }
            }
        } else {
            // INCOMPLETE: non-zero cells should be verified
            // Check each non-zero cell individually
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    int value = gameBoard.get(r, c);
                    if (value != 0) {
                        // Check if this value appears only once in its row, column, and box
                        boolean isCorrect = isValueValidAtPosition(gameBoard, r, c, value);
                        correctness[r][c] = isCorrect;
                    } else {
                        correctness[r][c] = true; // Empty cells are considered "correct" for display
                    }
                }
            }
        }
        
        return correctness;
    }
    
    @Override
    public int[][] solveGame(int[][] board) throws InvalidGameException {
        try {
            // Convert to Board
            Board gameBoard = arrayToBoard(board);
            
            // Check if we can solve (exactly 5 blanks)
            if (gameBoard.countZeros() != 5) {
                throw new InvalidGameException("Solve is only allowed when exactly 5 cells are empty.");
            }
            
            // Use the game controller to solve
            Board solved = gameController.solve(gameBoard);
            
            if (solved == null) {
                throw new InvalidGameException("No solution found for the current puzzle.");
            }
            
            // Find the empty cells and their solutions
            List<int[]> solutions = new ArrayList<>();
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (board[r][c] == 0) {
                        int solvedValue = solved.get(r, c);
                        // Return as 1-based indices: [row, col, value]
                        solutions.add(new int[]{r + 1, c + 1, solvedValue});
                    }
                }
            }
            
            // Convert list to 2D array
            int[][] result = new int[solutions.size()][3];
            for (int i = 0; i < solutions.size(); i++) {
                result[i] = solutions.get(i);
            }
            
            return result;
            
        } catch (IOException e) {
            throw new InvalidGameException("IO error while solving: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new InvalidGameException(e.getMessage());
        }
    }
    
    @Override
    public void logUserAction(String userAction) throws IOException {
        // Log the user action
        System.out.println("GUI User Action: " + userAction);
    }
    
    // Helper method to update a cell value
    public void updateCell(int row, int col, int value) throws IOException, IllegalStateException {
        if (currentBoard == null) {
            throw new IllegalStateException("No active game board");
        }
        
        // Convert to 1-based indices for game controller
        gameController.applyMove(currentBoard, row, col, value);
    }
    
    // Helper method to undo last move
    public void undoMove() throws IOException, IllegalStateException {
        if (currentBoard == null) {
            throw new IllegalStateException("No active game board");
        }
        
        gameController.undo(currentBoard);
    }
    
    // Helper method to check if solve button should be enabled
    public boolean canSolveCurrentGame() {
        if (currentBoard == null) return false;
        return currentBoard.countZeros() == 5;
    }
    
    // Helper method to get the current board as 2D array
    public int[][] getCurrentBoardAsArray() {
        if (currentBoard == null) return null;
        return boardTo2DArray(currentBoard);
    }
    
    // Helper method to set the current board (e.g., when resuming a game)
    public void setCurrentBoard(Board board) throws IOException {
        this.currentBoard = board;
        gameController.resumeTrackingExistingGame(board);
    }
    
    // Private helper methods
    
    private Difficulty charToDifficulty(char level) {
        switch (Character.toUpperCase(level)) {
            case 'E': return Difficulty.EASY;
            case 'M': return Difficulty.MEDIUM;
            case 'H': return Difficulty.HARD;
            default:
                throw new IllegalArgumentException("Invalid difficulty character: " + level + 
                                                 ". Use E, M, or H.");
        }
    }
    
    private int[][] boardTo2DArray(Board board) {
        int[][] array = new int[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                array[r][c] = board.get(r, c);
            }
        }
        return array;
    }
    
    private Board arrayToBoard(int[][] array) {
        Board board = new Board();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                board.set(r, c, array[r][c]);
            }
        }
        return board;
    }
    
    private boolean isValueValidAtPosition(Board board, int row, int col, int value) {
        // Check row
        for (int c = 0; c < 9; c++) {
            if (c != col && board.get(row, c) == value) {
                return false;
            }
        }
        
        // Check column
        for (int r = 0; r < 9; r++) {
            if (r != row && board.get(r, col) == value) {
                return false;
            }
        }
        
        // Check 3x3 box
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (r != row && c != col && board.get(r, c) == value) {
                    return false;
                }
            }
        }
        
        return true;
    }
}