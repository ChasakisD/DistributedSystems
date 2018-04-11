package gr.aueb.dist.partOne.Server;

import org.apache.commons.math3.linear.RealMatrix;

import java.io.Serializable;

public class CommunicationMessage implements Serializable {
    private MessageType type;

    private RealMatrix cArray;
    private RealMatrix pArray;
    private RealMatrix xArray;
    private RealMatrix yArray;

    private int fromUser;
    private int toUser;

    private String serverName;
    private String ip;
    private int port;
    private int cpuCores;
    private int ramGBSize;

    private long executionTime;

    public CommunicationMessage() {}

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public RealMatrix getCArray() {
        return cArray;
    }

    public void setCArray(RealMatrix cArray) {
        this.cArray = cArray;
    }

    public RealMatrix getPArray() {
        return pArray;
    }

    public void setPArray(RealMatrix pArray) {
        this.pArray = pArray;
    }

    public RealMatrix getXArray() {
        return xArray;
    }

    public void setXArray(RealMatrix xArray) {
        this.xArray = xArray;
    }

    public RealMatrix getYArray() {
        return yArray;
    }

    public void setYArray(RealMatrix yArray) {
        this.yArray = yArray;
    }

    public int getFromUser() {
        return fromUser;
    }

    public void setFromUser(int fromUser) {
        this.fromUser = fromUser;
    }

    public int getToUser() {
        return toUser;
    }

    public void setToUser(int toUser) {
        this.toUser = toUser;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public int getRamGBSize() {
        return ramGBSize;
    }

    public void setRamGBSize(int ramGBSize) {
        this.ramGBSize = ramGBSize;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
