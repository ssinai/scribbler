package ssinai.scribbler.actions;

import ssinai.scribbler.gui.ScribTabbedPane;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 2, 2009
 * Time: 6:18:17 PM
 * To change this template use File | Settings | File Templates.
 */

public class CutAction extends ScribAction {
    public CutAction() {
    //    super("Cut", "/images/stock_cut.png");
        super("Cut", "/images/cut.png");

    }

    public void actionPerformed (ActionEvent evt) {
        ScribTabbedPane.getCurrentEditor().getTextPane().cut();
    }
}
