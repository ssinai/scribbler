package ssinai.scribbler.parser;

public class MatrixHolder {
    private String varName;
    private ScribMatrix mat;
    private Number rowIndex;
    private Number colIndex;

    public MatrixHolder (String varName, ScribMatrix mat, Number rowIndex, Number colIndex) {
        this.varName = varName.trim();
        this.mat = mat;
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    public String getVarName () {
        return varName;
    }

    public ScribMatrix getMatrix () {
        return mat;
    }

    public Number getRowIndex () {
        return rowIndex;
    }

    public Number getColIndex (){
        return colIndex;
    }

    public String toString () {
        StringBuilder buf = new StringBuilder();
        buf.append("name=").append(getVarName());
        buf.append(", rowIndex= ").append(getRowIndex());
        buf.append(", colIndex=").append(getColIndex());
        buf.append(", matrix=").append(getMatrix());
        return buf.toString();
    }
}

