package solver;

import model.Board;

import java.util.ArrayList;
import java.util.List;

public class BlankFinder {

    public static List<BlankCell> findBlanks(Board board) {
        List<BlankCell> blanks = new ArrayList<>();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board.get(r, c) == 0) {
                    blanks.add(new BlankCell(r, c));
                }
            }
        }
        return blanks;
    }
}
