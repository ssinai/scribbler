package ssinai.scribbler.actions;

import ssinai.scribbler.gui.ScribOps;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 27, 2010
 * Time: 5:27:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReloadOpsAction extends ScribAction {

    public ReloadOpsAction () {
        super("Reload", "/images/stock_refresh.png");
    }

    public void actionPerformed (ActionEvent evt) {
        System.out.println("Calling loadOps");
        ScribOps.loadOps();
    }

}
