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
public class RowValidator {
  public List<Duplicate> validate(Board board) {
        List<Duplicate> duplicates = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            int[] freq = new int[10];  // values 1–9 only
            List<Integer>[] positions = new List[10];
            for (int i = 1; i <= 9; i++) {
                positions[i] = new ArrayList<>();
            }
            for (int col = 0; col < 9; col++) {
                int val = board.get(row, col);
                freq[val]++;
                positions[val].add(col + 1);   // store 1-based col index
            }
            // Now find duplicates
            for (int val = 1; val <= 9; val++) {
                if (freq[val] > 1) {
                    duplicates.add(
                        new Duplicate("ROW", row + 1, val, positions[val])
                    );
                }
            }
        }
        return duplicates;
    }  
}
