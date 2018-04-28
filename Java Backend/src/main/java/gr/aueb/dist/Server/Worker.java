package gr.aueb.dist.Server;

import gr.aueb.dist.Abstractions.IWorker;
import gr.aueb.dist.Models.CommunicationMessage;
import gr.aueb.dist.Models.MessageType;
import gr.aueb.dist.Utils.ParserUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.inverse.InvertMatrix;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.stream.IntStream;

public class Worker extends Server implements IWorker {
    private String masterIp;
    private int masterPort;

    private INDArray X, Y, P, C;

    private final static double L = 0.1;

    Worker(String name, String ip, int port){
        this.setName(name);
        this.setIp(ip);
        this.setPort(port);
    }

    public Worker(String name, String ip, int port, String masterIp, int masterPort){
        this(name, ip, port);
        this.masterIp = masterIp;
        this.masterPort = masterPort;
    }

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
            result.setServerName(getName());
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
                    CalculateXDerivative(message.getStartIndex(), message.getEndIndex());
                    double executionTime = ParserUtils.GetTimeInSec(startTime);

                    result.setExecutionTime(executionTime);
                    result.setType(MessageType.X_CALCULATED);
                    result.setStartIndex(message.getStartIndex());
                    result.setEndIndex(message.getEndIndex());
                    result.setXArray(X);

                    System.out.println("Finished X Calculation from :" + message.getStartIndex() + " to " + message.getEndIndex());
                    break;
                }
                case CALCULATE_Y:{
                    X = message.getXArray();

                    long startTime = System.nanoTime();
                    CalculateYDerivative(message.getStartIndex(), message.getEndIndex());
                    double executionTime = ParserUtils.GetTimeInSec(startTime);

                    result.setExecutionTime(executionTime);
                    result.setType(MessageType.Y_CALCULATED);
                    result.setStartIndex(message.getStartIndex());
                    result.setEndIndex(message.getEndIndex());
                    result.setYArray(Y);

                    System.out.println("Finished Y Calculation from :" + message.getStartIndex() + " to " + message.getEndIndex());
                    break;
                }
                default:{
                    break;
                }
            }

            this.SendCommunicationMessage(result, masterIp, masterPort);
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
        msg.setServerName(getName());
        msg.setIp(getIp());
        msg.setPort(getPort());
        msg.setCpuCores(getCpuCores());
        msg.setRamGBSize((int)getAvailableRamSizeInGB());
        msg.setType(MessageType.HELLO_WORLD);

        this.SendCommunicationMessage(msg, masterIp, masterPort);

        this.OpenServer();
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

    /**
     * Helper Methods
     */
    public void CalculateXDerivative(int startIndex, int endIndex){
        /* Initialize the X with length rows */
        X = Nd4j.zeros(endIndex - startIndex + 1, X.columns());

        INDArray YY = PreCalculateYY(Y);

        /* Run the calculation for each user in parallel */
        IntStream.range(startIndex, endIndex + 1).parallel().forEach((user) -> {
            INDArray Cu = CalculateCuMatrix(user, C);
            INDArray Pu = P.getRow(user);
            X.putRow(user - startIndex, CalculateDerivative(Y, Pu, Cu, YY));
        });
    }

    public void CalculateYDerivative(int startIndex, int endIndex){
        /* Initialize the Y with length rows */
        Y = Nd4j.zeros(endIndex - startIndex + 1, Y.columns());

        INDArray XX = PreCalculateXX(X);

        /* Run the calculation for each poi in parallel */
        IntStream.range(startIndex, endIndex + 1).parallel().forEach((poi) -> {
            INDArray Ci = CalculateCiMatrix(poi, C);
            INDArray Pi = P.getColumn(poi).transpose();
            Y.putRow(poi - startIndex, CalculateDerivative(X, Pi, Ci, XX));
        });
    }

    public INDArray CalculateDerivative(INDArray matrix, INDArray Pu, INDArray Cu, INDArray YY) {
        /* (Cu - I) */
        INDArray result = (Cu.sub(Nd4j.eye(Cu.rows())));

        /* Y.T(Cu - I)Y */
        result = matrix.transpose().mmul(result).mmul(matrix);

        /* Y.TY + Y.T(Cu - I)Y */
        result.addi(YY);

        /* Y.TY + Y.T(Cu - I)Y + λI */
        result.addi(Nd4j.eye(result.rows()).mul(L));

        /* invert the matrix */
        result = InvertMatrix.invert(result,true);

        return Pu
                .mmul(Cu)
                .mmul(matrix)
                .mmul(result);
    }

    @Override
    public String toString() {
        return "**************************************" +
                "\n" + getInstanceName() + ": " + getName() +
                "\n" + "IP: " + getIp() + ":" + getPort() +
                "\n" + "Available CPU Cores: " + getInstanceCpuCores() +
                "\n" + "Available Ram Size " + getInstanceRamSize() + "GB" +
                "\n" + "**************************************";
    }
}
