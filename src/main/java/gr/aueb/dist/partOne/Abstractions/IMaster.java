package gr.aueb.dist.partOne.Abstractions;

import gr.aueb.dist.partOne.Models.Poi;
import gr.aueb.dist.partOne.Models.CommunicationMessage;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.HashMap;
import java.util.List;

public interface IMaster {
    void Initialize();
    void TransferMatricesToWorkers();
    void DistributeXMatrixToWorkers();
    void DistributeYMatrixToWorkers();
    HashMap<String, Integer[]> SplitMatrix(INDArray matrix, String matrixName);
    void SendBroadcastMessageToWorkers(CommunicationMessage message);
    double CalculateError();
    double CalculateScore(int x, int y);
    List<Poi> CalculateBestLocalPOIsForUser(int user, int numberOfResults);
}
