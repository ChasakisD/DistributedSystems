package gr.aueb.dist.partOne.Server;

import gr.aueb.dist.partOne.Models.Worker;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;

public class Server {
    private String id;
    private String ip;
    private int port;

    private ServerSocket socketConn;

    public Server() {}

    public Server(String id, String ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    public void OpenServer(){
        try{
            socketConn = new ServerSocket(getPort());
            System.out.println(getName() + " " + getId() + " " + getIp() + ":" + getPort() + " server opened!");
        }catch(IOException ignored){}
        finally {
            if(socketConn != null){
                try{
                    socketConn.close();
                    System.out.println(getName() + " " + getId() + " " + getIp() + ":" + getPort() + " server closed!");
                }catch(IOException ignored){}
            }
        }
    }

    public void CloseServer(){
        if(socketConn != null){
            try{
                socketConn.close();
                System.out.println(getName() + " " + getId() + " " + getIp() + ":" + getPort() + " server closed!");
            }catch(IOException ignored){}
        }
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

    public ServerSocket getSocketConn() {
        return socketConn;
    }

    public void setSocketConn(ServerSocket socketConn) {
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
