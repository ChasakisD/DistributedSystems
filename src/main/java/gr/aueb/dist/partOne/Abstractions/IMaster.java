package gr.aueb.dist.partOne.Abstractions;

import gr.aueb.dist.partOne.Models.Poi;
import gr.aueb.dist.partOne.Models.Worker;
import gr.aueb.dist.partOne.Server.CommunicationMessage;
import java.util.List;

public interface IMaster {
    void Initialize();
    void TransferMatricesToWorkers();
    void DistributeXMatrixToWorkers();
    void DistributeYMatrixToWorkers();
    void SendBroadcastMessageToWorkers(CommunicationMessage message);
    void SendMessageToWorker(CommunicationMessage message, Worker worker);
    double CalculateError();
    double CalculateScore(int x, int y);
    List<Poi> CalculateBestLocalPoisForUser(int x, double xBound, double yBound, int y);
}
