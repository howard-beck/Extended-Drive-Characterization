package analysis.utils;

import org.ejml.simple.SimpleMatrix;

// utils specifically for 2x2 matrices
public class Matrix2Utils {
    public static class Matrix2 {
        public Complex a;
        public Complex b;
        public Complex c;
        public Complex d;

        public Matrix2(SimpleMatrix mat) {
            a = new Complex(mat.get(0));
            b = new Complex(mat.get(1));
            c = new Complex(mat.get(2));
            d = new Complex(mat.get(3));
        }

        public Matrix2(Complex a, Complex b, Complex c, Complex d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public Matrix2(double a, double b, double c, double d) {
            this(new Complex(a), new Complex(b), new Complex(c), new Complex(d));
        }

        public Matrix2 add(Matrix2 mat) {
            return new Matrix2(a.add(mat.a), b.add(mat.b), c.add(mat.c), d.add(mat.d));
        }

        public Matrix2 mult(Complex x) {
            return new Matrix2(a.mult(x), b.mult(x), c.mult(x), d.mult(x));
        }

        public Matrix2 divide(Complex x) {
            return new Matrix2(a.divide(x), b.divide(x), c.divide(x), d.divide(x));
        }

        public SimpleMatrix toSM() {
            return new SimpleMatrix(2, 2, true, new double[] {a.a, b.a, c.a, d.a});
        }
    }



    public static Complex getDiscriminant2(Matrix2 mat) {
        return mat.a.subtract(mat.d).pow(2).add(mat.b.mult(mat.c).mult(4));
    }

    public static Complex getDiscriminant(Matrix2 mat) {
        return Complex.sqrt(getDiscriminant2(mat));
    }

    public static Complex sinh(Complex x) {
        return x.exp().subtract(x.negate().exp()).divide(2);
    }

    public static Complex cosh(Complex x) {
        return x.exp().add(x.negate().exp()).divide(2);
    }

    public static Matrix2 expm(Matrix2 mat) {
        // diagonalizable case --> find eigenvalues, diagonalize, take exponent of eigenvalues
        // non-diagonalizable case --> take limit of diagonalizable case

        // https://mathworld.wolfram.com/MatrixExponential.html

        // same eigenvalues --> not diagonalizable
        Complex disc2 = getDiscriminant2(mat);
        Complex coeff = mat.a.add(mat.d).divide(2).exp();
        Complex diffAD = mat.a.subtract(mat.d);

        if (getDiscriminant2(mat).mag2 == 0) {
            return new Matrix2(
                coeff.mult(Complex.ONE.add(mat.a.subtract(mat.d).divide(2))),
                coeff.mult(mat.b),
                coeff.mult(mat.c),
                coeff.mult(Complex.ONE.subtract(mat.a.subtract(mat.d).divide(2)))
            );
        } else {
            Complex disc = Complex.sqrt(disc2);

            Complex s = sinh(disc.divide(2));
            Complex c = cosh(disc.divide(2));

            Complex m11byDisc = disc.mult(c).add(s.mult(diffAD)).mult(coeff).divide(disc);
            Complex m12byDisc = s.mult(coeff.mult(mat.b).mult(2)).divide(disc);
            Complex m21byDisc = s.mult(coeff.mult(mat.c).mult(2)).divide(disc);
            Complex m22byDisc = disc.mult(c).add(s.mult(diffAD)).mult(coeff).divide(disc);

            return new Matrix2(
                m11byDisc.a,
                m12byDisc.a,
                m21byDisc.a,
                m22byDisc.a
            );
        }
    }

    public static Matrix2[][] expmDeriv(Matrix2 mat) {
        return expmDeriv(mat, expm(mat));
    }

    // derivative of matrix w.r.t. every component in mat
    // 2x2x2x2 tensor :O
    // exp argument to avoid double calculations
    public static Matrix2[][] expmDeriv(Matrix2 mat, Matrix2 exp) {
        Complex disc2 = getDiscriminant2(mat);
        Complex disc = Complex.sqrt(disc2);

        Complex m11 = disc.mult(exp.a);
        Complex m12 = disc.mult(exp.b);
        Complex m21 = disc.mult(exp.c);
        Complex m22 = disc.mult(exp.d);

        Complex coeff = mat.a.add(mat.d).divide(2).exp();
        Complex s = sinh(disc.divide(2));
        Complex c = cosh(disc.divide(2));
        Complex diffAD = mat.a.subtract(mat.d);

        Complex discInv = disc.inv();
        Complex discA = discInv.mult(diffAD);
        Complex discB = discInv.mult(mat.c).mult(4);
        Complex discC = discInv.mult(mat.b).mult(4);
        Complex discD = discA.negate();



        Matrix2 m11partial = new Matrix2(
            m11.divide(2).add(s), m12.divide(2),
            m21.divide(2),        m22.divide(2).subtract(s)
        );

        Matrix2 m12partial = new Matrix2(
            Complex.ZERO, Complex.ZERO,
            Complex.ZERO, Complex.ZERO
        );

        Matrix2 m21partial = new Matrix2(
            Complex.ZERO, Complex.ZERO,
            Complex.ZERO, Complex.ZERO
        );

        Matrix2 m22partial = new Matrix2(
            m11.divide(2).subtract(s), m12.divide(2),
            m21.divide(2),             m22.divide(2).add(s)
        );

        // partial derivative of exponential w.r.t. discriminant
        Matrix2 termDisc;

        // calculate complete derivative of mXY by doing
        // partial eXY / partial aIJ + partial eXY / partial delta * partial delta / partial aIJ
        // where a is the matrix being exponentiated

        if (disc2.mag2 == 0) {
            // same eigenvalues

            
            // assuming continuous derivative,
            // https://www.wolframalpha.com/input/?i=lim_%28x+approaches+0%29+d%2Fdx+e%5E%28%28a%2Bd%29%2F2%29%2Fx*%28x+cosh%28x%2F2%29+%2B+%28a-d%29+sinh%28x%2F2%29%29
            // https://www.wolframalpha.com/input/?i=lim_%28x+approaches+0%29+d%2Fdx+e%5E%28%28a%2Bd%29%2F2%29%2Fx*%28x+cosh%28x%2F2%29+-+%28a-d%29+sinh%28x%2F2%29%29
            // https://www.wolframalpha.com/input/?i=lim_%28x+approaches+0%29+d%2Fdx+2*b*e%5E%28%28a+%2B+d%29%2F2%29+sinh%28x%2F2%29%2Fx
            // these are all 0, so just first derivative term :)

            termDisc = new Matrix2(
                Complex.ZERO, Complex.ZERO,
                Complex.ZERO, Complex.ZERO
            );
        } else {
            Complex m11Disc = coeff.divide(2).mult(disc.mult(s).add(Complex.TWO.add(diffAD).mult(c)));
            Complex m12Disc = coeff.mult(2).mult(mat.b).mult(c);
            Complex m21Disc = coeff.mult(2).mult(mat.c).mult(c);
            Complex m22Disc = coeff.divide(2).mult(disc.mult(s).add(Complex.TWO.subtract(diffAD).mult(c)));

            // termXYDisc = mXYDisc / disc - mXY / disc2
            termDisc = new Matrix2(
                m11Disc.divide(disc).subtract(m11.divide(disc2)),
                m12Disc.divide(disc).subtract(m12.divide(disc2)),
                m21Disc.divide(disc).subtract(m21.divide(disc2)),
                m22Disc.divide(disc).subtract(m22.divide(disc2))
            );
        }

        Complex term11A = m11partial.a.divide(disc).subtract(m11.mult(discA).divide(disc2)).add(termDisc.a.mult(discA));
        Complex term11B = m11partial.b.divide(disc).subtract(m11.mult(discB).divide(disc2)).add(termDisc.a.mult(discB));
        Complex term11C = m11partial.c.divide(disc).subtract(m11.mult(discC).divide(disc2)).add(termDisc.a.mult(discC));
        Complex term11D = m11partial.d.divide(disc).subtract(m11.mult(discD).divide(disc2)).add(termDisc.a.mult(discD));

        Complex term12A = m11partial.a.divide(disc).subtract(m12.mult(discA).divide(disc2)).add(termDisc.b.mult(discA));
        Complex term12B = m12partial.b.divide(disc).subtract(m12.mult(discB).divide(disc2)).add(termDisc.b.mult(discB));
        Complex term12C = m12partial.c.divide(disc).subtract(m12.mult(discC).divide(disc2)).add(termDisc.b.mult(discC));
        Complex term12D = m12partial.d.divide(disc).subtract(m12.mult(discD).divide(disc2)).add(termDisc.b.mult(discD));

        Complex term21A = m21partial.a.divide(disc).subtract(m21.mult(discA).divide(disc2)).add(termDisc.c.mult(discA));
        Complex term21B = m21partial.b.divide(disc).subtract(m21.mult(discB).divide(disc2)).add(termDisc.c.mult(discB));
        Complex term21C = m21partial.c.divide(disc).subtract(m21.mult(discC).divide(disc2)).add(termDisc.c.mult(discC));
        Complex term21D = m21partial.d.divide(disc).subtract(m21.mult(discD).divide(disc2)).add(termDisc.c.mult(discD));

        Complex term22A = m22partial.a.divide(disc).subtract(m22.mult(discA).divide(disc2)).add(termDisc.d.mult(discA));
        Complex term22B = m22partial.b.divide(disc).subtract(m22.mult(discB).divide(disc2)).add(termDisc.d.mult(discB));
        Complex term22C = m22partial.c.divide(disc).subtract(m22.mult(discC).divide(disc2)).add(termDisc.d.mult(discC));
        Complex term22D = m22partial.d.divide(disc).subtract(m22.mult(discD).divide(disc2)).add(termDisc.d.mult(discD));



        Matrix2 termA = new Matrix2(term11A, term12A, term21A, term22A);
        Matrix2 termB = new Matrix2(term11B, term12B, term21B, term22B);
        Matrix2 termC = new Matrix2(term11C, term12C, term21C, term22C);
        Matrix2 termD = new Matrix2(term11D, term12D, term21D, term22D);

        return new Matrix2[][] {
            new Matrix2[] {termA, termB},
            new Matrix2[] {termC, termD},
        };
    }
}