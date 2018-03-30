package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Abstractions.IMaster;
import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Master extends Server implements IMaster{

    RealMatrix R, P, C , X , Y;
    int numberOfWorkers;
    LinkedList<RealMatrix> CSplitMatrices = new LinkedList<>();
    final static double a = 40;
    static  double l = 0.1;
    public static void main(String[] args){
        ArrayList<Server> masters = ParserUtils.GetServersFromText("data/master.txt", true);
        RealMatrix originalMatrix = ParserUtils.loadDataset("data/inputMatrix.csv");
        Master master = (Master) masters.get(0);
        master.setR(originalMatrix);

    }

    public Master(){

    }


    public void setR(RealMatrix R){
        this.R = R.copy();
        initCMatrix();
        initPMatrix();
    }

    public void initCMatrix(){
        C = MatrixUtils.createRealMatrix(R.getRowDimension(), R.getColumnDimension());
    }

    public void initPMatrix(){
        C = MatrixUtils.createRealMatrix(R.getRowDimension(), R.getColumnDimension());
    }

    // an δεν σου αρεσει να το σπας με static απλα βαλτο σαν μεθοδο
    public void DistributeCMatrixToWorkers(RealMatrix r) {
        SplitCMatrix(numberOfWorkers);
        // it's sufficient to just pass each split RealMatrix to the workers
        // TODO: 29/3/2018 perase τα στοιχεια στους workers ενα splitlist einai 
    }

   
    @Override
    public void SplitCMatrix(int partsToSplit) {
        int sizeOfSubArrays = C.getRowDimension() / partsToSplit;
        int start = 0;
        int end = sizeOfSubArrays;
        for(int u = 0; u < partsToSplit; u++){
            RealMatrix newMatrix;
            // in case the division is not perfect
            // we want the last matrix to keep all the data that might not be included
            if(u == partsToSplit -1){
                newMatrix = C.getSubMatrix(start,C.getRowDimension(),
                        // get all submatrices
                        0,C.getColumnDimension());
            }else {
                newMatrix = C.getSubMatrix(start,end,
                        // get all submatrices
                        0,C.getColumnDimension());
            }
            start += sizeOfSubArrays;
            end += sizeOfSubArrays;
            CSplitMatrices.addLast(newMatrix);
        }

    }

    // TODO: 29/3/2018 Το χρησιμοποιεις για να ενωσεις τα στοιχεια που βρισκονται σοτ CSplitMatrices
    // TODO: 29/3/2018 Ο καινουριος πινακας θα ειναι static 
    public void CombineCMatrices(){
        // the matrices are on the same row
        // we use FIFO
        int column = C.getColumnDimension();
        int start = 0;
        for (int u = 0; u < CSplitMatrices.size(); u++) {
            RealMatrix temp = CSplitMatrices.getFirst();
            C.setSubMatrix(temp.getData(), start, column);
            start += temp.getRowDimension();
        }

    }

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

    public void CalculateCMatrix(RealMatrix r) {

    }

    public void CalculatePMatrix(RealMatrix r) {

    }

    public void DistributeXMatrixToWorkers(int x, int y, RealMatrix matrix) {

    }

    public void DistributeYMatrixToWorkers(int x, int y, RealMatrix matrix) {

    }

    public double CalculateError() {
        // multiply c
        // do the inside Σ actions as parallel
        // as possible
        c.multiply(
                // P table
                (p.subtract (
                        // X*Y
                        Y.preMultiply(X.transpose()))
                        )
                // increase the result to the power of 2
                .power(2));
        double sum = 0;

        // now calculate the Σ
        for (int u = 0; u < c.getRowDimension(); u++){
            for (int i = 0; i < c.getColumnDimension(); i++) {
                sum += c.getEntry(u,i);
            }
        }

        // now we calculate the second part of the equation

        // calculate the euclideian distance for each matrix
        RealMatrix tempX = X.preMultiply(X.transpose());
        RealMatrix tempY = Y.preMultiply(Y.transpose());
        double sumX = 0, sumY = 0;
        for (int u = 0; u < c.getRowDimension(); u++) {

            sumX += tempX.getEntry(u,0);
        }

        for (int i = 0; i < c.getColumnDimension(); i++) {
            // calculate the sum for each matrix
            sumY += tempY.getEntry(0, i);
        }


        return sum - l*(sumX + sumY);
    }

    public double CalculateScore(int x, int y) {
        return 0;
    }

    public List<Poi> CalculateBestLocalPoisForUser(int x, double xBound, double yBound, int y) {
        return null;
    }
}
