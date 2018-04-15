package gr.aueb.dist.Server;

import gr.aueb.dist.Abstractions.IServer;
import gr.aueb.dist.Models.CommunicationMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements IServer, Runnable{
    private String name;
    private String ip;
    private int port;
    private int cpuCores;
    private int ramSize;

    /* Define the socket that receives requests */
    private ServerSocket providerSocket;

    /* Define the socket that is used to handle the connection */
    private Socket socketConn = null;

    protected Server() {}

    /**
     * Runnable Implementation
     */
    public void run(){}

    /**
     * IServer Implementation
     */
    public void OpenServer(){
        try {
            providerSocket = new ServerSocket(getPort());

            System.out.println(getInstanceName() + " " + getName() + " " + getIp() + ":" + getPort() + " server opened!");

            //noinspection InfiniteLoopStatement
            while (true) {
                socketConn = providerSocket.accept();

                /*
                    Creates a new thread from the runnable implementation
                    instance and start it. Remember this will call the
                    override method of Worker/Master/Client respectively
                 */
                (new Thread(this)).start();
            }
        }catch(IOException ignored){}
        finally {
            if(providerSocket != null){
                try{
                    providerSocket.close();
                    System.out.println(getInstanceName() + " " + getName() + " " + getIp() + ":" + getPort() + " server closed!");
                }catch(IOException ignored){}
            }
        }
    }

    public void CloseServer(){
        if(providerSocket != null){
            try{
                providerSocket.close();
            }catch(IOException ignored){}
        }
    }

    public void SendCommunicationMessage(CommunicationMessage message, String ip, int port) {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        Socket socket = null;

        boolean messageSent = false;

        try{
            socket = new Socket(ip, port);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(message);
            out.flush();

            messageSent = true;
        }catch(IOException ex){
            System.out.println("Got exception while sending results to master...");
            ex.printStackTrace();
        }
        finally {
            CloseConnections(socket, in, out);
        }

        if(!messageSent){
            System.out.println("I am trying again!");
            SendCommunicationMessage(message, ip, port);
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

    /**
     * System Information
     */

    protected String getInstanceName(){
        if(this instanceof Worker){ return "Worker"; }
        else if(this instanceof Master){ return "Master"; }
        else {return "Client"; }
    }

    protected int getCpuCores(){
        return Runtime.getRuntime().availableProcessors();
    }

    protected long getAvailableRamSizeInGB(){
        com.sun.management.OperatingSystemMXBean fragment =
                (com.sun.management.OperatingSystemMXBean)
                        ManagementFactory.getOperatingSystemMXBean();

        long availRamInBytes = fragment.getTotalPhysicalMemorySize();
        return availRamInBytes / 1024 / 1024 / 1024;
    }

    /**
     * Getters and Setters
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    protected int getInstanceCpuCores() {
        return cpuCores;
    }

    protected void setInstanceCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    protected int getInstanceRamSize() {
        return ramSize;
    }

    protected void setInstanceRamSize(int ramSize) {
        this.ramSize = ramSize;
    }

    public ServerSocket getProviderSocket() {
        return providerSocket;
    }

    public void setProviderSocket(ServerSocket providerSocket) {
        this.providerSocket = providerSocket;
    }

    protected Socket getSocketConn() {
        return socketConn;
    }

    public void setSocketConn(Socket socketConn) {
        this.socketConn = socketConn;
    }

    @Override
    public String toString() {
        return "**************************************" +
                "\n" + getInstanceName() + ": " + name +
                "\n" + "IP: " + ip + ":" + port +
                "\n" + "Available CPU Cores: " + getCpuCores() +
                "\n" + "Available Ram Size " + getAvailableRamSizeInGB() + "GB" +
                "\n" + "**************************************";
    }
}
