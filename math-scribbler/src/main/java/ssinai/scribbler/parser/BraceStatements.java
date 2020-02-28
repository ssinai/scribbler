package ssinai.scribbler.parser;

import ssinai.scribbler.gui.CalculateWorker;

/**
 * Created by IntelliJ IDEA.
 * User: ssinai
 * Date: Dec 2, 2010
 * Time: 1:08:13 PM
 * To change this template use File | Settings | File Templates.
 */

class BraceStatements extends Statement {
    public void eval(Parser parser) throws InterruptedException {
        //      System.out.println("processing BraceStatemetns at "+getIndex());
        internalVariableMap.clear();
        for (int i = 1 ; i < size()-1 ; i++) {
            Statement statement = (Statement)get(i);
            System.out.println("brace statements processing statement "+statement);
            //           System.out.println("Parser.getMatchIndex="+Parser.getMatchIndex());
            statement.eval(parser);
            //        statement.exec(parser);
            //           System.out.println("past processing statement "+statement);

            if (checkIfCancelled()) {
                //               System.out.println("Cancelled, bye-bye");
                throw new InterruptedException("Cancelled in brace statements");
            }

            boolean done = internalVariableMap.contains(ControlEnum.CONTINUE);
            if (done) {
                return;
            }


            System.out.println("checking internalVariableMap for break");
            System.out.println("internalVariableMap is "+internalVariableMap);
            done = internalVariableMap.contains(ControlEnum.BREAK);
            if (done) {
                System.out.println("got break");
                return;
            }

            done = internalVariableMap.contains(ControlEnum.RETURN);
            if (done) {
                return;
            }
        }
    }

    public boolean checkIfCancelled () {

        if (CalculateWorker.getCalculateWorker() != null) {
            boolean cancelled = CalculateWorker.getCalculateWorker().isCancelled();
            //        System.out.println("checkIfCancelled returning "+cancelled);
            return cancelled;
            //      return CalculateWorker.getCalculateWorker().isCancelled();
        }
        return false;

    }
}


