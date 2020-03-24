package analysis.utils;

import org.ejml.simple.SimpleMatrix;

import jeigen.DenseMatrix;



public class Logm {
    public static SimpleMatrix logm(SimpleMatrix A) {
        // translate to a Jeigen matrix to calculate matrix logarithm
        DenseMatrix m = new DenseMatrix(A.numRows(), A.numCols());
        for (int i = 0; i < A.numRows() * A.numCols(); i++) {
            m.set(i, A.get(i));
        }
        // calculate logarithm
        m = m.mlog();
        // convert to an EJML matrix
        return new SimpleMatrix(A.numRows(), A.numCols(), true, m.getValues());
    }
}