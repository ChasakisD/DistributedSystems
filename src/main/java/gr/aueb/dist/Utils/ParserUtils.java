package gr.aueb.dist.Utils;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class ParserUtils {

    /**
     * Calculates the elapsed time from a start time
     * @param start the start time
     * @return the elapsed time
     */
    public static double GetTimeInSec(long start){
        long end = System.nanoTime();

        return (end-start) / 1000000 / 1000d;
    }

    /**
     * Loads and returns the dataSet
     * @param dataSet the path to the dataSet
     * @return the dataSet matrix
     */
    public static INDArray LoadDataSet(String dataSet){
        int[] dimensions = GetArrayDimensions(dataSet);
        INDArray matrix = Nd4j.zeros(dimensions);

        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(dataSet);
            bufferedReader = new BufferedReader(fileReader);

            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                sCurrentLine = sCurrentLine.replaceAll("\\s","");

                StringTokenizer tokenizer = new StringTokenizer(sCurrentLine,",");

                int row = Integer.parseInt(tokenizer.nextToken());
                int column = Integer.parseInt(tokenizer.nextToken());
                int value = Integer.parseInt(tokenizer.nextToken());

                matrix.put(row,column,value);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            CloseReaders(bufferedReader, fileReader);
        }

        return matrix;
    }

    private static void CloseReaders(BufferedReader bufferedReader, FileReader fileReader) {
        try {
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if (fileReader != null){
                fileReader.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static int[] GetArrayDimensions(String dataSet){
        int[] dimensions = new int[2];
        dimensions[0] = 0;
        dimensions[1] = 0;

        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(dataSet);
            bufferedReader = new BufferedReader(fileReader);

            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                sCurrentLine = sCurrentLine.replaceAll("\\s","");

                StringTokenizer tokenizer = new StringTokenizer(sCurrentLine,",");

                int userNumber = Integer.parseInt(tokenizer.nextToken());
                int poiNumber = Integer.parseInt(tokenizer.nextToken());

                if(userNumber > dimensions[0]){
                    dimensions[0] = userNumber;
                }

                if(poiNumber > dimensions[1]){
                    dimensions[1] = poiNumber;
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            CloseReaders(bufferedReader, fileReader);
        }

        dimensions[0]++;
        dimensions[1]++;

        return dimensions;
    }
}
