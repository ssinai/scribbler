package ssinai.scribbler.actions;

import ssinai.scribbler.gui.*;

import javax.swing.undo.CannotUndoException;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 4, 2009
 * Time: 11:16:03 PM
 * To change this template use File | Settings | File Templates.
 */


public class UndoAction extends ScribAction {

//    public static UndoAction UNDO_ACTION;

    public UndoAction() {
        super("Undo", "/images/edit-undo.png");

  //      UNDO_ACTION = this;
    }

    public void actionPerformed (ActionEvent evt) {

        ScribTabbedPane tabbedPane = ScribTabbedPane.getCurrentTabbedPane();
        int index = tabbedPane.getSelectedIndex();

        if (index < 0) {
            return;
        }

        ScribEditor editor = (ScribEditor)tabbedPane.getComponentAt(index);


        MonitorableUndoManager undoManager = editor.getUndoManager();

        try {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        } catch (CannotUndoException e) {
            System.out.println("CannotUndoException:"+e);
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
