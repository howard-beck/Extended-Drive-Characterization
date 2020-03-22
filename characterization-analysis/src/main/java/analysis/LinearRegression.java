package analysis;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.linsol.svd.SolvePseudoInverseSvd_DDRM;
import org.ejml.simple.SimpleMatrix;

public class LinearRegression {
    public static SimpleMatrix solve(SimpleMatrix X, SimpleMatrix Y, boolean useIntercept) {
        SimpleMatrix x;

        if (useIntercept) {
            x = new SimpleMatrix(X.numCols() + 1, X.numRows());
            x.insertIntoThis(0, 1, X);
            for (int i = 0; i < X.numRows(); i++) {
                x.set(i, 0, 1);
            }
        } else {
            x = X.copy();
        }

        DMatrixRMaj solution = new DMatrixRMaj(X.numRows(), 1);

        SolvePseudoInverseSvd_DDRM solver = new SolvePseudoInverseSvd_DDRM(x.numRows(), x.numCols());
        solver.setA(X.getMatrix());
        solver.solve(Y.getMatrix(), solution);

        return new SimpleMatrix(solution);
    }

    public static SimpleMatrix solve(SimpleMatrix X, SimpleMatrix Y) {
        return solve(X, Y, false);
    }
}