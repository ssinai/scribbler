package ssinai.scribbler.parser;

public class Repetition extends Parser {

    Parser o;
    int minTimes;
    int maxTimes;

    public Repetition (Parser o) {
        this.o = o;
        minTimes = 0;
        maxTimes = Integer.MAX_VALUE;
    }

    public Repetition (Parser o, int minTimes) {
        this(o);
        this.minTimes = minTimes;
    }

    public Repetition (Parser o, int minTimes, int maxTimes) {
        this(o, minTimes);
        this.maxTimes = maxTimes;
    }

    public String toString () {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[").append(o.toString()).append("]+");
        return buffer.toString();
    }

    public boolean match (Assembly a) {
        Assembly savedAssembly = a.copy();
        int counter = 0;
        boolean res = o.match(a);
        while (res) {
            counter++;
            setMatchIndex(a.getIndex());
            if (counter >= maxTimes) break;
            res = o.match(a);
        }

        if (counter >= minTimes && counter <= maxTimes) {
            if (assemblers != null) {
                for (Assembler assembler : assemblers) {
                    assembler.workOn(a);
                }
            }
            return true;
        }
        else {
            a.restore(savedAssembly);

            return false;
        }
    }

}
