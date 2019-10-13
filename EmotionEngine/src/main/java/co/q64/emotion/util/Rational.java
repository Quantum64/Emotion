package co.q64.emotion.util;

import java.math.BigInteger;

public final class Rational implements Comparable<Rational> {
    public final static Rational ZERO = of(0);
    public final static Rational ONE = of(1);
    public final static Rational MINUS_ONE = of(-1);

    private BigInteger numerator;
    private BigInteger denominator;

    private Rational(BigInteger numerator, BigInteger denominator) {
        if (denominator.equals(BigInteger.ZERO))
            throw new ArithmeticException();

        this.numerator = numerator;
        this.denominator = denominator;

        reduce();
    }

    private Rational(BigInteger numerator) {
        this(numerator, BigInteger.ONE);
    }

    private Rational() {
        this(BigInteger.ZERO);
    }

    private void reduce() {
        boolean sign = false;
        if (numerator.signum() >= 0 ^ denominator.signum() >= 0) {
            sign = true;
        } else {
            if (numerator.equals(BigInteger.ZERO)) {
                denominator = BigInteger.ONE;
            }
        }

        numerator = numerator.abs();
        denominator = denominator.abs();

        if (sign) {
            numerator = numerator.multiply(BigInteger.valueOf(-1));
        }

        final BigInteger divisor = pgcd(numerator.abs(), denominator);
        if (!divisor.equals(BigInteger.ONE)) {
            numerator = numerator.divide(divisor);
            denominator = denominator.divide(divisor);
        }
    }

    public int intValue() {
        return numerator.intValue() / denominator.intValue();
    }

    public long longValue() {
        return numerator.longValue() / denominator.longValue();
    }

    public float floatValue() {
        return numerator.floatValue() / denominator.floatValue();
    }

    public double doubleValue() {
        return numerator.doubleValue() / denominator.doubleValue();
    }

    public static Rational of(long num, long denom) {
        return new Rational(BigInteger.valueOf(num), BigInteger.valueOf(denom));
    }

    public static Rational of(long num) {
        return of(num, 1);
    }

    public static Rational of(String s) {
        if (s.contains(".")) {
            int digitsDec = s.length() - 1 - s.indexOf('.');
            String d = "1";
            for (int i = 0; i < digitsDec; i++) {
                d += "0";
            }
            return of(s.replace(".", ""), d);
        }
        return of(s, "1");
    }

    public static Rational of(double d) {
        return of(String.valueOf(d));
    }

    public static Rational of(String n, String d) {
        return new Rational(new BigInteger(n), new BigInteger(d));
    }

    public BigInteger getNumerator() {
        return numerator;
    }

    public BigInteger getDenominator() {
        return denominator;
    }

    public Rational add(Rational n) {
        BigInteger num1 = numerator, denom1 = denominator, num2 = n.numerator, denom2 = n.denominator;

        num1 = num1.multiply(denom2);
        num2 = num2.multiply(denom1);
        BigInteger denom = denom1.multiply(denom2);

        return new Rational(num1.add(num2), denom);
    }

    public Rational substract(Rational n) {
        return add(new Rational(n.numerator.multiply(BigInteger.valueOf(-1)), n.denominator));
    }

    public Rational multiply(Rational n) {
        BigInteger num1 = numerator, denom1 = denominator, num2 = n.numerator, denom2 = n.denominator;

        num1 = num1.multiply(num2);
        denom1 = denom1.multiply(denom2);

        return new Rational(num1, denom1);
    }

    public Rational divide(Rational n) {
        return multiply(new Rational(n.denominator, n.numerator));
    }

    public Rational reciprocal() {
        return ONE.divide(this);
    }

    public boolean isInteger() {
        return denominator.equals(BigInteger.ONE);
    }

    public boolean isOne() {
        return numerator.equals(denominator);
    }

    public Rational abs() {
        return new Rational(numerator.abs(), denominator);
    }

    public Rational pow(int n) {
        Rational b = ONE;

        for (int i = 0; i < n; i++) {
            b = b.multiply(this);
        }

        return b;
    }

    private BigInteger pgcd(BigInteger a, BigInteger b) {
        BigInteger r;
        while (!b.equals(BigInteger.ZERO)) {
            r = a.mod(b);
            a = b;
            b = r;
        }
        return a;
    }

    @Override
    public String toString() {
        if (denominator.longValue() == 1) {
            return numerator.toString();
        }
        return numerator.toString() + "/" + denominator.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode(); // Since there is one unique string representation, this should be unique too.
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Rational) {
            Rational n = (Rational) object;
            if (numerator.equals(n.numerator) && denominator.equals(n.denominator)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(Rational o) {
        BigInteger a = numerator.multiply(o.denominator);
        BigInteger b = denominator.multiply(o.numerator);

        return a.compareTo(b);
    }
}