/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.lang.Integer;

/**
 *
 * @author engom
 */
public class Board {
    private final int[][] grid = new int[9][9];

    public Board() {
    }
    public static Board fromCSV(String path) throws FileNotFoundException{
        Board br= new Board();
        Scanner sc= new Scanner(new File(path));
        for(int row=0; row<9; row++){
            if (!sc.hasNextLine()) {
            throw new RuntimeException("CSV file does not contain 9 lines.");
        }
            String line=sc.nextLine();
            String[] parts=line.split(",");
            if(parts.length!= 9)
                throw new RuntimeException("Line must contain 9 comma separated values");
            for(int col=0; col<9; col++){
                br.grid[row][col]=Integer.parseInt(parts[col].trim());
            }
        }
        sc.close();
        return br;     
    }
    public int get(int row, int col) {
    return grid[row][col];
}
    
}
