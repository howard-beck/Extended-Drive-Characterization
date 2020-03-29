package analysis.utils;



public class Complex {
    public double a;
    public double b;
    public double mag2;
    public double mag;

    public Complex(double real, double imag) {
        a = real;
        b = imag;
        mag2 = a*a + b*b;
        mag = Math.sqrt(mag2);
    }

    public Complex(double real) {
        this(real, 0);
    }

    public Complex conj() {
        return new Complex(a, -b);
    }

    public Complex add(Complex c) {
        return new Complex(a + c.a, b + c.b);
    }

    public Complex subtract(Complex c) {
        return new Complex(a - c.a, b - c.b);
    }

    public Complex negate() {
        return new Complex(-a, -b);
    }

    public Complex mult(double m) {
        return new Complex(a * m, b * m);
    }

    public Complex mult(Complex c) {
        // (a + bi)(c + di) = (ac - bd) + (bc + ad)i

        return new Complex(a*c.a - b*c.b, b*c.a + a*c.b);
    }

    public Complex divide(Complex c) {
        // (a + bi)/(c + di) = (a + bi)*(c-di)/(c^2 + d^2)

        return mult(c.conj()).divide(c.mag2);
    }

    public Complex divide(double m) {
        return new Complex(a / m, b / m);
    }

    public Complex inv() {
        return conj().divide(mag2);
    }

    public Complex exp() {
        // e^(a + bi) = e^a e^(bi)
        double coeff = Math.exp(a);
        return new Complex(
            coeff * Math.cos(b),
            coeff * Math.sin(b)
        );
    }

    public Complex pow(double x) {
        double theta = Math.atan2(b, a) * x;
        double newMag = Math.pow(mag, x);

        return new Complex(
            newMag * Math.cos(theta),
            newMag * Math.sin(theta)
        );
    }

    public static Complex sqrt(double real) {
        if (real < 0) {
            return new Complex(0, Math.sqrt(-real));
        } else {
            return new Complex(Math.sqrt(real), 0);
        }
    }

    public static Complex sqrt(Complex c) {
        return c.pow(0.5);
    }

    public static Complex ZERO = new Complex(0, 0);
    public static Complex ONE = new Complex(1, 0);
    public static Complex TWO = new Complex(2, 0);
}