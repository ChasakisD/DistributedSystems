package gr.aueb.dist.Models;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.Serializable;
import java.util.List;

public class CommunicationMessage implements Serializable {
    private MessageType type;

    private INDArray cArray;
    private INDArray pArray;
    private INDArray xArray;
    private INDArray yArray;

    private int startIndex;
    private int endIndex;

    private String serverName;
    private String ip;
    private int port;
    private int cpuCores;
    private int ramGBSize;

    private double executionTime;

    private int userToAsk;
    private int howManyPoisToRecommend;
    private List<Poi> poisToReturn;


    public CommunicationMessage() {}

    /**
     *   Getters and Setters
     */
    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public INDArray getCArray() {
        return cArray;
    }

    public void setCArray(INDArray cArray) {
        this.cArray = cArray;
    }

    public INDArray getPArray() {
        return pArray;
    }

    public void setPArray(INDArray pArray) {
        this.pArray = pArray;
    }

    public INDArray getXArray() {
        return xArray;
    }

    public void setXArray(INDArray xArray) {
        this.xArray = xArray;
    }

    public INDArray getYArray() {
        return yArray;
    }

    public void setYArray(INDArray yArray) {
        this.yArray = yArray;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
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

    public double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }

    public int getUserToAsk() {
        return userToAsk;
    }

    public void setUserToAsk(int userToAsk) {
        this.userToAsk = userToAsk;
    }

    public int getHowManyPoisToRecommend() {
        return howManyPoisToRecommend;
    }

    public void setHowManyPoisToRecommend(int howManyPoisToRecommend) {
        this.howManyPoisToRecommend = howManyPoisToRecommend;
    }

    public List<Poi> getPoisToReturn() {
        return poisToReturn;
    }

    public void setPoisToReturn(List<Poi> poisToReturn) {
        this.poisToReturn = poisToReturn;
    }
}
