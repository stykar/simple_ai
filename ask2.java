import java.io.*;
import java.util.*;
import java.util.Random;

public class ask2 {
    private static final int M = 5;
    private static float[][] centroids;
    private static float[][] myArray;
    private static int rows = 900;
    private static int[] belongs_to_centroid;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(new BufferedReader(new FileReader("./s2_train.txt")));
        int columns = 2;
        myArray = new float[rows][columns];

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
        centroids = new float[M][2];

        for (int i = 0; i < M; i++) {
            int rand_int1 = rand.nextInt(rows); 
            centroids[i][0] = myArray[rand_int1][0];
            centroids[i][1] = myArray[rand_int1][1];
        }

        System.out.println(Arrays.deepToString(centroids));

        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;

        File f = new File("ask2out.txt");

        if(f.exists()){
            f.delete();
        }

        f.createNewFile();

        for(int i = 0; i < 10; i++){
            int[] res = findBelongingCentroid();
            updateCentroids(res);

            System.out.println("--------");
            System.out.println("New centroids after updating");
            System.out.println(Arrays.deepToString(centroids));

            
            try{
                fw = new FileWriter(f, true);
                bw = new BufferedWriter(fw);
                pw = new PrintWriter(bw);
                
                for(int j=0; j<M; j++){
                    pw.println(centroids[j][0] + "," + centroids[j][1]);
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

            System.out.println("Clustering error after centroid updating");
            System.out.println(Arrays.toString(calculateClusteringError()));  
        }

    }

    public static float euclideanDistance(float x1, float y1, float x2, float y2) {
        float res = (float) (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
        return res;
    }

    public static int[] findBelongingCentroid() {
        int[] belongs_to_centroid = new int[rows];
        float min_distance;
        int min_centroid;
        
        for (int i = 0; i < rows; i++) {
            min_centroid = 0;
            min_distance = euclideanDistance(myArray[i][0], myArray[i][1], centroids[0][0], centroids[0][1]);
            
            for (int j = 1; j < M; j++) {
                if (euclideanDistance(myArray[i][0], myArray[i][1], centroids[j][0], centroids[j][1]) < min_distance) {
                    min_distance = euclideanDistance(myArray[i][0], myArray[i][1], centroids[j][0], centroids[j][1]);
                    min_centroid = j;
                }
            }
            belongs_to_centroid[i] = min_centroid;
        }
        return belongs_to_centroid;
    }

    public static void updateCentroids(int[] belongs_to_centroid) {
        int ar_length = myArray.length;
        float sum_of_points_x;
        float sum_of_points_y;
        int count_total;
        
        for (int i = 0; i < M; i++) {
            sum_of_points_x = 0;
            sum_of_points_y = 0;
            count_total = 0;
            for (int j = 0; j < ar_length; j++) {
                if (belongs_to_centroid[j] == i) {
                    sum_of_points_y += myArray[j][1];
                    sum_of_points_x += myArray[j][0];
                    count_total += 1;
                }
            }
            centroids[i][0] = sum_of_points_x / (float) count_total;
            centroids[i][1] = sum_of_points_y / (float)count_total;
        }
    }

    public static float[] calculateClusteringError(){
        float[] clustering_error = new float[M];
        belongs_to_centroid = new int[rows];
        float[] distances = new float[rows];

        for (int i = 0; i < rows; i++) {
            
            int min_centroid = 0;
            float min_distance = euclideanDistance(myArray[i][0], myArray[i][1], centroids[0][0], centroids[0][1]);
            
            for (int j = 1; j < M; j++) {
                if (euclideanDistance(myArray[i][0], myArray[i][1], centroids[j][0], centroids[j][1]) < min_distance) {
                    min_distance = euclideanDistance(myArray[i][0], myArray[i][1], centroids[j][0], centroids[j][1]);
                    min_centroid = j;
                }
            }
            belongs_to_centroid[i] = min_centroid;
            distances[i] = min_distance;
        }

        for(int i = 0; i < rows; i++){
            clustering_error[belongs_to_centroid[i]] += distances[i];
        }

        return clustering_error;
    }


    public static void CreateFile() {
    try {
      File myObj = new File("ask2out.txt");
      if (myObj.createNewFile()) {
        System.out.println("File created: " + myObj.getName());
      } else {
        System.out.println("File already exists.");
      }
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  public static void WriteFile() {
    try {
      FileWriter myWriter = new FileWriter("ask2out.txt");
        myWriter.write(Arrays.deepToString(myArray));
        myWriter.write("\n");
         for(int i=0 ; i<rows;i++){
            myWriter.write(belongs_to_centroid[i]);
        }
      myWriter.close();
      System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }
}
