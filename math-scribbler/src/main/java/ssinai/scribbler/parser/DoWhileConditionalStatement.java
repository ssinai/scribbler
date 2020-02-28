package ssinai.scribbler.parser;


public class DoWhileConditionalStatement extends Statement {
    public void eval (Parser parser) throws InterruptedException {
        //     internalVariableMap.clear();
        internalVariableMap.clear();

        System.out.println("DoWhileCOnditional this="+this);
        while (true) {
            System.out.println("DoWhileConditionalStatement loop");
            BraceStatements braceStatements = (BraceStatements)get(1);
            //           braceStatements.process();
            braceStatements.eval(parser);

            System.out.println("ScribParser variableMap="+ScribParser.variableMap);

            boolean done = internalVariableMap.contains(ControlEnum.BREAK);

            if (done) {
                return;
            }

            BooleanParenStatement boolParenStatement = (BooleanParenStatement)get(3);
            System.out.println("doWhile boolParenStatement="+boolParenStatement);
            boolean val = boolParenStatement.getConditionalTest(parser);

            if (!val) {
                System.out.println("internalVariableMap="+internalVariableMap);
                return;
            }
        }
    }
}

