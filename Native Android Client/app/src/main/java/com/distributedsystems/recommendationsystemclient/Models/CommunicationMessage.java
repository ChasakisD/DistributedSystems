package com.distributedsystems.recommendationsystemclient.Models;

import java.io.Serializable;
import java.util.List;

public class CommunicationMessage implements Serializable{
    private MessageType type;

    private int userToAsk;
    private int howManyPoisToRecommend;
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
