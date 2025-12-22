package verifier;

import model.Board;
import model.VerificationResult;
import model.VerificationStatus;
import model.Duplicate;

import java.util.ArrayList;
import java.util.List;

public class BoardVerifier {
    
    public VerificationResult verify(Board board) {
        List<Duplicate> allDuplicates = new ArrayList<>();
        
        // Check all rows
        for (int row = 0; row < 9; row++) {
            allDuplicates.addAll(checkRow(board, row));
        }
        
        // Check all columns
        for (int col = 0; col < 9; col++) {
            allDuplicates.addAll(checkColumn(board, col));
        }
        
        // Check all boxes
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                allDuplicates.addAll(checkBox(board, boxRow, boxCol));
            }
        }
        
        // Determine status
        if (!allDuplicates.isEmpty()) {
            return new VerificationResult(VerificationStatus.INVALID, allDuplicates);
        } else if (board.hasZero()) {
            return new VerificationResult(VerificationStatus.INCOMPLETE, allDuplicates);
        } else {
            return new VerificationResult(VerificationStatus.VALID, allDuplicates);
        }
    }
    
    private List<Duplicate> checkRow(Board board, int row) {
        List<Duplicate> duplicates = new ArrayList<>();
        int[] freq = new int[10];
        List<Integer>[] positions = new List[10];
        
        for (int i = 1; i <= 9; i++) positions[i] = new ArrayList<>();
        
        for (int col = 0; col < 9; col++) {
            int val = board.get(row, col);
            if (val != 0) {
                freq[val]++;
                positions[val].add(col + 1);
            }
        }
        
        for (int val = 1; val <= 9; val++) {
            if (freq[val] > 1) {
                duplicates.add(new Duplicate("ROW", row + 1, val, positions[val]));
            }
        }
        
        return duplicates;
    }
    
    private List<Duplicate> checkColumn(Board board, int col) {
        List<Duplicate> duplicates = new ArrayList<>();
        int[] freq = new int[10];
        List<Integer>[] positions = new List[10];
        
        for (int i = 1; i <= 9; i++) positions[i] = new ArrayList<>();
        
        for (int row = 0; row < 9; row++) {
            int val = board.get(row, col);
            if (val != 0) {
                freq[val]++;
                positions[val].add(row + 1);
            }
        }
        
        for (int val = 1; val <= 9; val++) {
            if (freq[val] > 1) {
                duplicates.add(new Duplicate("COL", col + 1, val, positions[val]));
            }
        }
        
        return duplicates;
    }
    
    private List<Duplicate> checkBox(Board board, int boxRow, int boxCol) {
        List<Duplicate> duplicates = new ArrayList<>();
        int[] freq = new int[10];
        List<Integer>[] positions = new List[10];
        
        for (int i = 1; i <= 9; i++) positions[i] = new ArrayList<>();
        
        int boxIndex = boxRow * 3 + boxCol + 1;
        int localPos = 0;
        
        for (int r = boxRow * 3; r < boxRow * 3 + 3; r++) {
            for (int c = boxCol * 3; c < boxCol * 3 + 3; c++) {
                localPos++;
                int val = board.get(r, c);
                if (val != 0) {
                    freq[val]++;
                    positions[val].add(localPos);
                }
            }
        }
        
        for (int val = 1; val <= 9; val++) {
            if (freq[val] > 1) {
                duplicates.add(new Duplicate("BOX", boxIndex, val, positions[val]));
            }
        }
        
        return duplicates;
    }
}