package main;

import controller.StartupController;
import controller.GameController;
import catalog.GameCatalogue;
import driver.GameDriver;
import storage.GameStorage;
import verifier.BoardVerifier;
import solver.SudokuSolver;
import gui.facades.ViewFacade;
import gui.facades.ControllerFacade;
import gui.frames.StartupFrame;
import util.RandomPairs;


import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("🚀 Starting Sudoku Game...");
            
            // Setup storage with base directory
            GameStorage storage = new GameStorage(Paths.get("games"));
            
            // Setup verifier with validators
            BoardVerifier verifier = new BoardVerifier();
            
            // Setup solver
            SudokuSolver solver = new SudokuSolver(verifier);
            
            // Setup catalogue and driver
            GameCatalogue catalogue = new GameCatalogue(storage);
            GameDriver driver = new GameDriver(storage, verifier);
            
            // Setup controllers
            StartupController startupController = new StartupController(storage, catalogue, driver);
            GameController gameController = new GameController(storage, verifier);
            
            // Setup facades
            ViewFacade viewFacade = new ViewFacade(startupController, gameController, catalogue, driver, storage, verifier);
            ControllerFacade controllerFacade = new ControllerFacade(startupController, gameController, catalogue, driver, storage, verifier, solver);
            
            // Create and show startup frame
            StartupFrame startupFrame = new StartupFrame(startupController, gameController, viewFacade);
            startupFrame.show();
            
            System.out.println("✅ Application started successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to start application: " + e.getMessage());
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(
                null, 
                "Failed to start application:\n" + e.getMessage(), 
                "Startup Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }
}