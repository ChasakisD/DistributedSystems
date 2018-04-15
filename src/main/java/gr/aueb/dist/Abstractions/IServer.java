package gr.aueb.dist.Abstractions;

import gr.aueb.dist.Models.CommunicationMessage;

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
