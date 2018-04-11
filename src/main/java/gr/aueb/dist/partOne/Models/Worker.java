package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Abstractions.IWorker;
import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.MessageType;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Worker extends Server implements IWorker{
    private int cpuCores;
    private int ramSize;

    private RealMatrix X, Y, P, C;

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
                    CalculateXDerivative(message.getFromUser(), message.getToUser());
                    result.setType(MessageType.X_CALCULATED);
                    result.setFromUser(message.getFromUser());
                    result.setToUser(message.getToUser());
                    result.setXArray(X);

                    System.out.println("Finished X Calculation from :" + message.getFromUser() + " to " + message.getToUser());
                    break;
                }
                case CALCULATE_Y:{
                    X = message.getXArray();
                    CalculateYDerivative(message.getFromUser(), message.getToUser());
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

    public RealMatrix CalculateCuMatrix(int user, RealMatrix C) {
        return MatrixUtils.createRealDiagonalMatrix(C.getRow(user));
    }

    public RealMatrix CalculateCiMatrix(int item, RealMatrix C) {
        return MatrixUtils.createRealDiagonalMatrix(C.getColumn(item));
    }

    public RealMatrix PreCalculateXX(RealMatrix X) {
        return X.transpose().multiply(X);
    }

    public RealMatrix PreCalculateYY(RealMatrix Y) {
        return Y.transpose().multiply(Y);
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
        }catch(IOException ex){
            ex.printStackTrace();
        }
        finally {
            this.CloseConnections(socket, in, out);
        }
    }

    /**
     * Helper Methods
     */
    public void CalculateXDerivative(int startIndex, int endIndex){
        int startingIndex = 0;
        int length = endIndex - startIndex + 1;

        X = MatrixUtils.createRealMatrix(length, X.getColumnDimension());

        RealMatrix YY = PreCalculateYY(Y);

        for (int user = startIndex; user <= endIndex; user++) {
            RealMatrix Cu = CalculateCuMatrix(user, C);
            RealMatrix Pu = P.getRowMatrix(user);
            X.setRowMatrix(startingIndex, CalculateDerivative(Y, Pu, Cu, YY));
            startingIndex++;
        }
    }

    public void CalculateYDerivative(int startIndex, int endIndex){
        int startingIndex = 0;
        int length = endIndex - startIndex + 1;

        Y = MatrixUtils.createRealMatrix(length, Y.getColumnDimension());

        RealMatrix XX = PreCalculateXX(X);

        for (int poi = startIndex; poi <= endIndex; poi++) {
            RealMatrix Ci = CalculateCiMatrix(poi, C);
            RealMatrix Pi = P.getColumnMatrix(poi).transpose();
            Y.setRowMatrix(startingIndex, CalculateDerivative(X, Pi, Ci, XX));
            startingIndex++;
        }
    }

    public RealMatrix CalculateDerivative(RealMatrix matrix, RealMatrix Pu, RealMatrix Cu, RealMatrix YY) {
        // (Cu - I)
        RealMatrix result = (Cu.subtract(MatrixUtils.createRealIdentityMatrix(Cu.getRowDimension())));
        // Y.T(Cu - I)Y
        result = matrix.transpose().multiply(result).multiply(matrix);
        // Y.TY + Y.T(Cu - I)Y
        result.add(YY);
        // Y.TY + Y.T(Cu - I)Y +λI
        result.add(MatrixUtils.createRealIdentityMatrix(result.getRowDimension()).scalarMultiply(L));
        //invert the matrix
        result = new QRDecomposition(result).getSolver().getInverse();

        return Pu
                .multiply(Cu)
                .multiply(matrix)
                .multiply(result);
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
