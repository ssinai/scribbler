package ssinai.scribbler.actions;

import ssinai.scribbler.gui.ScribTabbedPane;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 2, 2009
 * Time: 6:19:00 PM
 * To change this template use File | Settings | File Templates.
 */

public class CopyAction extends ScribAction {
    public CopyAction() {
        super("Copy", "/images/stock_copy.png");
    }

    public void actionPerformed (ActionEvent evt) {
        ScribTabbedPane.getCurrentEditor().getTextPane().copy();
    }
}
