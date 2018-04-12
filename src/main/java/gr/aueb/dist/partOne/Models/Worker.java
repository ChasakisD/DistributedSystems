package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Abstractions.IWorker;
import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.MessageType;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.inverse.InvertMatrix;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.IntStream;

public class Worker extends Server implements IWorker{
    private int cpuCores;
    private int ramSize;

    private INDArray X, Y, P, C;

    private final static double L = 0.1;

    public Worker() {}

    /**
     * Runnable Implementation
     */
    public synchronized void run() {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try{
            out = new ObjectOutputStream(getSocketConn().getOutputStream());
            in = new ObjectInputStream(getSocketConn().getInputStream());

            CommunicationMessage message = (CommunicationMessage) in.readObject();

            CommunicationMessage result = new CommunicationMessage();
            result.setServerName(getId());
            result.setRamGBSize((int)getAvailableRamSizeInGB());
            switch (message.getType()){
                case TRANSFER_MATRICES:{
                    C = message.getCArray();
                    P = message.getPArray();
                    X = message.getXArray();
                    Y = message.getYArray();

                    System.out.println("Got the matrices from master!");
                    return;
                }
                case CALCULATE_X:{
                    Y = message.getYArray();

                    long startTime = System.nanoTime();
                    CalculateXDerivative(message.getFromUser(), message.getToUser());
                    double executionTime = ParserUtils.GetTimeInSec(startTime);

                    result.setExecutionTime(executionTime);
                    result.setType(MessageType.X_CALCULATED);
                    result.setFromUser(message.getFromUser());
                    result.setToUser(message.getToUser());
                    result.setXArray(X);

                    System.out.println("Finished X Calculation from :" + message.getFromUser() + " to " + message.getToUser());
                    break;
                }
                case CALCULATE_Y:{
                    X = message.getXArray();

                    long startTime = System.nanoTime();
                    CalculateYDerivative(message.getFromUser(), message.getToUser());
                    double executionTime = ParserUtils.GetTimeInSec(startTime);

                    result.setExecutionTime(executionTime);
                    result.setType(MessageType.Y_CALCULATED);
                    result.setFromUser(message.getFromUser());
                    result.setToUser(message.getToUser());
                    result.setYArray(Y);

                    System.out.println("Finished Y Calculation from :" + message.getFromUser() + " to " + message.getToUser());
                    break;
                }
                default:{
                    break;
                }
            }

            SendResultsToMaster(result);
        }
        catch (ClassNotFoundException | IOException ignored) {}
        finally {
            this.CloseConnections(in, out);
        }
    }

    /**
     * IWorker Implementation
     */
    public void Initialize() {
        CommunicationMessage msg = new CommunicationMessage();
        msg.setServerName(getId());
        msg.setIp(getIp());
        msg.setPort(getPort());
        msg.setCpuCores(getCpuCores());
        msg.setRamGBSize((int)getAvailableRamSizeInGB());
        msg.setType(MessageType.HELLO_WORLD);

        SendResultsToMaster(msg);

        this.OpenServer();

        System.out.println("I did what i must do dear Master!");
    }

    public INDArray CalculateCuMatrix(int user, INDArray C) {
        return Nd4j.diag(C.getRow(user));
    }

    public INDArray CalculateCiMatrix(int item, INDArray C) {
        return Nd4j.diag(C.getColumn(item));
    }

    public INDArray PreCalculateXX(INDArray X) {
        return X.transpose().mmul(X);
    }

    public INDArray PreCalculateYY(INDArray Y) {
        return Y.transpose().mmul(Y);
    }

    public void SendResultsToMaster(CommunicationMessage message) {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        Socket socket = null;
        try{
            Master master = ParserUtils.GetServersFromText("data/master.txt");

            if(master == null) return;

            socket = new Socket(master.getIp(), master.getPort());

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(message);
            out.flush();
        }catch(IOException ignored){}
        finally {
            this.CloseConnections(socket, in, out);
        }
    }

    /**
     * Helper Methods
     */
    public void CalculateXDerivative(int startIndex, int endIndex){
        X = Nd4j.zeros(endIndex - startIndex + 1, X.columns());

        INDArray YY = PreCalculateYY(Y);

        IntStream.range(startIndex, endIndex + 1).parallel().forEach((user) -> {
            INDArray Cu = CalculateCuMatrix(user, C);
            INDArray Pu = P.getRow(user);
            X.putRow(user - startIndex, CalculateDerivative(Y, Pu, Cu, YY));
        });
    }

    public void CalculateYDerivative(int startIndex, int endIndex){
        Y = Nd4j.zeros(endIndex - startIndex + 1, Y.columns());

        INDArray XX = PreCalculateXX(X);

        IntStream.range(startIndex, endIndex + 1).parallel().forEach((poi) -> {
            INDArray Ci = CalculateCiMatrix(poi, C);
            INDArray Pi = P.getColumn(poi).transpose();
            Y.putRow(poi - startIndex, CalculateDerivative(X, Pi, Ci, XX));
        });
    }

    public INDArray CalculateDerivative(INDArray matrix, INDArray Pu, INDArray Cu, INDArray YY) {
        // (Cu - I)
        INDArray result = (Cu.sub(Nd4j.eye(Cu.rows())));
        // Y.T(Cu - I)Y
        result = matrix.transpose().mmul(result).mmul(matrix);
        // Y.TY + Y.T(Cu - I)Y
        result.addi(YY);
        // Y.TY + Y.T(Cu - I)Y +λI
        result.addi(Nd4j.eye(result.rows()).mul(L));
        //invert the matrix
        result = InvertMatrix.invert(result,true);

        return Pu
                .mmul(Cu)
                .mmul(matrix)
                .mmul(result);
    }

    /**
     * Getters and Setters
     */
    public int getInstanceCpuCores() {
        return cpuCores;
    }

    public void setInstanceCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public int getInstanceRamSize() {
        return ramSize;
    }

    public void setInstanceRamSize(int ramSize) {
        this.ramSize = ramSize;
    }

    @Override
    public String toString() {
        return "**************************************" +
                "\n" + getName() + ": " + getId() +
                "\n" + "IP: " + getIp() + ":" + getPort() +
                "\n" + "Available CPU Cores: " + getInstanceCpuCores() +
                "\n" + "Available Ram Size " + getInstanceRamSize() + "GB" +
                "\n" + "**************************************";
    }
}
