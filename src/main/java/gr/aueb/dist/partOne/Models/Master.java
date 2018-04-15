package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Abstractions.IMaster;
import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.MessageType;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.MatrixHelpers;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Master extends Server implements IMaster{
    private int CurrentIteration;
    private int HowManyWorkersToWait;

    private double LatestError;
    private long LoopCalculationStartTime;

    private ArrayList<Worker> AvailableWorkers;
    private ArrayList<CommunicationMessage> XMessages;
    private ArrayList<CommunicationMessage> YMessages;

    private HashMap<String, Double> XExecutionTimes;
    private HashMap<String, Double> YExecutionTimes;
    private HashMap<String, Integer> LatestWorkersXDistribution;
    private HashMap<String, Integer> LatestWorkersYDistribution;

    private INDArray RUpdated;
    private INDArray R, P, C ,X ,Y;

    private final static int MAX_ITERATIONS = 800;
    private final static double L = 0.1;
    private final static double A = 40;

    private final static String NEW_X_PATH = "data/newX.txt";
    private final static String NEW_Y_PATH = "data/newY.txt";

    public Master(){}

    /**
     * Runnable Implementation
     */
    public synchronized void run() {
        try{
            ObjectOutputStream out = new ObjectOutputStream(getSocketConn().getOutputStream());
            ObjectInputStream in = new ObjectInputStream(getSocketConn().getInputStream());

            CommunicationMessage message = (CommunicationMessage) in.readObject();

            switch(message.getType()){
                case HELLO_WORLD:{
                    Worker worker = new Worker();
                    worker.setId(message.getServerName());
                    worker.setIp(message.getIp());
                    worker.setPort(message.getPort());
                    worker.setInstanceCpuCores(message.getCpuCores());
                    worker.setInstanceRamSize(message.getRamGBSize());
                    AvailableWorkers.add(worker);

                    System.out.println(worker.toString());

                    if(AvailableWorkers.size() >= HowManyWorkersToWait){
                        StartMatrixFactorization();
                    }

                    break;
                }
                case X_CALCULATED:{
                    XMessages.add(message);
                    XExecutionTimes.put(message.getServerName(), message.getExecutionTime());
                    if(XMessages.size() >= HowManyWorkersToWait){
                        LinkedList<INDArray> XDist = new LinkedList<>();

                        //Ascending sort of fromUser
                        XMessages.sort(Comparator.comparingInt(CommunicationMessage::getFromUser));
                        XMessages.forEach((msg) -> XDist.add(msg.getXArray()));

                        X = Nd4j.vstack(XDist);

                        DistributeXMatrixToWorkers();

                        //When finished, clear it for the next loop
                        XMessages.clear();
                    }

                    break;
                }
                case Y_CALCULATED:{
                    YMessages.add(message);
                    YExecutionTimes.put(message.getServerName(), message.getExecutionTime());
                    if(YMessages.size() >= HowManyWorkersToWait){
                        LinkedList<INDArray> YDist = new LinkedList<>();

                        //Ascending sort of fromUser
                        YMessages.sort(Comparator.comparingInt(CommunicationMessage::getFromUser));
                        YMessages.forEach((msg) -> YDist.add(msg.getYArray()));

                        Y = Nd4j.vstack(YDist);

                        double error = CalculateError();
                        double difference = Math.abs(error - LatestError);

                        // we want our error to be min in each iteration
                        if(LatestError < error){
                            System.out.println("Previous Error: " + LatestError);
                            System.out.println("Current Error: " + error);
                            System.out.println("False Iteration");
                        }

                        System.out.println("***********************************************");
                        System.out.println("Loop No.: " + CurrentIteration);
                        System.out.println("Error: " + error);
                        System.out.println("Previous Error: " + LatestError);
                        System.out.println("Difference: " + difference);
                        System.out.println("Loop Elapsed Time: " +
                                ParserUtils.GetTimeInSec(LoopCalculationStartTime) + "sec");
                        System.out.println("***********************************************");

                        if(difference < 0.001 || CurrentIteration >= MAX_ITERATIONS){
                            FinishMatrixFactorization();
                            return;
                        }

                        LatestError = error;
                        CurrentIteration++;

                        DistributeYMatrixToWorkers();
                        LoopCalculationStartTime = System.nanoTime();

                        //When finished, clear it for the next loop
                        YMessages.clear();
                    }

                    break;
                }
                default:{
                    break;
                }
            }
        }
        catch (ClassNotFoundException | IOException ignored) {}
    }

    private void StartMatrixFactorization(){
        int totalCores = AvailableWorkers
                .stream()
                .map(Worker::getInstanceCpuCores)
                .reduce(0, (a, b) -> a + b);

        System.out.println("Number Of Workers: " + HowManyWorkersToWait + " Workers");
        System.out.println("Total Cores: " + totalCores + " Cores");

        // C and P matrices only need to be calculated once and passed once to the workers
        C = (R.mul(A)).add(1);
        P = Transforms.greaterThanOrEqual(R, Nd4j.zeros(R.rows(),R.columns()));

        // let's get the K << max{U,I}
        // meaning a number much smaller than the biggest column or dimension
        int BiggestDimension = R.columns() > R.rows() ?
                R.columns() : R.rows();

        int K = BiggestDimension / 10;

        X = MatrixHelpers.GenerateRandomMatrix(R, K, false);
        Y = MatrixHelpers.GenerateRandomMatrix(R, K, true);

        TransferMatricesToWorkers();
        DistributeYMatrixToWorkers();

        LoopCalculationStartTime = System.nanoTime();
    }

    private void FinishMatrixFactorization(){
        System.out.println("**************************************");
        System.out.println("Writing to " + NEW_X_PATH + ", " + NEW_Y_PATH + "newY.txt");
        Nd4j.writeTxt(X, NEW_X_PATH);
        Nd4j.writeTxt(Y, NEW_Y_PATH);

        long startTime = System.nanoTime();

        RUpdated = X.mmul(Y.transpose());

        System.out.println("New R Calculated in: " + ParserUtils.GetTimeInSec(startTime) + "sec");
        System.out.println("**************************************");
    }

    /**
     * IMaster Implementation
     */
    public void Initialize() {
        CurrentIteration = 0;
        LatestError = Double.MAX_VALUE - 1;

        AvailableWorkers = new ArrayList<>();

        XMessages = new ArrayList<>();
        YMessages = new ArrayList<>();

        XExecutionTimes = new HashMap<>();
        YExecutionTimes = new HashMap<>();
        LatestWorkersXDistribution = new HashMap<>();
        LatestWorkersYDistribution = new HashMap<>();

        R = ParserUtils.LoadDataSet("data/inputMatrix.csv");
        if(R == null){
            System.out.println("Wrong DataSet! Please contact with the Developers!");
            return;
        }

        C = Nd4j.zeros(R.rows(), R.columns());
        P = Nd4j.zeros(R.rows(), R.columns());

        Path newXFile = Paths.get(NEW_X_PATH);
        Path newYFile = Paths.get(NEW_Y_PATH);
        if(Files.exists(newXFile) && Files.exists(newYFile)){
            X = Nd4j.readTxt(NEW_X_PATH);
            Y = Nd4j.readTxt(NEW_Y_PATH);
            System.out.println("**************************************");
            System.out.println("Loading to " + NEW_X_PATH + ", " + NEW_Y_PATH + "newY.txt");
            System.out.println("**************************************");
            // Create the updated R
            FinishMatrixFactorization();
            CalculateBestLocalPOIsForUser(1,1);
        }else{
            System.out.println("No trained data found to load!. Waiting for master connections...");
        }

        this.OpenServer();
    }

    public void TransferMatricesToWorkers(){
        CommunicationMessage msg = new CommunicationMessage();
        msg.setType(MessageType.TRANSFER_MATRICES);
        msg.setCArray(C);
        msg.setPArray(P);
        msg.setXArray(X);
        msg.setYArray(Y);

        SendBroadcastMessageToWorkers(msg);
    }

    public void DistributeXMatrixToWorkers() {
        HashMap<String, Integer[]> workerIndexes = SplitMatrix(Y, "Y");

        AvailableWorkers.parallelStream().forEach(worker -> {
            CommunicationMessage xMessage = new CommunicationMessage();
            xMessage.setType(MessageType.CALCULATE_Y);
            xMessage.setXArray(X);
            xMessage.setFromUser(workerIndexes.get(worker.getId())[0]);
            xMessage.setToUser(workerIndexes.get(worker.getId())[1]);
            this.SendCommunicationMessage(xMessage, worker.getIp(), worker.getPort());
        });
    }

    public void DistributeYMatrixToWorkers() {
        HashMap<String, Integer[]> workerIndexes = SplitMatrix(X, "X");

        AvailableWorkers.parallelStream().forEach(worker -> {
            CommunicationMessage xMessage = new CommunicationMessage();
            xMessage.setType(MessageType.CALCULATE_X);
            xMessage.setYArray(Y);
            xMessage.setFromUser(workerIndexes.get(worker.getId())[0]);
            xMessage.setToUser(workerIndexes.get(worker.getId())[1]);
            this.SendCommunicationMessage(xMessage, worker.getIp(), worker.getPort());
        });
    }

    public void SendBroadcastMessageToWorkers(CommunicationMessage message) {
        AvailableWorkers.parallelStream().forEach(worker ->
                this.SendCommunicationMessage(message, worker.getIp(), worker.getPort()));
    }

    public double CalculateError() {
        // Compute the least square distance for every element
        INDArray temp = X.mmul(Y.transpose());
        temp.subi(P);
        temp.muli(temp);
        temp.muli(C);

        // compute the normalization
        INDArray normX = Nd4j.sum(X.mul(X),0);
        INDArray normY = Nd4j.sum(Y.mul(Y), 0);
        INDArray norma = normX.add(normY);
        norma.muli(L);

        // Sum all the elements of the least squares
        double sumPartOne = temp.sumNumber().doubleValue();

        // Compute the normalization for every element
        double sumPartTwo = norma.sumNumber().doubleValue();

        // Return Error + norma
        return sumPartOne + sumPartTwo;
    }

    public double CalculateScore(int x, int y) {
        return 0;
    }

    public List<Poi> CalculateBestLocalPOIsForUser(int user, int numberOfResults) {
        List<Poi> recommendedPOIs = new LinkedList<>();

        INDArray pois = RUpdated.getRow(user);

        double previousMax = Double.MAX_VALUE;
        for (int poi = 0; poi < numberOfResults; poi++) {
            int currentMaxIndex = getMax(pois, previousMax, user);
            previousMax = pois.getDouble(0, currentMaxIndex);

            // Creates a new poi with that id
            recommendedPOIs.add(new Poi(currentMaxIndex));
            System.out.println("Found "+(poi+1)+" poi, index: "+currentMaxIndex);
        }

        return recommendedPOIs;
    }

    /**
     * Helper Methods
     */
    public HashMap<String, Integer[]> SplitMatrix(INDArray matrix, String matrixName){
        int totalCores = AvailableWorkers
                .stream()
                .map(Worker::getInstanceCpuCores)
                .reduce(0, (a, b) -> a + b);

        int currentIndex = -1;

        int rowsPerCore = 0;
        LinkedHashMap<String, Double> sortedMap =
                new LinkedHashMap<>();

        HashMap<String, Double> matrixExecutionTimes =
                new HashMap<>(matrixName.equals("X") ?
                        XExecutionTimes :
                        YExecutionTimes);

        HashMap<String, Integer> latestWorkersDistribution =
                matrixName.equals("X") ?
                        LatestWorkersXDistribution :
                        LatestWorkersYDistribution;

        if(CurrentIteration == 0) {
            rowsPerCore = matrix.rows() / totalCores;
        }
        else {
            ArrayList<String> mapKeys = new ArrayList<>(matrixExecutionTimes.keySet());
            ArrayList<Double> mapValues = new ArrayList<>(matrixExecutionTimes.values());
            Collections.sort(mapValues);
            Collections.sort(mapKeys);

            for (Double val : mapValues) {
                Iterator<String> keyIt = mapKeys.iterator();

                while (keyIt.hasNext()) {
                    String key = keyIt.next();

                    if (matrixExecutionTimes.get(key).equals(val)) {
                        keyIt.remove();
                        sortedMap.put(key, val);
                        break;
                    }
                }
            }
        }

        HashMap<String, Integer[]> workerIndexes = new HashMap<>();

        double totalExTime = matrixExecutionTimes.values()
                .stream()
                .reduce(0.0, (a, b) -> a + b);

        double meanExTime = totalExTime / HowManyWorkersToWait;

        Iterator<Double> valueIt = sortedMap.values().iterator();
        Double lastValue = 0.0;
        while (valueIt.hasNext()) {
            lastValue = valueIt.next();
        }

        System.out.println("***********************************************");

        boolean needRedistribution = CurrentIteration == 0 || (lastValue - meanExTime) <= 0;

        if(!needRedistribution) {
            Iterator<String> keyIt = sortedMap.keySet().iterator();
            String lastKey = "";
            while (keyIt.hasNext()) {
                lastKey = keyIt.next();
            }

            int slowestWorkerIndices = latestWorkersDistribution.get(lastKey);
            int percentMoved = slowestWorkerIndices / 10;
            latestWorkersDistribution.put(lastKey, slowestWorkerIndices - percentMoved);
            int rowsToBeAddedToRest = percentMoved / (HowManyWorkersToWait - 1);

            for (String currentKey : sortedMap.keySet()) {
                if (currentKey.equals(lastKey)) break;
                int currentIndices = latestWorkersDistribution.get(currentKey);
                latestWorkersDistribution.put(currentKey, currentIndices + rowsToBeAddedToRest);
                percentMoved -= rowsToBeAddedToRest;
            }

            if(percentMoved > 0) {
                Iterator<String> keyIt3 = sortedMap.keySet().iterator();
                if (keyIt3.hasNext()) {
                    String currentKey = keyIt3.next();
                    int currentIndices = latestWorkersDistribution.get(currentKey);
                    latestWorkersDistribution.put(currentKey, currentIndices + percentMoved);
                }
            }
        }

        for (int i = 0; i < AvailableWorkers.size(); i++) {
            Worker worker = AvailableWorkers.get(i);

            int workerRows = needRedistribution ?
                    (rowsPerCore * worker.getInstanceCpuCores()) :
                    latestWorkersDistribution.get(worker.getId());

            Integer[] indexes = new Integer[2];
            indexes[0] = currentIndex + 1;

            if (i == AvailableWorkers.size() - 1) {
                if (currentIndex != matrix.rows()) {
                    indexes[1] = matrix.rows() - 1;
                    System.out.println("Distributing " + matrixName +
                            " to " + worker.getId() +
                            " from " + indexes[0] +
                            " to " + indexes[1] +
                            ". Total: " + (indexes[1] - indexes[0] + 1));
                }
            } else {
                currentIndex += workerRows;
                indexes[1] = currentIndex;
                System.out.println("Distributing " + matrixName +
                        " to " + worker.getId() +
                        " from " + indexes[0] +
                        " to " + indexes[1] +
                        ". Total: " + (indexes[1] - indexes[0] + 1));
            }
            latestWorkersDistribution.put(worker.getId(), indexes[1] - indexes[0] + 1);
            workerIndexes.put(worker.getId(), indexes);
        }

        System.out.println("***********************************************");

        return workerIndexes;
    }

    private int getMax(INDArray pois, double previousMax, int user){
        double max = -1;
        int maxIndex = -1;
        System.out.println(pois.rows());
        System.out.println(pois.columns());
        for (int i = 0; i < pois.columns(); i++) {
            double element = pois.getDouble(0, i);
            if (previousMax > element &&
                    element > max &&
                    P.getDouble(user, i) != 1){
                max = element;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /**
     *   Getters and Setters
     */
    public int getHowManyWorkersToWait() {
        return HowManyWorkersToWait;
    }

    public void setHowManyWorkersToWait(int howManyWorkersToWait) {
        this.HowManyWorkersToWait = howManyWorkersToWait;
    }
}
