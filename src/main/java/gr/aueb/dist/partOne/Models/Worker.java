package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.MessageType;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Worker extends Server{
    private int cpuCores;
    private int ramSize;

    private INDArray X, Y, P, C;
    private double l;
    private final static int a = 40;

    public Worker() {}

    /**
     * Runnable Implementation
     */
    public synchronized void run() {
        try{
            ObjectOutputStream out = new ObjectOutputStream(getSocketConn().getOutputStream());
            ObjectInputStream in = new ObjectInputStream(getSocketConn().getInputStream());

            CommunicationMessage message = (CommunicationMessage) in.readObject();

            CommunicationMessage result = new CommunicationMessage();
            result.setServerName(getName());
            result.setRamGBSize((int)getAvailableRamSizeInGB());
            switch (message.getType()){
                case TRANSFER_CP:{
                    C = message.getcArray();
                    P = message.getpArray();
                    System.out.println("C Array: " + C.rows() + " " + C.columns());
                    System.out.println("P Array: " + P.rows() + " " + P.columns());
                    return;
                }
                case CALCULATE_X:{
                    int fromUser = message.getFromUser();
                    int toUser = message.getToUser();

                    for(int i = fromUser; i <= toUser; i++){

                    }
                    break;
                }
                case CALCULATE_Y:{
                    break;
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
        // TODO: 04-Apr-18 Ρωτα αν το γαμημενο dimension που λεει στο paper ειναι αυτο. 
        return Nd4j.diag(C.getRow(user));
    }


    public INDArray CalculateDerivative(INDArray matrix,  INDArray Pu, INDArray Cu, INDArray YY, double l) {
        // (Cu - I)
        INDArray result = (Cu.sub(Nd4j.eye(Cu.rows())));
        // Y.T(Cu - I)Y
        result = matrix.transpose().mmul(result).mmul(matrix);

        // Y.TY + Y.T(Cu - I)Y
        result.addi(YY);
        // Y.TY + Y.T(Cu - I)Y +λI
        result.addi(Nd4j.eye(result.rows()).mul(l));
        //invert the matrix
        result = Nd4j.reverse(result);
        ParserUtils.PrintShape(result);
        INDArray secondPart = Pu.mmul(Cu    );
        secondPart = secondPart.mmul(matrix);

        INDArray finalPart = secondPart.mmul(result);
        return finalPart;
    }

    public INDArray CalculateCiMatrix(int item, INDArray matrix) {
        return Nd4j.diag(C.getColumn(item));

    }


    public INDArray PreCalculateYY(INDArray Y) {
        return Y.transpose().mmul(Y);
    }


    public INDArray PreCalculateXX(INDArray X) {
        return X.transpose().mmul(X);
    }

    public RealMatrix CalculateXU(int x, RealMatrix matrixX, RealMatrix matrixU) {
        return null;
    }

    public RealMatrix CalculateYI(int x, RealMatrix matrixY, RealMatrix matrixI) {
        return null;
    }


    // Ειναι ετοιμη αλλαζει το X. Φροντισε απλα να είναι στην class
    // Δεν γυρναει τιποτα
    public void CalculateXDerative(){
        for (int user = 0; user < X.rows() ; user++) {
            INDArray YY = PreCalculateYY(Y);
            // Get Cu
            INDArray Cu = CalculateCuMatrix(user, C);
            // Get the row
            INDArray Pu = P.getRow(user);

            X.putRow(user,CalculateDerivative(Y,Pu,Cu,YY,l));
        }
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
