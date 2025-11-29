package modes;

import model.Board;
import model.Duplicate;
import validator.RowValidator;
import validator.ColumnValidator;
import validator.BoxValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mode27 implements Mode {

    @Override
    public List<Duplicate> run(Board board) throws InterruptedException {

        // Thread-safe list for collecting ALL duplicates
        List<Duplicate> all = Collections.synchronizedList(new ArrayList<>());

        List<Thread> threads = new ArrayList<>();

        RowValidator rv = new RowValidator();
        ColumnValidator cv = new ColumnValidator();
        BoxValidator bv = new BoxValidator();

        // ---- 9 ROW THREADS ----
        for (int row = 1; row <= 9; row++) {
            int r = row;
            Thread t = new Thread(() -> {
                List<Duplicate> result = rv.validate(board);
                for (Duplicate d : result) {
                    if (d.getType().equals("ROW") && d.getIndex() == r) {
                        all.add(d);
                    }
                }
            });
            threads.add(t);
        }

        // ---- 9 COLUMN THREADS ----
        for (int col = 1; col <= 9; col++) {
            int c = col;
            Thread t = new Thread(() -> {
                List<Duplicate> result = cv.validate(board);
                for (Duplicate d : result) {
                    if (d.getType().equals("COL") && d.getIndex() == c) {
                        all.add(d);
                    }
                }
            });
            threads.add(t);
        }

        // ---- 9 BOX THREADS ----
        for (int box = 1; box <= 9; box++) {
            int b = box;
            Thread t = new Thread(() -> {
                List<Duplicate> result = bv.validate(board);
                for (Duplicate d : result) {
                    if (d.getType().equals("BOX") && d.getIndex() == b) {
                        all.add(d);
                    }
                }
            });
            threads.add(t);
        }

        // ---- START ALL THREADS ----
        for (Thread t : threads) t.start();

        // ---- WAIT FOR ALL THREADS ----
        for (Thread t : threads) t.join();

        return all;
    }
}
