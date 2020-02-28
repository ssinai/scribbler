package ssinai.scribbler.parser;

public class VectorHolder {
    private String varName;
    private ScribVector v;
    private Number index;

    public VectorHolder (String varName, ScribVector v, Number index) {
        this.varName = varName.trim();
        this.v = v;
        this.index = index;
    }

    public String getVarName () {
        return varName;
    }

    public ScribVector getVector () {
        return v;
    }

    public Number getIndex () {
        return index;
    }

    public String toString () {
        StringBuilder buf = new StringBuilder();
        buf.append("name=").append(getVarName());
        buf.append(", index= ").append(getIndex());
        buf.append(", vector=").append(getVector());
        return buf.toString();
    }
}
