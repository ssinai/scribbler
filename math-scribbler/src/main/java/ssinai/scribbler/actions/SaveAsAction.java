package ssinai.scribbler.actions;

import ssinai.scribbler.gui.ScribTabbedPane;
import ssinai.scribbler.gui.ScribEditor;

import java.awt.event.ActionEvent;

import ssinai.scribbler.utils.FileUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 29, 2009
 * Time: 3:48:11 AM
 * To change this template use File | Settings | File Templates.
 */


public class SaveAsAction extends ScribAction {
    public SaveAsAction() {
        super("Save As...", "/images/stock_save_as.png");
    }

    public void actionPerformed (ActionEvent evt) {
        System.out.println("SaveAs Action actionPerformed");
        ScribTabbedPane tabbedPane = ScribTabbedPane.getCurrentTabbedPane();
        int index = tabbedPane.getSelectedIndex();
        System.out.println("index="+index);
        if (index > -1) {
            ScribEditor editor = (ScribEditor)tabbedPane.getComponentAt(index);
            FileUtils.checkForSave(editor, FileUtils.SAVE_AS);
        }
    }
}