package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Abstractions.IWorker;
import gr.aueb.dist.partOne.Client.Main;
import gr.aueb.dist.partOne.Server.CommunicationMessage;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Worker extends Server implements IWorker, Runnable{
    RealMatrix R, P, C;
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

    public void CalculateCMatrix(int x, RealMatrix R) {
        for (int u = 0 ; u < R.getRowDimension(); u++) {
            for (int i = 0; i < R.getColumnDimension(); i++) {
                C.setEntry(u, i, 1 + a*R.getEntry(u,i));
            }
        }
    }

    public void CalculateCuMatrix(int x, RealMatrix C) {
        
    }

    public void CalculateCiMatrix(int x, RealMatrix C) {

    }

    public RealMatrix PreCalculateYY(RealMatrix matrix) {
        return matrix.transpose().multiply(matrix);
    }

    public RealMatrix PreCalculateXX(RealMatrix matrix) {
        return matrix.transpose().multiply(matrix);
    }

    public RealMatrix CalculateXU(int x, RealMatrix matrixX, RealMatrix matrixU) {
        return null;
    }

    public RealMatrix CalculateYI(int x, RealMatrix matrixY, RealMatrix matrixI) {
        return null;
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
