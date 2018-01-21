package rewards;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VisualizePath extends JFrame {
    char[][] pdWorld = new char[5][5];
    char[][] pdWorldDuplicate;
    static int agentXCoordinate = 0;
    static int agentYCoordinate = 4;
    int hasBlock = 0;
    public VisualizePath(){

        // draw the PD World
        pdWorld[0][0] = 'P';
        pdWorld[2][2] = 'P';
        pdWorld[3][0] = 'P';
        pdWorld[4][4] = 'P';
        pdWorld[3][3] = 'D';
        pdWorld[4][0] = 'D';
        pdWorld[0][4] = 'A';
        pdWorldDuplicate = pdWorld;
        setTitle("Visualizing the Agent Path");
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label1 = new JLabel("Test");
        label1.setText("Label Text");
    }

    public void resetPickupDropoffColors(){
        pdWorld[0][0] = 'P';
        pdWorld[2][2] = 'P';
        pdWorld[3][0] = 'P';
        pdWorld[4][4] = 'P';
        pdWorld[3][3] = 'D';
        pdWorld[4][0] = 'D';
    }



    public void paint(Graphics g) {
        super.paint(g);
        g.translate(50, 50);
        //System.out.println("X coordinate = "+agentXCoordinate+" Y coordinate = "+agentYCoordinate);
        pdWorld[agentXCoordinate][agentYCoordinate] = 'A';

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                Color color;
                switch (pdWorld[row][col]) {
                    case 'R' : color = Color.WHITE; break;
                    case 'A' : color = Color.BLACK; break;
                    case 'P' : color = Color.GREEN; break;
                    case 'D' : color = Color.RED; break;
                    default : color = Color.WHITE;
                }
                if (pdWorld[row][col] == 'A' && hasBlock == 1)
                    color = Color.BLUE;
                g.setColor(color);
                g.fillRect(30 * col, 30 * row, 30, 30);
                //g.setColor(Color.BLACK);
                g.drawRect(30 * col, 30 * row, 30, 30);
            }
        }
        pdWorld[agentXCoordinate][agentYCoordinate] = 'R';
        resetPickupDropoffColors();
    }


    public void run() {
        int iterationsCount = QLearning.directionsList.size();
        int iteration = 0;
        System.out.println(QLearning.directionsList);
        while(iteration < iterationsCount){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            applyOperator(QLearning.directionsList.get(iteration));
            //System.out.println(QLearning.directionsList.get(iteration));
            repaint();
            iteration++;
        }
    }

    private void applyOperator(Operator operator){

        if (operator == Operator.NORTH)
            agentXCoordinate--;
        else if (operator == Operator.EAST)
            agentYCoordinate++;
        else if (operator == Operator.WEST)
            agentYCoordinate--;
        else if (operator == Operator.SOUTH)
            agentXCoordinate++;
        else if (operator == Operator.PICKUP)
            hasBlock = 1;
        else
            hasBlock = 0;
    }

}
