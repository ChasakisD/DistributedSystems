package gr.aueb.dist.Abstractions;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface IWorker {
    void Initialize();
    INDArray CalculateCuMatrix(int x, INDArray matrix);
    INDArray CalculateCiMatrix(int x, INDArray matrix);
    INDArray PreCalculateYY(INDArray matrix);
    INDArray PreCalculateXX(INDArray matrix);
    INDArray CalculateDerivative(INDArray matrix, INDArray Pu, INDArray Cu, INDArray YY);
    void CalculateXDerivative(int startIndex, int endIndex);
    void CalculateYDerivative(int startIndex, int endIndex);
}
