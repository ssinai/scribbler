package ssinai.scribbler.parser;

import ssinai.scribbler.ops.FormatOps;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealVectorFormat;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Apr 1, 2010
 * Time: 3:34:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScribVector extends ArrayRealVector {

    private static RealVectorFormat realVectorFormat = new RealVectorFormat("[", "]", ", ", FormatOps.getNumFormat());


    public ScribVector() {
    }

    public ScribVector(int size) {
        super(size);
    }

    public ScribVector(int size, double preset) {
        super(size, preset);
    }

    public ScribVector(double[] d) {
        super(d);
    }

    public ScribVector(double[] d, boolean copyArray) throws NullPointerException, IllegalArgumentException {
        super(d, copyArray);
    }

    public ScribVector(double[] d, int pos, int size) {
        super(d, pos, size);
    }

    public ScribVector(Double[] d) {
        super(d);
    }

    public ScribVector(Double[] d, int pos, int size) {
        super(d, pos, size);
    }

    public ScribVector(RealVector v) {
        super(v);
    }

    public ScribVector(ArrayRealVector v, boolean deep) {
        super(v, deep);
    }

    public ScribVector(ArrayRealVector v1, ArrayRealVector v2) {
        super(v1, v2);
    }

    public ScribVector(ArrayRealVector v1, double[] v2) {
        super(v1, v2);
    }

    public ScribVector(double[] v1, ArrayRealVector v2) {
        super(v1, v2);
    }

    public ScribVector(double[] v1, double[] v2) {
        super(v1, v2);
    }

    public String toString () {
        return realVectorFormat.format(this);
    }

    public void setEntry (Number index, double value) {
        super.setEntry(index.intValue(), value);
    }

    @Override
    public void checkIndex(int index) throws OutOfRangeException {
        super.checkIndex(index);
    }

}
