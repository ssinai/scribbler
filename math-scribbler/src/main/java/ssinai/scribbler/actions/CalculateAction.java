package ssinai.scribbler.actions;

import java.awt.event.ActionEvent;

import ssinai.scribbler.gui.CalculateWorker;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 30, 2009
 * Time: 1:28:14 AM
 * To change this template use File | Settings | File Templates.
 */

public class CalculateAction extends ScribAction {
    public CalculateAction() {
        super("Calculate", "/images/lc_repeat.png");
    }

    public void actionPerformed (ActionEvent evt) {
        CalculateWorker worker = new CalculateWorker();
        worker.execute();
    }

}
