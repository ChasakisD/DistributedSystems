package com.distributedsystems.recommendationsystemclient.Models;

import java.io.Serializable;
import java.util.List;

public class CommunicationMessage implements Serializable{
    private MessageType type;

    private int userToAsk;
    private int radiusInKm;

    private double userLat;
    private double userLng;

    private List<Poi> poisToReturn;

    public CommunicationMessage() {}

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public int getUserToAsk() {
        return userToAsk;
    }

    public void setUserToAsk(int userToAsk) {
        this.userToAsk = userToAsk;
    }

    public int getRadiusInKm() {
        return radiusInKm;
    }

    public void setRadiusInKm(int radiusInKm) {
        this.radiusInKm = radiusInKm;
    }

    public double getUserLat() {
        return userLat;
    }

    public void setUserLat(double userLat) {
        this.userLat = userLat;
    }

    public double getUserLng() {
        return userLng;
    }

    public void setUserLng(double userLng) {
        this.userLng = userLng;
    }

    public List<Poi> getPoisToReturn() {
        return poisToReturn;
    }

    public void setPoisToReturn(List<Poi> poisToReturn) {
        this.poisToReturn = poisToReturn;
    }
}
