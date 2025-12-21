package solver;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class AssignmentIterator implements Iterator<int[]> {
    private final int k;
    private final int[] digits;   // base-9 counter, each 0..8 -> value = +1
    private boolean hasMore = true;

    public AssignmentIterator(int k) {
        if (k <= 0) throw new IllegalArgumentException("k must be > 0");
        this.k = k;
        this.digits = new int[k];
    }

    @Override
    public boolean hasNext() {
        return hasMore;
    }

    @Override
    public int[] next() {
        if (!hasMore) throw new NoSuchElementException();

        int[] values = new int[k];
        for (int i = 0; i < k; i++) {
            values[i] = digits[i] + 1; // 1..9
        }

        increment();
        return values;
    }

    private void increment() {
        for (int i = k - 1; i >= 0; i--) {
            if (digits[i] < 8) {
                digits[i]++;
                return;
            } else {
                digits[i] = 0;
            }
        }
        hasMore = false;
    }
}
