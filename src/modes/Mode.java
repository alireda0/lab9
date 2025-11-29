/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modes;
import model.Board;
import model.Duplicate;
import java.util.List;


/**
 *
 * @author engom
 */
public interface Mode {
    List<Duplicate> run(Board board) throws InterruptedException;
    
}
