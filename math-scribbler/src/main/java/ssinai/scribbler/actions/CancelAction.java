package ssinai.scribbler.actions;

import java.awt.event.ActionEvent;

import ssinai.scribbler.gui.CalculateWorker;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 30, 2009
 * Time: 11:32:28 PM
 * To change this template use File | Settings | File Templates.
 */


public class CancelAction extends ScribAction {
    public CancelAction() {
        super("Cancel", "/images/stop24.gif");
    }

    public void actionPerformed (ActionEvent evt) {
        CalculateWorker worker = CalculateWorker.getCalculateWorker();
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }
    }
}
