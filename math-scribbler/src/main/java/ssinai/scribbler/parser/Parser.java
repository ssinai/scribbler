package ssinai.scribbler.parser;

import org.apache.log4j.*;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Jun 22, 2009
 * Time: 2:43:22 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Parser {

    public static enum Pass {UNINITIALIZED, PARSE, CALC};

    public ArrayList<Assembler> assemblers;
    public boolean discard;
    public String name;
    public static int matchCount;
    public static boolean stepMode;
    public String key;
    public Matcher m;
    public ConcurrentHashMap table;
    public int searchOffset;
    public int cursorOffset;
    public boolean useEnd;
    public boolean forward;
    static public int matchIndex;
    public static Pass pass;

    static Logger logger;

    static public Logger getLogger () {
        if (logger == null) {
            logger = Logger.getRootLogger();
            ConsoleAppender appender = new ConsoleAppender(new PatternLayout());
            logger.addAppender(appender);
            logger.setLevel(Level.OFF);
        }
        return logger;
    }


    public Parser () {
        table = new ConcurrentHashMap();
        forward = true;
        useEnd = false;
        matchIndex=0;
    }

    public Parser (String name) {
        this();
        this.name = name;
        matchIndex=0;
    }

    public void setMatchIndex (int index) {
        if (matchIndex < index) {
            matchIndex = index;
        }
    }

    public static int getMatchIndex () {
        return matchIndex;
    }

    public void setName (String name) {
        this.name=name;
    }

    public void setForward (boolean forward) {
        this.forward = forward;
    }

    public boolean isForward () {
        return forward;
    }

    public void setUseEnd (boolean useEnd) {
        this.useEnd = useEnd;
    }

    public boolean getUseEnd () {
        return useEnd;
    }

    public void setCursorOffset (int cursorOffset) {
        this.cursorOffset = cursorOffset;
    }

    public int getCursorOffset () {
        return cursorOffset;
    }

    public void setSearchOffset (int searchOffset) {
        this.searchOffset = searchOffset;
    }

    public int getSearchOffset () {
        return searchOffset;
    }

    public void put (Object key, Object value) {
        table.put(key, value);
    }

    public Object get (Object key) {
        return table.get(key);
    }

    public String getName () {
        if (name == null) {
            return getClass().getName();
        }
        return name;
    }

    public void addAssembler (Assembler assembler) {
        if (assemblers == null) {
            assemblers = new ArrayList<Assembler>();
        }
        assemblers.add(assembler);
    }

    public ArrayList<Assembler> getAssemblers () {
        return assemblers;
    }

    public abstract boolean match (Assembly a);

    public boolean find (Assembly a, int startPos) {
        return false;
    }

    public boolean lookingBack (Assembly a, int startPos) {
        return false;
    }

    public boolean fullMatch (Assembly a) {
        boolean res = match(a);
        if (res == true && a.atEnd()) {
            return true;
        } else {
            return false;
        }
    }

    public Parser discard () {
        return setDiscard(true);
    }

    public Parser setDiscard(boolean discard) {
        this.discard = discard;
        return this;
    }

    public Matcher getMatcher () {
        return m;
    }

}
