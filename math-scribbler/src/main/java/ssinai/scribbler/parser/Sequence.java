package ssinai.scribbler.parser;

public class Sequence extends CollectionParser {

    public Sequence () {
        super();
    }

    public Sequence (String s) {
        super(s);
    }

    public String toString () {
        StringBuilder buffer = new StringBuilder();
        for (Parser p : al) {
            buffer.append(p.toString());
        }
        return buffer.toString();
    }

    public boolean match (Assembly a) {
  //      logger.debug("matching Seq\t"+getName());

        Assembly savedAssembly = a.copy();
        for (Parser p : al) {
            boolean res = p.match(a);
            if (!res) {
                a.restore(savedAssembly);
      //          logger.debug("leaving Seq\t\t"+getName()+", false");
                return false;
            }  else {
                setMatchIndex(a.getIndex());
            }
        }

        if (assemblers != null && assemblers.size() > 0) {
            for (Assembler assembler : assemblers) {
                assembler.workOn(a);
            }
        }
  //      logger.debug("leaving Seq\t\t"+getName()+", true");
        return true;
    }
}
