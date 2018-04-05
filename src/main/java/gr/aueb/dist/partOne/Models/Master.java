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
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.Op;
import org.nd4j.linalg.api.ops.impl.transforms.Sigmoid;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.util.NDArrayMath;
import org.nd4j.linalg.util.NDArrayUtil;
import org.nd4j.nativeblas.NativeOps;
import sun.awt.image.ImageWatched;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.nd4j.linalg.ops.transforms.Transforms;

import static org.nd4j.linalg.factory.Nd4j.cumsum;
import static org.nd4j.linalg.factory.Nd4j.sum;
import static org.nd4j.linalg.ops.transforms.Transforms.*;


public class Master extends Server{

    INDArray R, P, C , X , Y;
    int numberOfWorkers;

    // Holding the matrices to be distrubted to the workers
    // Use it as a FIFO
    LinkedList<INDArray> C_Matrices = new LinkedList<>();
    LinkedList<INDArray> P_Matrices = new LinkedList<>();
    LinkedList<INDArray> X_Matrices = new LinkedList<>();
    LinkedList<INDArray> Y_Matrices = new LinkedList<>();
    final static double a = 40;
    static  double l = 0.1;

    public static void main(String[] args){
        ArrayList<Server> masters = ParserUtils.GetServersFromText("data/master.txt", true);
        INDArray originalMatrix = ParserUtils.loadDataset("data/inputMatrix.csv");
        Master master = (Master) masters.get(0);
        ParserUtils.loadDataset("data/inputMatrix.csv");
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

    public void TransferCMatrix(){}


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
        int BiggestDimension = R.columns() > R.rows()?
                                R.columns(): R.rows();
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

    public INDArray PreCalculateYY(INDArray Y) {
        return Y.transpose().mmul(Y);
    }

    // TODO: 04-Apr-18 Η μεθοδος πρεπει να ειναι στον worker to εβαλα εδω για testing
    public INDArray CalculateCuMatrix(int user, INDArray C) {
        return Nd4j.diag(C.getRow(user));
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

    // Ειναι ετοιμη αλλαζει το X. Φροντισε απλα να είναι στην class
    // Δεν γυρναει τιποτα
    public void CalculateXDerative(int user){
        INDArray YY = PreCalculateYY(Y);
        // Get Cu
        INDArray Cu = CalculateCuMatrix(user, C);
        // Get the row
        INDArray Pu = P.getRow(user);

        X.putRow(user,CalculateDerivative(Y,Pu,Cu,YY,l));

    }


    public void Mainloop(){
        int iter = 0;
        int epoch = 200;
        while (iter<epoch){
            // WaitX = GetXResultsFromWorkers();
            // Send the new X needed for Y calculation
            // DistributeXToWorkers()
            // Wait for Y results
            // With both X and Y

            for (int i = 0; i < Y.rows(); i++) {
                INDArray YY = PreCalculateYY(X);
                // Get Cu
                INDArray Cu = CalculateCuMatrix(i,C);
                // Get the row
                INDArray Pu = P.getColumn(i);

                X.putRow(i,CalculateDerivative(Y,Pu,Cu,YY,l));

            }


            double error = CalculateError();
            System.out.println(error);
            System.out.println("Iter: "+iter);
            // genika mia apisteyta mikri timi allagis metajy dyo iteration
            if(error< 0.0000001){

            }
            iter++;
        }
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



    // Sent the P matrix to all of the workers
    public void TransferPMatrix(){

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



    public void CalculateCMatrix() {
       C = (R.mul(a)).add(1);


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
}
