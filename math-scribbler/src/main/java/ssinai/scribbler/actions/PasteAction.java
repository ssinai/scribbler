package ssinai.scribbler.actions;

import ssinai.scribbler.gui.ScribTabbedPane;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 2, 2009
 * Time: 6:20:04 PM
 * To change this template use File | Settings | File Templates.
 */


public class PasteAction extends ScribAction {
    public PasteAction() {
        super("Paste", "/images/stock_paste.png");
    }

    public void actionPerformed (ActionEvent evt) {
        ScribTabbedPane.getCurrentEditor().getTextPane().paste();
    }
}
