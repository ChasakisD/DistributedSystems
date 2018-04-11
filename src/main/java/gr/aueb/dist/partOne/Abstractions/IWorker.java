package gr.aueb.dist.partOne.Abstractions;

import gr.aueb.dist.partOne.Server.CommunicationMessage;
import org.apache.commons.math3.linear.RealMatrix;

public interface IWorker {
    void Initialize();
    RealMatrix CalculateCuMatrix(int x, RealMatrix matrix);
    RealMatrix CalculateCiMatrix(int x, RealMatrix matrix);
    RealMatrix PreCalculateYY(RealMatrix matrix);
    RealMatrix PreCalculateXX(RealMatrix matrix);
    RealMatrix CalculateDerivative(RealMatrix matrix, RealMatrix Pu, RealMatrix Cu, RealMatrix YY);
    void CalculateXDerivative(int startIndex, int endIndex);
    void CalculateYDerivative(int startIndex, int endIndex);
    void SendResultsToMaster(CommunicationMessage msg);
}
