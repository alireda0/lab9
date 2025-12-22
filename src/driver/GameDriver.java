package driver;

import model.Board;
import model.Difficulty;
import model.VerificationResult;
import model.VerificationStatus;
import storage.GameStorage;
import util.RandomPairs;
import verifier.BoardVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class GameDriver {
    private final GameStorage storage;
    private final BoardVerifier verifier;

    public GameDriver(GameStorage storage, BoardVerifier verifier) {
        this.storage = storage;
        this.verifier = verifier;
    }

    public void generateDifficultyGamesFromSolved(Path solvedCsvPath, int gamesPerDifficulty) throws IOException {
        storage.ensureFolderStructure();

        Board solved = storage.loadBoard(solvedCsvPath);

        VerificationResult res = verifier.verify(solved);
        if (res.getStatus() != VerificationStatus.VALID) {
            throw new IllegalArgumentException(
                    "Provided source board must be a complete VALID solved Sudoku. Found: " + res.getStatus()
            );
        }

        for (int i = 1; i <= gamesPerDifficulty; i++) {
            // Generate puzzles for each difficulty
            Board easy = removeCellsFromSolved(solved, 10);   // Easy: 10 holes
            Board medium = removeCellsFromSolved(solved, 20); // Medium: 20 holes
            Board hard = removeCellsFromSolved(solved, 25);   // Hard: 25 holes

            storage.saveGame(easy, Difficulty.EASY, "game_" + pad3(i) + ".csv");
            storage.saveGame(medium, Difficulty.MEDIUM, "game_" + pad3(i) + ".csv");
            storage.saveGame(hard, Difficulty.HARD, "game_" + pad3(i) + ".csv");
        }
    }

    private Board removeCellsFromSolved(Board solved, int holes) {
        Board puzzle = new Board(solved);

        long seed = System.currentTimeMillis() ^ (long)holes * 31;
        RandomPairs pairs = new RandomPairs(seed);

        List<int[]> positionsToRemove = pairs.generateDistinctPairs(holes);
        
        for (int[] pos : positionsToRemove) {
            int r = pos[0];
            int c = pos[1];
            puzzle.set(r, c, 0);
        }

        return puzzle;
    }

    private String pad3(int x) {
        if (x < 10) return "00" + x;
        if (x < 100) return "0" + x;
        return String.valueOf(x);
    }
}