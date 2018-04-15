package gr.aueb.dist.partOne.Abstractions;

import gr.aueb.dist.partOne.Models.CommunicationMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public interface IServer {
    void OpenServer();
    void CloseServer();
    void SendCommunicationMessage(CommunicationMessage message, String ip, int port);
    void CloseConnections(ObjectInputStream in, ObjectOutputStream out);
    void CloseConnections(Socket socket, ObjectInputStream in, ObjectOutputStream out);
}
