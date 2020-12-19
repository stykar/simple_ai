import java.io.*;
import java.util.*;

public class ask1 {
    private static int d = 3;
    private static int K = 4;
    private static int H1 = 7;
    private static int H2 = 4;
    private static boolean type;
    private static float[][] train_data;
    private static int[] train_data_cat;
    private static int rows = 3000;
    private static int columns = 3;
    private static float[][] input_weights;
    private static float[] input_values;
    private static float[][] H1_weights;
    private static float[] H1_values;
    private static float[][] H2_weights;
    private static float[] H2_values;
    private static float[][] output_weights;
    private static float[] output_values;
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
        forwardPass(d,K);
    }

    public static void initializeWeights(){
        input_weights = new float[d][3];
        input_values = new float[d+1];
        input_values[0] = 1f;
        H1_weights = new float[H1][d+1];
        H1_values = new float[H1+1];
        H1_values[0] = 1f;
        H2_weights = new float[H2][H1+1];
        H2_values = new float[H2+1];
        H2_values[0] = 1f;
        output_weights = new float[K][H2+1];
        output_values = new float[K];

        Random rand = new Random();

        for(int i=0; i<d; i++){
            for(int j=0; j<3 ;j++){
                input_weights[i][j] = rand.nextFloat() * 2 - 1;
            }
        }

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

    public static float calculateTotalInput(float[][] p1, float[][] p2, int num){
        float res = 0;
        int r = p1.length;
        int col = p1[0].length;
        for(int i=0; i<r ;i++){
            for(int j=0; j<col; j++){
                res += p1[i][j] * p2[num][j];
            }
        }
        return res;
    }

    public static float[] forwardPass(int d, int K){
        for(int l=0; l<d; l++){
            for(int i=0; i<rows ;i++){
                for(int j=0; j<columns; j++){
                    input_values[l] += train_data[i][j] * input_weights[l][j];
                }
            }
        }

        for(int l=0; l<H1; l++){
            for(int i=0; i<d+1 ;i++){
                H1_values[l] += input_values[i] * H1_weights[l][i];
            }
        }

        for(int l=0; l<H2; l++){
            for(int i=0; i<H1+1 ;i++){
                H2_values[l] += H1_values[i] * H2_weights[l][i];
            }
        }

        for(int l=0; l<K; l++){
            for(int i=0; i<H2+1 ;i++){
                output_values[l] += H2_values[i] * output_weights[l][i];
            }
        }

        System.out.println("==input values==");
        
        for(int l=0; l<d; l++){
            System.out.println(input_values[l]);
        }
        System.out.println("==first hidden==");
        
        for(int l=0; l<H1; l++){
            System.out.println(H1_values[l]);
        }
        System.out.println("==second hidden==");

        for(int l=0; l<H2; l++){
            System.out.println(H2_values[l]);
        }
        System.out.println("==output values==");

        for(int l=0; l<K; l++){
            System.out.println(output_values[l]);
        }

        return output_values;
    }

    public double sigmoid(double x) {
		return 1 / (1 + Math.exp(-x));
    }
    
    public double tanh(double x){
        return (Math.exp(x) - Math.exp(-x))/(Math.exp(x) - Math.exp(-x));
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