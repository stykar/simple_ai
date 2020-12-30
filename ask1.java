import java.io.*;
import java.util.*;

public class ask1 {
    private static int d = 2;
    private static int K = 4;
    private static int H1 = 7;
    private static int H2 = 4;
    private static int B = 50;
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
    private static float[] delta_out;
    private static float[] delta_hidden;
    private static float learning_rate = 0.1f;
    private static float min_error = 0.001f;
    private static float previous_epoch_error;

    private static Hashtable<Integer, float[][]> weights;
    private static Hashtable<Integer, float[][]> values;
    private static Hashtable<Integer, Integer> layerSize;
    private static Hashtable<Integer, float[][]> weight_derivatives;
    


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
        for(int i=0; i<rows; i++){
            forwardPass(train_data[i],d, K, i);
            for(int j=2; j>=0; j--){
                backprop(j, i);
            }
            if(i % B == 0){
                gradientDescent(i - B, i);
            }
        }
    }

    public static void initializeWeights(){
        H1_weights = new float[H1][d+1];
        H1_values = new float[rows][H1+1];
        H2_weights = new float[H2][H1+1];
        H2_values = new float[rows][H2+1];
        output_weights = new float[K][H2+1];
        output_values = new float[rows][K];

        weights = new Hashtable<Integer, float[][]>();
        weights.put(0, H1_weights);
        weights.put(1, H2_weights);
        weights.put(2, output_weights);

        values = new Hashtable<Integer, float[][]>();
        values.put(-1, train_data);
        values.put(0, H1_values);
        values.put(1, H2_values);
        values.put(2, output_values);

        layerSize = new Hashtable<Integer, Integer>();
        layerSize.put(-1, rows);
        layerSize.put(0, H1);
        layerSize.put(1, H2);
        layerSize.put(2, K);

        weight_derivatives = new Hashtable<Integer, float[][]>();

        Random rand = new Random();

        for(int i=0; i<H1; i++){
            for(int j=0; j<d+1; j++){
                weights.get(0)[i][j] = rand.nextFloat() * 2 - 1;
            }
        }

        for(int i=0; i<H2; i++){
            for(int j=0; j<H1+1; j++){
                weights.get(1)[i][j] = rand.nextFloat() * 2 - 1;
            }
        }

        for(int i=0; i<K; i++){
            for(int j=0; j<H2+1; j++){
                weights.get(2)[i][j] = rand.nextFloat() * 2 - 1;
            }
        }
    }

    public static void forwardPass(float[] x, int d, int K, int inp){
        float sum = 0;
        for(int i=0; i<H1; i++){
            for(int j=0; j<d; j++){
                sum += x[j+1] * weights.get(0)[i][j];
            }
            sum += weights.get(0)[i][0];
            values.get(0)[inp][i] = sum;
            sum = 0;
        }

        for(int i=0; i<H2; i++){
            for(int j=0; j<H1; j++){
                sum += values.get(0)[inp][j+1] * weights.get(1)[i][j];
            }
            sum += weights.get(1)[i][0];
            values.get(1)[inp][i] = sum;
            sum = 0;
        }

        for(int i=0; i<K; i++){
            for(int j=0; j<H2; j++){
                sum += values.get(1)[inp][j+1] * weights.get(2)[i][j];
            }
            sum += weights.get(2)[i][0];
            values.get(2)[inp][i] = sum;
            sum = 0;
        }

    }

    public static float sigmoid(float x) {
		return (float)(1 / (1 + Math.exp(-x)));
    }
    
    public static float tanh(float x){
        return (float)((Math.exp(x) - Math.exp(-x))/(Math.exp(x) + Math.exp(-x)));
    }

    
    public static void backprop(int layer, int inp){
        delta_out = new float[layerSize.get(2)];
        delta_hidden = new float[layerSize.get(1)];
        float[][] res = new float[layerSize.get(layer)][layerSize.get(layer-1)];
        
        if(layer == 1){
            float sum = 0f;

            for(int j=0; j<layerSize.get(layer); j++){
                for(int k=0; k<layerSize.get(layer + 1) ;k++){
                    sum += delta_out[k] * weights.get(k)[inp][j];
                }
                delta_hidden[j] = sum * sigmoid(values.get(layer)[inp][j]) * (1-sigmoid(values.get(layer)[inp][j]));
                for(int i=0; i<layerSize.get(layer-1); i++){
                    res[j][i] = delta_hidden[j]*values.get(layer-1)[inp][i];
                }
            }
        }
        else if(layer == 0){
            float sum = 0f;

            for(int j=0; j<layerSize.get(layer + 1); j++){
                for(int k=0; k<layer+1 ; k++){
                    sum += values.get(k)[inp][j] * weights.get(k)[inp][j];
                }
                for(int i=0; i<layerSize.get(layer-1); i++){
                    res[j][i] = (1 - tanh(values.get(layer)[inp][j]) * tanh(values.get(layer)[inp][j])) * sum * values.get(layer-1)[inp][i];
                }
            }
        }
        else if(layer == 2){
            //Loss for each input (output layer)
            float[] loss = new float [layerSize.get(layer)];

            for(int i=0; i<layerSize.get(layer); i++){
                if(train_data_cat[inp]==i+1){
                    /*1 : [1 0 0 0]
                      2 : [0 1 0 0]
                      3 : [0 0 1 0]
                      4 : [0 0 0 1]*/
                    loss[i] = (values.get(layer)[inp][i] - 1);
                    delta_out[i] = sigmoid(values.get(layer)[inp][i]) * (1-sigmoid(values.get(layer)[inp][i]));
                    delta_out[i] = delta_out[i] * loss[i] * (-1);
                    for(int j=0; j<layerSize.get(layer-1); j++){
                        res[i][j] = delta_out[i] * values.get(layer-1)[inp][j];
                    }
                }else{
                    loss[i] = values.get(layer)[inp][i];
                    delta_out[i] = sigmoid(values.get(layer)[inp][i]) * (1-sigmoid(values.get(layer)[inp][i]));
                    delta_out[i] = delta_out[i] * loss[i] * (-1);
                    for(int j=0; j<layerSize.get(layer-1); j++){
                        res[i][j] = delta_out[i] * values.get(layer-1)[inp][j];
                    }
                }
            }
        }
        weight_derivatives.put(layer, res);

        
    }

    public static void gradientDescent(int from, int to){
        for(int i=0; i<3; i++){
            for(int j=0; j<layerSize.get(i); j++){
                for(int k=from; k<to; k++){
                    weights.get(i)[k][j] = weights.get(i)[k][j] - learning_rate * weight_derivatives.get(i)[k][j];
                }
            }
        }
    }

   
}

  



  