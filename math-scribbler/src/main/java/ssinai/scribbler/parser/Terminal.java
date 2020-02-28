package ssinai.scribbler.parser;

import org.apache.log4j.Level;

import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Jun 22, 2009
 * Time: 2:57:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Terminal extends Parser {

    Pattern p;

    boolean ignoreWhitespace;

    public String toString () {
        return p.toString();
    }

    public Terminal () {
    }

    public Terminal (String s) {
        this(s, true);
    }

    public Terminal (String s, boolean ignoreWhitespace) {
        super(s);
        this.ignoreWhitespace = ignoreWhitespace;

        if (ignoreWhitespace == true) {
            s = "\\s{0,}" + s;
        }
        p = Pattern.compile(s);
    }


    public void setIgnoreWhitespace (boolean ignore) {
        ignoreWhitespace = ignore;
    }

    public boolean match (Assembly a) {
 //       logger.debug("Terminal\t\t"+getName()+" match comparing "+a+" against '"+p.pattern()+"' inMatrix="+inMatrix);

        if (p == null) return false;

        m = p.matcher(a.getString());
        matchCount++;

        if (stepMode) {
            logger.debug("Press Return Key");
            try {
                byte [] byteArray = new byte[10];
                int r = System.in.read(byteArray);
            } catch (Exception e) {
                logger.debug("StepMode exception:"+e);
            }
        }

  //      logger.debug("Calling m.lookingAt()");
        boolean found = m.lookingAt();
        //    logger.info("found="+found+", m="+m);

        if (logger.getLevel()==Level.DEBUG) {
            StringBuilder buf = new StringBuilder();
            buf.append(found).append(" match\t\t").append(m.pattern());
            if (found) {
                buf.append(" group='").append(m.group()).append("' at ").append(a.getIndex());
            }
     //       logger.debug(buf.toString());
        }

        if (found) {

  //          logger.debug("Terminal found '"+m.group()+"' at start "+m.start()
   //                 + ", end="+m.end() +", index="+a.getIndex());

            a.incrementIndex(m.end());
            setMatchIndex(a.getIndex());
            //     logger.info("matchIndex="+matchIndex);

            if (pass == Pass.PARSE) {
                a.push(m.group());
            } else if (!discard) {
                a.push(m.group());
            }

            if (assemblers != null && assemblers.size() > 0) {
                for (Assembler assembler : assemblers) {
                    assembler.workOn(a);
                }
            }

            return true;
        }

        return false;
    }
}
