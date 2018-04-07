package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.MessageType;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.nd4j.linalg.ops.transforms.Transforms.*;


public class Master extends Server{
    private int howManyWorkersToWait;
    private ArrayList<Worker> availableWorkers;

    private ArrayList<CommunicationMessage> XMessages;
    private ArrayList<CommunicationMessage> YMessages;

    private INDArray R, P, C ,X ,Y;

    // Holding the matrices to be distrubted to the workers
    // Use it as a FIFO
    private LinkedList<INDArray> C_Matrices = new LinkedList<>();
    private LinkedList<INDArray> P_Matrices = new LinkedList<>();
    private LinkedList<INDArray> X_Matrices = new LinkedList<>();
    private LinkedList<INDArray> Y_Matrices = new LinkedList<>();
    private final static double a = 40;
    private static double l = 0.1;

    public Master(){}

    public void StartAlgorithm(){
        // C and P matrices only need to be calculated once and passed once to the workers
        long startTime = System.nanoTime();
        CalculatePMatrix();
        System.out.println("Calculated P Matrix: "+ParserUtils.GetTimeInMs(startTime)+" MilliSecond");

        P_Matrices = SplitMatrix(P, availableWorkers.size());

        startTime = System.nanoTime();
        CalculateCMatrix();
        System.out.println("Calculated C Matrix: "+ParserUtils.GetTimeInMs(startTime)+" MilliSecond");

        C_Matrices = SplitMatrix(C, availableWorkers.size());

        DistributeCPMatricesToWorkers();

        // let's get the K << max{U,I}
        // meaning a number much smaller than the biggest column or dimension
        int BiggestDimension = R.columns() > R.rows()?
                                R.columns(): R.rows();
        // TODO: 31-Mar-18 Steile gamimeno mail an ειναι κομπλε σαν τιμη ayto που εχω κανει
        int K = BiggestDimension / 10;

        // now let's generate them
        GenerateX(K);
        X_Matrices = SplitMatrix(X, availableWorkers.size());

        DistributeXMatrixToWorkers();

        GenerateY(K);
        Y_Matrices = SplitMatrix(Y, availableWorkers.size());
        // TODO: 31-Mar-18 Make it async
        DistributeYMatrixToWorkers();
    }

    public INDArray PreCalculateYY(INDArray Y) {
        return Y.transpose().mmul(Y);
    }

    // TODO: 04-Apr-18 Η μεθοδος πρεπει να ειναι στον worker to εβαλα εδω για testing
    public INDArray CalculateCuMatrix(int user, INDArray C) {
        return Nd4j.diag(C.getRow(user));
    }

    public INDArray PreCalculateXX(INDArray X) {
        return X.transpose().mmul(X);
    }


    public INDArray CalculateCiMatrix(int item, INDArray matrix) {
        return Nd4j.diag(C.getColumn(item));
    }


    public INDArray CalculateDerivative(INDArray matrix,  INDArray Pu, INDArray Cu, INDArray YY, double l) {
        // (Cu - I)
        INDArray result = (Cu.sub(Nd4j.eye(Cu.rows())));
        // Y.T(Cu - I)Y
        ParserUtils.PrintShape(matrix);
        result = matrix.transpose().mmul(result).mmul(matrix);

        // Y.TY + Y.T(Cu - I)Y
        result.addi(YY);
        // Y.TY + Y.T(Cu - I)Y +λI
        result.addi(Nd4j.eye(result.rows()).mul(l));
        //invert the matrix
        result = Nd4j.reverse(result);
        ParserUtils.PrintShape(result);
        INDArray secondPart = Pu.mmul(Cu);
        secondPart = secondPart.mmul(matrix);

        INDArray finalPart = secondPart.mmul(result);
        return finalPart;
    }

    public void getXResults(){}

    public void GenerateX(int K){
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

    public void GenerateY(int K){
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

    // Sent the C matrix to all of the workers
    public void DiS(){

    }

    // Returns a linked list with the matrices
    public LinkedList<INDArray> SplitMatrix(INDArray matrix, int numberOfWorkers){
        int start = 0;
        int end = matrix.rows() / numberOfWorkers;
        LinkedList<INDArray> splitted = new LinkedList<>();
        for (int u = 0; u < numberOfWorkers ; u++) {
            // if it's the last iteration, we want to make sure we lose no data
            if (numberOfWorkers - 1 == u) {
                splitted.addLast(matrix.get(NDArrayIndex.interval(start,matrix.rows()-1), NDArrayIndex.all()));
            }
            splitted.addLast(matrix.get(NDArrayIndex.interval(start,end), NDArrayIndex.all()));
        }
        return splitted;
    }

    public INDArray CombineMatrix(LinkedList<INDArray> Temp) {
        // init the matrix
        INDArray newMatrix = Nd4j.zeros(0,0);
        int start = 0;
        for (INDArray matrix: Temp) {
            // concat them one on top of the other
            // ORDER MATTERS
            newMatrix = Nd4j.vstack(newMatrix,matrix);
        }
        return newMatrix;
    }


    public void CalculatePMatrix(){
        System.out.println(R.getDouble(0,149));
        P = greaterThanOrEqual(R, Nd4j.zeros(R.rows(),R.columns()));
        System.out.println(P.getDouble(0,148));

    }

    public void loadOriginalMatrix(INDArray R){
        this.R = R;
        C = Nd4j.zeros(R.rows(), R.columns());
        P = Nd4j.zeros(R.rows(),R.columns());
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
                }
                case X_CALCULATED:{
                    XMessages.add(message);
                    if(XMessages.size() >= howManyWorkersToWait){
                        int iter = 0;
                        int epoch = 200;

                        LinkedList<INDArray> XDist = new LinkedList<>();

                        //Ascending sort of fromUser
                        XMessages.sort(Comparator.comparingInt(CommunicationMessage::getFromUser));
                        XMessages.forEach((msg) -> XDist.add(msg.getxArray()));

                        //TODO Den exw idea pws na kanw to linked list se 1 array, des an yparxei
                        //TODO Ready mathodos alliws tha to dw egw manually
                        //TODO De kserw pou tha ginei to calculateerror, an otan erthei to y h to x

                        //TODO 7/4/2018 this work will be done in workers
                        //CalculateXDerative();

                        double error = CalculateError();
                        System.out.println(error);
                        System.out.println("Iter: "+iter);
                        // genika mia apisteyta mikri timi allagis metajy dyo iteration
                        if(error< 0.001){

                        }

                        //When finished, clear it for the next loop
                        XMessages.clear();
                    }
                }
                case Y_CALCULATED:{
                    YMessages.add(message);
                    if(YMessages.size() >= howManyWorkersToWait){
                        LinkedList<INDArray> YDist = new LinkedList<>();

                        //Ascending sort of fromUser
                        YMessages.sort(Comparator.comparingInt(CommunicationMessage::getFromUser));
                        YMessages.forEach((msg) -> YDist.add(msg.getxArray()));

                        //TODO IDIA DOULEIA OPWS PRIN

                        //When finished, clear it for the next loop
                        YMessages.clear();
                    }
                }
                default:{
                    break;
                }
            }

            System.out.println("Received Message From: " + message.getServerName());
        }
        catch (ClassNotFoundException | IOException ignored) {}
    }

    /**
     * IMaster Implementation
     */
    public void Initialize() {
        availableWorkers = new ArrayList<>();
        XMessages = new ArrayList<>();
        YMessages = new ArrayList<>();

        INDArray originalMatrix = ParserUtils.loadDataset("data/inputMatrix.csv");
        ParserUtils.loadDataset("data/inputMatrix.csv");
        loadOriginalMatrix(originalMatrix);

        SendBroadcastMessageToWorkers(new CommunicationMessage());

        this.OpenServer();
    }


    public void CalculateCMatrix() {
       C = (R.mul(a)).add(1);
    }

    public  void DistributeXMatrixToWorkers() {
        int totalCores = availableWorkers
                .stream()
                .map(Server::getCpuCores)
                .reduce(0, (a, b) -> a+b);

        int currentIndex = 0;
        int rowsPerCore = X.rows() / totalCores;
        for(int i = 0; i < availableWorkers.size(); i++){
            Worker worker = availableWorkers.get(i);
            int workerRows = rowsPerCore * worker.getCpuCores();

            CommunicationMessage xMessage = new CommunicationMessage();
            xMessage.setType(MessageType.CALCULATE_X);
            xMessage.setyArray(Y);
            xMessage.setFromUser(0);
            xMessage.setToUser(workerRows);

            currentIndex += workerRows;

            if(i == availableWorkers.size() - 1){
                if(currentIndex != X.rows()){
                    xMessage.setToUser(X.rows()-1);
                }
            }

            SendMessageToWorker(xMessage, worker);
        }
    }

    public void DistributeYMatrixToWorkers() {
        int totalCores = availableWorkers
                .stream()
                .map(Server::getCpuCores)
                .reduce(0, (a, b) -> a+b);

        int currentIndex = 0;
        int rowsPerCore = Y.rows() / totalCores;
        for(int i = 0; i < availableWorkers.size(); i++){
            Worker worker = availableWorkers.get(i);
            int workerRows = rowsPerCore * worker.getCpuCores();

            CommunicationMessage xMessage = new CommunicationMessage();
            xMessage.setType(MessageType.CALCULATE_Y);
            xMessage.setxArray(X);
            xMessage.setFromUser(0);
            xMessage.setToUser(workerRows);

            currentIndex += workerRows;

            if(i == availableWorkers.size() - 1){
                if(currentIndex != Y.rows()){
                    xMessage.setToUser(Y.rows()-1);
                }
            }

            SendMessageToWorker(xMessage, worker);
        }
    }

    public void DistributeCPMatricesToWorkers(){
        CommunicationMessage msg = new CommunicationMessage();
        msg.setType(MessageType.TRANSFER_CP);
        msg.setcArray(C);
        msg.setpArray(P);

        SendBroadcastMessageToWorkers(msg);
    }

    public double CalculateError() {
        // multiply c
        // do the inside Σ actions as parallel
        // as possible
        int k =0;
        long starttime = System.nanoTime();

        double result = ((pow(P.sub(X.mmul(Y.transpose())), 2)).mul(C)).sumNumber().doubleValue();


        System.out.println("Calculated Score Time: "+ParserUtils.GetTimeInMs(starttime));
        return result;
    }

    public double CalculateScore(int x, int y) {
        return 0;
    }

    public List<Poi> CalculateBestLocalPoisForUser(int x, double xBound, double yBound, int y) {
        return null;
    }

    public void SendBroadcastMessageToWorkers(CommunicationMessage message) {
        for(Worker worker : availableWorkers){
            SendMessageToWorker(message, worker);
        }
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

        try{
            if (in != null) {
                in.close();
            }
            if (out != null){
                out.close();
            }
            if (socket != null){
                socket.close();
            }
        }
        catch(IOException ignored){}
    }

    public int getHowManyWorkersToWait() {
        return howManyWorkersToWait;
    }

    public void setHowManyWorkersToWait(int howManyWorkersToWait) {
        this.howManyWorkersToWait = howManyWorkersToWait;
    }
}
