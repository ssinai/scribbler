package ssinai.scribbler.gui;

import ssinai.scribbler.parser.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.undo.UndoManager;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.ArrayList;
import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 8, 2010
 * Time: 11:56:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScribDocument extends DefaultStyledDocument {


    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public HashMap<Integer, MPComponent> componentIndexMap = new HashMap<Integer, MPComponent>();
    private ComponentMap componentStringMap = ComponentMap.getComponentMap();
    private MatrixMap matrixMap = MatrixMap.getMatrixMap();


    UpdateListener updateListener;
//    private UndoableEditListener[] listeners;

    private boolean changed = false;
    public static final String NO_FILE = "No File";

    private String name = NO_FILE;

    private static String startCommentString = "[/][*]";
    private static String answerString = ">>[^;]*;";
    private static Matcher commentMatcher;
    private static Matcher answerMatcher;
    private static Matcher startCommentMatcher;

    static {
        Pattern commentPattern = Pattern.compile("[/][/][^\n]*([\n]|\\z)");
        commentMatcher = commentPattern.matcher("");
        Pattern answerPattern = Pattern.compile(answerString);
        answerMatcher = answerPattern.matcher("");
        Pattern startCommentPattern = Pattern.compile(startCommentString);
        startCommentMatcher = startCommentPattern.matcher("");

        painter = new UnderlineHighlighter.UnderlineHighlightPainter(Color.red);
    }

    boolean useColors = true;
    public static UnderlineHighlighter.UnderlineHighlightPainter painter;

    public static TreeMap<Integer, ArrayList> tabMap = new TreeMap<Integer, ArrayList>();

    public ScribDocument () {
        super(DocStyles.getStyleContext());
        System.out.println("ScribDocument constructor");
        init();
    }

    private void init () {
        tabMap = new TreeMap<Integer, ArrayList>();

        setDefaultTabs();
        setupTabs();
        updateListener = new UpdateListener();
        propertyChangeSupport = new PropertyChangeSupport(this);
        enableListeners();
        changed = false;
    }


    DocumentListener docListener = new DocListener();

    class DocListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            System.out.println("insertUpdate e="+e);
            setChanged(true);
        }

        public void removeUpdate(DocumentEvent e) {
            System.out.println("removeUpdate getLength()="+getLength());
            changed = getLength() > 0;
            setChanged(changed);
        }

        public void changedUpdate(DocumentEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private UndoManager undoManager;

    public void setUndoableEditListener (UndoManager undoManager) {
        this.undoManager = undoManager;
        addUndoableEditListener(undoManager);
    }


    public void setChanged (final boolean changed) {
        this.changed = changed;

        if (changed == true) {
            SwingUtilities.invokeLater(new Runnable () {
                public void run () {
                    firePropertyChange("highlightDoc", true, false);
                }
            }) ;
        }


    }


    public void replace (int offset, int length, String text, AttributeSet atts) {
        try {
            super.replace(offset, length, text, atts);
        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void remove (int offset, int len) {
        try {
            super.remove(offset, len);
        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public void firePropertyChange (String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }


    public void highlightDocument () {

        //      if (true) return;

        if (getLength() == 0) {
            return;
        }


        try {

            String text = getText(0, getLength());

            disableListeners();

            if (!useColors) {
                //     setCharacterAttributes(0, getLength(), monoStyle, true);
            } else {

                setCharacterAttributes(0, getLength(), DocStyles.getStyle(DocStyles.REGULAR_STYLE), false);

                answerMatcher.reset(text);

                while (answerMatcher.find()) {
                    int start = answerMatcher.start()+2;
                    int end = answerMatcher.end();
                    if (end <= start) end = start;
                    setCharacterAttributes(start, end - start - 1 , DocStyles.getStyle(DocStyles.ANSWER_STYLE), false);
                }


                Highlighter.Highlight [] highlights = ScribTextPane.highlighter.getHighlights();
                for (Highlighter.Highlight h : highlights) {
                    if (h.getPainter() instanceof UnderlineHighlighter.UnderlineHighlightPainter) {
                        ScribTextPane.highlighter.removeHighlight(h);
                    }
                }

                if (error != null) {
                    System.out.println("found an error");
                /*
                    int index = error.getMatchIndex();
                    startIndex = -1;
                    endIndex = -1;
                    getErrorString(index);
                    setCharacterAttributes(startIndex, endIndex, DocStyles.getStyle(DocStyles.ERROR_STYLE), false);
                    ScribTextPane.highlighter.addHighlight(startIndex, endIndex, painter);
                  */

                    /*
                    if (startErrorPosition != null && endErrorPosition != null) {
                        int start = startErrorPosition.getOffset();
                        int end = endErrorPosition.getOffset();
                        System.out.println("h error start="+start+", end="+end);
                        setCharacterAttributes(start, end, DocStyles.getStyle(DocStyles.ERROR_STYLE), false);
                        ScribTextPane.highlighter.addHighlight(start, end, painter);
                    }
                    */
                }


                startCommentMatcher.reset(text);
                int startIndex = 0;
                Style commentStyle = DocStyles.getStyle(DocStyles.COMMENT_STYLE);
                while(startCommentMatcher.find(startIndex)) {
                    int start = startCommentMatcher.start();
                    int endIndex = text.indexOf("*/", start);
                    if (endIndex != -1) {
                        setCharacterAttributes(start, endIndex+2-start, commentStyle, false);
                    } else {
                        break;
                    }
                    startIndex = endIndex+2;
                }


                commentMatcher.reset(text);
                startIndex = 0;
                while(commentMatcher.find(startIndex)) {
                    int start = commentMatcher.start();
                    int endIndex = text.indexOf("\n", start);
                    if (endIndex == -1) {
                        endIndex = getLength();
                    }
                    setCharacterAttributes(start, endIndex-start, commentStyle, false);

                    startIndex = endIndex;
                }

            }
        } catch (Exception e) {
            System.out.println("highlightDocument exception:"+e);
        } finally {
            setChanged(false);
            SwingUtilities.invokeLater(new Runnable () {
                public void run () {
                    enableListeners();
                }
            });
        }

        // System.out.println("highlightDocument calling enableListeners doc="+this);
    }

    int startIndex = -1;
    int endIndex = -1;
    public void getErrorString (int matchIndex) {
        System.out.println("error in parsing at "+matchIndex);
        String candidate = getText();
        if (matchIndex >= candidate.length()) {
            matchIndex = candidate.length()-1;
        }
        char c = candidate.charAt(matchIndex);
        System.out.println("c="+c);

        System.out.println("getErrorString candidate="+candidate);

        if (c == '\n') {
            endIndex = matchIndex;
            startIndex = candidate.lastIndexOf("\n", matchIndex-1);
        } else {
            endIndex = candidate.indexOf("\n", matchIndex);
            startIndex = candidate.lastIndexOf("\n", matchIndex);
        }

        if (endIndex == -1) {
            endIndex=candidate.length();
        }

        if (startIndex == -1) {
            startIndex = 0;
        }

        /*
        String tmpString = candidate.substring(startOfLineIndex, endOfLineIndex);
        tmpString = tmpString.trim();

        return tmpString;
        */
    }

    public static void setFont (String family, int size, boolean isItalic, boolean isBold) {
        Style baseStyle = DocStyles.getStyle(DocStyles.BASE_STYLE);
        if (family != null) {
            StyleConstants.setFontFamily(baseStyle, family);
        }

        StyleConstants.setFontSize(baseStyle, size);
        StyleConstants.setItalic(baseStyle, isItalic);
        StyleConstants.setBold(baseStyle, isBold);
    }



    public void styleChanged (Style style) {
        // this will freeze if try to highlight in the middle of a calculation
        if (CalculateWorker.isCalculating()) {
            return;
        }

        if (getLength() == 0) {
            return;
        }

        firePropertyChange("highlightDoc", true, false);
    }



    public void enableListeners () {
        addUndoableEditListener(undoManager);
        addDocumentListener(docListener);
        propertyChangeSupport.addPropertyChangeListener(updateListener);
    }

    public void disableListeners () {
        removeUndoableEditListener(undoManager);
        removeDocumentListener(docListener);
        propertyChangeSupport.removePropertyChangeListener(updateListener);
    }


    class UpdateListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            String strPropertyName = evt.getPropertyName();
            if ("highlight".equals(strPropertyName) && changed == true) {
                highlightDocument();
            } else if ("highlightDoc".equals(strPropertyName)) {
                highlightDocument();
            }
        }
    }

    public String getText () {
        try {
            return getText(0, getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }

    private void copyMatMap(HashMap<Integer, Integer> map) {
        if (map != null) {
            matMap = new HashMap<Integer, Integer>(map);
        }
    }

    private void copyTableMap (TreeMap<Integer, int []> map) {
        if (map != null) {
            tableMap = new TreeMap<Integer, int[]>(map);
        }
    }

    public void copyComponentMap (ComponentMap map) {
        if (map != null) {
            componentStringMap.putAll(map);
        }
    }


    public void copyMatrixMap (MatrixMap map) {
        if (map != null) {
            matrixMap.putAll(map);
        }
    }

    private HashMap<Integer, Integer> matMap;
    private TreeMap<Integer, int []> tableMap;


    public void parseForMatrices () {

        String s2 = getText();
        System.out.println("parseForMatrix oldString='"+s2);


        Pattern p1 = Pattern.compile("__Matrix:\\d+:|__JComponent:\\d+:");
        Matcher m1 = p1.matcher(s2);
        StringBuffer buf2 = new StringBuffer();
        matMap = new HashMap<Integer, Integer>();
        tableMap = new TreeMap<Integer, int []>();

        while (m1.find()) {

            System.out.println("found group '"+m1.group()+"'");
            if (m1.group().startsWith("__Matrix")) {
                ScribMatrix mat = matrixMap.get(m1.group());
                System.out.println("SPOT 1 m1.start="+m1.start()+", mat="+mat);
                String matString = mat.toString();

                m1.appendReplacement(buf2, matString);
                //        System.out.println("buf2 now='"+buf2+"'");
                //  String tmpString = buf2.substring(m1.start());
                //      System.out.println("tmpString='"+tmpString+"'");
                int start = buf2.length()-matString.length();

                //       System.out.println("matMap put "+start+", "+(buf2.length()-start));
                matMap.put(start, buf2.length()-start);
                int [] tabArray = mat.formatTable();
                tableMap.put(start, tabArray);
            } else if (m1.group().startsWith("__JComponent"))  {
                m1.appendReplacement(buf2, "\n ");
                int bufLength = buf2.length();
                //             System.out.println("bufLength="+bufLength);
                MPComponent component = componentStringMap.get(m1.group());

                componentIndexMap.put(bufLength-1, component);
            }
        }
        m1.appendTail(buf2);
        System.out.println("matMap="+matMap);
        System.out.println("tableMap="+tableMap);



        replace(0, getLength(), buf2.toString(), null);


        setupTabs();

        Set<Integer> keys = componentIndexMap.keySet();
        for (Integer ckey : keys) {
            MPComponent c = componentIndexMap.get(ckey);
            //      System.out.println("component key="+ckey+", c="+c);
            //     System.out.println("newText.charAt='"+newText.charAt(key)+"'");
            SimpleAttributeSet atts = new SimpleAttributeSet();

            StyleConstants.setComponent(atts, (Component)c.copy());


            setCharacterAttributes(ckey, 1, atts, false);
        }

    }

    public void setupTabs () {

        System.out.println("in setupTabs matMap="+matMap);
        if (matMap == null) return;

        Set<Integer> keys1 = matMap.keySet();
        //    System.out.println("setupTabs matMap="+matMap);

        for (Integer key : keys1) {
            Integer len = matMap.get(key);
            /*
            String t = null;
            try {
                t = getText(key, len);
            } catch (BadLocationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
                   System.out.println("t='"+t+"'");
            */
            Style baseStyle = DocStyles.getStyle(DocStyles.BASE_STYLE);


            Font font = getFont(baseStyle);
            FontMetrics fm = DocStyles.getStyleContext().getFontMetrics(font);
            int fontSize = fm.charWidth('3');
            //        System.out.println("fontSize="+fontSize);

            // if there are no tables in this document, don't set up tabs for tables

            //        System.out.println("setupTables tableMap="+tableMap);

            if (tableMap == null) {
                return;
            }

            int [] maxColumnDimension =  tableMap.get(key);
            //        System.out.println("key="+key+", maxColumnDimension.length="+maxColumnDimension.length);

            if (maxColumnDimension.length > 0) {
                TabStop[] tabStops = new TabStop[maxColumnDimension.length];

                SimpleAttributeSet paraAtts = new SimpleAttributeSet();

                //            System.out.println("starting loop");
                for (int i = 0 ; i < maxColumnDimension.length ; i++) {
                    //               System.out.println("i="+i+", fontSize="+fontSize+", maxColDim="+maxColumnDimension[i]);
                    int w = fontSize * maxColumnDimension[i] ;
                    //               System.out.println("column "+i+" tab at "+w);
                    TabStop ts = new TabStop(w, TabStop.ALIGN_DECIMAL, TabStop.LEAD_NONE);
                    tabStops[i] = ts;
                }
                TabSet tabSet = new TabSet(tabStops);
                StyleConstants.setTabSet(paraAtts, tabSet);

                setParagraphAttributes(key, len, paraAtts, false);
            }
        }
    }

    /*
    public String parseForComponents (String oldString) {
        Pattern p = Pattern.compile("__JComponent:\\d+:");
        Matcher m = p.matcher(oldString);
        StringBuffer buf = new StringBuffer();


        while (m.find()) {
            m.appendReplacement(buf, "\n ");
            int bufLength = buf.length();
            //      System.out.println("bufLength="+bufLength);
            MPComponent component = componentStringMap.get(m.group());
            componentIndexMap.put(bufLength-1, component);
        }
        m.appendTail(buf);

        //       System.out.println("buf="+buf);
        return buf.toString();
    }
    */


    public ScribDocument copy () {

        //       System.out.println("in ScribDocument copy");
        final ScribDocument doc = new ScribDocument();

        try {
            String name = getName();
            doc.setName(name);
            //           System.out.println("copied doc with title = "+doc.getName());


            String text = getText();
            doc.insertString(0, text, null);

            //         System.out.println("Calling doc.copyMatMap");
            if (matMap != null) {
                doc.copyMatMap(matMap);
            }
            //           System.out.println("Calling doc.copyTableMap tableMap="+tableMap);
            if (tableMap != null) {
                doc.copyTableMap(tableMap);
            }
            //      System.out.println("Calling doc.setupTabs");
            doc.setDefaultTabs();
            doc.setupTabs();
            //      System.out.println("past doc.setupTabs");

            for (int i = 0 ; i < getLength() ; i++) {
                Element e = getCharacterElement(i);
                if (e.getName().equals("component")) {
                    AttributeSet atts = e.getAttributes();
                    MPComponent c = (MPComponent)StyleConstants.getComponent(atts);
                    // c.setUseColor(color);
                    SimpleAttributeSet newAtts = new SimpleAttributeSet();
                    StyleConstants.setComponent(newAtts, (Component)c.copy());
                    doc.setCharacterAttributes(i, 1, newAtts, false);
                }
            }

        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return doc;
    }


    public void setDefaultTabs () {
        //       System.out.println("in setDefaultTabs");
        Style baseStyle = DocStyles.getStyle(DocStyles.BASE_STYLE);
        Font font = getFont(baseStyle);
        FontMetrics fm = DocStyles.getStyleContext().getFontMetrics(font);
        int fontSize = fm.charWidth('3');
        //    System.out.println("fontSize="+fontSize);

        int w = 3 * fontSize;
        //    System.out.println("tab set w="+w);

        TabStop [] defaultTabStops = new TabStop[20];
        for (int i = 0 ; i < 20 ; i++) {
            TabStop ts = new TabStop(w*(i+1));
            defaultTabStops[i] = ts;
        }
        TabSet defaultTabSet = new TabSet(defaultTabStops);
        StyleConstants.setTabSet(baseStyle, defaultTabSet);

        setParagraphAttributes(0, getLength(), baseStyle, false);
    }


    private MPException error = null;

    //  private Position startErrorPosition;
    //  private Position endErrorPosition;

    public void setError (MPException error) {
        this.error = error;
        /*
        startErrorPosition = null;
        endErrorPosition = null;
        //    System.out.println("setError to "+error);

        try {
            
            startErrorPosition = createPosition(error.getLineStart());
            int errLength = error.getLineEnd()-error.getLineStart();
            endErrorPosition = createPosition(error.getLineStart()+errLength);

        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        */

    }
}

