package ssinai.scribbler.actions;

import ssinai.scribbler.utils.FileUtils;

import java.awt.event.ActionEvent;
import java.io.File;

import ssinai.scribbler.gui.ScribEditor;
import ssinai.scribbler.gui.ScribTabbedPane;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 28, 2009
 * Time: 2:00:55 AM
 * To change this template use File | Settings | File Templates.
 */

public class OpenAction extends ScribAction {
    public OpenAction () {
        super("Open...", "/images/stock_open.png");
    }

    public void actionPerformed (ActionEvent evt) {


        File file = FileUtils.selectFile();
        if (file == null) return;

        ScribEditor editor = ScribTabbedPane.getAvailableEditor(file);
    //    ScribEditor editor = ScribTabbedPane.getAvailableEditor();

        FileUtils.loadFile(editor, file);

    }
}
