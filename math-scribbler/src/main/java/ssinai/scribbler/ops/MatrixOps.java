package ssinai.scribbler.ops;

import org.apache.commons.math3.linear.*;
import org.apache.log4j.Logger;
import ssinai.scribbler.parser.Parser;
import ssinai.scribbler.parser.ScribMatrix;
import ssinai.scribbler.parser.ScribVector;
import ssinai.scribbler.visitors.PowVisitor;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Oct 10, 2009
 * Time: 3:26:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class MatrixOps {

    static private Logger logger = Parser.getLogger();
    public static void init () {
        logger.info("In MatrixOps init");
    }


    public static ScribMatrix matrix () {
        return new ScribMatrix();
    }

    public static ScribMatrix matrix(Number rowDim, Number colDim) {
        return new ScribMatrix(rowDim.intValue(), colDim.intValue());
    }

    public static ScribMatrix add (ScribMatrix m, ScribMatrix n) {
        System.out.println("in MatrixOps.add");
        MatrixUtils.checkAdditionCompatible(m,n);
        RealMatrix newMatrix = m.add(n);
        return new ScribMatrix(newMatrix);
    }

    public static ScribMatrix add (ScribMatrix m, Number scalar) {
        return new ScribMatrix(m.scalarAdd(scalar.doubleValue()));
    }

    public static ScribMatrix add (Number scalar, ScribMatrix m) {
        return add(m, scalar);
    }

    public static ScribMatrix add (ScribMatrix m, Number rowIndex, Number colIndex, Number value) {
        RealMatrix m2 = m.copy();
        m2.addToEntry(rowIndex.intValue(), colIndex.intValue(), value.doubleValue());
        return new ScribMatrix(m2);
    }

    public static Number coldim (ScribMatrix m) {
        return m.getColumnDimension();
    }

    public static ScribMatrix submatrix (ScribMatrix m,
                                         Number startRow, Number endRow,
                                         Number startCol, Number endCol) {
        double [][] dest = new double [endRow.intValue()-startRow.intValue()+1][endCol.intValue()-startCol.intValue()+1];
        m.copySubMatrix(startRow.intValue(), endRow.intValue(),startCol.intValue(), endCol.intValue(), dest);
        return new ScribMatrix(dest);
    }

    public static ScribMatrix submatrix (ScribMatrix m,
                                         RealVector rowIndexes, RealVector colIndexes) {
        Number rowStart = rowIndexes.getEntry(0);
        Number rowEnd = rowIndexes.getEntry(1);
        Number colStart = colIndexes.getEntry(0);
        Number colEnd = colIndexes.getEntry(1);

        return submatrix(m, rowStart, rowEnd, colStart, colEnd);
    }


    public static ScribMatrix multiply (ScribMatrix m, Number rowIndex, Number colIndex, Number value) {
        RealMatrix m2 = m.copy();
        m2.multiplyEntry(rowIndex.intValue(), colIndex.intValue(), value.doubleValue());
        return new ScribMatrix(m2);
    }

    public static ScribMatrix appendcol (ScribMatrix m, ScribMatrix newCol) {
        double [][] newData = new double [m.getRowDimension()][m.getColumnDimension()+1];
        m.copySubMatrix(0,m.getRowDimension()-1, 0, m.getColumnDimension()-1, newData);

        for (int i = 0 ; i < newCol.getRowDimension(); i++) {
            newData[i][newData[0].length-1] = newCol.getData()[i][0];
        }
        return new ScribMatrix(newData);
    }

    public static ScribMatrix appendrow (ScribMatrix m, ScribMatrix newRow) {
        double [][] newData = new double [m.getRowDimension()+1][m.getColumnDimension()];
        m.copySubMatrix(0,m.getRowDimension()-1, 0, m.getColumnDimension()-1, newData);

        for (int i = 0 ; i < newRow.getColumnDimension(); i++) {
            newData[newData.length-1][i] = newRow.getData()[0][i];
        }
        return new ScribMatrix(newData);
    }

    public static Number rowdim (ScribMatrix m) {
        return m.getRowDimension();
    }

    public static Number get (ScribMatrix m, Number rowIndex, Number colIndex) {
        return m.getEntry(rowIndex.intValue(), colIndex.intValue());
    }

    public static ScribMatrix set (ScribMatrix m, Number rowIndex, Number colIndex, Number value) {
        RealMatrix m2 = m.copy();
        m2.setEntry(rowIndex.intValue(), colIndex.intValue(), value.doubleValue());
        return new ScribMatrix(m2);
    }

    public static ScribMatrix copy (ScribMatrix m) {
        return new ScribMatrix(m.copy());
    }

    public static Double det (ScribMatrix m) {
        return new LUDecomposition(m).getDeterminant();
        /*
        try {
            return new LUDecompositionImpl(m).getDeterminant();
        } catch (MathRuntimeException e) {
            System.out.println("det caught MathRuntimeException");
        } catch (Exception e) {
            System.out.println("det caught exception");
        }
        return null;
        */
    }

    public static ScribMatrix divide (ScribMatrix m, ScribMatrix b) {
        return mldivide(m,b);
    }



    public static ScribMatrix ebeMultiply (ScribMatrix m, ScribMatrix n) {
        double [][] mData = m.getData();
        double [][] nData = n.getData();
        double [][] newData = new double[m.getRowDimension()][m.getColumnDimension()];

        for (int i = 0 ; i < m.getRowDimension() ; i++) {
            for (int j = 0 ; j < m.getColumnDimension() ; j++) {
                newData[i][j] = mData[i][j] * nData[i][j];
            }
        }
        return new ScribMatrix (newData);
    }



    /*
    public static ScribMatrix ebePow (ScribMatrix m, Double d) {
        double [][] data = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int i = 0 ; i < m.getRowDimension() ; i++) {
            for (int j = 0 ; j < m.getColumnDimension() ; j++) {
                Double val = m.getEntry(i,j);
                data[i][j]  = Math.pow(val, d);
            }
        }
        return new ScribMatrix(data);
    }

    public static ScribMatrix ebePow (ScribMatrix m, ScribMatrix n) {
        double [][] data = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int i = 0 ; i < m.getRowDimension() ; i++) {
            for (int j = 0 ; j < m.getColumnDimension(); j++) {
                data[i][j] = Math.pow(m.getEntry(i,j), n.getEntry(i,j));
            }
        }
        return new ScribMatrix(data);
    }
    */

    public static ScribMatrix eigenVector (ScribMatrix m) {
        System.out.println("in eigenVector m="+m);
        EigenDecomposition e = new EigenDecomposition(m);
        RealMatrix n = e.getV();
        return new ScribMatrix(n);
    }

    public static ScribMatrix elementRDivide (ScribMatrix m, Double d) {
        double [][] data = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int i = 0 ; i < m.getRowDimension() ; i++) {
            for (int j = 0 ; j < m.getColumnDimension() ; j++) {
                Double val = m.getEntry(i,j);
                data[i][j]  = val/d;
            }
        }
        return new ScribMatrix(data);
    }

    public static ScribMatrix elementRDivide (ScribMatrix m, ScribMatrix n) {
        double [][] data = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int i = 0 ; i < m.getRowDimension() ; i++) {
            for (int j = 0 ; j < m.getColumnDimension() ; j++) {
                Double val = m.getEntry(i,j);
                Double d = n.getEntry(i,j);
                data[i][j]  = val/d;
            }
        }
        return new ScribMatrix(data);
    }

    public static Boolean equals (ScribMatrix m, ScribMatrix n) {
        return m.equals(n);
    }

    public static ScribMatrix fill (Double rowSize, Double colSize, Double val) {
        Integer rowDim = rowSize.intValue();
        Integer colDim = colSize.intValue();
        double [][] data = new double[rowDim][colDim];
        for (int i = 0 ; i < rowDim ; i++) {
            for (int j = 0 ; j < colDim ; j++) {
                data[i][j]=val;
            }
        }
        return new ScribMatrix(data);
    }

    public static RealVector col(ScribMatrix m, Number index) {
        return new ScribVector(m.getColumn(index.intValue()));
    }

    public static Number entry (ScribMatrix m, Number rowIndex, Number colIndex) {
        return m.getEntry(rowIndex.intValue(), colIndex.intValue());
    }

    public static Number frobnorm (ScribMatrix m) {
        return m.getFrobeniusNorm();
    }

    public static Number norm (ScribMatrix m) {
        return m.getNorm();
    }

    public static ScribMatrix rowmatrix (ScribMatrix m, Number rowIndex) {
        RealMatrix m2 = m.getRowMatrix(rowIndex.intValue());
        return new ScribMatrix(m2);
    }


    /*
    public static RealVector rowvector (ScribMatrix m, Number rowIndex) {
        RealVector v = m.getRowVector(rowIndex.intValue());
        return new ScribVector(v);
    }
    */

    public static RealVector row (ScribMatrix m, Number index) {
        return new ScribVector(m.getRow(index.intValue()));
    }


    public static RealVector rowtotals (ScribMatrix m) {
        double [] data = new double[m.getRowDimension()];

        for (int i = 0 ; i < m.getRowDimension() ; i++) {
            RealVector v = m.getRowVector(i);
            data[i] = StatisticsOps.sum(v).doubleValue();
        }
        return new ScribVector(data);
    }

    public static RealVector coltotals (ScribMatrix m) {
        double [] data = new double[m.getColumnDimension()];

        for (int i = 0 ; i < m.getColumnDimension() ; i++) {
            RealVector v = m.getColumnVector(i);
            data[i] = StatisticsOps.sum(v).doubleValue();
        }
        return new ScribVector(data);
    }


    public static Number rowtotal (ScribMatrix m, Number rowIndex) {
        RealVector rowVector = m.getRowVector(rowIndex.intValue());
        return StatisticsOps.sum(rowVector);
    }

    public static Number coltotal (ScribMatrix m, Number colIndex) {
        RealVector colVector = m.getColumnVector(colIndex.intValue());
        return StatisticsOps.sum(colVector);
    }

    public static ScribMatrix identity (Double size) {
        Integer dim = size.intValue();
        RealMatrix mat = MatrixUtils.createRealIdentityMatrix(dim);
        return new ScribMatrix(mat);
    }

    public static ScribMatrix inverse (ScribMatrix m) {
        RealMatrix inverse = new LUDecomposition(m).getSolver().getInverse();
        return new ScribMatrix(inverse);
    }

    public static Boolean isSingular (ScribMatrix m) {
        Boolean b = new LUDecomposition(m).getSolver().isNonSingular();
        return !b;
    }

    public static Boolean isSquare (ScribMatrix m) {
        return m.isSquare();
    }

    public static ScribMatrix mldivide (ScribMatrix m, ScribMatrix b) {
        return solve(m,b);
    }

    public static ScribMatrix mrdivide (ScribMatrix m, ScribMatrix b) {
        ScribMatrix tm = transpose(m);
        ScribMatrix tb = transpose(b);
        ScribMatrix m1 = solve(tm,tb);
        return transpose(m1);
    }

    public static ScribMatrix multiply (ScribMatrix m, ScribMatrix n) {
        return new ScribMatrix(m.multiply(n));
    }

    public static ScribMatrix multiply (ScribMatrix m, Number scalar) {
        RealMatrix newMatrix = m.scalarMultiply(scalar.doubleValue());
        ScribMatrix mat = new ScribMatrix(newMatrix);
        return mat;
    }

    public static ScribMatrix multiply (Number scalar, ScribMatrix m) {
        return multiply(m, scalar);
    }

    /*
    public static RealVector add(RealVector a, RealVector b) {
        return a.add(b);
    }
    */

    public static ScribMatrix setcol (ScribMatrix m, Number index, RealVector v) {
        ScribMatrix m2 = new ScribMatrix(m);
        m2.setColumn(index.intValue(), v.toArray());
        return m2;
    }

    public static ScribMatrix setcol (ScribMatrix m, Number index, ScribMatrix m2) {
        ScribMatrix m3 = new ScribMatrix(m);
        m3.setColumnMatrix(index.intValue(), m2);
        return m3;
    }

    public static ScribMatrix setrow (ScribMatrix m, Number index, RealVector v) {
        ScribMatrix m2 = new ScribMatrix(m);
        m2.setRow(index.intValue(), v.toArray());
        return m2;
    }

    public static ScribMatrix setrow (ScribMatrix m, Number index, ScribMatrix m2) {
        ScribMatrix m3 = new ScribMatrix(m);
        m3.setRowMatrix(index.intValue(), m2);
        return m3;
    }

    public static ScribMatrix getrows(ScribMatrix m, Number startRow, Number endRow) {
        System.out.println("in getrows startRow="+startRow+", endRow="+endRow);
        RealMatrix subm = m.getSubMatrix(startRow.intValue(), endRow.intValue(),
                0, m.getColumnDimension()-1);
        return new ScribMatrix(subm);
    }

    public static ScribMatrix getcols (ScribMatrix m, Number startCol, Number endCol) {
        System.out.println("in getcols startCol="+startCol+", endCol="+endCol);
        RealMatrix subm = m.getSubMatrix(0, m.getRowDimension()-1, startCol.intValue(), endCol.intValue());
        return new ScribMatrix(subm);
    }


    public static ScribMatrix solve (ScribMatrix m, ScribMatrix b) {
        System.out.println("in solve m="+m+", b="+b);
        DecompositionSolver solver = new LUDecomposition(m).getSolver();
        return new ScribMatrix(solver.solve(b));
    }

    public static ScribMatrix solve (ScribMatrix m, RealVector b) {
        System.out.println("in solve m="+m+", b="+b);
        DecompositionSolver solver = new LUDecomposition(m).getSolver();
        return new ScribMatrix(solver.solve(b));
    }

    public static ScribMatrix submatrix (ScribMatrix m, Double startRow, Double endRow, Double startColumn, Double endColumn) {
        RealMatrix subm = m.getSubMatrix(startRow.intValue(), endRow.intValue(),
                startColumn.intValue(), endColumn.intValue());
        return new ScribMatrix(subm);
    }

    public static ScribMatrix submatrix (ScribMatrix m, ScribMatrix rows, ScribMatrix cols) {
        double [] rowData = rows.getRow(0);
        int [] rData = new int[rowData.length];
        for (int i = 0; i < rowData.length ; i++) {
            rData[i] = (int)rowData[i];
        }
        double [] colData = cols.getRow(0);
        int [] cData = new int[colData.length];
        for (int i = 0; i < colData.length ; i++) {
            cData[i] = (int)colData[i];
        }

        RealMatrix subM = m.getSubMatrix(rData, cData);
        return new ScribMatrix(subM);
    }

    public static ScribMatrix submatrix (ScribMatrix m, ScribMatrix m2, Number rowIndex, Number colIndex) {
        ScribMatrix m3 = new ScribMatrix(m);
        m3.setSubMatrix(m2.getData(), rowIndex.intValue(), colIndex.intValue());
        return m3;
    }

    public static ScribMatrix subtract (ScribMatrix m, ScribMatrix n) {
        RealMatrix newMatrix = m.subtract(n);
        return new ScribMatrix(newMatrix);
    }

    public static ScribMatrix subtract (ScribMatrix m, Double scalar) {
        return add(m, -scalar);
    }

    public static ScribMatrix subtract (Double scalar, ScribMatrix m) {
        return add(m, -scalar);
    }

    public static Double trace (ScribMatrix m) {
        return m.getTrace();
    }

    public static ScribMatrix transpose (ScribMatrix m) {
        RealMatrix n = m.transpose();
        return new ScribMatrix(n);
    }

    public static RealVector operate (ScribMatrix m, RealVector v) {
        RealVector v2 = m.operate(v);
        return new ScribVector(v2);
    }

    public static RealVector premult (ScribMatrix m, RealVector v) {
        RealVector v2 = m.preMultiply(v);
        return new ScribVector(v2);
    }

    public static RealMatrix premult (ScribMatrix m1, ScribMatrix m2) {
        RealMatrix m3 = m1.preMultiply(m2);
        return new ScribMatrix(m3);
    }

    public static ScribMatrix setsubmatrix (ScribMatrix m,
                                            Number rowIndex, Number colIndex, ScribMatrix submatrix) {
        ScribMatrix m2 = new ScribMatrix(m);
        m2.setSubMatrix(submatrix.getData(), rowIndex.intValue(), colIndex.intValue());
        return m2;
    }

    public static ScribMatrix pow (ScribMatrix m, Number pow) {

        System.out.println("in pow");
        ScribMatrix m2 = new ScribMatrix(m);
        PowVisitor cv = new PowVisitor(pow);
        double retval = m2.walkInColumnOrder(cv);
        System.out.println("pow retval="+retval);
        return m2;
    }


}

