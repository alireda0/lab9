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
import java.util.List;
/**
 *
 * @author engom
 */
public class Mode0 implements Mode {
    @Override
    public List<Duplicate> run(Board board){
        List<Duplicate> all= new ArrayList<>();
        RowValidator rv= new RowValidator();
        ColumnValidator cv= new ColumnValidator();
        BoxValidator bv= new BoxValidator();
        all.addAll(rv.validate(board));
        all.addAll(cv.validate(board));
        all.addAll(bv.validate(board));
        return all;
    }
    
}
