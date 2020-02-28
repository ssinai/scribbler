package ssinai.scribbler.ops;

import org.apache.commons.math3.analysis.function.*;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Logger;
import ssinai.scribbler.parser.Parser;
import ssinai.scribbler.parser.ScribMatrix;
import ssinai.scribbler.parser.ScribVector;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Apr 7, 2010
 * Time: 3:11:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class VectorOps {

    static private Logger logger = Parser.getLogger();
    public static void init () {
        logger.info("In VectorOps init");
    }

    public static RealVector abs (RealVector v) {
  //      return new ScribVector(v.mapAbs());
            return new ScribVector(v.map(new Abs()));
    }

    public static RealVector acos (RealVector v) {
        return new ScribVector(v.map(new Acos()));
    }

    public static RealVector add (Number scalar, RealVector v) {
        return new ScribVector(v.mapAdd(scalar.doubleValue()));
    }

    public static RealVector add (RealVector v, Number scalar) {
        return add (scalar, v);
    }

    public static RealVector add (RealVector v1, RealVector v2) throws Exception {
        System.out.println("in VectorOps.add");
        try {
            return new ScribVector(v1.add(v2));
        } catch (Exception e) {
            System.out.println("vectorOps throwing exception "+e);
            throw e;
        }
    }

    public static RealVector append (RealVector v1, RealVector v2) {
        return new ScribVector(v1.append(v2));
    }

    public static RealVector asin (RealVector v) {
        return new ScribVector(v.map(new Asin()));
    }

    public static RealVector atan (RealVector v) {
        return new ScribVector(v.map(new Atan()));
    }

    public static RealVector cbrt (RealVector v) {
        return new ScribVector(v.map(new Cbrt()));
    }

    public static RealVector ceil (RealVector v) {
        return new ScribVector(v.map(new Ceil()));
    }

    public static RealVector copy (RealVector v) {
        return new ScribVector(v.copy());
    }

    public static RealVector cos (RealVector v) {
        return new ScribVector(v.map(new Cos()));
    }

    public static RealVector cosh (RealVector v) {
        return new ScribVector(v.map(new Cosh()));
    }

    public static RealVector diff (RealVector v1, RealVector v2) {
        double [] res = new double[v1.getDimension() * v2.getDimension()];
        ScribVector m3 = new ScribVector(res.length);
        for (int i = 0, index=0 ; i < v1.getDimension() ; i++) {
            for (int j = 0 ; j < v2.getDimension() ; j++) {
                double d = v1.getEntry(i) - v2.getEntry(j);
                m3.setEntry(index++, d);
            }
        }
        return m3;
        /*
            try {
                double [] res = new double[v1.getDimension() * v2.getDimension()];
                ScribVector m3 = new ScribVector(res.length);
                for (int i = 0, index=0 ; i < v1.getDimension() ; i++) {
                    for (int j = 0 ; j < v2.getDimension() ; j++) {
                        double d = v1.getEntry(i) - v2.getEntry(j);
                        m3.setEntry(index++, d);
                    }
                }
                return m3;
            } catch (Exception e) {
                throw new MPException(e.getMessage());
            }
        */

    }

    public static RealVector divide (RealVector v, Number scalar) {
        return new ScribVector(v.mapDivide(scalar.doubleValue()));
    }

    public static RealVector divide (RealVector v1, RealVector v2) {
        return new ScribVector(v1.ebeDivide(v2));
    }

    public static Number dim (RealVector v) {
        return v.getDimension();
    }

    public static Number dist (RealVector v1, RealVector v2) {
        return v1.getDistance(v2);
    }

    public static Double dot (RealVector v1, RealVector v2) {
        return v1.dotProduct(v2);
    }

    public static Number entry (RealVector v, Number index) {
        return v.getEntry(index.intValue());
    }

    public static RealVector exp (RealVector v) {
        return new ScribVector(v.map(new Exp()));
    }

    public static RealVector expm1 (RealVector v) {
        return new ScribVector(v.map(new Expm1()));
    }

    public static RealVector fillby (Number size, Number value) {
        double [] data = new double[size.intValue()];
        double newValue = 0;

        for (int i = 0 ; i < data.length ; i++) {
            data[i] = newValue;
            newValue += value.doubleValue();
        }

        return new ScribVector(data);
    }

    public static RealVector floor (RealVector v) {
        return new ScribVector(v.map(new Floor()));
    }

    public static RealVector inv (RealVector v) {
        return new ScribVector(v.map( new Inverse()));
    }

    public static Boolean isinfinite(RealVector v) {
        return v.isInfinite();
    }

    public static Boolean isnan (RealVector v) {
        return v.isNaN();
    }

    public static Number ldist (RealVector v1, RealVector v2) {
        return v1.getL1Distance(v2);
    }

    public static Number lnorm (RealVector v1) {
        return v1.getL1Norm();
    }

    public static Number linfdist (RealVector v1, RealVector v2) {
        return v1.getLInfDistance(v2);
    }

    public static Number linfnorm (RealVector v) {
        return v.getLInfNorm();
    }

    public static RealVector log (RealVector v) {
        return new ScribVector(v.map(new Log()));
    }

    public static RealVector log10 (RealVector v) {
        return new ScribVector(v.map(new Log10()));
    }

    public static RealVector log1p (RealVector v) {
        return new ScribVector(v.map(new Log1p()));
    }

    public static RealVector multiply (RealVector v, Number scalar) {
        return new ScribVector(v.mapMultiply(scalar.doubleValue()));
    }

    public static RealVector multiply(Number scalar, RealVector v) {
        return multiply(v, scalar);
    }

    public static RealVector ebeMultiply (RealVector v1, RealVector v2) {
        return new ScribVector(v1.ebeMultiply(v2));
    }

    public static Number multiply (RealVector v1, RealVector v2) {
        return v1.dotProduct(v2);
    }

    public static RealVector negate (RealVector v1) {
        return new ScribVector(v1.mapMultiply(-1));
    }

    public static Number norm (RealVector v) {
        return v.getNorm();
    }

    public static RealVector pow (RealVector v, Number scalar) {
        return new ScribVector(v.map(new Power(scalar.doubleValue())));
    }

    public static RealVector rint (RealVector v) {
        return new ScribVector (v.map(new Rint()));
    }

    public static RealVector set (RealVector v, Number value) {
        v.set(value.doubleValue());
        return new ScribVector(v);
    }

    public static RealVector setentry (RealVector v, Number index, Number value) {
        RealVector v2 = v.copy();
        v2.setEntry(index.intValue(), value.doubleValue());
        return new ScribVector(v2);
    }

    public static RealVector setsubvector (RealVector v, Number index, RealVector subvector) {
        ScribVector v2 = new ScribVector(v);
        v2.checkIndex(index.intValue());
        v2.setSubVector(index.intValue(), subvector);
        return v2;
    }

    public static RealVector signum (RealVector v) {
        return new ScribVector(v.map(new Signum()));
    }

    public static RealVector sin (RealVector v) {
        return new ScribVector(v.map(new Sin()));
    }

    public static RealVector sinh (RealVector v) {
        return new ScribVector(v.map(new Sinh()));
    }

    public static RealVector sqrt (RealVector v) {
        return new ScribVector(v.map(new Sqrt()));
    }

    public static RealVector subtract (RealVector v, Number n) {
        return new ScribVector(v.mapSubtract(n.doubleValue()));
    }

    public static RealVector subtract (RealVector v1, RealVector v2) {
        return new ScribVector(v1.subtract(v2));
    }

    public static RealVector subvector (RealVector v, Number index, Number length) {
        return new ScribVector(v.getSubVector(index.intValue(), length.intValue()));
    }

    public static RealVector tan (RealVector v) {
        return new ScribVector (v.map(new Tan()));
    }

    public static RealVector tanh (RealVector v) {
        return new ScribVector(v.map(new Tanh()));
    }

    public static RealVector ulp (RealVector v) {
        return new ScribVector (v.map(new Ulp()));
    }

    public static RealMatrix outerproduct (RealVector v1, RealVector v2) {
        return new ScribMatrix(v1.outerProduct(v2));
    }

    public static RealVector projection (RealVector v1, RealVector v2) {
        return new ScribVector(v1.projection(v2));
    }

    public static RealVector unit (RealVector v) {
        return new ScribVector(v.unitVector());
    }

    public static RealVector vector (Number size) {
        return new ScribVector(size.intValue());
    }

}

