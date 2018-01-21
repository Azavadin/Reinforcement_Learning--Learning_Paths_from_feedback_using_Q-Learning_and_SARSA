package rewards;

import javax.swing.*;
import java.awt.*;;

public class Qcell extends JFrame{
    public static final int SIZE = 4;
    protected void frameInit(){
        super.frameInit();
        setLayout(new GridLayout(2,2));
        Cell cell = new Cell(0);
        getContentPane().add(cell);
    }
    public static void main(String args[]){
        JFrame frame = new TicTacToeFrame();
        frame.setSize(150, 150);
        frame.setVisible(true);
    }

}