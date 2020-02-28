package ssinai.scribbler.parser;

class ForConditionalStatement extends Statement {
    public void eval (Parser parser) throws InterruptedException {
        internalVariableMap.clear();
        String s = getInitialCondition();
//           System.out.println("ForStatement initialCondition="+s);
        Assembly startAssembly = new Assembly(s);
        boolean b = parser.match(startAssembly);

        Object _start = startAssembly.pop();
        if (!b && _start == null) {
            //  return new ParseException(s);
        }

        String booleanTestString = getBooleanTest();
        BraceStatements braceStatements = (BraceStatements)get(size()-1);
        String incrementStatement =getIncrementStatement();

        while (true) {
            //              System.out.println("ForStatement getBooleanTest="+booleanTestString);
            Assembly testAssembly = new Assembly(booleanTestString);
            b = parser.match(testAssembly);
            Object _test = testAssembly.pop();
            if (!b && _test == null) {
                //  return new ParseException(s);
            }

            if (!((Boolean) lastAnswer)) {
                return;
            }
            //           braceStatements.process();
            braceStatements.eval(parser);

            boolean done = internalVariableMap.contains(ControlEnum.BREAK);
            if (done) {
                return;
            }

            done = internalVariableMap.contains(ControlEnum.RETURN);
            if (done) {
                return;
            }

            //            System.out.println("ForStatement incrementStatement="+incrementStatement);
            Assembly incrementAssembly = new Assembly(incrementStatement+";");
            b = parser.match(incrementAssembly);

            Object _end = incrementAssembly.pop();
            if (!b && _end == null) {
            }
        }


    }

    public void add (String s) {
        System.out.println("in forConditionalStatement add");
        String startParen = s.substring(0,1);
        String endParen = s.substring(s.length()-1,s.length());
        String substr = s.substring(1, s.length()-1);
        String [] splitArray = substr.split(";");

        String s1 = splitArray[0]+";";
        String s2 = splitArray[1]+";";
        String s3 = splitArray[2];
        super.add(startParen);
        super.add(s1);
        super.add(s2);
        super.add(s3);
        super.add(endParen);
    }

    public String getInitialCondition () {
        return (String)get(2);
    }

    public String getBooleanTest () {
        return (String)get(3);
    }

    public String getIncrementStatement () {
        return (String)get(4);
    }
}

