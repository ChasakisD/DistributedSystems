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

    public void CalculateCMatrix(int x, RealMatrix matrix) {

    }

    public void CalculatePMatrix(int x, RealMatrix matrix) {

    }

    public void DistributeXMatrixToWorkers(int x, int y, RealMatrix matrix) {

    }

    public void DistributeYMatrixToWorkers(int x, int y, RealMatrix matrix) {

    }

    public double CalculateError() {
        return 0;
    }

    public double CalculateScore(int x, int y) {
        return 0;
    }

    public List<Poi> CalculateBestLocalPoisForUser(int x, double xBound, double yBound, int y) {
        return null;
    }
}
