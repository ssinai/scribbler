package ssinai.scribbler.parser;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Logger;

import java.util.ArrayList;

import ssinai.scribbler.ops.FormatOps;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Oct 24, 2009
 * Time: 2:07:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScribMatrix extends Array2DRowRealMatrix {
    static private Logger logger = Parser.getLogger();

    public ScribMatrix() {
        logger.info("in ScribMatrix constructor");
    }

    public ScribMatrix (Number rowDimension, Number columnDimension) throws IllegalArgumentException {
        this(rowDimension.intValue(), columnDimension.intValue());
    }

    public ScribMatrix(int rowDimension, int columnDimension) throws IllegalArgumentException {
        super(rowDimension, columnDimension);
    }

    public ScribMatrix(double[][] d) throws IllegalArgumentException, NullPointerException {
        super(d);
    }

    public ScribMatrix(double[][] d, boolean copyArray) throws IllegalArgumentException, NullPointerException {
        super(d, copyArray);
    }

    public ScribMatrix(double[] v) {
        super(v);
    }

    public ScribMatrix(RealMatrix m) {
        this(m.getData());
    }

    public ScribMatrix(RealVector v) {
        super(1,v.getDimension());
        setRowVector(0, v);
    }

    public boolean isVector () {
        return getRowDimension() <= 1;
    }

    public void setEntry (Number rowIndex, Number colIndex, double value) {
        super.setEntry(rowIndex.intValue(), colIndex.intValue(), value);
    }

    public boolean isMatrix () {
        return getRowDimension() > 1;
    }

    public ScribMatrix append (ArrayList matrixList, boolean newRows) {

        //      logger.info("in append currentMatrix="+this+", matrixList="+matrixList+", newRows="+newRows);
        if (matrixList.size() == 0) {
            logger.info("passed empty matrixList to appendRows");
            System.exit(1);
            return this;
        }

        ArrayList<ArrayList<Double>> tmpArray = new ArrayList<ArrayList<Double>>();
        for (int i = 0 ; i < getRowDimension() ; i++) {
            ArrayList<Double> newRow = new ArrayList<Double>();
            for (int j = 0 ; j < getColumnDimension() ; j++) {
                newRow.add(getEntry(i,j));
            }
            tmpArray.add(newRow);
        }

        Boolean flat = null;
        int matrixCounter = 0;
        for (Object o : matrixList) {
            if (o instanceof Number) {
                if (flat != null && !flat) {
                    System.exit(1);
                    logger.info("Found number when matrix expected");
                }
                flat = true;
            } else if (o instanceof ScribMatrix) {
                ScribMatrix mat = (ScribMatrix)o;
                if (mat.isVector()) {
                    if (flat != null && !flat) {
                        logger.info("Found ScribMatrix when flat expected");
                        System.exit(1);
                        return this;
                    }
                    flat = true;
                } else {
                    if (flat != null && flat) {
                        logger.info("found vector when matrix expected");
                        System.exit(1);
                        return this;
                    }
                    matrixCounter++;
                    flat = false;
                }
            }
        }

        System.out.println("newRows true append processing flat="+flat);
        if (flat) {
            ArrayList newRow = new ArrayList();
            for (Object val : matrixList) {
                System.out.println("val type=" + val.getClass().getName());
                if (val instanceof Number) {
                    System.out.println("SPOT 1 appending " + val);
                    newRow.add(val);
                } else if (val instanceof ScribMatrix && ((ScribMatrix) val).isVector()) {
                    ScribMatrix mat = (ScribMatrix) val;
                    double[] d = mat.getRow(mat.getRowDimension() - 1);
                    for (double o : d) {
                        System.out.println("SPOT 2 appending " + o);
                        newRow.add(o);
                    }
                } else if (val instanceof ArrayList) {
                    ArrayList al = (ArrayList) val;
                    for (Object o : al) {
                        System.out.println("SPOT 3 appending " + o);
                        newRow.add(o);
                    }
                }
                System.out.println("newRow now=" + newRow);
            }
            tmpArray.add(newRow);
        } else {
            if (matrixCounter == 1) {
                for (Object val : matrixList) {
                    System.out.println("SPOT 4 appending " + val + ", type=" + val.getClass().getName());
                    if (val instanceof ScribMatrix) {
                        ScribMatrix mat = (ScribMatrix) val;

                        for (int j = 0; j < mat.getRowDimension(); j++) {
                            double[] d = mat.getRow(j);
                            ArrayList<Double> newRow = new ArrayList<Double>();
                            for (double o : d) {
                                newRow.add(o);
                            }
                            tmpArray.add(newRow);
                        }
                    }
                }
            } else if (matrixCounter > 1) {
                ScribMatrix firstMatrix = (ScribMatrix)matrixList.get(0);
                for (int i = 0 ; i < firstMatrix.getRowDimension() ; i++) {
                    ArrayList<Double> newRow = new ArrayList<Double>();
                    for (Object aMatrixList : matrixList) {
                        ScribMatrix mat = (ScribMatrix) aMatrixList;
                        double[] d = mat.getRow(i);
                        for (double obj : d) {
                            newRow.add(obj);
                        }
                    }
                    tmpArray.add(newRow);
                }
            }
        }


        double [][] data = new double[tmpArray.size()][];

        for (int i = 0 ; i < tmpArray.size() ; i++) {
            ArrayList<Double> row = tmpArray.get(i);
            data[i] = new double[row.size()];
            for (int j = 0 ; j < row.size() ; j++) {
                data[i][j] = row.get(j);
            }
        }

        ScribMatrix newMatrix = new ScribMatrix(data);
        //       logger.info("returning newMatrix "+newMatrix);
        return newMatrix;
    }

    public ScribMatrix appendColumn (double [] dataCol) {
        double [][] data = new double [1][dataCol.length];
        for (int i = 0 ; i < dataCol.length ; i++) {
            data[i][0] = dataCol[i];
        }
        return appendColumns(data);
    }

    public ScribMatrix appendColumns (double [][] data) {
        System.out.println("in appendColumns data[0].length="+data[0].length+", getColumnDimension="+getColumnDimension());
        int nRows = data.length + getRowDimension();
        int nCols = data[0].length + getColumnDimension();
        ScribMatrix newMatrix = new ScribMatrix(data.length, nCols);
        if (getRowDimension() > 0) {
            newMatrix.setSubMatrix(getData(), 0, 0);
        }
        newMatrix.setSubMatrix(data, 0, getColumnDimension());
        return newMatrix;
    }

    public ScribMatrix appendRow (double [] dataRow) {
        /*
        if (dataRow == null || dataRow.length == 0) {
            return this;
        }
        */
        double [][] data = new double[1][];
        data[0] = dataRow;
        return appendRows(data);
    }

    public ScribMatrix appendRows (double [][] data) {
        int nRows = data.length + getRowDimension();
        System.out.println("appendRows nRows="+nRows+", getColumnDimension()="+getColumnDimension()+", data[0].length="+data[0].length);
        ScribMatrix newMatrix = new ScribMatrix(nRows, data[0].length);
        if (getRowDimension() > 0) {
            newMatrix.setSubMatrix(getData(), 0, 0);
        }
        newMatrix.setSubMatrix(data, getRowDimension(), 0);

        return newMatrix;
    }

    public ScribMatrix appendRows (RealMatrix data) {
        System.out.println("in appendRows this="+toString() +" newData="+data);
        return appendRows(data.getData());
    }


    public ScribMatrix fill (double val) {
        double [][] data = getData();
        for (int i = 0 ; i < getRowDimension(); i++) {
            for (int j = 0 ; j < getColumnDimension() ; j++) {
                data[i][j]=val;
            }
        }
        return this;
    }



    private static boolean compact;
    public static void setCompact (boolean b) {
        compact = b;
    }

    public static boolean getCompact () {
        return compact;
    }

    public int [] formatTable () {
        System.out.println("in ScribMatrix formatTable");
        int [] maxColumnDimension = new int [getColumnDimension()];
        int [] pointColumnDimension = new int[getColumnDimension()];

        int [] spaceColumnDimension = new int[getColumnDimension()];
        for (int i = 0 ; i < getColumnDimension(); i++) {
            for (int j = 0 ; j < getRowDimension(); j++) {
                String s = FormatOps.format(getEntry(j,i));
                int decIndex = -1;
                int spaceIndex = 0;

                decIndex = s.indexOf(".");
                if (decIndex == -1) {
                    decIndex = s.length();
                    spaceIndex = 0;
                } else {
                    spaceIndex = s.length() - decIndex ;

                }
                //            System.out.println("decLen="+decIndex+", spaceIndex="+spaceIndex);

                if (decIndex > pointColumnDimension[i]) {
                    pointColumnDimension[i] = decIndex;
                }

                if (spaceIndex > spaceColumnDimension[i]) {
                    spaceColumnDimension[i] = spaceIndex;
                }
            }
        }
        /*
        for (int i = 0 ; i < pointColumnDimension.length; i++) {
            System.out.println("maxDecLen for "+i+"="+pointColumnDimension[i]+", spaceColDim="+spaceColumnDimension[i]);
        }
        */
        int padding = 2;
        int pos = 0;

        for (int i = 0 ; i < pointColumnDimension.length; i++) {

            if (i == 0) {
                pos += pointColumnDimension[i]+padding;
            } else {
                pos += pointColumnDimension[i]+padding+spaceColumnDimension[i-1];
            }
            maxColumnDimension[i] = pos;
//            System.out.println("maxColumnDimension["+i+"]="+maxColumnDimension[i]);
        }

        return maxColumnDimension;
    }


    public String toString () {
        StringBuilder buf = new StringBuilder("\n");
        double [][] data = getData();

        if (data.length == 0) {
            return "No Data";
        }

        for (double[] aData : data) {
            for (double anAData : aData) {
                buf.append("\t").append(FormatOps.format(anAData));
            }
            buf.append("\n");
        }

        //     System.out.println("toString returning "+buf);

        return buf.toString();
    }

}

