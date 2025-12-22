package storage;

import model.Board;
import model.Difficulty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameStorage {

    private final Path baseDir;

    public static final String INCOMPLETE_FOLDER = "incomplete";
    public static final String CURRENT_BOARD_FILE = "current.csv";
    public static final String CURRENT_LOG_FILE = "current.log";

    public GameStorage(Path baseDir) {
        this.baseDir = baseDir;
    }

    public Path getBaseDir() {
        return baseDir;
    }

    // -------------------- FOLDERS --------------------
    public void ensureFolderStructure() throws IOException {
        Files.createDirectories(baseDir);

        for (Difficulty d : Difficulty.values()) {
            Files.createDirectories(baseDir.resolve(d.folder()));
        }

        Files.createDirectories(baseDir.resolve(INCOMPLETE_FOLDER));
    }

    public Path difficultyDir(Difficulty d) {
        return baseDir.resolve(d.folder());
    }

    public Path incompleteDir() {
        return baseDir.resolve(INCOMPLETE_FOLDER);
    }

    public Path currentBoardPath() {
        return incompleteDir().resolve(CURRENT_BOARD_FILE);
    }

    public Path currentLogPath() {
        return incompleteDir().resolve(CURRENT_LOG_FILE);
    }

    // -------------------- LIST / EXISTENCE --------------------
    public List<Path> listGames(Difficulty d) throws IOException {
        Path dir = difficultyDir(d);
        if (!Files.exists(dir)) return List.of();

        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".csv"))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public boolean hasAnyGame(Difficulty d) throws IOException {
        return !listGames(d).isEmpty();
    }

    // FIXED: Added missing method
    public boolean hasAllDifficulties() throws IOException {
        for (Difficulty d : Difficulty.values()) {
            if (!hasAnyGame(d)) return false;
        }
        return true;
    }

    // -------------------- PICK A GAME --------------------
    public Path pickRandomGameFile(Difficulty d) throws IOException {
        List<Path> games = listGames(d);
        if (games.isEmpty()) {
            throw new FileNotFoundException("No games found in: " + difficultyDir(d));
        }
        return games.get(new Random().nextInt(games.size()));
    }

    // -------------------- LOAD --------------------
    public Board loadBoard(Path csvPath) throws FileNotFoundException {
        return Board.fromCSV(csvPath.toString());
    }

    // -------------------- SAVE CURRENT --------------------
    public void saveCurrentBoard(Board board) throws IOException {
        Path out = currentBoardPath();
        writeBoardCsv(board, out);
    }

    public void clearIncompleteFolder() throws IOException {
        Path dir = incompleteDir();
        if (!Files.exists(dir)) return;

        try (Stream<Path> stream = Files.list(dir)) {
            for (Path p : stream.collect(Collectors.toList())) {
                Files.deleteIfExists(p);
            }
        }
    }

    public boolean hasUnfinishedGame() {
        return Files.exists(currentBoardPath());
    }

    // Optional: enforce rule "incomplete folder is empty OR exactly 2 files"
    public boolean incompleteFolderLooksValid() throws IOException {
        Path dir = incompleteDir();
        if (!Files.exists(dir)) return true;

        List<Path> files;
        try (Stream<Path> stream = Files.list(dir)) {
            files = stream.filter(Files::isRegularFile).collect(Collectors.toList());
        }

        if (files.isEmpty()) return true;

        boolean hasBoard = Files.exists(currentBoardPath());
        boolean hasLog = Files.exists(currentLogPath());

        return hasBoard && hasLog && files.size() == 2;
    }

    // -------------------- DELETE GAME --------------------
    public void deleteGameFile(Path csvPath) throws IOException {
        Files.deleteIfExists(csvPath);
    }

    // FIXED: Added missing method
    public void deleteCurrentGame() throws IOException {
        Files.deleteIfExists(currentBoardPath());
        Files.deleteIfExists(currentLogPath());
    }

    // -------------------- SAVE GAME --------------------
    public Path saveGame(Board board, Difficulty difficulty, String fileName) throws IOException {
        if (!fileName.toLowerCase().endsWith(".csv")) fileName += ".csv";
        Path out = difficultyDir(difficulty).resolve(fileName);
        writeBoardCsv(board, out);
        return out;
    }

    // -------------------- CSV WRITER (NO BUFFER) --------------------
    private void writeBoardCsv(Board board, Path out) throws IOException {
        Files.createDirectories(out.getParent());

        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (c > 0) sb.append(',');
                sb.append(board.get(r, c));
            }
            sb.append('\n');
        }

        Files.writeString(
                out,
                sb.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }
}