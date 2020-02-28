package ssinai.scribbler.ops;

import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionConversionException;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.CombinatoricsUtils;

import org.apache.commons.math3.util.Precision;
import ssinai.scribbler.parser.Parser;
import org.apache.log4j.Logger;

import ssinai.scribbler.parser.ScribVector;

import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Oct 4, 2009
 * Time: 10:31:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class MathOps {

    static private Logger logger = Parser.getLogger();
    public static void init () {
        logger.info("In MathOps init");
    }

    public static Number abs (Number n) {
        return Math.abs(n.doubleValue());
    }

    public static Boolean and (Boolean a, Boolean b) {
        System.out.println("in and a="+a+", b="+b);
        return a && b;
    }

    public static Boolean or (Boolean a, Boolean b) {
        System.out.println("in or a="+a+", b="+b);
        return a || b;
    }

    public static Number ceil (Number n) {
        return Math.ceil(n.doubleValue());
    }

    public static Number factorial (Number n) {
        return CombinatoricsUtils.factorialDouble(n.intValue());
    }


    public static Number comb (Number n, Number k) {
        return binom(n.intValue(), k.intValue());
    }


    public static Number perm (Number n, Number r) {
        BigInteger fact = BigInteger.ONE;
        for (int i = n.intValue() ; i > (n.intValue()-r.intValue()) ; i--) {
            fact = fact.multiply(new BigInteger(Integer.toString(i)));
        }
        return fact;
    }

    public static Number binom (Number n, Number k) {
        return CombinatoricsUtils.binomialCoefficientDouble(n.intValue(),k.intValue());
    }

    public static Number binom (Number n, Number x, Number p) {
        BinomialDistribution bdist =
                new BinomialDistribution(n.intValue(), p.doubleValue());
        return bdist.probability(x.intValue());
    }

    public static Number binom (Number n, RealVector v, Number p) {

        BinomialDistribution bdist =
                new BinomialDistribution(n.intValue(), p.doubleValue());
        double [] data = v.toArray();
        double sum = 0;
        for (Double d : data) {
            sum += bdist.probability(d.intValue());
        }
        return sum;
    }

    public static Number cumprob (Number n, Number x, Number p) {
        BinomialDistribution bdist = new BinomialDistribution(n.intValue(), p.doubleValue());

            return bdist.cumulativeProbability(x.intValue());

    }

    public static RealVector binomdist (Number max, Number p) {
        BinomialDistribution bdist =
                new BinomialDistribution(max.intValue(), p.doubleValue());
        double [] newData = new double[max.intValue()+1];

        for (int i = 0 ; i <= max.intValue() ; i++) {
            double prob = bdist.probability(i);
            newData[i] = prob;
        }
        return new ScribVector(newData);
    }

    public static Boolean lt (Number n1, Number n2) {
        return n1.doubleValue() < n2.doubleValue();
    }

    public static Boolean lte (Number n1, Number n2) {
        return n1.doubleValue() <= n2.doubleValue();
    }

    public static Boolean gt (Number n1, Number n2) {
        return n1.doubleValue() > n2.doubleValue();
    }

    public static Boolean gte (Number n1, Number n2) {
        return n1.doubleValue() >= n2.doubleValue();
    }

    public static Boolean isEquals (Number n1, Number n2) {
        String s = n1.toString();
        int index = s.indexOf(".");

        if (index != -1) {
            int decimals = s.length() - index-1;
            Double d = Precision.round(n2.doubleValue(), decimals);
            return n1.equals(d);
        }
        return n1.equals(n2);
    }

    public static Boolean isEquals (Boolean n1, Boolean n2) {
        return n1.equals(n2);
    }

    public static Boolean notEquals (Number n1, Number n2) {
        return !n1.equals(n2);
    }

    public static Boolean notEquals (Boolean n1, Boolean n2) {
        return !n1.equals(n2);
    }

    public static Number add (Number n1, Number n2) {
        return (n1.doubleValue() + n2.doubleValue());
    }

    public static Number subtract (Number n1, Number n2) {
        return n1.doubleValue() - n2.doubleValue();
    }

    public static Number multiply (Number n1, Number n2) {
        return n1.doubleValue() * n2.doubleValue();
    }

    public static Number divide (Number n1, Number n2) {
        System.out.println("in divide n1="+n1+", n2="+n2);
        if (n2.doubleValue() == 0.0) {
            throw new ArithmeticException("custom / by zero");
        }
        return (n1.doubleValue() / n2.doubleValue());
    }

    public static Number pow (Number n1, Number n2) {
        return (Math.pow(n1.doubleValue(), n2.doubleValue()));
    }

    public static Number sqrt (Number n) {
        return Math.sqrt(n.doubleValue());
    }

    public static Number max (Number a, Number b) {
        return Math.max(a.doubleValue(), b.doubleValue());
    }

    public static Number min (Number a, Number b) {
        return Math.min(a.doubleValue(), b.doubleValue());
    }


    public static Number negate (Number d) {
        return -d.doubleValue();
    }

    public static Boolean negate (Boolean b) {
        return !b;
    }

    public static Fraction fraction(Number d) {

        try {
            return new Fraction (d.doubleValue());
        } catch (FractionConversionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

}

