package validator;

import model.Board;
import model.Duplicate;

import java.util.ArrayList;
import java.util.List;

public class BoxValidator {

    public List<Duplicate> validate(Board board) {
        List<Duplicate> duplicates = new ArrayList<>();

        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                int[] freq = new int[10];

                @SuppressWarnings("unchecked")
                List<Integer>[] positions = (List<Integer>[]) new List[10];
                for (int i = 1; i <= 9; i++) positions[i] = new ArrayList<>();

                int boxIndex = boxRow * 3 + boxCol + 1; // 1..9
                int localIndex = 0; // 0..8 inside box

                for (int r = boxRow * 3; r < boxRow * 3 + 3; r++) {
                    for (int c = boxCol * 3; c < boxCol * 3 + 3; c++) {
                        int val = board.get(r, c);

                        localIndex++;

                        if (val == 0) continue;
                        if (val < 0 || val > 9) {
                            throw new IllegalArgumentException("Invalid value in board: " + val);
                        }

                        freq[val]++;
                        positions[val].add(localIndex); // 1..9 local position in the box
                    }
                }

                for (int val = 1; val <= 9; val++) {
                    if (freq[val] > 1) {
                        duplicates.add(new Duplicate("BOX", boxIndex, val, positions[val]));
                    }
                }
            }
        }

        return duplicates;
    }
}
