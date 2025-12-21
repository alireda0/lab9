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

public class GameDriver {

    private final GameStorage storage;
    private final BoardVerifier verifier;

    public GameDriver(GameStorage storage, BoardVerifier verifier) {
        this.storage = storage;
        this.verifier = verifier;
    }

    // Generates 1 game for each difficulty by default
    public void generateDifficultyGamesFromSolved(Path solvedCsvPath) throws IOException {
        generateDifficultyGamesFromSolved(solvedCsvPath, 1);
    }

    // You can generate multiple games per difficulty (optional)
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
            Board easy = makePuzzleFromSolved(solved, 10);
            Board medium = makePuzzleFromSolved(solved, 25);
            Board hard = makePuzzleFromSolved(solved, 20);

            storage.saveGame(easy, Difficulty.EASY,   "game_" + pad3(i) + ".csv");
            storage.saveGame(medium, Difficulty.MEDIUM,"game_" + pad3(i) + ".csv");
            storage.saveGame(hard, Difficulty.HARD,   "game_" + pad3(i) + ".csv");
        }
    }

    private Board makePuzzleFromSolved(Board solved, int holes) {
        Board puzzle = new Board(solved);

        long seed = System.currentTimeMillis() ^ (long)holes * 31;
        RandomPairs pairs = new RandomPairs(seed);

        int removed = 0;
        while (removed < holes) {
            int[] rc = pairs.next();
            int r = rc[0], c = rc[1];

            if (puzzle.get(r, c) != 0) {
                puzzle.set(r, c, 0);
                removed++;
            }
        }

        return puzzle;
    }

    private String pad3(int x) {
        if (x < 10) return "00" + x;
        if (x < 100) return "0" + x;
        return String.valueOf(x);
    }
}
