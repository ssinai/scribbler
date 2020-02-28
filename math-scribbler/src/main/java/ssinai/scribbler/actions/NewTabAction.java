package ssinai.scribbler.actions;

import ssinai.scribbler.gui.ScribTabbedPane;

import java.awt.event.ActionEvent;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 29, 2009
 * Time: 3:10:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class NewTabAction extends ScribAction {
    public NewTabAction() {
        super("New Tab", "/images/stock_new.png");
    }

    public void actionPerformed (ActionEvent evt) {
        ScribTabbedPane tabbedPane = ScribTabbedPane.getCurrentTabbedPane();
        tabbedPane.addTab();
    }

}
