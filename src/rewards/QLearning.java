package rewards;

import rewards.VisualizePath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

enum Operator{
    NORTH, SOUTH, EAST, WEST, PICKUP, DROPOFF;
}
public class QLearning {
    static char[][] pdWorld = new char[5][5];
    static int[][] counts = new int[5][5];
    static double[][][][] QTableExperiment1 = new double[5][5][2][6];
    static double[][][][] QTableExperiment2 = new double[5][5][2][6];
    static double[][][][] QTableExperiment3 = new double[5][5][2][6];
    static double leaningRate = 0.3;
    static double discountRate = 0.5;
    static State startState = new State(0, 4, 0);
    static Random rand = new Random();
    static int totalBlocksToBeDroppedExperiment;
    static int bankAccountExperiment;
    static int firstDropOffIteration;
    static int totalStepsToReachTerminal;
    static int firstDropOffIterationFlag;
    static List<Operator> directionsList = new ArrayList<Operator>();

    static int hasBlock;
    static State state; // Default state. We will set it to start state in setPdWorld()

    static VisualizePath visualizePath;

    public static void setPdWorld(int seed) {
        pdWorld[0][0] = 'P';
        pdWorld[2][2] = 'P';
        pdWorld[3][0] = 'P';
        pdWorld[4][4] = 'P';
        pdWorld[3][3] = 'D';
        pdWorld[4][0] = 'D';

        for (int i = 0; i < counts.length; i++)
            Arrays.fill(counts[i], -1);

        counts[0][0] = 4;
        counts[2][2] = 4;
        counts[3][0] = 4;
        counts[4][4] = 4;
        counts[3][3] = 0;
        counts[4][0] = 0;
        if (seed == 1)
        rand.setSeed(115);
        else
            rand.setSeed(2241);
        //rand.setSeed(21234);
        totalBlocksToBeDroppedExperiment = 16;

        bankAccountExperiment = 0;

        directionsList.clear();

        hasBlock = 0;
        state = startState;
        firstDropOffIterationFlag = 0;
        firstDropOffIteration = 0;
        totalStepsToReachTerminal = 0;

        visualizePath = new VisualizePath();
        visualizePath.setVisible(true);
    }

    public static int operatorValue(Operator operator){
        if (operator == Operator.NORTH)
            return 0;
        else if (operator == Operator.SOUTH)
            return 1;
        else if (operator == Operator.EAST)
            return 2;
        else if (operator == Operator.WEST)
            return 3;
        else if (operator == Operator.PICKUP)
            return 4;
        else
            return 5;

    }

    public static List<Operator> validOperators(State state){
        int xPos = state.Xcoordinate;
        int yPos = state.Ycoordinate;
        int hasBlock = state.blockStatus;
        List<Operator> validOperatorsList = new ArrayList<Operator>();

        if (xPos == 0 && yPos == 0){
            validOperatorsList.add(Operator.SOUTH);
            validOperatorsList.add(Operator.EAST);
            if (counts[xPos][yPos] > 0 && hasBlock == 0)
            validOperatorsList.add(Operator.PICKUP);
        }
        else if (xPos == 0 && yPos == 4){
            validOperatorsList.add(Operator.SOUTH);
            validOperatorsList.add(Operator.WEST);
        }
        else if (xPos == 4 && yPos == 0){
            validOperatorsList.add(Operator.NORTH);
            validOperatorsList.add(Operator.EAST);
            if (counts[xPos][yPos] < 8 && hasBlock == 1)
                validOperatorsList.add(Operator.DROPOFF);
        }
        else if (xPos == 4 && yPos == 4){
            validOperatorsList.add(Operator.NORTH);
            validOperatorsList.add(Operator.WEST);
            if (counts[xPos][yPos] > 0 && hasBlock == 0)
                validOperatorsList.add(Operator.PICKUP);
        }
        else if (xPos == 3 && yPos == 0){
            validOperatorsList.add(Operator.NORTH);
            validOperatorsList.add(Operator.SOUTH);
            validOperatorsList.add(Operator.EAST);
            if (counts[xPos][yPos] > 0 && hasBlock == 0)
                validOperatorsList.add(Operator.PICKUP);
        }
        else if (xPos == 2 && yPos == 2){
            validOperatorsList.add(Operator.NORTH);
            validOperatorsList.add(Operator.SOUTH);
            validOperatorsList.add(Operator.EAST);
            validOperatorsList.add(Operator.WEST);
            if (counts[xPos][yPos] > 0 && hasBlock == 0)
                validOperatorsList.add(Operator.PICKUP);
        }
        else if (xPos == 3 && yPos == 3){
            validOperatorsList.add(Operator.NORTH);
            validOperatorsList.add(Operator.SOUTH);
            validOperatorsList.add(Operator.EAST);
            validOperatorsList.add(Operator.WEST);
            if (counts[xPos][yPos] < 8 && hasBlock == 1)
                validOperatorsList.add(Operator.DROPOFF);
        }
        else if (xPos == 0){
            validOperatorsList.add(Operator.SOUTH);
            validOperatorsList.add(Operator.EAST);
            validOperatorsList.add(Operator.WEST);
        }
        else if (xPos == 4){
            validOperatorsList.add(Operator.NORTH);
            validOperatorsList.add(Operator.EAST);
            validOperatorsList.add(Operator.WEST);
        }
        else if (yPos == 0){
            validOperatorsList.add(Operator.NORTH);
            validOperatorsList.add(Operator.SOUTH);
            validOperatorsList.add(Operator.EAST);
        }
        else if (yPos == 4){
            validOperatorsList.add(Operator.NORTH);
            validOperatorsList.add(Operator.SOUTH);
            validOperatorsList.add(Operator.WEST);
        }
        else {
            validOperatorsList.add(Operator.NORTH);
            validOperatorsList.add(Operator.SOUTH);
            validOperatorsList.add(Operator.EAST);
            validOperatorsList.add(Operator.WEST);

        }
        return validOperatorsList;
    }

    public static State applyOperator(State state, Operator operator){
        int xPos = state.Xcoordinate;
        int yPos = state.Ycoordinate;
        int hasBlock = state.blockStatus;


        if (operator == Operator.NORTH){
            xPos--;
        }
        else if (operator == Operator.EAST){
            yPos++;
        }
        else if (operator == Operator.WEST){
            yPos--;
        }
        else if (operator == Operator.SOUTH){
            xPos++;
        }
        else if (operator == Operator.PICKUP){
            counts[xPos][yPos]--;
                hasBlock = 1;
        }
        else {
            counts[xPos][yPos]++;
                hasBlock = 0;
        }
        State newState = new State(xPos, yPos, hasBlock);
        return newState;
    }



    private static double getDiscountedReward(State state, int policy, int formula,  double[][][][] QTable){
        double reward = 1000;
        switch (formula){
            case 1:reward = getDiscountedRewardForQLearning(state, QTable);break;
            case 2:reward = getDiscountedRewardForSarsa(state, policy, QTable);break;
        }
        return reward;
    }
    private static double getDiscountedRewardForQLearning(State state, double[][][][] QTable) {
        List<Operator> operators = validOperators(state);
        double maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < operators.size(); i++){
            int operatorIndex = operatorValue(operators.get(i));
            maxValue = Math.max(maxValue, QTable[state.Xcoordinate][state.Ycoordinate][state.blockStatus][operatorIndex]);
        }
        return maxValue;
    }

    private static double getDiscountedRewardForSarsa(State state, int policy, double[][][][] QTable) {
        if(policy == 1){
            Operator direction = policyPRANDOM(state);
            int directionValue = operatorValue(direction);
            return QTable[state.Xcoordinate][state.Ycoordinate][state.blockStatus][directionValue];
        }
        else if (policy == 2){
            Operator direction = policyPEPLOIT(state, QTable);
            int directionValue = operatorValue(direction);
            return QTable[state.Xcoordinate][state.Ycoordinate][state.blockStatus][directionValue];
        }
        else {
            Operator direction = policyPGREEDY(state, QTable);
            int directionValue = operatorValue(direction);
            return QTable[state.Xcoordinate][state.Ycoordinate][state.blockStatus][directionValue];
        }
    }


    private static Operator policyPGREEDY(State state, double[][][][] QTable) {
        List<Operator> operators = validOperators(state);
        operators.remove(Operator.PICKUP);
        operators.remove(Operator.DROPOFF);
        //Operator direction = Operator.EAST;
        Operator direction = policyPRANDOM(state);
        Operator secondMaxDirection = policyPRANDOM(state);
        double maxValue = Integer.MIN_VALUE;
        double secondMaxValue = Integer.MIN_VALUE;
        for (int i = 0; i < operators.size(); i++){
            int operValue = operatorValue(operators.get(i));
            if (maxValue <= QTable[state.Xcoordinate][state.Ycoordinate][state.blockStatus][operValue]){
                secondMaxValue = maxValue;
                secondMaxDirection = direction;
                maxValue = QTable[state.Xcoordinate][state.Ycoordinate][state.blockStatus][operValue];
                direction = operators.get(i);
            }

        }
        if (maxValue == secondMaxValue){
            //Random r = new Random();
            int index = rand.nextInt(2);
            if (index == 0)
            return secondMaxDirection;
        }
        return direction;
    }

    private static Operator policyPEPLOIT(State state, double[][][][] QTable) {
        List<Operator> operators = validOperators(state);
        operators.remove(Operator.PICKUP);
        operators.remove(Operator.DROPOFF);
        double probability = (rand.nextInt(101))/100.0;
        //System.out.println(probability);
        if (probability <= 0.15)
            return policyPRANDOM(state);
        return policyPGREEDY(state, QTable);
    }

    private static Operator policyPRANDOM(State state) {
        List<Operator> operators = validOperators(state);
        operators.remove(Operator.PICKUP);
        operators.remove(Operator.DROPOFF);
        int index = rand.nextInt(operators.size());
        Operator direction = operators.get(index);
        return direction;
    }

    public static Boolean checkIfAllBlocksDropped(){
        if (totalBlocksToBeDroppedExperiment <= 0)
                return true;
        return false;
        }

    public static void performRandomPolicyForNIterations(int numOfIterations, int policy, int formula,  double[][][][] QTable, int seed){
        int directionIndex;
        double maxQvalue;
        int reward;


        for (int i = 0; i < numOfIterations; i++){
            if (checkIfAllBlocksDropped()){
                System.out.println("First Drop Off iteration  = "+firstDropOffIteration);
                System.out.println("Total Number of steps = "+totalStepsToReachTerminal);
                System.out.println("Bank Account  = "+bankAccountExperiment);
                System.out.println("***************************************");
                setPdWorld(seed);

            }
            List<Operator> operators = validOperators(state);
            if (operators.contains(Operator.PICKUP) && hasBlock == 0 &&  counts[state.Xcoordinate][state.Ycoordinate] > 0){
                totalStepsToReachTerminal++;
                directionsList.add(Operator.PICKUP);
                directionIndex = operatorValue(Operator.PICKUP);
                State nextState = applyOperator(state, Operator.PICKUP);
                maxQvalue = getDiscountedReward(nextState, policy, formula, QTable);
                reward = 12;
                bankAccountExperiment += reward;
                QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] = (1-leaningRate)*QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] + leaningRate*(reward + discountRate*maxQvalue);
                state = nextState;
                hasBlock = 1;
            }
            else if (operators.contains(Operator.DROPOFF) && hasBlock == 1 && counts[state.Xcoordinate][state.Ycoordinate] < 8){

                totalStepsToReachTerminal++;
                if (firstDropOffIterationFlag == 0){
                    firstDropOffIteration = totalStepsToReachTerminal;
                    firstDropOffIterationFlag = 1;
                }
                directionsList.add(Operator.DROPOFF);
                directionIndex = operatorValue(Operator.DROPOFF);
                State nextState = applyOperator(state, Operator.DROPOFF);
                maxQvalue = getDiscountedRewardForQLearning(nextState, QTable);
                reward = 12;
                bankAccountExperiment += reward;
                QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] = (1-leaningRate)*QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] + leaningRate*(reward + discountRate*maxQvalue);
                state = nextState;
                hasBlock = 0;
                totalBlocksToBeDroppedExperiment--;
            }
            else {
                totalStepsToReachTerminal++;
                Operator direction1 = policyPRANDOM(state);
                directionsList.add(direction1);
                directionIndex = operatorValue(direction1);
                State nextState = applyOperator(state, direction1);
                maxQvalue = getDiscountedRewardForQLearning(nextState, QTable);
                reward = -1;
                bankAccountExperiment += reward;
                QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] = (1-leaningRate)*QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] + leaningRate*(reward + discountRate*maxQvalue);
                state = nextState;

            }
        }
    }

    public static void performGreedyPolicyForNIterations(int numOfIterations, int policy, int formula,  double[][][][] QTable, int seed){
        int directionIndex;
        double maxQvalue;
        int reward;
        for (int i = 0; i < numOfIterations; i++){
            if (checkIfAllBlocksDropped()){
                System.out.println("First Drop Off iteration  = "+firstDropOffIteration);
                System.out.println("Total Number of steps = "+totalStepsToReachTerminal);
                System.out.println("Bank Account  = "+bankAccountExperiment);
                System.out.println("***************************************");
                setPdWorld(seed);
            }

            List<Operator> operators = validOperators(state);
            if (operators.contains(Operator.PICKUP) && hasBlock == 0 && counts[state.Xcoordinate][state.Ycoordinate] > 0){
                totalStepsToReachTerminal++;
                directionsList.add(Operator.PICKUP);
                directionIndex = operatorValue(Operator.PICKUP);
                State nextState = applyOperator(state, Operator.PICKUP);
                maxQvalue = getDiscountedReward(nextState, policy, formula, QTable);
                reward = 12;
                bankAccountExperiment += reward;
                QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] = (1-leaningRate)*QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] + leaningRate*(reward + discountRate*maxQvalue);
                state = nextState;
                hasBlock = 1;
            }
            else if (operators.contains(Operator.DROPOFF) && hasBlock == 1 && counts[state.Xcoordinate][state.Ycoordinate] < 8){
                totalStepsToReachTerminal++;
                if (firstDropOffIterationFlag == 0){
                    firstDropOffIteration = totalStepsToReachTerminal;
                    firstDropOffIterationFlag = 1;
                }
                directionsList.add(Operator.DROPOFF);
                directionIndex = operatorValue(Operator.DROPOFF);
                State nextState = applyOperator(state, Operator.DROPOFF);
                maxQvalue = getDiscountedRewardForQLearning(nextState, QTable);
                reward = 12;
                bankAccountExperiment += reward;
                QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] = (1-leaningRate)*QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] + leaningRate*(reward + discountRate*maxQvalue);
                state = nextState;
                hasBlock = 0;
                totalBlocksToBeDroppedExperiment--;
            }
            else {
                totalStepsToReachTerminal++;
                Operator direction1 = policyPGREEDY(state, QTable);
                directionsList.add(direction1);
                directionIndex = operatorValue(direction1);
                State nextState = applyOperator(state, direction1);
                maxQvalue = getDiscountedRewardForQLearning(nextState, QTable);
                reward = -1;
                bankAccountExperiment += reward;
                QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] = (1-leaningRate)*QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] + leaningRate*(reward + discountRate*maxQvalue);
                state = nextState;
            }
        }
    }

    public static void performExploitPolicyForNIterations(int numOfIterations, int policy, int formula,  double[][][][] QTable, int seed) {
        int directionIndex;
        double maxQvalue;
        int reward;
        for (int i = 0; i < numOfIterations; i++) {
            if (checkIfAllBlocksDropped()) {
                System.out.println("First Drop Off iteration  = "+firstDropOffIteration);
                System.out.println("Total Number of steps = "+totalStepsToReachTerminal);
                System.out.println("Bank Account  = "+bankAccountExperiment);
                System.out.println("***************************************");
                setPdWorld(seed);
            }

            List<Operator> operators = validOperators(state);
            if (operators.contains(Operator.PICKUP) && hasBlock == 0 &&  counts[state.Xcoordinate][state.Ycoordinate] > 0){
                totalStepsToReachTerminal++;
                directionsList.add(Operator.PICKUP);
                directionIndex = operatorValue(Operator.PICKUP);
                State nextState = applyOperator(state, Operator.PICKUP);
                maxQvalue = getDiscountedReward(nextState, policy, formula, QTable);
                reward = 12;
                bankAccountExperiment += reward;
                QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] = (1-leaningRate)*QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] + leaningRate*(reward + discountRate*maxQvalue);
                state = nextState;
                hasBlock = 1;
            }
            else if (operators.contains(Operator.DROPOFF) && hasBlock == 1 && counts[state.Xcoordinate][state.Ycoordinate] < 8){
                totalStepsToReachTerminal++;
                if (firstDropOffIterationFlag == 0){
                    firstDropOffIteration = totalStepsToReachTerminal;
                    firstDropOffIterationFlag = 1;
                }
                directionsList.add(Operator.DROPOFF);
                directionIndex = operatorValue(Operator.DROPOFF);
                State nextState = applyOperator(state, Operator.DROPOFF);
                maxQvalue = getDiscountedRewardForQLearning(nextState, QTable);
                reward = 12;
                bankAccountExperiment += reward;
                QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] = (1-leaningRate)*QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] + leaningRate*(reward + discountRate*maxQvalue);
                state = nextState;
                hasBlock = 0;
                totalBlocksToBeDroppedExperiment--;
            }
            else {
                totalStepsToReachTerminal++;
                Operator direction1 = policyPEPLOIT(state, QTable);
                directionsList.add(direction1);
                directionIndex = operatorValue(direction1);
                State nextState = applyOperator(state, direction1);
                maxQvalue = getDiscountedRewardForQLearning(nextState, QTable);
                reward = -1;
                bankAccountExperiment += reward;
                QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] = (1-leaningRate)*QTable[state.Xcoordinate][state.Ycoordinate][hasBlock][directionIndex] + leaningRate*(reward + discountRate*maxQvalue);
                state = nextState;
            }

        }
    }



    public static double[][][][] experiment1(int seed, int decision) throws IOException{
        setPdWorld(seed);
        System.out.println("RANDOM POLICY IN EXPERIMENT1");
        System.out.println("-----------------------------");
        System.out.println("-----------------------------");
        performRandomPolicyForNIterations(3000, 1, 1, QTableExperiment1, seed);
        String fileName1;
        String fileName2;
        if (seed == 1){
        fileName1 = "experiment1_after_3000_iterations_seed1.csv";
        fileName2 = "experiment1_after_6000_iterations_seed1.csv";
        }
        else {
            fileName1 = "experiment1_after_3000_iterations_seed2.csv";
            fileName2 = "experiment1_after_6000_iterations_seed2.csv";
        }

        writeQTableToFile(QTableExperiment1, fileName1);
        System.out.println("GREEDY POLICY IN EXPERIMENT1");
        System.out.println("-----------------------------");
        System.out.println("-----------------------------");
        performGreedyPolicyForNIterations(3000, 3, 1, QTableExperiment1, seed);
        writeQTableToFile(QTableExperiment1, fileName2);
        if (decision == 1)
        visualizePath.run();

        return QTableExperiment1;
    }

    private static void writeQTableToFile(double[][][][] qTable, String fileName) throws IOException{
        FileWriter fileWriter = new FileWriter(new File(fileName));
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                for (int k = 0; k < 2; k++){
                    for (int l = 0; l < 6; l++){
                        fileWriter.write(Double.toString(qTable[i][j][k][l]));
                        fileWriter.write(",");
                    }
                    fileWriter.write("\n");
                }
            }
        }
        fileWriter.flush();
    }


    public static double[][][][] experiment2(int seed, int decision) throws IOException{
        setPdWorld(seed);
        System.out.println("RANDOM POLICY IN EXPERIMENT2");
        System.out.println("-----------------------------");
        System.out.println("-----------------------------");
        performRandomPolicyForNIterations(200, 1, 1, QTableExperiment2, seed);
        String fileName1;
        String fileName2;
        if (seed == 1){
            fileName1 = "experiment2_after_200_iterations_seed1.csv";
            fileName2 = "experiment2_after_6000_iterations_seed1.csv";
        }
        else {
            fileName1 = "experiment2_after_200_iterations_seed2.csv";
            fileName2 = "experiment2_after_6000_iterations_seed2.csv";
        }
        writeQTableToFile(QTableExperiment2, fileName1);
        System.out.println("EXPLOIT POLICY IN EXPERIMENT2");
        System.out.println("-----------------------------");
        System.out.println("-----------------------------");
        performExploitPolicyForNIterations(5800, 2, 1, QTableExperiment2, seed);
        writeQTableToFile(QTableExperiment2, fileName2);
        if (decision == 1)
            visualizePath.run();
        return QTableExperiment2;
    }

    public static double[][][][] experiment3(int seed, int decision) throws IOException{
        setPdWorld(seed);
        System.out.println("RANDOM POLICY IN EXPERIMENT3");
        System.out.println("-----------------------------");
        System.out.println("-----------------------------");
        performRandomPolicyForNIterations(200, 1, 2, QTableExperiment3, seed);
        String fileName1;
        String fileName2;
        if (seed == 1){
            fileName1 = "experiment3_after_200_iterations_seed1.csv";
            fileName2 = "experiment3_after_6000_iterations_seed1.csv";
        }
        else {
            fileName1 = "experiment3_after_200_iterations_seed2.csv";
            fileName2 = "experiment3_after_6000_iterations_seed2.csv";
        }
        writeQTableToFile(QTableExperiment3, fileName1);
        System.out.println("EXPLOIT POLICY IN EXPERIMENT3");
        System.out.println("-----------------------------");
        System.out.println("-----------------------------");
        performExploitPolicyForNIterations(5800, 2, 2, QTableExperiment3, seed);
        writeQTableToFile(QTableExperiment3, fileName2);
        if (decision == 1)
            visualizePath.run();
        return QTableExperiment3;
    }

}
