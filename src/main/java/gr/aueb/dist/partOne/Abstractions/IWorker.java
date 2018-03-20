package gr.aueb.dist.partOne.Abstractions;

import gr.aueb.dist.partOne.Server.CommunicationMessage;
import org.apache.commons.math3.linear.RealMatrix;

public interface IWorker {
    void Initialize();
    void CalculateCMatrix(int x, RealMatrix matrix);
    void CalculateCuMatrix(int x, RealMatrix matrix);
    void CalculateCiMatrix(int x, RealMatrix matrix);
    RealMatrix PreCalculateYY(RealMatrix matrix);
    RealMatrix PreCalculateXX(RealMatrix matrix);
    RealMatrix CalculateXU(int x, RealMatrix matrixX, RealMatrix matrixU);
    RealMatrix CalculateYI(int x, RealMatrix matrixY, RealMatrix matrixI);
    void SendResultsToMaster(CommunicationMessage msg);
}
