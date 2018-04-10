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
import java.net.Socket;
import java.util.*;

public class Master extends Server implements IMaster{
    private int totalCores;
    private int currentIteration;
    private int howManyWorkersToWait;

    private double latestError;
    private long LoopCalculationStartTime;

    private ArrayList<Worker> availableWorkers;
    private ArrayList<CommunicationMessage> XMessages;
    private ArrayList<CommunicationMessage> YMessages;

    private INDArray R, P, C ,X ,Y;

    private final static int MAX_ITERATIONS = 200;
    private final static double L = 0.1;
    private final static double A = 40;

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
                    availableWorkers.add(worker);

                    System.out.println(worker.toString());

                    if(availableWorkers.size() >= howManyWorkersToWait){
                        StartMatrixFactorization();
                    }

                    break;
                }
                case X_CALCULATED:{
                    XMessages.add(message);
                    if(XMessages.size() >= howManyWorkersToWait){
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
                    if(YMessages.size() >= howManyWorkersToWait){
                        LinkedList<INDArray> YDist = new LinkedList<>();

                        //Ascending sort of fromUser
                        YMessages.sort(Comparator.comparingInt(CommunicationMessage::getFromUser));
                        YMessages.forEach((msg) -> YDist.add(msg.getYArray()));

                        Y = Nd4j.vstack(YDist);

                        double error = CalculateError();

                        // we want our error to be min in each iteration
                        if(latestError < error){
                            System.out.println("Previous Error: " + latestError);
                            System.out.println("Current Error: " + error);
                            System.out.println("False Iteration");
                        }

                        System.out.println("***********************************************");
                        System.out.println("Loop No.: " + currentIteration);
                        System.out.println("Error: " + error);
                        System.out.println("Loop Elapsed Time: " +
                                ParserUtils.GetTimeInMs(LoopCalculationStartTime));
                        System.out.println("***********************************************");

                        if(error< 0.001){
                            System.out.println("Error is less than 0.001. Time to finish!");
                            return;
                        }

                        latestError = error;
                        currentIteration++;

                        if(currentIteration >= MAX_ITERATIONS){
                            System.out.println("Finished Algorithm!");
                            return;
                        }

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
        totalCores = availableWorkers
                .stream()
                .map(Worker::getInstanceCpuCores)
                .reduce(0, (a, b) -> a+b);

        System.out.println("Number Of Workers: " + howManyWorkersToWait + " Workers");
        System.out.println("Total Cores: " + totalCores + " Cores");

        // C and P matrices only need to be calculated once and passed once to the workers
        C = (R.mul(A)).add(1);
        P = Transforms.greaterThanOrEqual(R, Nd4j.zeros(R.rows(),R.columns()));

        // let's get the K << max{U,I}
        // meaning a number much smaller than the biggest column or dimension
        int BiggestDimension = R.columns() > R.rows()?
                R.columns() : R.rows();

        int K = BiggestDimension / 10;

        X = MatrixHelpers.GenerateRandomMatrix(R, K, false);
        Y = MatrixHelpers.GenerateRandomMatrix(R, K, true);

        TransferMatricesToWorkers();
        DistributeYMatrixToWorkers();

        LoopCalculationStartTime = System.nanoTime();
    }

    /**
     * IMaster Implementation
     */
    public void Initialize() {
        currentIteration = 0;
        latestError = Double.MAX_VALUE - 1;

        XMessages = new ArrayList<>();
        YMessages = new ArrayList<>();
        availableWorkers = new ArrayList<>();

        R = ParserUtils.loadDataSet("data/inputMatrix.csv");
        C = Nd4j.zeros(R.rows(), R.columns());
        P = Nd4j.zeros(R.rows(), R.columns());

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
        int currentIndex = -1;
        int rowsPerCore = Y.rows() / totalCores;

        System.out.println("***********************************************");

        HashMap<Worker, CommunicationMessage> messages = new HashMap<>();

        for(int i = 0; i < availableWorkers.size(); i++){
            Worker worker = availableWorkers.get(i);
            int workerRows = rowsPerCore * worker.getInstanceCpuCores();

            CommunicationMessage xMessage = new CommunicationMessage();
            xMessage.setType(MessageType.CALCULATE_Y);
            xMessage.setXArray(X);
            xMessage.setFromUser(currentIndex + 1);

            if(i == availableWorkers.size() - 1){
                if(currentIndex != Y.rows()){
                    xMessage.setToUser(Y.rows() - 1);
                    System.out.println("Distributing X to " + worker.getId() + " from " + (currentIndex + 1) + " to " + (Y.rows() - 1));
                }
            }else{
                int startIndex = currentIndex;
                currentIndex += workerRows;
                xMessage.setToUser(currentIndex);
                System.out.println("Distributing X to " + worker.getId() + " from " + (startIndex + 1) + " to " + currentIndex);
            }

            messages.put(worker, xMessage);
        }

        availableWorkers.parallelStream().forEach(worker -> SendMessageToWorker(messages.get(worker), worker));

        System.out.println("***********************************************");
    }

    public void DistributeYMatrixToWorkers() {
        int currentIndex = -1;
        int rowsPerCore = X.rows() / totalCores;

        System.out.println("***********************************************");

        HashMap<Worker, CommunicationMessage> messages = new HashMap<>();

        for(int i = 0; i < availableWorkers.size(); i++){
            Worker worker = availableWorkers.get(i);
            int workerRows = rowsPerCore * worker.getInstanceCpuCores();

            CommunicationMessage xMessage = new CommunicationMessage();
            xMessage.setType(MessageType.CALCULATE_X);
            xMessage.setYArray(Y);
            xMessage.setFromUser(currentIndex + 1);

            if(i == availableWorkers.size() - 1){
                if(currentIndex != X.rows()){
                    xMessage.setToUser(X.rows() - 1);
                    System.out.println("Distributing Y to " + worker.getId() + " from " + (currentIndex + 1) + " to " + (X.rows() - 1));
                }
            }else{
                int startIndex = currentIndex;
                currentIndex += workerRows;
                xMessage.setToUser(currentIndex);
                System.out.println("Distributing Y to " + worker.getId() + " from " + (startIndex + 1) + " to " + currentIndex);
            }

            messages.put(worker, xMessage);
        }

        availableWorkers.parallelStream().forEach(worker -> SendMessageToWorker(messages.get(worker), worker));

        System.out.println("***********************************************");
    }

    public void SendBroadcastMessageToWorkers(CommunicationMessage message) {
        availableWorkers.parallelStream().forEach(worker -> SendMessageToWorker(message, worker));
    }

    public void SendMessageToWorker(CommunicationMessage message, Worker worker) {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        Socket socket = null;

        try{
            socket = new Socket(worker.getIp(), worker.getPort());

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(message);
            out.flush();
        }catch(IOException ignored){}
        finally {
            this.CloseConnections(socket, in, out);
        }
    }

    public double CalculateError() {
        // multiply c
        // do the inside Σ actions as parallel
        // as possible
        INDArray temp = X.mmul(Y.transpose());
        temp.subi(P);
        temp.muli(temp);
        temp.muli(C);

        INDArray normX = Nd4j.sum(X.mul(X),0);
        INDArray normY = Nd4j.sum(Y.mul(Y), 0);
        INDArray norma = normX.add(normY);
        norma.muli(L);

        double regu = norma.sumNumber().doubleValue();

        return temp.sumNumber().doubleValue() + regu;
    }

    public double CalculateScore(int x, int y) {
        return 0;
    }

    public List<Poi> CalculateBestLocalPoisForUser(int x, double xBound, double yBound, int y) {
        return null;
    }

    /**
     *   Getters and Setters
     */
    public int getHowManyWorkersToWait() {
        return howManyWorkersToWait;
    }

    public void setHowManyWorkersToWait(int howManyWorkersToWait) {
        this.howManyWorkersToWait = howManyWorkersToWait;
    }
}
