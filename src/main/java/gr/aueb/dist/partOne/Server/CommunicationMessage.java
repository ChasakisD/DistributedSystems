package gr.aueb.dist.partOne.Server;

import java.io.Serializable;

public class CommunicationMessage implements Serializable {
    private String serverName;

    public CommunicationMessage() {}

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
