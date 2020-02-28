package ssinai.scribbler.gui;

import ssinai.scribbler.parser.Parser;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 30, 2009
 * Time: 3:25:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class MPError {

    private String errorString;
    private int matchIndex;

    private int start =-1;
    private int end = -1;
    private Parser.Pass pass = Parser.Pass.UNINITIALIZED ;

    public static MPError lastError;

    public MPError () {
        System.out.println("created MPError");
        lastError = this;
    }

    public void setLineBounds (int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getLineStart () {
        return start;
    }

    public int getLineEnd () {
        return end;
    }

    public void setErrorString (String errorString) {
        System.out.println("setting errorString to '"+errorString+"'");
        this.errorString = errorString;
    }

    public String getErrorString () {
        StringBuilder builder = new StringBuilder();
        builder.append(errorString);
        return builder.toString();
    }

    public void setMatchIndex (int matchIndex) {
        this.matchIndex = matchIndex;
    }

    public int getMatchIndex () {
        return matchIndex;
    }

    public int getEndIndex () {
        return matchIndex + errorString.length();
    }

    public void setPass (Parser.Pass pass) {
        this.pass = pass;
    }

    public Parser.Pass getPass () {
        return pass;
    }

    boolean detailed = false;
    public String toString () {
        System.out.println("MPError toString getErrorString="+getErrorString());
        StringBuilder builder = new StringBuilder();
        builder.append("ERROR-> ");
        if (detailed) {
            builder.append(getErrorString());
            builder.append(", matchIndex=");
            builder.append(matchIndex);
            builder.append(" line from ").append(start).append(" to ").append(end);
            Parser.Pass pass = getPass();
            builder.append(", pass="+pass);
        } else {
            String s = getErrorString();
            System.out.println("s='"+s+"'");
            int index = s.indexOf(":");
            System.out.println("index="+index);
            String newS = s.substring(index, s.length());
            System.out.println("newS="+newS);
            builder.append(newS);
        }
        System.exit(1);

        return builder.toString();
    }
}

