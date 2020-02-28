package ssinai.scribbler.actions;

import ssinai.scribbler.utils.Props;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 28, 2009
 * Time: 1:01:21 AM
 * To change this template use File | Settings | File Templates.
 */

public class ExitAction extends ScribAction {

    public ExitAction () {
        super("Exit", "/images/stock_calc-cancel.png");
    }

    public void actionPerformed (ActionEvent evt) {

/*
        System.out.println("ExitAction padList="+ScribPad.padList);
       // ScribPad scribPad = ScribPad.currentScribPad;
        for (ScribPad sp : ScribPad.padList) {
            System.out.println(sp.getDocument().getTitle() + " saveable="+sp.getDocument().isChanged());

            int res = FileUtils.checkForSave(sp, FileUtils.EXITING);
            if (res == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        Props.writeProperties();
      //  scribPad.dispose();
     */
        Props.writeProperties();
        System.exit(1);
    }
}
