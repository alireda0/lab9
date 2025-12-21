package solver;

import model.Board;
import model.VerificationResult;
import model.VerificationStatus;
import verifier.BoardVerifier;

import java.util.List;

public class SudokuSolver {

    private final BoardVerifier verifier;

    public SudokuSolver(BoardVerifier verifier) {
        this.verifier = verifier;
    }

    public Board solveIfExactlyFiveBlanks(Board board) {
        List<BlankCell> blanks = BlankFinder.findBlanks(board);

        if (blanks.size() != 5) {
            throw new IllegalArgumentException("Solve is allowed only when exactly 5 cells are empty.");
        }

        BoardFlyweight fly = new BoardFlyweight(board);
        AssignmentIterator it = new AssignmentIterator(blanks.size());

        while (it.hasNext()) {
            int[] candidate = it.next();

            fly.apply(blanks, candidate);

            VerificationResult res = verifier.verify(fly.getWorking());
            if (res.getStatus() == VerificationStatus.VALID) {
                return new Board(fly.getWorking()); // return a copy of the solved board
            }

            fly.clear(blanks);
        }

        return null; // no solution found within brute-force search
    }
}
