package main;

import model.Board;
import model.Duplicate;
import modes.Mode;
import modes.ModeFactory;

import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // ----- 1. CHECK ARGUMENTS -----
            if (args.length < 2) {
                System.out.println("Usage: java -jar app.jar <csv-file-path> <mode>");
                System.out.println("Modes: 0 = sequential, 3 = 3-threads, 27 = 27-threads");
                return;
            }
            
            String csvPath = args[0];  // First argument: CSV file
            int mode;
            
            try {
                mode = Integer.parseInt(args[1]);  // Second argument: mode
            } catch (NumberFormatException e) {
                System.out.println("Error: Mode must be a number (0, 3, or 27)");
                return;
            }
            
            if (mode != 0 && mode != 3 && mode != 27) {
                System.out.println("Error: Invalid mode. Only 0, 3, or 27 are allowed.");
                return;
            }
            
            // ----- 2. LOAD BOARD FROM CSV -----
            System.out.println("Loading Sudoku board from: " + csvPath);
            Board board = Board.fromCSV(csvPath);
            System.out.println("Board loaded successfully!");
            
            // ----- 3. GET MODE USING FACTORY PATTERN -----
            Mode modeRunner = ModeFactory.getMode(mode);
            
            // ----- 4. RUN VALIDATION WITH TIMING -----
            System.out.println("Running validation in mode " + mode + "...\n");
            
            long startTime = System.nanoTime();
            List<Duplicate> duplicates = modeRunner.run(board);
            long endTime = System.nanoTime();
            
            long executionTimeNano = endTime - startTime;
            double executionTimeMs = executionTimeNano / 1_000_000.0;
            
            // ----- 5. DISPLAY RESULTS -----
            if (duplicates.isEmpty()) {
                System.out.println("VALID");
            } else {
                System.out.println("INVALID");
                
                // Print ROWs
                for (int i = 0; i < duplicates.size(); i++) {
                    if (duplicates.get(i).getType().equals("ROW")) {
                        System.out.println(duplicates.get(i));
                    }
                }
                
                // Separator
                System.out.println("------------------------------------------");
                
                // Print COLs
                for (int i = 0; i < duplicates.size(); i++) {
                    if (duplicates.get(i).getType().equals("COL")) {
                        System.out.println(duplicates.get(i));
                    }
                }
                
                // Separator
                System.out.println("------------------------------------------");
                
                // Print BOXes
                for (int i = 0; i < duplicates.size(); i++) {
                    if (duplicates.get(i).getType().equals("BOX")) {
                        System.out.println(duplicates.get(i));
                    }
                }
            }
            
            // ----- 6. DISPLAY PERFORMANCE METRICS -----
            System.out.println("\n==========================================");
            System.out.println("PERFORMANCE METRICS");
            System.out.println("==========================================");
            System.out.println("Mode: " + mode);
            System.out.println("Execution Time: " + String.format("%.4f", executionTimeMs) + " ms");
            System.out.println("Execution Time: " + executionTimeNano + " ns");
            System.out.println("==========================================");
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: CSV file not found at the specified path!");
        } catch (NumberFormatException e) {
            System.out.println("Error: CSV file contains invalid number format!");
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Error: Thread execution was interrupted!");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Unexpected error occurred!");
            e.printStackTrace();
        }
    }
}