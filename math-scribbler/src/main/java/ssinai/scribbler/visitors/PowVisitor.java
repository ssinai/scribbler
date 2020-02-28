package ssinai.scribbler.visitors;

import org.apache.commons.math3.linear.DefaultRealMatrixChangingVisitor;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Apr 8, 2010
 * Time: 4:32:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class PowVisitor extends DefaultRealMatrixChangingVisitor {
    private double power;

    public PowVisitor(Number pow) {
        power = pow.doubleValue();
    }

    @Override
    public double visit(int row, int column, double value)  {
        return Math.pow(value, power);
    }
}