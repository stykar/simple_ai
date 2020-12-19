import java.io.*;
import java.util.*;

public class ask3 {

    private static final int M = 5;
    private static float[][] weights;
    private static float[][] myArray;
    private static int rows = 900;
    public static int[][] labels; 
    public static int[] randoms; 
    private static int[] belongs_to_centroid;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(new BufferedReader(new FileReader("./s2_train.txt")));
        int columns = 2;
        myArray = new float[rows][columns];
        belongs_to_centroid = new int[rows-M];


        while (sc.hasNextLine()) {
            for (int i = 0; i < myArray.length; i++) {
                String[] line = sc.nextLine().split(" ");
                for (int j = 0; j < line.length; j++) {
                    myArray[i][j] = Float.parseFloat(line[j]);
                }
            }
        }
        sc.close();
        Random rand = new Random(); 
        weights = new float[M][2];
        randoms= new int[M];

        for (int i = 0; i < M; i++) {
            int rand_int1 = rand.nextInt(rows); 
            weights[i][0] = myArray[rand_int1][0];
            weights[i][1] = myArray[rand_int1][1];
        }


        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;

        File f = new File("ask3out.txt");

        if(f.exists()){
            f.delete();
        }

        f.createNewFile();


        int epochs = 30;
        float alpha=0.05f;

        for(int i=0; i<=epochs; i++){
            for(int j=0; j<rows;j++){
                int win=Winner(j);
                weights[win][0]+= alpha * (myArray[j][0] - weights[win][0]);
                weights[win][1]+= alpha * (myArray[j][1] - weights[win][1]);

            }
                 try{
                fw = new FileWriter(f, true);
                bw = new BufferedWriter(fw);
                pw = new PrintWriter(bw);
                
                for(int j=0; j<M; j++){
                    pw.println(weights[j][0] + "," + weights[j][1]);
                }

                pw.println("-----");

                for(int j=0; j<rows; j++){
                    pw.println(myArray[j][0] + "," + myArray[j][1]);
                }

                pw.println("/////");

                pw.flush();
            }
            finally{
                try {
                    fw.close();
                } catch (IOException io) {
                    System.out.println("IO Error");
                }
            } 
         alpha=0.95f*alpha;
          System.out.println("\n");
          System.out.println("Updated Weights:");
          System.out.println(Arrays.deepToString(weights));
        }



        findCentroids();
        System.out.println("Clustering error:");
        System.out.println(Arrays.toString(calculateClusteringError()));

    }



     public static void findCentroids(){
        for(int i=0; i<rows-M; i++){
            float current_distance;
            float min= euclideanDistance(myArray[i][0],myArray[i][1],weights[0][0],weights[0][1]);

            for(int j=0; j<M;j++){
                current_distance = euclideanDistance(myArray[i][0],myArray[i][1],weights[j][0],weights[j][1]);
                if(current_distance < min){
                    min = current_distance;
                    belongs_to_centroid[i] = j;
                }
            }
        }
    }

    public static float euclideanDistance(float x1, float y1, float x2, float y2) {
        float res = (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
        return res;
    }



    public static int Winner(int T){
        float[] D;
        D = new float[M];
        float min= euclideanDistance(myArray[T][0],myArray[T][1],weights[0][0],weights[0][1]);
        int mini=0;

        for(int i=0; i<M;i++){
            D[i] = euclideanDistance(myArray[T][0],myArray[T][1],weights[i][0],weights[i][1]);
            if(D[i] < min){
                mini = i;
                min = D[i];
            }
        }
        return mini;
    }



     public static float[] calculateClusteringError(){
        float[] clustering_error = new float[M];
        float[] distances = new float[rows];

        for(int i=0; i<M; i++){
            clustering_error[i] = 0.0f;
        }

        for (int i = 0; i < rows-M; i++) {
            
            float min_distance = euclideanDistance(myArray[i][0], myArray[i][1], weights[0][0], weights[0][1]);
            
            for (int j = 1; j < M; j++) {
                if (euclideanDistance(myArray[i][0], myArray[i][1], weights[j][0], weights[j][1]) < min_distance) {
                    min_distance = euclideanDistance(myArray[i][0], myArray[i][1], weights[j][0], weights[j][1]);
                }
            }
            distances[i] = min_distance;
        }

        for(int i = 0; i < rows-M; i++){
            clustering_error[belongs_to_centroid[i]] += distances[i];
        }

        return clustering_error;
    }
}
 