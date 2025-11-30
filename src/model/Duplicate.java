/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.util.List;
/**
 *
 * @author engom
 */
public class Duplicate {
    private final String type;
    private final int index;
    private final int value;
    private final List<Integer> positions;
    public Duplicate(String type, int index, int value, List<Integer> positions) {
        this.type = type;
        this.index = index;
        this.value = value;
        this.positions = positions;
    }
    public String getType() {
        return type;
    }
    public int getIndex() {
        return index;
    }
    public int getValue() {
        return value;
    }
    public List<Integer> getPositions() {
        return positions;
    }
    @Override
    public String toString() {
        return String.format("%s %d, #%d, %s", type, index, value, positions);
    } 
}
