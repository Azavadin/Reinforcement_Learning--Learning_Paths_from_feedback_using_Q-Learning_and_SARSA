package rewards;

import java.io.IOException;

public class QLearningDriver {
    public static void main(String args[]) throws IOException{
        int experimentNumber = Integer.parseInt(args[0]);
        int seed = Integer.parseInt(args[1]);
        int decision = Integer.parseInt(args[2]);
        QLearning ql = new QLearning();
        switch (experimentNumber){
            case 1 :
                ql.experiment1(seed, decision);
                break;
            case 2 :
                ql.experiment2(seed, decision);
                break;
            case 3 :
                ql.experiment3(seed, decision);
                break;
        }
    }
}
