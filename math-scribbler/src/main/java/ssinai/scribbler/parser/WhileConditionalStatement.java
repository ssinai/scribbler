package ssinai.scribbler.parser;

public class WhileConditionalStatement extends Statement {

    public void eval (Parser parser) throws InterruptedException {
        //        System.out.println("processing WhileConditionalList at "+getIndex());
        internalVariableMap.clear();
        while (true) {

            BooleanParenStatement boolParenStatement = (BooleanParenStatement)get(1);
            boolean val = boolParenStatement.getConditionalTest(parser);

            if (!val) return;

            BraceStatements braceStatements = (BraceStatements)get(2);
            //         braceStatements.process();
            braceStatements.eval(parser);
            /*
            if (checkIfCancelled()) {
                //             System.out.println("While Cancelled, bye-bye");
                throw new InterruptedException("Interrupted in While Loop");
            }
            */

            boolean done = internalVariableMap.contains(ControlEnum.BREAK);
            if (done) {
                return;
            }


            done = internalVariableMap.contains(ControlEnum.RETURN);
            if (done) {
                return;
            }
        }
    }

    public void printStatements () {
        for (Object o : this) {
            System.out.println("while statement ='"+o+"'");
        }
    }
}

