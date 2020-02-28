package ssinai.scribbler.actions;

import ssinai.scribbler.gui.MonitorableUndoManager;
import ssinai.scribbler.gui.ScribDocument;
import ssinai.scribbler.gui.ScribTabbedPane;
import ssinai.scribbler.gui.ScribEditor;

import javax.swing.undo.CannotRedoException;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 4, 2009
 * Time: 11:17:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class RedoAction extends ScribAction {

//    public static RedoAction REDO_ACTION;

    public RedoAction() {
        super("Redo", "/images/edit-redo.png");
  //      REDO_ACTION = this;
    }

    public void actionPerformed (ActionEvent evt) {

        ScribTabbedPane tabbedPane = ScribTabbedPane.getCurrentTabbedPane();
        int index = tabbedPane.getSelectedIndex();
        System.out.println("index="+index);
        if (index < 0) {
            return;
        }

        ScribEditor editor = (ScribEditor)tabbedPane.getComponentAt(index);
        System.out.println("redo editor="+editor);

        MonitorableUndoManager undoManager = editor.getUndoManager();
        System.out.println("\n\nRedo Action undoManager="+undoManager);

        try {

            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        } catch (CannotRedoException e) {
            System.out.println("CannotRedoException:"+e);
            Toolkit.getDefaultToolkit().beep();
        }
        System.out.println("leaving RedoAction actionPerformed ");
        ScribDocument doc = editor.getDocument();
        String text = doc.getText();

        System.out.println("undone text="+text);
    }
}

