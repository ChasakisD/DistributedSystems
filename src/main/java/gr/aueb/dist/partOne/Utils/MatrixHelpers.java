package gr.aueb.dist.partOne.Utils;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.concurrent.ThreadLocalRandom;

public class MatrixHelpers {
    public static RealMatrix GenerateRandomMatrix(RealMatrix R, int K, boolean isY){
        // Create the matrix
        RealMatrix matrix = MatrixUtils.createRealMatrix(isY ? R.getColumnDimension() : R.getRowDimension(), K);
        // Fill it with random values between zero and 1
        for (int u = 0; u < matrix.getRowDimension(); u++) {
            for (int k = 0; k < matrix.getColumnDimension(); k++) {
                // Enter a random value between 0 and 1
                matrix.setEntry(u, k, ThreadLocalRandom.current().nextDouble(0, 1));
            }
        }

        return matrix;
    }
}
