package controller;

import logging.MoveLog;
import logging.MoveRecord;
import model.Board;
import model.VerificationResult;
import solver.SudokuSolver;
import storage.GameStorage;
import verifier.BoardVerifier;

import java.io.IOException;

public class GameController {

    private final GameStorage storage;
    private final BoardVerifier verifier;
    private final MoveLog moveLog;
    private final SudokuSolver solver;

    public GameController(GameStorage storage, BoardVerifier verifier) {
        this.storage = storage;
        this.verifier = verifier;
        this.moveLog = new MoveLog(storage.currentLogPath());
        this.solver = new SudokuSolver(verifier);
    }

    public void startTrackingNewOrLoadedGame(Board board) throws IOException {
        storage.ensureFolderStructure();
        storage.clearIncompleteFolder();
        storage.saveCurrentBoard(board);
        moveLog.ensureExists();
    }

    public void resumeTrackingExistingGame() throws IOException {
        storage.ensureFolderStructure();
        moveLog.ensureExists();
    }

    public void applyMove(Board board, int row1, int col1, int newVal) throws IOException {
        if (row1 < 1 || row1 > 9 || col1 < 1 || col1 > 9) {
            throw new IllegalArgumentException("Row/Col must be 1..9");
        }
        if (newVal < 0 || newVal > 9) {
            throw new IllegalArgumentException("Value must be 0..9");
        }

        int r = row1 - 1;
        int c = col1 - 1;

        int prev = board.get(r, c);
        if (prev == newVal) return;

        board.set(r, c, newVal);

        MoveRecord rec = new MoveRecord(row1, col1, newVal, prev);
        moveLog.append(rec);

        storage.saveCurrentBoard(board);
    }

    public void undo(Board board) throws IOException {
        MoveRecord last = moveLog.popLast();

        int r = last.row1() - 1;
        int c = last.col1() - 1;

        board.set(r, c, last.prevVal());

        storage.saveCurrentBoard(board);
    }

    public VerificationResult verify(Board board) {
        return verifier.verify(board);
    }

    public boolean canSolve(Board board) {
        return board.countZeros() == 5;
    }

    public Board solve(Board board) throws IOException {
        if (!canSolve(board)) {
            throw new IllegalStateException("Solve is only allowed when exactly 5 cells are empty.");
        }

        Board solved = solver.solveIfExactlyFiveBlanks(board);
        if (solved == null) {
            return null;
        }

        storage.saveCurrentBoard(solved);
        return solved;
    }

    public void clearSolvedGameIfValid(VerificationResult result) throws IOException {
        if (result != null && result.isValid()) {
            storage.deleteCurrentGame();
        }
    }
}
