package ssinai.scribbler.actions;

import java.awt.event.ActionEvent;

import ssinai.scribbler.gui.CalculateWorker;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 30, 2009
 * Time: 4:00:50 AM
 * To change this template use File | Settings | File Templates.
 */


public class ClearAction extends ScribAction {
    public ClearAction() {
        super("Clear", "/images/edit24.gif");
    }

    public void actionPerformed (ActionEvent evt) {
        CalculateWorker worker = new CalculateWorker(true);
        worker.execute();
    }
}
