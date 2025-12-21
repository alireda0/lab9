package controller;

import catalog.GameCatalogue;
import driver.GameDriver;
import model.Board;
import model.Difficulty;
import storage.GameStorage;

import java.io.IOException;
import java.nio.file.Path;

public class StartupController {

    public enum StartupCase {
        RESUME_UNFINISHED,
        CHOOSE_DIFFICULTY,
        NEED_SOLVED_SOURCE
    }

    public static class StartupInfo {
        private final StartupCase startupCase;

        public StartupInfo(StartupCase startupCase) {
            this.startupCase = startupCase;
        }

        public StartupCase getStartupCase() {
            return startupCase;
        }
    }

    private final GameStorage storage;
    private final GameCatalogue catalogue;
    private final GameDriver driver;

    public StartupController(GameStorage storage, GameCatalogue catalogue, GameDriver driver) {
        this.storage = storage;
        this.catalogue = catalogue;
        this.driver = driver;
    }

    public StartupInfo decideStartup() throws IOException {
        storage.ensureFolderStructure();

        if (catalogue.hasUnfinishedGame()) {
            return new StartupInfo(StartupCase.RESUME_UNFINISHED);
        }

        if (catalogue.hasAtLeastOneGameInEachDifficulty()) {
            return new StartupInfo(StartupCase.CHOOSE_DIFFICULTY);
        }

        return new StartupInfo(StartupCase.NEED_SOLVED_SOURCE);
    }

    public Board resumeUnfinished() throws IOException {
        return catalogue.loadUnfinishedGame();
    }

    public Board startNewGame(Difficulty difficulty) throws IOException {
        Board board = catalogue.getGame(difficulty);
        storage.clearIncompleteFolder();
        storage.saveCurrentBoard(board);
        return board;
    }

    public void bootstrapFromSolvedSource(Path solvedCsvPath, int gamesPerDifficulty) throws IOException {
        driver.generateDifficultyGamesFromSolved(solvedCsvPath, gamesPerDifficulty);
    }
}
