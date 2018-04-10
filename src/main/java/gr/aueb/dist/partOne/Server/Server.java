package gr.aueb.dist.partOne.Server;

import gr.aueb.dist.partOne.Models.Worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
    private String id;
    private String ip;
    private int port;
    private boolean cancelReceived = false;

    /* Define the socket that receives requests */
    private ServerSocket providerSocket;

    /* Define the socket that is used to handle the connection */
    private Socket socketConn = null;

    protected Server() {}

    public void run(){}

    protected void OpenServer(){
        try {
            providerSocket = new ServerSocket(getPort());

            System.out.println(getName() + " " + getId() + " " + getIp() + ":" + getPort() + " server opened!");

            while (!cancelReceived) {
                socketConn = providerSocket.accept();

                (new Thread(this)).start();
            }
        }catch(IOException ignored){}
        finally {
            if(providerSocket != null){
                try{
                    providerSocket.close();
                    System.out.println(getName() + " " + getId() + " " + getIp() + ":" + getPort() + " server closed!");
                }catch(IOException ignored){}
            }
        }
    }

    public void CloseServer(){
        if(providerSocket != null){
            cancelReceived = true;

            try{
                providerSocket.close();
                System.out.println(getName() + " " + getId() + " " + getIp() + ":" + getPort() + " server closed!");
            }catch(IOException ignored){}
        }
    }

    public void CloseConnections(ObjectInputStream in, ObjectOutputStream out){
        try{
            if (in != null) {
                in.close();
            }
            if (out != null){
                out.close();
            }
        }
        catch(IOException ignored){}
    }

    public void CloseConnections(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        try{
            if (socket != null){
                socket.close();
            }
            CloseConnections(in, out);
        }
        catch(IOException ignored){}
    }

    /* System Information */

    public String getName(){
        if(this instanceof Worker){ return "Worker"; }
        else { return "Master"; }
    }

    public int getCpuCores(){
        return Runtime.getRuntime().availableProcessors();
    }

    public long getAvailableRamSizeInGB(){
        com.sun.management.OperatingSystemMXBean fragment =
                (com.sun.management.OperatingSystemMXBean)
                        ManagementFactory.getOperatingSystemMXBean();

        long availRamInBytes = fragment.getTotalPhysicalMemorySize();
        return availRamInBytes / 1024 / 1024 / 1024;
    }

    /* Getters and Setters */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerSocket getProviderSocket() {
        return providerSocket;
    }

    public void setProviderSocket(ServerSocket providerSocket) {
        this.providerSocket = providerSocket;
    }

    public Socket getSocketConn() {
        return socketConn;
    }

    public void setSocketConn(Socket socketConn) {
        this.socketConn = socketConn;
    }

    @Override
    public String toString() {
        return "**************************************" +
                "\n" + getName() + ": " + id +
                "\n" + "IP: " + ip + ":" + port +
                "\n" + "Available CPU Cores: " + getCpuCores() +
                "\n" + "Available Ram Size " + getAvailableRamSizeInGB() + "GB" +
                "\n" + "**************************************";
    }
}
