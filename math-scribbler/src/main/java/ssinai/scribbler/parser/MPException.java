package ssinai.scribbler.parser;

import ssinai.scribbler.parser.Parser;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Oct 18, 2009
 * Time: 11:24:52 AM
 * To change this template use File | Settings | File Templates.
 */

public class MPException extends RuntimeException {

    private int matchIndex = -1;


    private String errorString;

 //   private int start =-1;
 //   private int end = -1;
    private Parser.Pass pass = Parser.Pass.UNINITIALIZED ;

  //  public static MPException lastException;


    public MPException (String s) {
        super(s);
        System.out.println("created MPException s="+s);
    }


    public MPException (Throwable cause) {
        super(cause);
        System.out.println("created MPException cause="+cause);
    }

    public MPException (Throwable cause, String message) {
        super(message, cause);
        System.out.println("created MPException cause="+cause+", message="+message);
    }

    public void setMatchIndex (int matchIndex) {
        this.matchIndex = matchIndex;
    }

    public int getMatchIndex () {
        return matchIndex;
    }

    /*
    public String toString () {
        return getCause().getMessage()+", matchIndex="+matchIndex;
    }
    */


    /*
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
    */
    

    public void setErrorString (String errorString) {
        System.out.println("setting errorString to '"+errorString+"'");
        this.errorString = errorString;
    }

    public String getErrorString () {
        return errorString;
    }

    public String toString () {
        StringBuilder builder = new StringBuilder();
        if (getCause() != null) {
            builder.append(getCause().getMessage());
        } else if (getMessage() != null) {
            builder.append(getMessage());
        } else {
            builder.append("Unknown error");
        }
        builder.append (" : ");
        builder.append(errorString);
        return builder.toString();
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

    boolean detailed = true;
    /*
    public String toString () {
        //    System.out.println("MPException toString getErrorString="+getErrorString());
        System.out.println("MPException errorString="+getCause());
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
        //     System.exit(1);

        return builder.toString();
    }
    */

}

