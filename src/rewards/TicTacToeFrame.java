package rewards;

import javax.swing.*;
import java.awt.*;;

public class TicTacToeFrame extends JFrame{
    public static final int SIZE = 5;
    protected void frameInit(){
        super.frameInit();
        setLayout(new GridLayout(5,5));
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                Qcell cell = new Qcell();
                getContentPane().add(cell);
            }
        }
    }
    public static void main(String args[]){
        JFrame frame = new TicTacToeFrame();
        frame.setSize(150, 150);
        frame.setVisible(true);
    }
}