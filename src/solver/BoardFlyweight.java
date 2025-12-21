package solver;

import model.Board;

import java.util.List;

public class BoardFlyweight {
    private final Board working;

    public BoardFlyweight(Board base) {
        this.working = new Board(base); // copy once
    }

    public Board getWorking() {
        return working;
    }

    public void apply(List<BlankCell> blanks, int[] values) {
        if (blanks.size() != values.length) {
            throw new IllegalArgumentException("blanks and values length mismatch");
        }
        for (int i = 0; i < blanks.size(); i++) {
            BlankCell bc = blanks.get(i);
            working.set(bc.row(), bc.col(), values[i]);
        }
    }

    public void clear(List<BlankCell> blanks) {
        for (BlankCell bc : blanks) {
            working.set(bc.row(), bc.col(), 0);
        }
    }
}
