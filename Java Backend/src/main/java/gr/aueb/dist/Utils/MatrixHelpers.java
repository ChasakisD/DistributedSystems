package gr.aueb.dist.Utils;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.concurrent.ThreadLocalRandom;

public class MatrixHelpers {

    /**
     * Generates a random Matrix
     * @param R R DataSet Matrix
     * @param K K Dimension
     * @param isY If the generated matrix is the Y or the X
     * @return the random matrix
     */
    public static INDArray GenerateRandomMatrix(INDArray R, int K, boolean isY){
        /* Create the matrix */
        INDArray matrix = Nd4j.zeros(isY ? R.columns() : R.rows(), K);

        /* Fill it with random values between zero and 1 */
        for (int u = 0; u < matrix.rows(); u++) {
            for (int k = 0; k < matrix.columns(); k++) {
                /* Enter a random value between 0 and 1 */
                matrix.putScalar(u, k, ThreadLocalRandom.current().nextDouble(0, 1));
            }
        }

        return matrix;
    }
}
