package rewards;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class VisualizePaths extends JFrame {


    private final List<Integer> path = new ArrayList<Integer>();
    private int pathIndex;

    public VisualizePaths() {
        setTitle("Agent Movement");
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//DepthFirst.searchPath(maze, 1, 1, path);
//pathIndex = path.size() - 2;
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.translate(50, 50);

        char[][] pdWorld = new char[5][5];
        pdWorld[0][0] = 'P';
        pdWorld[2][2] = 'P';
        pdWorld[3][0] = 'P';
        pdWorld[4][4] = 'P';
        pdWorld[3][3] = 'D';
        pdWorld[4][0] = 'D';

// draw the PD World
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                Color color;
                switch (pdWorld[row][col]) {
                    case 'P' : color = Color.GREEN; break;
                    case 'D' : color = Color.RED; break;
                    default : color = Color.WHITE;
                }
                g.setColor(color);
                g.fillRect(30 * col, 30 * row, 30, 30);
                g.setColor(Color.BLACK);
                g.drawRect(30 * col, 30 * row, 30, 30);
            }
        }
    }

    public void run() {
        int n = 0;
        while(n!= 1){
            n++;
            int color = n%10;
            //maze[1][1]= color;
            System.out.print(n);


            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }


    public static void main(String[] args) {

        VisualizePaths view = new VisualizePaths();
        view.setVisible(true);
        view.run();
    }



}
