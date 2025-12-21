package catalog;

import model.Board;
import model.Difficulty;
import storage.GameStorage;

import java.io.IOException;
import java.nio.file.Path;

public class GameCatalogue {

    private final GameStorage storage;

    public GameCatalogue(GameStorage storage) {
        this.storage = storage;
    }

    // GUI startup boolean #1
    public boolean hasUnfinishedGame() throws IOException {
        // optional strict check:
        if (!storage.incompleteFolderLooksValid()) {
            // If you want: you could auto-clean here.
            // For now: treat as "no unfinished" OR throw exception.
            return storage.hasUnfinishedGame();
        }
        return storage.hasUnfinishedGame();
    }

    // GUI startup boolean #2
    public boolean hasAtLeastOneGameInEachDifficulty() throws IOException {
        return storage.hasAllDifficulties();
    }

    public Board loadUnfinishedGame() throws IOException {
        if (!storage.hasUnfinishedGame()) {
            throw new IOException("No unfinished game found.");
        }
        return storage.loadBoard(storage.currentBoardPath());
    }

    // Load one game from selected difficulty (random)
    public Board getGame(Difficulty difficulty) throws IOException {
        Path picked = storage.pickRandomGameFile(difficulty);
        return storage.loadBoard(picked);
    }

    // If later you want the chosen file path too:
    public Path pickGameFile(Difficulty difficulty) throws IOException {
        return storage.pickRandomGameFile(difficulty);
    }
}
