package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Abstractions.IMaster;
import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Master extends Server implements IMaster{

    RealMatrix p, c , X , Y;
    static double a = 40;
    static  double l = 0.1;
    public static void main(String[] args){
        ArrayList<Server> masters = ParserUtils.GetServersFromText("data/master.txt", true);

        Master master = (Master) masters.get(0);
        master.Initialize();
    }

    public Master(){}

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

    public void CalculateCMatrix(int x, RealMatrix r) {
        RealMatrix temp = r.copy();
        // a*R
        temp.scalarMultiply(a);
        temp.scalarAdd(1);
        c.add(temp);
    }

    public void CalculatePMatrix(int x, RealMatrix r) {
        for (int u = 0 ; u < r.getRowDimension(); u++){
            for(int i = 0; i < r.getColumnDimension(); i++){
                p.setEntry(u, i, r.getEntry(u,i) > 0 ? 1: 0);
            }
        }
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
