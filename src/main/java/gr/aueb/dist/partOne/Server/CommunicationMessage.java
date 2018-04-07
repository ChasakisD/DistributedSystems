package gr.aueb.dist.partOne.Server;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.Serializable;

public class CommunicationMessage implements Serializable {
    private MessageType type;

    private INDArray cArray;
    private INDArray pArray;
    private INDArray xArray;
    private INDArray yArray;

    private int fromUser;
    private int toUser;

    private String serverName;
    private String ip;
    private int port;
    private int cpuCores;
    private int ramGBSize;

    public CommunicationMessage() {}

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public INDArray getcArray() {
        return cArray;
    }

    public void setcArray(INDArray cArray) {
        this.cArray = cArray;
    }

    public INDArray getpArray() {
        return pArray;
    }

    public void setpArray(INDArray pArray) {
        this.pArray = pArray;
    }

    public INDArray getxArray() {
        return xArray;
    }

    public void setxArray(INDArray xArray) {
        this.xArray = xArray;
    }

    public INDArray getyArray() {
        return yArray;
    }

    public void setyArray(INDArray yArray) {
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
}
