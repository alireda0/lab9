package gui.facades;

import gui.interfaces.Viewable;
import gui.interfaces.NotFoundException;
import gui.interfaces.SolutionInvalidException;
import gui.interfaces.InvalidGameException;
import integration.Game;
import model.Board;
import model.Difficulty;
import model.VerificationResult;
import model.VerificationStatus;
import model.Duplicate;
import controller.StartupController;
import controller.GameController;
import catalog.GameCatalogue;
import driver.GameDriver;
import storage.GameStorage;
import verifier.BoardVerifier;
import solver.SudokuSolver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringJoiner;

public class ControllerFacade implements Viewable {
    
    private final StartupController startupController;
    private final GameController gameController;
    private final GameCatalogue gameCatalogue;
    private final GameDriver gameDriver;
    private final GameStorage storage;
    private final BoardVerifier verifier;
    private final SudokuSolver solver;
    
    public ControllerFacade(StartupController startupController,
                           GameController gameController,
                           GameCatalogue gameCatalogue,
                           GameDriver gameDriver,
                           GameStorage storage,
                           BoardVerifier verifier,
                           SudokuSolver solver) {
        this.startupController = startupController;
        this.gameController = gameController;
        this.gameCatalogue = gameCatalogue;
        this.gameDriver = gameDriver;
        this.storage = storage;
        this.verifier = verifier;
        this.solver = solver;
    }
    
    @Override
    public boolean[] getCatalog() {
        try {
            boolean hasUnfinished = gameCatalogue.hasUnfinishedGame();
            boolean hasAllDifficulties = gameCatalogue.hasAtLeastOneGameInEachDifficulty();
            return new boolean[]{hasUnfinished, hasAllDifficulties};
        } catch (IOException e) {
            // If there's an IO error, assume no games available
            return new boolean[]{false, false};
        }
    }
    
    @Override
    public Game getGame(Difficulty level) throws NotFoundException {
        try {
            Board board = gameCatalogue.getGame(level);
            return new Game(board);
        } catch (IOException e) {
            throw new NotFoundException("No game found for difficulty: " + level + ". Error: " + e.getMessage());
        }
    }
    
    @Override
    public void driveGames(Game sourceGame) throws SolutionInvalidException {
        try {
            // Extract the Board from the Game wrapper
            Board sourceBoard = sourceGame.getBoard();
            
            // Verify the source board
            VerificationResult result = verifier.verify(sourceBoard);
            if (result.getStatus() != VerificationStatus.VALID) {
                throw new SolutionInvalidException(
                    "Source board is not a valid complete Sudoku. Status: " + result.getStatus()
                );
            }
            
            // Save the source board temporarily to a file
            Path tempPath = storage.getBaseDir().resolve("temp_source.csv");
            storage.saveGame(sourceBoard, Difficulty.EASY, "temp_source.csv");
            
            // Use the driver to generate difficulty games
            gameDriver.generateDifficultyGamesFromSolved(tempPath, 1);
            
            // Clean up temp file
            storage.deleteGameFile(tempPath);
            
        } catch (IOException e) {
            throw new SolutionInvalidException("IO error while generating games: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new SolutionInvalidException("Invalid source board: " + e.getMessage());
        }
    }
    
@Override
public String verifyGame(Game game) {
    Board board = game.getBoard();
    VerificationResult result = verifier.verify(board);
    
    switch (result.getStatus()) {
        case VALID:
            return "valid";
        case INCOMPLETE:
            return "incomplete";
        case INVALID:
            StringBuilder sb = new StringBuilder("invalid");
            for (Duplicate dup : result.getDuplicates()) {
                for (Integer pos : dup.getPositions()) {
                    if (dup.getType().equals("ROW")) {
                        sb.append(" ").append(dup.getIndex()).append(",").append(pos);
                    } else if (dup.getType().equals("COL")) {
                        sb.append(" ").append(pos).append(",").append(dup.getIndex());
                    } else if (dup.getType().equals("BOX")) {
                        int boxIndex = dup.getIndex() - 1;
                        int boxRow = boxIndex / 3;
                        int boxCol = boxIndex % 3;
                        int localPos = pos - 1;
                        int cellRow = boxRow * 3 + (localPos / 3);
                        int cellCol = boxCol * 3 + (localPos % 3);
                        sb.append(" ").append(cellRow + 1).append(",").append(cellCol + 1);
                    }
                }
            }
            return sb.toString().trim();
        default:
            return "error";
    }
}
    @Override
    public int[] solveGame(Game game) throws InvalidGameException {
        try {
            Board board = game.getBoard();
            
            // Check if we can solve (exactly 5 blanks)
            if (board.countZeros() != 5) {
                throw new InvalidGameException("Solve is only allowed when exactly 5 cells are empty.");
            }
            
            // Attempt to solve
            Board solved = solver.solveIfExactlyFiveBlanks(board);
            
            if (solved == null) {
                throw new InvalidGameException("No solution found for the current puzzle.");
            }
            
            // Find empty cells and encode: (row << 16) | (col << 8) | value
            int[] solutions = new int[5];
            int solutionIndex = 0;
            
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (board.get(r, c) == 0) {
                        int solvedValue = solved.get(r, c);
                        
                        // Encode: row (0-8) in bits 16-23, col (0-8) in bits 8-15, value (1-9) in bits 0-7
                        int encoded = ((r & 0xFF) << 16) | ((c & 0xFF) << 8) | (solvedValue & 0xFF);
                        solutions[solutionIndex++] = encoded;
                    }
                }
            }
            
            return solutions;
            
        } catch (IllegalArgumentException e) {
            throw new InvalidGameException(e.getMessage());
        } catch (Exception e) {
            throw new InvalidGameException("Error solving game: " + e.getMessage());
        }
    }
    
    @Override
    public void logUserAction(String userAction) throws IOException {
        System.out.println("User action: " + userAction);
    }
    
    // Helper method to get the startup controller
    public StartupController getStartupController() {
        return startupController;
    }
    
    // Helper method to get the game controller
    public GameController getGameController() {
        return gameController;
    }
}