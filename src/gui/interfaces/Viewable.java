package gui.interfaces;

import integration.Game;
import model.Difficulty;
import java.io.IOException;

public interface Viewable {
    
    /**
     * Returns catalog information for the GUI startup.
     * @return boolean array where:
     *         [0] = true if there is an unfinished game
     *         [1] = true if there is at least one game available for each difficulty
     */
    boolean[] getCatalog();
    
    /**
     * Returns a random game with the specified difficulty.
     * @param level The difficulty level (EASY, MEDIUM, HARD)
     * @return A Game object containing the Sudoku board
     * @throws NotFoundException if no game exists for that difficulty
     */
    Game getGame(Difficulty level) throws NotFoundException;
    
    /**
     * Gets a source solution and generates three levels of difficulty from it.
     * @param sourceGame The solved Sudoku board to use as source
     * @throws SolutionInvalidException if the source game is not valid
     */
    void driveGames(Game sourceGame) throws SolutionInvalidException;
    
    /**
     * Verifies a game state.
     * @param game The game to verify
     * @return String representation of the verification result:
     *         - "valid" if game is valid and complete
     *         - "incomplete" if valid but incomplete
     *         - "invalid r1,c1 r2,c2 ..." if invalid, with duplicate locations
     *         Example: "invalid 1,2 3,3 6,7"
     */
    String verifyGame(Game game);
    
    /**
     * Returns the correct values for the missing numbers.
     * @param game The game to solve
     * @return Array of correct values for each missing cell in order
     *         Hint: You can encode location and value in one int if needed
     * @throws InvalidGameException if the game cannot be solved
     */
    int[] solveGame(Game game) throws InvalidGameException;
    
    /**
     * Logs a user action.
     * @param userAction String description of the user action
     * @throws IOException if logging fails
     */
    void logUserAction(String userAction) throws IOException;
}