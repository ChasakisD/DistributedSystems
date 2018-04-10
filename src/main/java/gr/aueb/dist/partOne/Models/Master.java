package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.MessageType;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.nd4j.linalg.ops.transforms.Transforms.*;

public class Master extends Server{
    private int currentIteration;
    private double latestError;

    private int howManyWorkersToWait;
    private ArrayList<Worker> availableWorkers;

    private ArrayList<CommunicationMessage> XMessages;
    private ArrayList<CommunicationMessage> YMessages;

    private INDArray R, P, C ,X ,Y;

    private long LoopCalculationStartTime;
    private long XArrayCalculationStartTime;
    private long YArrayCalculationStartTime;

    private final static int MAX_ITERATIONS = 200;
    private final static double L = 0.1;
    private final static double A = 40;

    public Master(){}

    public void StartAlgorithm(){
        // C and P matrices only need to be calculated once and passed once to the workers
        long startTime = System.nanoTime();
        CalculatePMatrix();
        System.out.println("Calculated P Matrix: "+ParserUtils.GetTimeInMs(startTime)+" MilliSecond");

        startTime = System.nanoTime();
        CalculateCMatrix();
        System.out.println("Calculated C Matrix: "+ParserUtils.GetTimeInMs(startTime)+" MilliSecond");

        // let's get the K << max{U,I}
        // meaning a number much smaller than the biggest column or dimension
        int BiggestDimension = R.columns() > R.rows()?
                                R.columns(): R.rows();

        int K = BiggestDimension / 10;

        GenerateX(K);
        GenerateY(K);

        DistributeCPMatricesToWorkers();

        DistributeYMatrixToWorkers();

        LoopCalculationStartTime = System.nanoTime();
        XArrayCalculationStartTime = System.nanoTime();
    }

    private void GenerateX(int K){
        // Create the matrix
        X = Nd4j.zeros(R.rows(), K);
        Random rand = new Random();
        // Fill it with random values between zero and 1
        for (int u = 0; u < X.rows(); u++) {
            for (int k = 0; k < X.columns(); k++) {
                // Enter a random value between 0 and 1
                X.putScalar(u,k,ThreadLocalRandom.current().nextDouble(0, 1));
            }
        }
    }

    private void GenerateY(int K){
        // Initialize the Y matrix
        Y = Nd4j.zeros(R.columns(), K);
        Random rand = new Random();
        // Fill it with random values between zero and 1
        for (int u = 0; u < Y.rows(); u++) {
            for (int k = 0; k < Y.columns(); k++) {
                // enter a random value between 0 and 1
                Y.putScalar(u,k,ThreadLocalRandom.current().nextDouble(0, 1));
            }
        }
    }

    private void LoadOriginalMatrix(INDArray R){
        this.R = R;
        C = Nd4j.zeros(R.rows(), R.columns());
        P = Nd4j.zeros(R.rows(),R.columns());
    }

    private INDArray CombineMatrix(LinkedList<INDArray> Temp) {
        /* Initialize the matrix */
        INDArray newMatrix = Nd4j.zeros(0,0);
        for (INDArray matrix: Temp) {
            // concat them one on top of the other
            newMatrix = Nd4j.vstack(newMatrix,matrix);
        }
        return newMatrix;
    }

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

                    System.out.println("Received: " + worker.toString());

                    if(availableWorkers.size() >= howManyWorkersToWait){
                        System.out.println("Number Of Workers: " + howManyWorkersToWait);
                        StartAlgorithm();
                    }

                    break;
                }
                case X_CALCULATED:{
                    XMessages.add(message);
                    if(XMessages.size() >= howManyWorkersToWait){
                        System.out.println("Calculated X in: " +
                                ParserUtils.GetTimeInMs(XArrayCalculationStartTime));

                        LinkedList<INDArray> XDist = new LinkedList<>();

                        //Ascending sort of fromUser
                        XMessages.sort(Comparator.comparingInt(CommunicationMessage::getFromUser));
                        XMessages.forEach((msg) -> XDist.add(msg.getxArray()));

                        X = CombineMatrix(XDist);

                        System.out.println("New X rows: " + X.rows());

                        DistributeXMatrixToWorkers();

                        YArrayCalculationStartTime = System.nanoTime();

                        //When finished, clear it for the next loop
                        XMessages.clear();
                    }

                    break;
                }
                case Y_CALCULATED:{
                    YMessages.add(message);
                    if(YMessages.size() >= howManyWorkersToWait){
                        System.out.println("Calculated Y in: " +
                                ParserUtils.GetTimeInMs(YArrayCalculationStartTime));
                        System.out.println("Calculated X and Y in: " +
                                ParserUtils.GetTimeInMs(LoopCalculationStartTime));

                        LinkedList<INDArray> YDist = new LinkedList<>();

                        //Ascending sort of fromUser
                        YMessages.sort(Comparator.comparingInt(CommunicationMessage::getFromUser));
                        YMessages.forEach((msg) -> YDist.add(msg.getxArray()));

                        Y = CombineMatrix(YDist);

                        System.out.println("New Y rows: " + Y.rows());

                        double error = CalculateError();
                        System.out.println("Error is: "+error);

                        if(error< 0.001){
                            System.out.println("Inside Margin");
                            return;
                        }
                        // we want our error to be min in each iteration
                        if(latestError < error){
                            System.out.println("Previous Error: " + latestError);
                            System.out.println("Current Error: " + error);
                            System.out.println("False Iteration");
                        }

                        latestError = error;
                        currentIteration++;

                        if(currentIteration >= MAX_ITERATIONS){
                            System.out.println("Finished Algorithm!");
                            return;
                        }

                        DistributeYMatrixToWorkers();

                        LoopCalculationStartTime = System.nanoTime();
                        XArrayCalculationStartTime = System.nanoTime();

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

    /**
     * IMaster Implementation
     */
    public void Initialize() {
        currentIteration = 0;
        latestError = Double.MAX_VALUE - 1;

        XMessages = new ArrayList<>();
        YMessages = new ArrayList<>();
        availableWorkers = new ArrayList<>();

        INDArray originalMatrix = ParserUtils.loadDataset("data/inputMatrix.csv");
        ParserUtils.loadDataset("data/inputMatrix.csv");
        LoadOriginalMatrix(originalMatrix);

        this.OpenServer();
    }


    private void CalculateCMatrix() {
       C = (R.mul(A)).add(1);
    }

    private void CalculatePMatrix(){
        System.out.println(R.getDouble(0,149));
        P = greaterThanOrEqual(R, Nd4j.zeros(R.rows(),R.columns()));
        System.out.println(P.getDouble(0,148));
    }

    private  void DistributeXMatrixToWorkers() {
        int totalCores = availableWorkers
                .stream()
                .map(Server::getCpuCores)
                .reduce(0, (a, b) -> a+b);

        System.out.println("Total Cores: " + totalCores);

        int currentIndex = -1;
        int rowsPerCore = Y.rows() / totalCores;
        for(int i = 0; i < availableWorkers.size(); i++){
            Worker worker = availableWorkers.get(i);
            int workerRows = rowsPerCore * worker.getCpuCores();

            CommunicationMessage xMessage = new CommunicationMessage();
            xMessage.setType(MessageType.CALCULATE_Y);
            xMessage.setxArray(X);
            xMessage.setFromUser(currentIndex + 1);

            System.out.println(worker.getName() + " starts on " + (currentIndex + 1) + " user!");

            if(i == availableWorkers.size() - 1){
                if(currentIndex != Y.rows()){
                    xMessage.setToUser(Y.rows() - 1);
                    System.out.println(worker.getName() + " ends on " + (Y.rows() - 1) + " user!");
                }
            }else{
                currentIndex += workerRows;
                xMessage.setToUser(currentIndex);
                System.out.println(worker.getName() + " ends on " + currentIndex + " user!");
            }

            SendMessageToWorker(xMessage, worker);
        }
    }

    private void DistributeYMatrixToWorkers() {
        int totalCores = availableWorkers
                .stream()
                .map(Server::getCpuCores)
                .reduce(0, (a, b) -> a+b);

        System.out.println("Total Cores: " + totalCores);

        int currentIndex = -1;
        int rowsPerCore = X.rows() / totalCores;
        for(int i = 0; i < availableWorkers.size(); i++){
            Worker worker = availableWorkers.get(i);
            int workerRows = rowsPerCore * worker.getCpuCores();

            CommunicationMessage xMessage = new CommunicationMessage();
            xMessage.setType(MessageType.CALCULATE_X);
            xMessage.setyArray(Y);
            xMessage.setFromUser(currentIndex + 1);

            System.out.println(worker.getName() + " starts on " + (currentIndex + 1) + " user!");

            if(i == availableWorkers.size() - 1){
                if(currentIndex != X.rows()){
                    xMessage.setToUser(X.rows() - 1);
                    System.out.println(worker.getName() + " ends on " + (X.rows() - 1) + " user!");
                }
            }else{
                currentIndex += workerRows;
                xMessage.setToUser(currentIndex);
                System.out.println(worker.getName() + " ends on " + currentIndex + " user!");
            }

            SendMessageToWorker(xMessage, worker);
        }
    }

    private void DistributeCPMatricesToWorkers(){
        CommunicationMessage msg = new CommunicationMessage();
        msg.setType(MessageType.TRANSFER_CP);
        msg.setcArray(C);
        msg.setpArray(P);
        msg.setxArray(X);
        msg.setyArray(Y);

        SendBroadcastMessageToWorkers(msg);
    }

    private double CalculateError() {
        // multiply c
        // do the inside Σ actions as parallel
        // as possible
        long starttime = System.nanoTime();
        INDArray temp = X.mmul(Y.transpose());
        temp.subi(P);
        temp.muli(temp);
        temp.muli(C);

        INDArray normX = Nd4j.sum(X.mul(X),0);
        INDArray normY = Nd4j.sum(Y.mul(Y), 0);
        INDArray norma = normX.add(normY);
        norma.muli(L);

        double regu = norma.sumNumber().doubleValue();

        System.out.println(regu);

        System.out.println("Calculated Score Time: "+ParserUtils.GetTimeInMs(starttime));
        return temp.sumNumber().doubleValue() + regu;
    }

    private void SendBroadcastMessageToWorkers(CommunicationMessage message) {
        for(Worker worker : availableWorkers){
            SendMessageToWorker(message, worker);
        }
    }

    private void SendMessageToWorker(CommunicationMessage message, Worker worker) {
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

    public int getHowManyWorkersToWait() {
        return howManyWorkersToWait;
    }

    public void setHowManyWorkersToWait(int howManyWorkersToWait) {
        this.howManyWorkersToWait = howManyWorkersToWait;
    }
}
