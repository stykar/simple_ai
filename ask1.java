import java.io.*;
import java.util.*;

public class ask1 {
    private static int d = 2;
    private static int K = 4;
    private static int H1 = 7;
    private static int H2 = 4;
    private static boolean type;
    private static float[][] train_data;
    private static int[] train_data_cat;
    private static int rows = 3000;
    private static int columns = 3;
    private static float[][] H1_weights;
    private static float[][] H1_values;
    private static float[][] H2_weights;
    private static float[][] H2_values;
    private static float[][] output_weights;
    private static float[][] output_values;
    private static float learning_rate = 0.1f;
    private static float min_error = 0.001f;
    private static float previous_epoch_error;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(new BufferedReader(new FileReader("./s1_train.txt")));
        train_data = new float[rows][columns];
        train_data_cat = new int[rows];



        while (sc.hasNextLine()) {
            for (int i = 0; i < train_data.length; i++) {
                String[] line = sc.nextLine().split(" ");
                train_data[i][0] = 1;
                for (int j = 1; j < 3; j++) {
                    train_data[i][j] = Float.parseFloat(line[j-1]);
                }
                train_data_cat[i] = Integer.parseInt((line[2].substring(line[2].length() - 1)));
            }
        }
        
        sc.close();
        initializeWeights();
        for(int i=0; i<3; i++){
            forwardPass(train_data[i],d, K, i);
        }
    }

    public static void initializeWeights(){
        H1_weights = new float[H1][d+1];
        H1_values = new float[H1+1][rows];
        H2_weights = new float[H2][H1+1];
        H2_values = new float[H2+1][rows];
        output_weights = new float[K][H2+1];
        output_values = new float[K][rows];

        Random rand = new Random();

        for(int i=0; i<H1; i++){
            for(int j=0; j<d+1; j++){
                H1_weights[i][j] = rand.nextFloat() * 2 - 1;
            }
        }

        for(int i=0; i<H2; i++){
            for(int j=0; j<H1+1; j++){
                H2_weights[i][j] = rand.nextFloat() * 2 - 1;
            }
        }

        for(int i=0; i<K; i++){
            for(int j=0; j<H2+1; j++){
                output_weights[i][j] = rand.nextFloat() * 2 - 1;
            }
        }
    }

    public static void forwardPass(float[] x, int d, int K, int inp){
        float sum = 0;
        for(int i=0; i<H1; i++){
            for(int j=0; j<d; j++){
                sum += x[j+1] * H1_weights[i][j];
            }
            sum += H1_weights[0][inp];
            H1_values[i][inp] = tanh(sum);
            sum = 0;
        }

        for(int i=0; i<H2; i++){
            for(int j=0; j<H1; j++){
                sum += H1_values[j+1][inp] * H2_weights[i][j];
            }
            sum += H2_weights[0][inp];
            H2_values[i][inp] = sigmoid(sum);
            sum = 0;
        }

        for(int i=0; i<K; i++){
            for(int j=0; j<H2; j++){
                sum += H2_values[j+1][inp] * output_weights[i][j];
            }
            sum += H2_values[0][inp];
            output_values[i][inp] = sigmoid(sum);
            System.out.println(output_values[i][inp]);
            sum = 0;
        }
        System.out.println("--------");

        //calculate total error
        //(output kathe fora - desired output)^2
    }

    public static float sigmoid(float x) {
		return (float)(1 / (1 + Math.exp(-x)));
    }
    
    public static float tanh(float x){
        return (float)((Math.exp(x) - Math.exp(-x))/(Math.exp(x) + Math.exp(-x)));
    }

    public static float tanh2(float x){
        return (float)((2/1+Math.exp(-2*x)-1));
    }

    public static void backprop(int d, int K){

    }
}


    /*
    public double[] calculateOutput(double input[], double weights[][], double bias[]) {
        double output[] = new double[weights[0].length];
        for (int prevLayer = 0; prevLayer < weights.length; prevLayer++)
            for (int currLayer = 0; currLayer < weights[prevLayer].length; currLayer++)
                output[currLayer] += weights[prevLayer][currLayer] * input[prevLayer];
        for (int currLayer = 0; currLayer < weights[0].length; currLayer++)
            output[currLayer] = sigmoid(output[currLayer] + bias[currLayer]);
        return output;
    }*/