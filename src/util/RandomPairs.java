package util;

import java.util.Random;

public class RandomPairs {
    private final int[] order;
    private int idx = 0;

    public RandomPairs(long seed) {
        order = new int[81];
        for (int i = 0; i < 81; i++) order[i] = i;

        Random rnd = new Random(seed);

        // Fisher-Yates shuffle
        for (int i = 80; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = order[i];
            order[i] = order[j];
            order[j] = tmp;
        }
    }

    // returns {row, col}
    public int[] next() {
        if (idx >= 81) throw new IllegalStateException("No more pairs left.");
        int pos = order[idx++];
        return new int[]{pos / 9, pos % 9};
    }
}
