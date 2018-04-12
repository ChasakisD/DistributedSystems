package gr.aueb.dist.partOne.Utils;

import gr.aueb.dist.partOne.Models.Master;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class ParserUtils {
    public static Master GetServersFromText(String file){
        Master master = new Master();

        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(sCurrentLine);
                int i = 1;
                while (st.hasMoreTokens()){
                    String token = st.nextToken();

                    switch (i){
                        case 1: master.setIp(token); break;
                        case 2: master.setPort(Integer.parseInt(token)); break;
                        case 3: master.setId(token); break;
                        case 4: master.setHowManyWorkersToWait(Integer.parseInt(token)); break;
                    }
                    i++;
                }
            }

            return master;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            CloseReaders(bufferedReader, fileReader);
        }

        return null;
    }

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

    public static double GetTimeInSec(long start){
        long end = System.nanoTime();

        return (end-start) / 1000000 / 1000d;
    }
}
