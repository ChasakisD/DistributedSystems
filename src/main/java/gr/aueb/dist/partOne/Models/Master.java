package gr.aueb.dist.partOne.Models;

import gr.aueb.dist.partOne.Abstractions.IMaster;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.List;

public class Master implements IMaster{
    public void Initialize() {

    }

    public void CalculateCMatrix(int x, RealMatrix matrix) {

    }

    public void CalculatePMatrix(int x, RealMatrix matrix) {

    }

    public void DistributeXMatrixToWorkers(int x, int y, RealMatrix matrix) {

    }

    public void DistributeYMatrixToWorkers(int x, int y, RealMatrix matrix) {

    }

    public double CalculateError() {
        return 0;
    }

    public double CalculateScore(int x, int y) {
        return 0;
    }

    public List<Poi> CalculateBestLocalPoisForUser(int x, double xBound, double yBound, int y) {
        return null;
    }
}
