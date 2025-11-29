/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modes;

import model.Board;
import model.Duplicate;
import validator.RowValidator;
import validator.ColumnValidator;
import validator.BoxValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ali
 */
public class Mode3 implements Mode {

    @Override
    public List<Duplicate> run(Board board) throws InterruptedException {

        // Thread-safe lists to collect results from each worker thread
        List<Duplicate> rowDup = Collections.synchronizedList(new ArrayList<>());
        List<Duplicate> colDup = Collections.synchronizedList(new ArrayList<>());
        List<Duplicate> boxDup = Collections.synchronizedList(new ArrayList<>());

        // Create threads
        Thread rowThread = new Thread(() -> {
            RowValidator rv = new RowValidator();
            rowDup.addAll(rv.validate(board));
        });

        Thread colThread = new Thread(() -> {
            ColumnValidator cv = new ColumnValidator();
            colDup.addAll(cv.validate(board));
        });

        Thread boxThread = new Thread(() -> {
            BoxValidator bv = new BoxValidator();
            boxDup.addAll(bv.validate(board));
        });

        // Start all threads
        rowThread.start();
        colThread.start();
        boxThread.start();

        // Wait for all to finish
        rowThread.join();
        colThread.join();
        boxThread.join();

        // Merge all results into one list
        List<Duplicate> all = new ArrayList<>();
        all.addAll(rowDup);
        all.addAll(colDup);
        all.addAll(boxDup);

        return all;
    }
}
