package ssinai.scribbler.parser;


class BooleanParenStatement extends Statement {
    boolean val;

    public boolean getConditionalTest (Parser parser) {
        exec(parser);
        return val;
    }
  

    public void exec (Parser parser) {
        StringBuilder buf = new StringBuilder();
        for (Object o : this) {
            buf.append(o.toString());
        }

        Assembly x = new Assembly(buf.toString()+";");
        boolean b = parser.match(x);

        if (b) {
            val = (Boolean)lastAnswer;
        }
    }
}

