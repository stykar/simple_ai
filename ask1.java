import java.io.*;
import java.util.*;

public class ask1 {
    private static int d = 2;
    private static int K = 4;
    private static int H1 = 7;
    private static int H2 = 4;
    private static int B = 100;
    private static int epochs = 500;
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
    private static Hashtable<Integer, float[][]> weight_derivatives_out;
    private static Hashtable<Integer, float[][]> weight_derivatives_h1;
    private static Hashtable<Integer, float[][]> weight_derivatives_h2;
    


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
        for(int ep=0; ep<epochs; ep++){
            for(int i=0; i<rows; i++){
                forwardPass(train_data[i],d, K, i);
                delta_out = new float[layerSize.get(2)];
                delta_hidden = new float[layerSize.get(1)];
                for(int j=2; j>=0; j--){
                    backprop(j, i);
                }
                if(i % B == 0 && i > 0){
                    gradientDescent(i - B, i);
                }
            }
            //System.out.println(Arrays.deepToString(weights.get(1))+"\n");
            calculateTotalError();
        }
        /*
        System.out.println("~~~~~~~~~~~~~~");
        for(int j=1; j<3; j++){
            System.out.println(Arrays.deepToString(weights.get(j)));
        }*/
    }

    public static void initializeWeights(){
        H1_weights = new float[H1][d+1];
        H2_weights = new float[H2][H1+1];
        output_weights = new float[K][H2+1];

        H1_values = new float[rows][H1+1];
        H2_values = new float[rows][H2+1];
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
        layerSize.put(-1, d+1);
        layerSize.put(0, H1+1);
        layerSize.put(1, H2+1);
        layerSize.put(2, K+1);

        weight_derivatives_out = new Hashtable<Integer, float[][]>();
        weight_derivatives_h1 = new Hashtable<Integer, float[][]>();
        weight_derivatives_h2 = new Hashtable<Integer, float[][]>();


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
        float[][] res = new float[layerSize.get(layer) - 1][layerSize.get(layer-1)];
        
        if(layer == 1){
            float sum = 0f;

            for(int j=0; j<layerSize.get(layer) - 1; j++){
                for(int k=0; k<layerSize.get(layer + 1) - 1 ;k++){
                    sum += delta_out[k] * weights.get(layer + 1)[k][j];
                }
                delta_hidden[j] = sum * sigmoid(values.get(layer)[inp][j]) * (1-sigmoid(values.get(layer)[inp][j]));
                for(int i=0; i<layerSize.get(layer-1); i++){
                    res[j][i] = delta_hidden[j] * tanh(values.get(layer-1)[inp][i]);
                }
            }
            weight_derivatives_h2.put(inp, res);
        }
        else if(layer == 0){
            float sum = 0f;

            for(int j=0; j<layerSize.get(layer) - 1; j++){
                for(int k=0; k<layerSize.get(layer + 1) - 1 ; k++){
                    sum += delta_hidden[k] * weights.get(layer + 1)[k][j];
                }
                for(int i=0; i<layerSize.get(layer-1); i++){
                    res[j][i] = (1 - (tanh(values.get(layer)[inp][j]) * tanh(values.get(layer)[inp][j]))) * sum * values.get(layer-1)[inp][i];
                }
            }
            weight_derivatives_h1.put(inp, res);
        }
        else if(layer == 2){
            //Loss for each input (output layer)
            float[] loss = new float [layerSize.get(layer)];

            for(int i=0; i<layerSize.get(layer) - 1; i++){ //gia kathe  neurwna tou epipedou
                if(train_data_cat[inp]==i+1){
                    /*1 : [1 0 0 0]
                      2 : [0 1 0 0]
                      3 : [0 0 1 0]
                      4 : [0 0 0 1]*/
                    loss[i] = (sigmoid(values.get(layer)[inp][i]) - 1);
                    
                }else{
                    loss[i] = sigmoid(values.get(layer)[inp][i]);
                }
                delta_out[i] = sigmoid(values.get(layer)[inp][i]) * (1-sigmoid(values.get(layer)[inp][i]));
                delta_out[i] = delta_out[i] * loss[i] * (-1);
                for(int j=0; j<layerSize.get(layer-1); j++){
                    res[i][j] = delta_out[i] * sigmoid(values.get(layer-1)[inp][j]);
                }
            }
            weight_derivatives_out.put(inp, res);
        }
    }

    public static void gradientDescent(int from, int to){
        //System.out.println(Arrays.deepToString(weight_derivatives_h1.get(1)));
        //System.out.println(Arrays.deepToString(weight_derivatives_h2.get(0)));
        //System.out.println(Arrays.deepToString(weight_derivatives.get(2)));
        for(int i=0; i<3; i++){ //gia kathe layer
            float[][] sum = new float[layerSize.get(i) - 1][layerSize.get(i - 1)];
            for(int j=0; j<layerSize.get(i) - 1; j++){ //gia kathe neurwna sto layer
                for(int k=from; k<to; k++){ //gia kathe eisodo tou batch
                    if(i==0){
                        //System.out.println("i:"+i+" j:"+j+" k:"+k);
                        //System.out.println(weight_derivatives_h1.get(k).length);
                        //System.out.println(weight_derivatives_h1.get(k)[i].length+"\n");
                        for(int l=0; l<layerSize.get(i - 1); l++){
                            sum[j][l] += weight_derivatives_h1.get(k)[j][l];
                        }
                        
                    }
                    else if(i==1){
                        for(int l=0; l<layerSize.get(i - 1); l++){
                            sum[j][l] += weight_derivatives_h2.get(k)[j][l];
                        }
                    }
                    else if(i==2){
                        for(int l=0; l<layerSize.get(i - 1); l++){
                            sum[j][l] += weight_derivatives_out.get(k)[j][l];
                        }
                    }
                }
                if(i == 0){
                    for(int k=0; k<d + 1; k++){
                        //System.out.println("i:"+i+" j:"+j+" k:"+k);
                        weights.get(i)[j][k] = weights.get(i)[j][k] - (learning_rate * sum[j][k]/B);
                    } 
                }
                else{
                    for(int k=0; k<layerSize.get(i-1); k++){
                        //System.out.println("i:"+i+" j:"+j+" k:"+k);
                        weights.get(i)[j][k] = weights.get(i)[j][k] - (learning_rate * sum[j][k]/B);
                    }
                }
            }
        }
    }

    public static void calculateTotalError(){
        float totalErr = 0f;
        int desired;
        for(int i=0; i<rows; i++){
            for(int j=0; j<layerSize.get(2) - 1; j++){
                if(train_data_cat[i] == j+1){
                    desired = 1;
                }
                else{
                    desired = 0;
                }
                totalErr += ((sigmoid(values.get(2)[i][j]) - desired) * (sigmoid(values.get(2)[i][j]) - desired)) / 2 ;
            }
        }
        System.out.println(totalErr);
    }
   
}

  



  