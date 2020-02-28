package ssinai.scribbler.parser;

import ssinai.scribbler.ops.FormatOps;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: ssinai
 * Date: Dec 2, 2010
 * Time: 1:52:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class Statement extends ArrayList implements Executable {
    String s;
    int index;
    Object answer;
    boolean showAnswer;
    public static Object lastAnswer;
    public enum ControlEnum {CONTINUE, BREAK, RETURN};
    public static TreeSet<ControlEnum> internalVariableMap = new TreeSet<ControlEnum>();

    public Statement () {
        super();
    }

    public Object getLastAnswer() {
        return lastAnswer;
    }

    public void setLastAnswer (Object lastAnswer) {
        this.lastAnswer = lastAnswer;
    }


    public Statement (Assembly a) {
        super(a.getStack());
        index = a.getIndex();
    }

    public Statement (String s) {
        this.s = s;
    }


    public void setShowAnswer(boolean val) {
        showAnswer=val;
    }

    public boolean getShowAnswer () {
        return showAnswer;
    }

    public void setAnswer (Object answer) {
        this.answer = answer;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex (int index) {
        this.index = index;
    }

    public void eval (Parser parser) throws InterruptedException {
        //        System.out.println("in eval with parser");
        Assembly x = new Assembly(toString());
        //        System.out.println("exec matching '"+toString()+"' getIndex()="+getIndex()+", parserType="+parser.getClass().getName());
        try {
            boolean b = parser.match(x);
            //             System.out.println("b was "+b);
        } catch (Exception e) {
            //             System.out.println("Caught a statement exception:"+e);
            //             System.out.println("getIndex="+getIndex());
            MPException ex = new MPException(e);
            ex.setMatchIndex(getIndex());
            throw ex;
        }
    }

    public String toString () {
        StringBuilder buf = new StringBuilder();
        for (Object o : this) {
            buf.append(o.toString());
        }
        s = buf.toString();

        if (showAnswer) {
            s = s.replace(">>", ">>"+getAnswer());
        }
        return s;
    }

    public Object getAnswer () {
        if (answer == null) {
            return null;
        } else if (answer instanceof Number) {
            return FormatOps.format(answer);
        } else if (answer instanceof Executable)  {
            return ((Executable)answer).getAnswer();
        }
        return answer;
    }


}

