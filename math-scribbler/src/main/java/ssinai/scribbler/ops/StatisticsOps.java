package ssinai.scribbler.ops;

import ssinai.scribbler.annote.Example;
import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.distribution.*;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.log4j.Logger;

import java.util.*;

import ssinai.scribbler.parser.Parser;
import ssinai.scribbler.parser.ScribMatrix;
import ssinai.scribbler.parser.ScribVector;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Oct 10, 2009
 * Time: 3:23:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class StatisticsOps {

    static private Logger logger = Parser.getLogger();

    private StatisticsOps () {}

    public static void init() {
        logger.info("In StatisticsOps init");
    }

    private static DescriptiveStatistics descStats = new DescriptiveStatistics();
    private static SummaryStatistics summaryStats = new SummaryStatistics();
    private static SimpleRegression regression = new SimpleRegression();

    public static Number setData(RealVector v) {
        fillDescStats(v);
        return v.getDimension();
    }

    public static RealVector getData () {
        double [] data = descStats.getValues();
        return new ScribVector(data);
    }

    private static void fillDescStats(RealVector v) {
        descStats.clear();
        double[] d = v.toArray();
        for (double val : d) {
            descStats.addValue(val);
        }
    }




    /*
  private static void fillSummaryStats(RealVector v) {
      summaryStats.clear();
      double[] d = v.getData();
      for (double val : d) {
          summaryStats.addValue(val);
      }
  }
    */

    public static Number alphagt (Number z) {
        NormalDistribution normalDist = new NormalDistribution();
            double prob = normalDist.cumulativeProbability(z.doubleValue());
            return 1-prob;
    }

    public static Number alphalt (Number z)  {
        return new NormalDistribution().cumulativeProbability(z.doubleValue());
    }

    @Example("Here is an example of avgdev")
    public static Number avgdev () {
        return avgdev(new ScribVector(descStats.getValues()));
    }

    public static Number avgdev (RealVector v) {
        Double mean = mean(v);
        RealVector newVector = v.mapSubtract(mean);
  //      newVector = newVector.mapAbs();
        newVector = newVector.map(new Abs());
        double sum = StatUtils.sum(newVector.toArray());
        return sum / v.getDimension();
    }

    public static Number betalt (Number z) {
        double prob = new NormalDistribution().cumulativeProbability(z.doubleValue());
        return 1-prob;
    }

    public static Number betagt (Number z) {
        return new NormalDistribution().cumulativeProbability(z.doubleValue());
    }

    public static Boolean between (RealVector c, Number n) {
        Number high = max(c);
        Number low = min(c);
        if (n.doubleValue() >= low.doubleValue()
                && n.doubleValue() <= high.doubleValue()) {
            return true;
        } else {
            return false;
        }
    }

        /*
    public static RealVector binomdist (Number max, Number p) {
        BinomialDistribution bdist =
                new BinomialDistributionImpl(max.intValue(), p.doubleValue());
        double [] newData = new double[max.intValue()+1];

        for (int i = 0 ; i <= max.intValue() ; i++) {
            double prob = bdist.probability((double)i);
            newData[i] = prob;
        }
        return new ScribVector(newData);
    }
    */

    public static RealVector binomdist (Number max, Number p) {
        BinomialDistribution bdist =
                new BinomialDistribution(max.intValue(), p.doubleValue());
        //    MPArray array = new MPArray();
        ArrayRealVector array = new ArrayRealVector();
        for (int i = 0 ; i <= max.intValue() ; i++) {
            double prob = bdist.probability(i);
            array.append(prob);
            //        array.add(new Double(prob));
        }
        return array;
    }


    @Example("Here is an example of cheb")
    public static RealVector cheb (Number k, Number mean, Number stdev) {
        double [] data = new double[3];
        data[0] = 1 - 1/Math.pow(k.doubleValue(), 2);
        data[1] = mean.doubleValue() - k.doubleValue() * stdev.doubleValue();
        data[2] = mean.doubleValue() + k.doubleValue() * stdev.doubleValue();
        return new ScribVector(data);
    }

    public static Number chi (Number df, Number alpha) {
        ChiSquaredDistribution chi = new ChiSquaredDistribution(df.doubleValue());
        return chi.inverseCumulativeProbability(1-alpha.doubleValue());
    }

    public static Number chisum (ScribMatrix obs_vals, ScribMatrix exp_vals) {
        double [][] obs = obs_vals.getData();
        double [][] exp = exp_vals.getData();
        double sum = 0;
        for (int i = 0 ; i < obs_vals.getRowDimension() ; i++) {
            for (int j = 0 ; j < exp_vals.getColumnDimension() ; j++) {
                double n1 = obs[i][j];
                double n2 = exp[i][j];
                double num = Math.pow(n1-n2, 2);
                double val = num / n2;
                sum += val;
            }
        }
        return sum;
    }

    public static Number chisum (RealVector obs_vals, RealVector exp_vals) {
        double [] obs = obs_vals.toArray();
        double [] exp = exp_vals.toArray();
        double sum = 0;

        for (int i = 0 ; i < obs.length ; i++) {
            double n1 = obs[i];
            double n2 = exp[i];
            double num = Math.pow(n1 - n2, 2);
            double val = num / n2;
            sum += val;
        }
        return sum;
    }

    public static RealVector cint (Number z, Number mean, Number stdev) {
        double [] data = new double[2];
        double a = z.doubleValue() * stdev.doubleValue();
        data[0] = mean.doubleValue() - a;
        data[1] = mean.doubleValue() + a;
        return new ScribVector(data);
    }

    public static ScribMatrix contable (RealVector rowTotals, RealVector colTotals) {
        Number total = sum(rowTotals);
        double [] rows = rowTotals.toArray();
        double [] cols = colTotals.toArray();
        double [][] matrix = new double[rows.length][cols.length];

        for (int i = 0 ; i < rows.length ; i++) {
            for (int j = 0 ; j < cols.length ; j++) {
                double rowValue = rows[i];
                double colValue = cols[j];
                matrix[i][j] = (colValue*rowValue)/total.doubleValue();
            }
        }
        return new ScribMatrix(matrix);
    }

    public static Number dim (RealVector v) {
        return v.getDimension();
    }

    public static Number ftest (Number numdf, Number denomdf, Number alpha) {
        FDistribution ftest =
                new FDistribution(numdf.doubleValue(), denomdf.doubleValue());
        double d = ftest.inverseCumulativeProbability(alpha.doubleValue());
        return d;
    }

    public static Number gmean () {
        return gmean(descStats.getValues());
    }

    public static Number gmean (RealVector v) {
        return gmean(v.toArray());
    }
    public static Number gmean (final double [] data) {
        GeometricMean geoMean = new GeometricMean();
        return geoMean.evaluate(data);
    }

    public static Number hmean () {
        return hmean(new ScribVector(descStats.getValues()));
    }

    public static Number hmean (RealVector v) {
        RealVector newVector = v.map(new Inverse());
        Double sum = StatUtils.sum(newVector.toArray());
        return v.getDimension() / sum;
    }

    public static Number max() {
        return descStats.getMax();
    }

    public static Number max(RealVector v) {
        return StatUtils.max(v.toArray());
    }

    public static Double mean () {
        return descStats.getMean();
    }

    public static Double mean (RealVector v) {
        return StatUtils.mean(v.toArray());
    }

    public static Number median () {
        return new Median().evaluate(descStats.getValues());
    }

    public static Number median(RealVector row) {
        return new Median().evaluate(row.toArray());
    }

    public static Number min() {
        return descStats.getMin();
    }

    public static Number min(RealVector v) {
        return StatUtils.min(v.toArray());
    }

    public static Number mode () {
        return mode(descStats.getValues());
    }

    public static Number mode (RealVector row) {
        return mode(row.toArray());
    }

    public static Number mode(final double [] data) {
        Hashtable<Number, Integer> table = new Hashtable<Number, Integer>();
        for (double d : data) {
            if (table.containsKey(d)) {
                Integer i = table.get(d);
                table.put(d, i + 1);
            } else {
                table.put(d, 1);
            }
        }

        Set keySet = table.keySet();
        Iterator iter = keySet.iterator();
        Number mode = null;
        Number key;

        Integer maxCount = null;
        Integer count;
        while (iter.hasNext()) {
            if (maxCount == null) {
                mode = (Number) iter.next();
                maxCount = table.get(mode);
            } else {
                key = (Number) iter.next();
                count = table.get(key);
                if (count > maxCount) {
                    maxCount = count;
                    mode = key;
                }
            }
        }

        table.remove(mode);
        if (table.containsValue(maxCount)) {
            return Double.NaN;
        } else {
            return mode;
        }
    }

    public static Number pois (Number mean, Number successes) {
        PoissonDistribution poisson = new PoissonDistribution(mean.doubleValue());
        return poisson.probability(successes.intValue());
    }

    public static ScribVector pois (Number interval, RealVector c) {
        PoissonDistribution pdist =
                new PoissonDistribution(interval.doubleValue());
        double [] data = c.toArray();
        double [] newData = new double[data.length];
        for (int i = 0 ; i < data.length ; i++) {
            newData[i] = pdist.probability((int)Math.round(data[i]));
        }
        return new ScribVector(newData);
    }

    public static Number position (Number n) {
        double [] data = descStats.getValues();
        ScribVector v = new ScribVector(data);
        return position(v, n);
    }

    public static Number position (RealVector v, Number n) {
        double [] data = v.toArray();
        for (int i = 0 ; i < data.length ; i++) {
            Double d = data[i];
            if (MathOps.isEquals(n, d)) {
                return i;
            }
        }
        return -1;
    }

    public static Number prank (RealVector v, Number p) {
        double [] data = sort(v);
        double n = p.doubleValue();
        for (int i = 0 ; i < data.length ; i++) {
            if (data[i] >= n) {
                double dval= (double)i/data.length;
                return dval*100;
            }
        }
        return -1;
    }

    public static Number pstdev () {
        StandardDeviation st = new StandardDeviation(false); // false = pop dev, true = sample
        return st.evaluate(descStats.getValues());
    }

    public static Number pstdev (RealVector v) {
        StandardDeviation st = new StandardDeviation(false);
        return st.evaluate(v.toArray());
    }

    public static Number range() {
        return descStats.getMax() - descStats.getMin();
    }

    public static Number range(RealVector v) {
        return StatUtils.max(v.toArray()) - StatUtils.min(v.toArray());
    }

    public static Number rawval (Number z, Number mean, Number sdev) {
        return mean.doubleValue() + z.doubleValue() * sdev.doubleValue();
    }

    public static RealVector rawval (RealVector zlist, Number mean, Number stdev) {
        double [] data = zlist.toArray();
        double [] newData = new double[data.length];
        for (int i = 0 ; i < data.length ; i++) {
            Number n = rawval(data[i], mean, stdev);
            newData[i] = n.doubleValue();
        }
        return new ScribVector(newData);
    }

    public static RealVector setRegression (RealVector v1, RealVector v2) {
        regression.clear();
        double [] v1data = v1.toArray();
        double [] v2data = v2.toArray();
        for (int i = 0 ; i < v1data.length ; i++) {
            regression.addData(v1data[i], v2data[i]);
        }

        double [] data = new double[2];
        data[0] = regression.getSlope();
        data[1] = regression.getIntercept();
        return new ScribVector(data);
    }

    public static Number regr_slope () {
        return regression.getSlope();
    }

    public static Number regr_yint () {
        return regression.getIntercept();
    }

    public static Number regr_stderr () {
        return regression.getSlopeStdErr();
    }

    public static Number regr_predict (Number xval) {
        return regression.predict(xval.doubleValue());
    }

    public static RealVector relfreq () {
        double [] data = descStats.getValues();
        RealVector v2 = new ScribVector(data);
        return relfreq(v2);
    }

    public static RealVector relfreq (RealVector v) {
        Number sum = sum(v);
        return v.mapDivide(sum.doubleValue());
    }

    public static Number samplesize (Number z, Number stdev, Number error) {
        double ssize = Math.pow((z.doubleValue()
                * stdev.doubleValue() / error.doubleValue()), 2.0);
        return Math.round(ssize+.5);
    }

    private static double [] sort (RealVector v) {
        RealVector v2 = v.copy();
        double [] data = v2.toArray();
        Arrays.sort(data);
        return data;
    }

    public static RealVector sortasc (RealVector v) {
        double [] data = sort(v);
        return new ScribVector(data);
    }

    public static RealVector sortdes (RealVector v) {
        double [] data = sort(v);
        double [] newData = new double[data.length];
        for (int i = data.length-1, index=0; i >= 0 ; i--, index++) {
            newData[index] = data[i];
        }
        return new ScribVector(newData);
    }


    public static Number srank (RealVector v, Number value) {
        double [] data = sort(v);

        for (int i = 0 ; i < data.length ; i++) {
            if (data[i] >= value.doubleValue()) {
                return data.length - i;
            }
        }
        return -1;
    }

    public static Number ssq (final RealVector v) {
        SumOfSquares ssq = new SumOfSquares();
        return ssq.evaluate(v.toArray(), 0, v.getDimension());
    }

    public static Number stdev () {
        return descStats.getStandardDeviation();
    }

    public static Number stdev (RealVector v) {
        StandardDeviation st = new StandardDeviation(true);
        return st.evaluate(v.toArray());
    }

    public static Number sum () {
        return descStats.getSum();
    }
    

    public static Number sum (RealVector row) {
        return StatUtils.sum(row.toArray());
    }

    public static Number tinv (Number df, Number n) {
        TDistribution tdist = new TDistribution(df.intValue());
        double i = tdist.inverseCumulativeProbability(n.doubleValue());
        return i;
    }

    public static Number tmean (RealVector c, Number start, Number end) {
        return StatUtils.mean(c.toArray(), start.intValue(), end.intValue()-start.intValue());
    }

    public static Number svar() {
        return descStats.getVariance();
    }

    public static Number pvar () {
        Variance var = new Variance(false); // false = pop dev, true = sample
        return var.evaluate(descStats.getValues());
    }

    public static Number pvar (RealVector v) {
        Variance var = new Variance(false);
        return var.evaluate(v.toArray());
    }

    public static Number svar (RealVector v) {
        return new Variance(true).evaluate(v.toArray());
    }

    public static RealVector zscore (RealVector v, Number mean, Number stdev) {
        double [] data = v.toArray();
        double [] newData = new double[data.length];
        for (int i = 0 ; i < data.length ; i++) {
            Number n = zscore(data[i], mean, stdev);
            newData[i] = n.doubleValue();
        }
        return new ScribVector(newData);
    }

    public static Number zscore (RealVector v, Number val) {
        Number mean = mean(v);
        Number stdev = pstdev(v);
        return zscore(val, mean, stdev);
    }

    public static Number zscore (Number n, Number mean, Number stdev) {
        return (n.doubleValue()-mean.doubleValue()) / stdev.doubleValue();
    }

    public static RealVector zscores () {
        double [] data = descStats.getValues();
        RealVector v = new ScribVector(data);
        return zscores(v);
    }

    public static RealVector zscores (RealVector v) {
        Number mean = mean(v);
        Number stdev = pstdev(v);
        RealVector v2 = v.mapSubtract(mean.doubleValue());
        RealVector v3 = v2.mapDivide(stdev.doubleValue());
        return v3;
    }

    /*
    public static Number prob2z (Number n) {
        // prob2z .3159 >> 0.8998;
        return normprob(n);
    }
    */

    public static Number prob2z (Number x) {
        NormalDistribution normalDist = new NormalDistribution();

            return normalDist.inverseCumulativeProbability(x.doubleValue()+.5);

    }


    public static Number z2prob (Number x, Number mean, Number sd) {

        NormalDistribution normalDist = new NormalDistribution(mean.doubleValue(), sd.doubleValue());

            return normalDist.cumulativeProbability(x.doubleValue()) - .5;

    }

    public static Number z2prob (Number zscore) {
        NormalDistribution normalDist = new NormalDistribution();
            double prob = normalDist.cumulativeProbability(zscore.doubleValue());
            return Math.abs(prob-.5);

    }

    /*
    public static Number z2prob (Number z) {
        // z2prob .6 >> 0.2258;
        return normarea(z);
    }
    */

    /*
    public static Number normprob (Number n) {
        return inv(n);
    }
    */


    public static RealVector subset (Number start, Number length, RealVector c) {
        return new ArrayRealVector(c.toArray(), start.intValue(), length.intValue());
    }

    public static RealVector append (RealVector c, Number n) {
        return c.append(n.doubleValue());
    }

}

