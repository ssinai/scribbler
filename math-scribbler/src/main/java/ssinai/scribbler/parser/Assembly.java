package ssinai.scribbler.parser;

import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Jun 22, 2009
 * Time: 3:01:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class Assembly {

    String s;
    int index;
    Stack<Object> stack;
    Target target;
    int numMatches;
    static int maxIndex;

    Logger logger;

    public Assembly (String s) {
        this.s = s;
        index = 0;
        maxIndex = 0;
        numMatches = 0;
        stack = new Stack<Object>();
        logger = Parser.getLogger();
    }

    public String getString () {
        return s.substring(index);
    }

    public String getInitialString () {
        return s;
    }


    public void setTarget (Target target) {
        this.target = target;
    }

    public Target getTarget () {
        if (target == null) {
            target = new Target();
        }
        return target;
    }

    public String toString () {
        StringBuilder buffer = new StringBuilder();
        buffer.append("s='").append(s).append("'  ");
        buffer.append(stack);
        buffer.append(" [index:");
        buffer.append(index).append("] ");
        buffer.append("atEnd=").append(atEnd()).append(" ");
        if (target != null) {
            buffer.append(" [target=");
            buffer.append(target.toString()).append("] ");
        }
        buffer.append(s.substring(0,index));
        buffer.append("^");
        buffer.append(s.substring(index));
        return buffer.toString();
    }

    public void reset () {
        index = 0;
        stack.clear();
    }

    public int getNumMatches () {
        return numMatches;
    }

    public Stack<Object> copyStack () {
        Stack<Object> newStack = new Stack<Object>();
        newStack.addAll(stack);
        return newStack;
    }

    public Target copyTarget () {
        if (target == null) return null;
        return target.copy();
    }

    public void setStack (Stack<Object> stack) {
        this.stack = stack;
    }

    public boolean stackIsEmpty () {
        return stack.isEmpty();
    }

    public Stack<Object> getStack () {
        return stack;
    }

    public int getLength () {
        return s.length();
    }

    public void setIndex (int i) {
        index = i;
    }

    public int getIndex () {
        return index;
    }

    public void incrementIndex (int i) {
        index += i;
    }

    public boolean atEnd () {
        return (getIndex() == getLength()) ;
    }

    public Object pop () {
        if (stack.isEmpty()) return null;
        return stack.pop();
    }

    public void push (Object o) {
        stack.push(o);
        numMatches++;
   //     logger.debug("pushed "+o+", stack="+stack);
    }

    public Object peek () {
        if (stack.isEmpty()) return null;
        return stack.peek();
    }

    public Assembly copy () {
        Assembly assembly = new Assembly(s);
        assembly.setStack(copyStack());
        assembly.setIndex(index);
        assembly.setTarget(copyTarget());
        return assembly;
    }

    public void restore (Assembly oldAssembly) {
        s = oldAssembly.s;
        stack = oldAssembly.stack;
        index = oldAssembly.index;
        target = oldAssembly.target;
    }

    public void put (String key, Object value) {
        getTarget().put(key, value);
    }

    public Object get (String key) {
        return getTarget().get(key);
    }
}

