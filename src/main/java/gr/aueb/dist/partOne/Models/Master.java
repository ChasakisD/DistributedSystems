package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Abstractions.IMaster;
import gr.aueb.dist.partOne.Client.Main;
import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import javafx.concurrent.Task;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import sun.awt.image.ImageWatched;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class Master extends Server implements IMaster{

    RealMatrix R, P, C , X , Y;
    int numberOfWorkers;

    // Holding the matrices to be distrubted to the workers
    // Use it as a FIFO
    LinkedList<RealMatrix> C_Matrices = new LinkedList<>();
    LinkedList<RealMatrix> P_Matrices = new LinkedList<>();
    LinkedList<RealMatrix> X_Matrices = new LinkedList<>();
    LinkedList<RealMatrix> Y_Matrices = new LinkedList<>();
    final static double a = 40;
    static  double l = 0.1;

    public static void main(String[] args){
        ArrayList<Server> masters = ParserUtils.GetServersFromText("data/master.txt", true);
        RealMatrix originalMatrix = ParserUtils.loadDataset("data/inputMatrix.csv");
        Master master = (Master) masters.get(0);
        master.loadOriginalMatrix(originalMatrix);
        System.out.println("Number Of Workers: ");
        System.out.println();
        master.SetNumberOfWorkers(2);
        master.StartAlgorithm();

    }

    public Master(){

    }

    public void SetNumberOfWorkers(int x){
        numberOfWorkers = x;
    }

    public void StartAlgorithm(){
        // C and P matrices only need to be calculated once and passed once to the workers
        long startTime = System.nanoTime();
        CalculatePMatrix();
        System.out.println("Calculated P Matrix: "+ParserUtils.GetTimeInMs(startTime)+" MilliSecond");

        P_Matrices = SplitMatrix(P,numberOfWorkers);

        // TODO: 31-Mar-18 Make it async
        TransferPMatrix();



        startTime = System.nanoTime();
        CalculateCMatrix();
        System.out.println("Calculated C Matrix: "+ParserUtils.GetTimeInMs(startTime)+" MilliSecond");

        C_Matrices = SplitMatrix(C, numberOfWorkers);


        // TODO: 31-Mar-18 Make it async
        TransferCMatrix();

        // now the workers have all the necessary tables but the X and Y

        // let's get the K << max{U,I}
        // meaning a number much smaller than the biggest column or dimension
        int BiggestDimension = R.getColumnDimension() > R.getRowDimension()?
                                R.getColumnDimension(): R.getRowDimension();
        // TODO: 31-Mar-18 Steile gamimeno mail an ειναι κομπλε σαν τιμη ayto που εχω κανει
        int K = BiggestDimension / 10;

        // now let's generate them
        GenerateX(K);
        X_Matrices = SplitMatrix(X,numberOfWorkers);
        // TODO: 31-Mar-18 Make it Async
        DistributeXMatrixToWorkers();

        GenerateY(K);
        Y_Matrices = SplitMatrix(Y,numberOfWorkers);
        // TODO: 31-Mar-18 Make it async
        DistributeYMatrixToWorkers();
        Mainloop();
    }

    public void Mainloop(){
        // WaitX = GetXResultsFromWorkers();
        // Send the new X needed for Y calculation
        // DistributeXToWorkers()
        // Wait for Y results
        // With both X and Y
        CalculateError();
    }

    public void getXResults(){}

    public void GenerateX(int K){
        // Create the matrix
        X = MatrixUtils.createRealMatrix(R.getRowDimension(), K);
        Random rand = new Random();
        // Fill it with random values between zero and 1
        for (int u = 0; u < X.getRowDimension(); u++) {
            for (int k = 0; k < X.getColumnDimension(); k++) {
                // Enter a random value between 0 and 1
                X.setEntry(u,k,ThreadLocalRandom.current().nextDouble(0, 1));
            }
        }
    }

    public void GenerateY(int K){
        // Initialize the Y matrix
        Y = MatrixUtils.createRealMatrix(R.getColumnDimension(), K);
        Random rand = new Random();
        // Fill it with random values between zero and 1
        for (int u = 0; u < X.getRowDimension(); u++) {
            for (int k = 0; k < X.getColumnDimension(); k++) {
                // enter a random value between 0 and 1
                Y.setEntry(u,k,ThreadLocalRandom.current().nextDouble(0, 1));
            }
        }
    }


    // Sent the C matrix to all of the workers
    public void DiS(){

    }

    @Override
    // Returns a linked list with the matrices
    public LinkedList<RealMatrix> SplitMatrix(RealMatrix matrix, int numberOfWorkers){
        int start = 0;
        int end = matrix.getRowDimension() / numberOfWorkers;
        LinkedList<RealMatrix> splitted = new LinkedList<>();
        for (int u = 0; u < numberOfWorkers ; u++) {
            // if it's the last iteration, we want to make sure we lose no data
            if (numberOfWorkers - 1 == u) {
                splitted.addLast(matrix.getSubMatrix(start, matrix.getRowDimension() - 1, 0, matrix.getColumnDimension() -1));
            }
            splitted.addLast(matrix.getSubMatrix(start, end, 0, matrix.getColumnDimension() -1));
        }
        return splitted;
    }



    // Sent the P matrix to all of the workers
    public void TransferPMatrix(){

    }


    public RealMatrix CombineMatrix(LinkedList<RealMatrix> Temp) {
        // init the matrix
        RealMatrix newMatrix = MatrixUtils.createRealMatrix(Temp.getFirst().getRowDimension(),Temp.getFirst().getColumnDimension());
        int start = 0;
        for (RealMatrix matrix: Temp) {
            newMatrix.setSubMatrix(matrix.getData(), start,0);
            start += matrix.getRowDimension();
        }
        return newMatrix;
    }


    public void CalculatePMatrix(){
        for (int u = 0; u < R.getRowDimension(); u++) {
            for (int i = 0; i < R.getColumnDimension(); i++) {
                P.setEntry(u,i, R.getEntry(u,i) > 0 ? 1: 0);
            }
        }
    }

    public void loadOriginalMatrix(RealMatrix R){
        this.R = R;
        initCMatrix();
        initPMatrix();
    }

    public void initCMatrix(){
        C = MatrixUtils.createRealMatrix(R.getRowDimension(), R.getColumnDimension());
    }

    public void initPMatrix(){
        P = MatrixUtils.createRealMatrix(R.getRowDimension(), R.getColumnDimension());
    }

    // an δεν σου αρεσει να το σπας με static απλα βαλτο σαν μεθοδο
    public void DistributeCMatrixToWorkers() {
    }

    public void DistributePMatrixToWorkers(){}


    /**
     * Runnable Implementation
     */
    public synchronized void run() {
        try{
            ObjectOutputStream out = new ObjectOutputStream(getSocketConn().getOutputStream());
            ObjectInputStream in = new ObjectInputStream(getSocketConn().getInputStream());

            CommunicationMessage message = (CommunicationMessage) in.readObject();
            System.out.println("Received Message From: " + message.getServerName());
        }
        catch (ClassNotFoundException | IOException ignored) {}
    }

    /**
     * IMaster Implementation
     */
    public void Initialize() {
        this.OpenServer();
    }

    @Override
    public void TransferCMatrix() {

    }

    public void CalculateCMatrix() {
        RealMatrix R_Copy = R.copy();
        C = R_Copy.scalarMultiply(a);
        C = C.scalarAdd(1);
    }

    public void DistributeXMatrixToWorkers() {

    }

    public void DistributeYMatrixToWorkers() {

    }

    public double CalculateError() {
        // multiply c
        // do the inside Σ actions as parallel
        // as possible
        int k =0;
        long starttime = System.nanoTime();
        RealMatrix p_copy = P.copy();
        RealMatrix tempResult = Y.transpose().preMultiply(X);
        RealMatrix temp = p_copy.subtract(tempResult);
        System.out.println(temp.getRowDimension()+" " + temp.getColumnDimension());


        return 0;
    }

    public double CalculateScore(int x, int y) {
        return 0;
    }

    public List<Poi> CalculateBestLocalPoisForUser(int x, double xBound, double yBound, int y) {
        return null;
    }
}
