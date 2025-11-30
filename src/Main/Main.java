package main;

import model.Board;
import model.Duplicate;
import modes.Mode;
import modes.ModeFactory;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        try {
            // ----- 1. CHECK CSV ARGUMENT -----
            if (args.length < 1) {
                System.out.println("Usage: java -jar app.jar <csv-file-path>");
                return;
            }
            
            String csvPath = args[0];
            
            // ----- 2. READ MODE FROM USER -----
            System.out.print("Choose mode (0 = sequential, 3 = 3-threads, 27 = 27-threads): ");
            
            if (!input.hasNextInt()) {
                System.out.println("Error: Mode must be a number!");
                return;
            }
            
            int mode = input.nextInt();
            
            if (mode != 0 && mode != 3 && mode != 27) {
                System.out.println("Error: Invalid mode. Only 0, 3, or 27 are allowed.");
                return;
            }
            
            // ----- 3. LOAD BOARD FROM CSV -----
            System.out.println("\nLoading Sudoku board from: " + csvPath);
            Board board = Board.fromCSV(csvPath);
            System.out.println("Board loaded successfully!");
            
            // ----- 4. GET MODE USING FACTORY PATTERN -----
            Mode modeRunner = ModeFactory.getMode(mode);
            
            // ----- 5. RUN VALIDATION -----
            System.out.println("Running validation in mode " + mode + "...\n");
            List<Duplicate> duplicates = modeRunner.run(board);
            
            // ----- 6. DISPLAY RESULTS -----
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
        } finally {
            input.close();
        }
    }
}