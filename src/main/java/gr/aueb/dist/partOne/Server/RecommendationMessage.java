package gr.aueb.dist.partOne.Server;

import gr.aueb.dist.partOne.Models.Poi;

import java.io.Serializable;
import java.util.List;

public class RecommendationMessage implements Serializable {
    private int userToAsk;
    private List<Poi> poisToReturn;

    private MessageType messageType;

    public RecommendationMessage() {}

    /**
     *   Getters and Setters
     */
    public int getUserToAsk() {
        return userToAsk;
    }

    public void setUserToAsk(int userToAsk) {
        this.userToAsk = userToAsk;
    }

    public List<Poi> getPoisToReturn() {
        return poisToReturn;
    }

    public void setPoisToReturn(List<Poi> poisToReturn) {
        this.poisToReturn = poisToReturn;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
