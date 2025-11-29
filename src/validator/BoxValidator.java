/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package validator;
import model.Board;
import model.Duplicate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author engom
 */
public class BoxValidator {
    public List<Duplicate> validate(Board board) {
        List<Duplicate> duplicates = new ArrayList<>();

        // boxRow and boxCol go from 0 to 2 → 3x3 grid of boxes
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {

                int[] freq = new int[10];
                List<Integer>[] positions = new List[10];
                for (int i = 1; i <= 9; i++) {
                    positions[i] = new ArrayList<>();
                }

                int boxIndex = boxRow * 3 + boxCol + 1; // 1..9

                // Scan the 3x3 cells in this box
                int localIndex = 0; // 0..8 inside the box
                for (int r = boxRow * 3; r < boxRow * 3 + 3; r++) {
                    for (int c = boxCol * 3; c < boxCol * 3 + 3; c++) {
                        int val = board.get(r, c);
                        freq[val]++;
                        positions[val].add(localIndex + 1); // 1..9 position in box
                        localIndex++;
                    }
                }

                // Check for duplicates in this box
                for (int val = 1; val <= 9; val++) {
                    if (freq[val] > 1) {
                        duplicates.add(
                            new Duplicate("BOX", boxIndex, val, positions[val])
                        );
                    }
                }
            }
        }

        return duplicates;
    }
    
}
