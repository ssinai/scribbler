package ssinai.scribbler.parser;

public class IfConditionalStatement extends Statement {

    public void eval (Parser parser) throws InterruptedException {
        int i = 0;
        boolean done = false;
        while (i < size() && (!done)) {
            Object startToken = get(i);
            i++;
            boolean val;
            if (!startToken.toString().trim().equals("else")) {

                BooleanParenStatement boolParenStatement = (BooleanParenStatement)get(i);
                val = boolParenStatement.getConditionalTest(parser);

                i++;
            } else {
                val = true;
            }

            if (val) {
                BraceStatements braceStatements = (BraceStatements)get(i);
                //              braceStatements.process();
                braceStatements.eval(parser);
                done = true;
            }
            i++;
        }
    }


    public String toString () {

        StringBuilder buf = new StringBuilder();
        for (Object o : this) {
            buf.append(o.toString());
        }

        s = buf.toString();
        return s;
    }

}
