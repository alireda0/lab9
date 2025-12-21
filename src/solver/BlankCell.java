package solver;

public class BlankCell {
    private final int row; // 0..8
    private final int col; // 0..8

    public BlankCell(int row, int col) {
        if (row < 0 || row > 8 || col < 0 || col > 8) {
            throw new IllegalArgumentException("row/col must be 0..8");
        }
        this.row = row;
        this.col = col;
    }

    public int row() { return row; }
    public int col() { return col; }
}
