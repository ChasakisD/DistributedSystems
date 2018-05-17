package com.distributedsystems.recommendationsystems.Abstractions;

import com.distributedsystems.recommendationsystems.Models.CommunicationMessage;

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
