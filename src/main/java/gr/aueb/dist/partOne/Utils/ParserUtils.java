package gr.aueb.dist.partOne.Utils;

import gr.aueb.dist.partOne.Models.Master;
import gr.aueb.dist.partOne.Models.Worker;
import gr.aueb.dist.partOne.Server.Server;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javax.xml.datatype.Duration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ParserUtils {
    public static ArrayList<Server> GetServersFromText(String file, boolean isMaster){
        ArrayList<Server> servers = new ArrayList<Server>();

        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                Server server = isMaster ? new Master() : new Worker();

                StringTokenizer st = new StringTokenizer(sCurrentLine);
                int i = 1;
                while (st.hasMoreTokens()){
                    String token = st.nextToken();

                    switch (i){
                        case 1: server.setIp(token); break;
                        case 2: server.setPort(Integer.parseInt(token)); break;
                        case 3: server.setId(token); break;
                    }
                    i++;
                }
                servers.add(server);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
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

        return servers;
    }

    public static INDArray loadDataset(String dataset){
        // the index start from zero to 764. So the dimension will be 765
        INDArray matrix = Nd4j.zeros(765, 1964);


        System.out.println(matrix.getDouble(0,149));
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(dataset);
            bufferedReader = new BufferedReader(fileReader);

            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
              // separate the commas
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
        System.out.println(matrix.getDouble(0,149));

        return matrix;
    }



    public static long GetTimeInMs(long start){
        long end = System.nanoTime();
        return (end-start)/1000000;
    }
}
