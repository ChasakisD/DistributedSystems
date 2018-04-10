package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.MessageType;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.inverse.InvertMatrix;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Worker extends Server{
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
            result.setServerName(getName());
            result.setRamGBSize((int)getAvailableRamSizeInGB());
            switch (message.getType()){
                case TRANSFER_CP:{
                    C = message.getcArray();
                    P = message.getpArray();
                    X = message.getxArray();
                    Y = message.getyArray();
                    System.out.println("C Array: " + C.rows() + " " + C.columns());
                    System.out.println("P Array: " + P.rows() + " " + P.columns());
                    System.out.println("X Array: " + X.rows() + " " + X.columns());
                    System.out.println("Y Array: " + Y.rows() + " " + Y.columns());
                    return;
                }
                case CALCULATE_X:{
                    Y = message.getyArray();
                    System.out.println("Got Y From Master: " + Y.rows());

                    System.out.println("Going to calculate from :" + message.getFromUser() + " to " + message.getToUser() + " From X");
                    CalculateXDerivative(message.getFromUser(), message.getToUser());
                    result.setType(MessageType.X_CALCULATED);
                    result.setFromUser(message.getFromUser());
                    result.setToUser(message.getToUser());
                    result.setxArray(X);
                    break;
                }
                case CALCULATE_Y:{
                    X = message.getxArray();
                    System.out.println("Got X From Master: " + X.rows());

                    CalculateYDerivative(message.getFromUser(), message.getToUser());
                    result.setType(MessageType.Y_CALCULATED);
                    result.setFromUser(message.getFromUser());
                    result.setToUser(message.getToUser());
                    result.setyArray(Y);
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

    private INDArray CalculateCuMatrix(int user, INDArray C) {
        return Nd4j.diag(C.getRow(user));
    }

    private INDArray CalculateCiMatrix(int item, INDArray C) {
        return Nd4j.diag(C.getColumn(item));
    }

    private INDArray PreCalculateXX(INDArray X) {
        return X.transpose().mmul(X);
    }

    private INDArray PreCalculateYY(INDArray Y) {
        ParserUtils.PrintShape(Y);
        return Y.transpose().mmul(Y);
    }
    public RealMatrix CalculateXU(int x, RealMatrix matrixX, RealMatrix matrixU) {
        return null;
    }

    public RealMatrix CalculateYI(int x, RealMatrix matrixY, RealMatrix matrixI) {
        return null;
    }

    private void CalculateXDerivative(int start, int end){
        X = Nd4j.zeros((end-start+1), X.columns());
        int startingIndex = 0;
        INDArray YY = PreCalculateYY(Y);
        for (int user = start; user <= end; user++) {
            INDArray Cu = CalculateCuMatrix(user, C);
            INDArray Pu = P.getRow(user);
            X.putRow(startingIndex,CalculateDerivative(Y, Pu, Cu, YY, L));
            startingIndex++;
        }
    }

    private void CalculateYDerivative(int start, int end){
        Y = Nd4j.zeros((end-start+1), Y.columns());
        int startingIndex = 0;
        INDArray XX = PreCalculateXX(X);
        for (int poi = start; poi <= end; poi++) {
            INDArray Ci = CalculateCiMatrix(poi, C);
            INDArray Pi = P.getColumn(poi).transpose();
            Y.putRow(startingIndex, CalculateDerivative(X, Pi, Ci, XX, L));
            startingIndex++;
        }
    }

    private INDArray CalculateDerivative(INDArray matrix,  INDArray Pu, INDArray Cu, INDArray YY, double l) {
        // (Cu - I)
        INDArray result = (Cu.sub(Nd4j.eye(Cu.rows())));
        // Y.T(Cu - I)Y
        result = matrix.transpose().mmul(result).mmul(matrix);

        // Y.TY + Y.T(Cu - I)Y
        result.addi(YY);
        // Y.TY + Y.T(Cu - I)Y +λI
        result.addi(Nd4j.eye(result.rows()).mul(l));
        //invert the matrix
        result = InvertMatrix.invert(result,true);
        INDArray secondPart = Pu.mmul(Cu);
        secondPart = secondPart.mmul(matrix);

        return secondPart.mmul(result);
    }

    private void SendResultsToMaster(CommunicationMessage message) {
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
}
