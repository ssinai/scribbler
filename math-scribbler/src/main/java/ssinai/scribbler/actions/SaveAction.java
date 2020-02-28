package ssinai.scribbler.actions;

import ssinai.scribbler.utils.FileUtils;

import java.awt.event.ActionEvent;

import ssinai.scribbler.gui.ScribTabbedPane;
import ssinai.scribbler.gui.ScribEditor;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 29, 2009
 * Time: 3:47:08 AM
 * To change this template use File | Settings | File Templates.
 */

public class SaveAction extends ScribAction {
    public SaveAction () {
        super("Save", "/images/stock_save.png");
    }

    public void actionPerformed (ActionEvent evt) {
        System.out.println("Save Action actionPerformed");
        ScribTabbedPane tabbedPane = ScribTabbedPane.getCurrentTabbedPane();
        int index = tabbedPane.getSelectedIndex();
        System.out.println("index="+index);
        if (index > -1) {
            ScribEditor editor = (ScribEditor)tabbedPane.getComponentAt(index);
            FileUtils.checkForSave(editor, FileUtils.SAVE);
        }
    }
}
