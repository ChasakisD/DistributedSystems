package gr.aueb.dist.partOne.Abstractions;

import gr.aueb.dist.partOne.Models.Poi;
import org.apache.commons.math3.linear.RealMatrix;
import sun.awt.image.ImageWatched;

import java.util.LinkedList;
import java.util.List;

public interface IMaster {
    void Initialize();
    void TransferCMatrix();
    void TransferPMatrix();
    RealMatrix CombineMatrix(LinkedList<RealMatrix> Temp);
    // Will split the matrix to equal
    LinkedList<RealMatrix> SplitMatrix(RealMatrix matrix, int partsToSplit);
    void DistributeXMatrixToWorkers();
    void DistributeYMatrixToWorkers();
    void DistributeCMatrixToWorkers();
    void DistributePMatrixToWorkers();
    double CalculateError();
    double CalculateScore(int x, int y);
    List<Poi> CalculateBestLocalPoisForUser(int x, double xBound, double yBound, int y);
}
