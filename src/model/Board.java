package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Board {
    private final int[][] grid = new int[9][9];

    public Board() {}

    // copy constructor
    public Board(Board other) {
        for (int r = 0; r < 9; r++) {
            System.arraycopy(other.grid[r], 0, this.grid[r], 0, 9);
        }
    }

    public static Board fromCSV(String path) throws FileNotFoundException {
        Board br = new Board();
        try (Scanner sc = new Scanner(new File(path))) {

            for (int row = 0; row < 9; row++) {
                if (!sc.hasNextLine()) {
                    throw new IllegalArgumentException("CSV file does not contain 9 lines.");
                }

                String line = sc.nextLine().trim();
                String[] parts = line.split(",");

                if (parts.length != 9) {
                    throw new IllegalArgumentException("Line " + (row + 1) + " must contain 9 comma-separated values.");
                }

                for (int col = 0; col < 9; col++) {
                    int val;
                    try {
                        val = Integer.parseInt(parts[col].trim());
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "Invalid number at row " + (row + 1) + ", col " + (col + 1) + ": " + parts[col].trim()
                        );
                    }

                    if (val < 0 || val > 9) {
                        throw new IllegalArgumentException(
                                "Value out of range (0..9) at row " + (row + 1) + ", col " + (col + 1) + ": " + val
                        );
                    }

                    br.grid[row][col] = val;
                }
            }
        }
        return br;
    }

    public int get(int row, int col) {
        return grid[row][col];
    }
    public void set(int row, int col, int value) {
        if (row < 0 || row > 8 || col < 0 || col > 8) {
            throw new IllegalArgumentException("Row/Col out of range.");
        }
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException("Value out of range (0..9).");
        }
        grid[row][col] = value;
    }

    public boolean hasZero() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid[r][c] == 0) return true;
            }
        }
        return false;
    }
    public int countZeros() {
    int cnt = 0;
    for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
            if (grid[r][c] == 0) cnt++;
        }
    }
    return cnt;
}
}
