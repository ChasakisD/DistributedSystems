package gr.aueb.dist.partOne.Abstractions;

import gr.aueb.dist.partOne.Models.Poi;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.List;

public interface IMaster {
    void Initialize();
    void CalculateCMatrix(int x, RealMatrix matrix);
    void CalculatePMatrix(int x, RealMatrix matrix);
    void DistributeXMatrixToWorkers(int x, int y, RealMatrix matrix);
    void DistributeYMatrixToWorkers(int x, int y, RealMatrix matrix);
    double CalculateError();
    double CalculateScore(int x, int y);
    List<Poi> CalculateBestLocalPoisForUser(int x, double xBound, double yBound, int y);
}
