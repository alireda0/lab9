package gui.interfaces;

import java.io.IOException;

public interface Controllable {
    
    /**
     * Returns catalog information for the GUI startup.
     * @return boolean array where:
     *         [0] = true if there is an unfinished game
     *         [1] = true if there is at least one game available for each difficulty
     */
    boolean[] getCatalog();
    
    /**
     * Returns a game board for the specified difficulty level.
     * @param level Character representing difficulty: 'E' for EASY, 'M' for MEDIUM, 'H' for HARD
     * @return 9x9 integer array representing the Sudoku board
     * @throws NotFoundException if no game exists for that difficulty
     */
    int[][] getGame(char level) throws NotFoundException;
    
    /**
     * Generates games from a solved Sudoku source file.
     * @param sourcePath Path to the solved Sudoku CSV file
     * @throws SolutionInvalidException if the source file contains invalid/incomplete Sudoku
     */
    void driveGames(String sourcePath) throws SolutionInvalidException;
    
    /**
     * Verifies a game board and indicates which cells are correct/invalid.
     * @param board 9x9 integer array representing the Sudoku board
     * @return 9x9 boolean array where true means the cell is correct, false means invalid
     */
    boolean[][] verifyGame(int[][] board);
    
    /**
     * Solves the game and returns solutions for missing cells.
     * @param board 9x9 integer array representing the Sudoku board
     * @return 2D array where each row contains [x, y, solution] for each missing cell
     *         (x and y are 1-based indices)
     * @throws InvalidGameException if the game cannot be solved
     */
    int[][] solveGame(int[][] board) throws InvalidGameException;
    
    /**
     * Logs a user action.
     * @param userAction String description of the user action
     * @throws IOException if logging fails
     */
    void logUserAction(String userAction) throws IOException;
}