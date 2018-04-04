package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Abstractions.IMaster;
import gr.aueb.dist.partOne.Abstractions.IWorker;
import gr.aueb.dist.partOne.Client.Main;
import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Worker extends Server implements Runnable{
    INDArray X, Y,  R, P, C;
    final  static  int a = 40;

    public Worker() {}

    /**
     * Runnable Implementation
     */
    public void run() {

    }





    /**
     * IWorker Implementation
     */
    public void Initialize() {
        CommunicationMessage msg = new CommunicationMessage();
        msg.setServerName(getId());
        SendResultsToMaster(msg);
        System.out.println("I did what i must do dear Master!");
    }

    public INDArray CalculateCuMatrix(int user, INDArray C) {
        // TODO: 04-Apr-18 Ρωτα αν το γαμημενο dimension που λεει στο paper ειναι αυτο. 
        return C.getRow(user).mul(Nd4j.eye(C.columns()));
    }


    public void CalculateXDerative(){
        INDArray YY = PreCalculateYY(Y);
        // for each user in X
        for (int u = 0; u < X.rows(); u++) {
            // Get Cu
            INDArray Cu = CalculateCuMatrix(u, C);
            INDArray Pu = P.getRow(u);
            INDArray temp = Cu.sub(Nd4j.eye(Cu.rows()));
            Cu.mmul(Pu);

        }
    }

    public void CalculateCiMatrix(int x, RealMatrix matrix) {

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

    public void calculateXDerative(){

    }


    public void SendResultsToMaster(CommunicationMessage message) {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        Socket socket = null;
        try{
            ArrayList<Server> masters = ParserUtils.GetServersFromText("data/master.txt", true);
            Master master = (Master) masters.get(0);

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
}
