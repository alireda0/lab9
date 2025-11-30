/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modes;
/**
 *
 * @author engom
 */
public class ModeFactory {
    public static Mode getMode(int mode){
        if(mode==0)
            return new Mode0();
        else if(mode==3)
            return new Mode3();
        else if(mode==27)
            return new Mode27();
        else throw new IllegalArgumentException("Unsupported mode: " + mode);
    }
}
