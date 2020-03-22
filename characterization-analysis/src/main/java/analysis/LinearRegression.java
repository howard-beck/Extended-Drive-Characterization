package analysis;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.linsol.svd.SolvePseudoInverseSvd_DDRM;
import org.ejml.simple.SimpleMatrix;

public class LinearRegression {
    private SimpleMatrix B;
    private double yAvg;
    private double R2;



    public LinearRegression(SimpleMatrix X, SimpleMatrix Y, boolean useIntercept) {
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

        B = new SimpleMatrix(solution);

        yAvg = Y.elementSum() / Y.numRows();

        double SStot = 0;
        double SSreg = 0;

        for (int i = 0; i < Y.numRows(); i++) {
            SStot += Math.pow(Y.get(i) - yAvg, 2);
            SSreg += Math.pow(x.extractVector(true, i).dot(B) - yAvg, 2);
        }

        R2 = SSreg / SStot;
    }

    public LinearRegression(SimpleMatrix X, SimpleMatrix Y) {
        this(X, Y, false);
    }



    public SimpleMatrix getSolution() { return B; }
    public double getR2() { return R2; }
}