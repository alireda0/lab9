package integration;

import model.Board;

/**
 * Wrapper class for a Sudoku game board as specified in the lab.
 * Used by the controller (application layer) to represent a game.
 * IMPORTANT: This class holds a reference to the Board, not a copy.
 */
public class Game {
    // The underlying board - using a reference as per lab spec
    private Board board;

    /**
     * Constructs a Game wrapper for the given board.
     * @param board The board to wrap (reference is kept, not copied)
     */
    public Game(Board board) {
        // IMPORTANT: DON'T COPY THE BOARD BY VALUE
        // USE REFERENCES
        this.board = board;
    }

    /**
     * Gets the wrapped board.
     * @return The board reference
     */
    public Board getBoard() {
        return board;
    }

    // You can add other methods if needed for your implementation
    // For example: getDifficulty(), isCompleted(), etc.
}