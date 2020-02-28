package ssinai.scribbler.actions;

import ssinai.scribbler.gui.ScribTabbedPane;
import ssinai.scribbler.gui.ScribFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 6, 2010
 * Time: 4:26:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class NewPaneAction extends ScribAction {
    public NewPaneAction () {
        super("New Pane", "/images/stock_next.png");

    }

    public void actionPerformed (ActionEvent evt) {

        SwingUtilities.invokeLater(new Runnable () {
            public void run () {
                JComponent c = ScribTabbedPane.createNewPane();
                System.out.println("ScribTabbedPane count="+ScribTabbedPane.count());
                ScribFrame.FRAME.setMainTabbedPane(c);

            }
        }) ;
    }

}
