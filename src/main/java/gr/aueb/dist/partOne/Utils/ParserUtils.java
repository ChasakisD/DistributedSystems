package gr.aueb.dist.partOne.Utils;

import gr.aueb.dist.partOne.Models.Worker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ParserUtils {
    public static ArrayList<Worker> GetWorkersFromText(String file){
        ArrayList<Worker> workers = new ArrayList<Worker>();

        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                Worker worker = new Worker();

                StringTokenizer st = new StringTokenizer(sCurrentLine);
                int i = 1;
                while (st.hasMoreTokens()){
                    String token = st.nextToken();

                    switch (i){
                        case 1: worker.setIp(token); break;
                        case 2: worker.setPort(Integer.parseInt(token)); break;
                        case 3: worker.setId(token); break;
                    }
                    i++;
                }
                workers.add(worker);
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

        return workers;
    }
}
