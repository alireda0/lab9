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
public class ColumnValidator {
    public List<Duplicate> validate(Board board){
        List<Duplicate> duplicates= new ArrayList<>();
        for(int col=0;col<9;col++){
            int[] freq=new int[10];
            List<Integer>[] positions=new List[10];
            for(int i=1;i<10;i++){
                positions[i]= new ArrayList<>();
            }
            for (int row = 0; row < 9; row++) {
                int val = board.get(row, col);
                freq[val]++;
                positions[val].add(row + 1);
            }
            for(int val=1;val<10;val++){
                if(freq[val]>1){
                    duplicates.add(new Duplicate("COL",col+1,val,positions[val]));
                }
            }    
        }
        return duplicates;  
    }
    
}
