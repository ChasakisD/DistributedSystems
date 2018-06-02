package com.distributedsystems.recommendationsystems.Abstractions;

import com.distributedsystems.recommendationsystems.Models.CommunicationMessage;
import com.distributedsystems.recommendationsystems.Models.Poi;
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
    double CalculateDistance(double userLat, double userLon, double poiLat, double poiLon);
    List<Poi> CalculateBestLocalPOIsForUser(int user, int radius, double userLat, double userLng);
}
