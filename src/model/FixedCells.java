package model;

public class FixedCells {

    private final boolean[][] fixed = new boolean[9][9];

    public FixedCells(Board board) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                fixed[r][c] = board.get(r, c) != 0;
            }
        }
    }

    public boolean isFixed(int row1, int col1) {
        int r = row1 - 1;
        int c = col1 - 1;
        return fixed[r][c];
    }
}
