package ssinai.scribbler.parser;

public class Alternation extends CollectionParser {

    public Alternation () {
        super();
    }

    public Alternation (String s) {
        super(s);
    }

    public String toString () {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0 ; i < al.size() ; i++) {
            if (i > 0) {
                buffer.append("|");
            }
            Parser p = al.get(i);
            buffer.append(p.toString());
        }
        return buffer.toString();
    }

    public boolean match (Assembly a) {
  //      logger.info("matching Alt\t"+getName());
        Assembly savedAssembly = a.copy();
        for (Parser p : al) {
            Assembly b = a.copy();
            boolean res = p.match(a);
            if (res) {
                setMatchIndex(a.getIndex());
                if (assemblers != null && assemblers.size() > 0) {
                    for (Assembler assembler : assemblers) {
                        assembler.workOn(a);
                    }
                }
  //              logger.info("leaving Alt\t\t"+getName()+", true");
                return true;
            }
            a.restore(b);
        }

        a.restore(savedAssembly);
  //      logger.info("leaving Alt\t\t"+getName()+", false");
        return false;
    }
}
