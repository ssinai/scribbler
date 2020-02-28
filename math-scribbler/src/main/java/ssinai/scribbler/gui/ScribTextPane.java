package ssinai.scribbler.gui;

import javax.swing.*;
import javax.swing.text.Highlighter;

import ssinai.scribbler.utils.Props;

import javax.swing.text.Caret;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 13, 2010
 * Time: 3:21:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScribTextPane extends JTextPane {

    public final static Highlighter highlighter = new UnderlineHighlighter(null);

    public ScribTextPane (ScribDocument doc) {
        System.out.println("in ScribTextPane constructor");
        setEditorKit(new ScribEditorKit());
        setSelectedTextColor(Color.cyan);
        setSelectionColor(Color.black);
        
        
        Color backgroundColor = (Color) Props.PROPS.get(Props.BACKGROUND);
        setBackground(backgroundColor);

        setHighlighter(highlighter);
        Caret caret = new CornerCaret();
        setCaret(caret);
        setCaretColor(Color.black);
        System.out.println("setting document");

        setDocument(doc);

        System.out.println("past setting document");
        System.out.println("leaving ScribTextPane constructor");
    }


}

