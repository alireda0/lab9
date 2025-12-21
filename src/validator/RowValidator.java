package validator;

import model.Board;
import model.Duplicate;

import java.util.ArrayList;
import java.util.List;

public class RowValidator {

    public List<Duplicate> validate(Board board) {
        List<Duplicate> duplicates = new ArrayList<>();

        for (int row = 0; row < 9; row++) {
            int[] freq = new int[10];

            @SuppressWarnings("unchecked")
            List<Integer>[] positions = (List<Integer>[]) new List[10];
            for (int i = 1; i <= 9; i++) positions[i] = new ArrayList<>();

            for (int col = 0; col < 9; col++) {
                int val = board.get(row, col);

                if (val == 0) continue; // empty cell
                if (val < 0 || val > 9) {
                    throw new IllegalArgumentException("Invalid value in board: " + val);
                }

                freq[val]++;
                positions[val].add(col + 1); // 1-based column index
            }

            for (int val = 1; val <= 9; val++) {
                if (freq[val] > 1) {
                    duplicates.add(new Duplicate("ROW", row + 1, val, positions[val]));
                }
            }
        }

        return duplicates;
    }
}
