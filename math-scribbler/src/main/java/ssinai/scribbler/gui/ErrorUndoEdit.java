package ssinai.scribbler.gui;

import ssinai.scribbler.parser.MPException;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Jan 13, 2010
 * Time: 1:23:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorUndoEdit extends AbstractUndoableEdit {

    ScribEditor editor;
    //   MPError error;
    MPException error;

//    public ErrorUndoEdit(ScribEditor editor, MPError error) {
    public ErrorUndoEdit(ScribEditor editor, MPException error) {
        super();
        this.editor = editor;
        this.error = error;
    }

    public void undo () throws CannotUndoException {
        super.undo();
        try {

            editor.getDocument().setError(null);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        editor.getDocument().firePropertyChange("highlight", true, false);
        editor.getTextPane().requestFocusInWindow();
    }

    public void redo () throws CannotRedoException {
        super.redo();
        try {
            editor.getDocument().setError(error);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        editor.getDocument().firePropertyChange("highlight", true, false);
        editor.getTextPane().requestFocusInWindow();
    }

    public String getPresentationName () {
        return "highlight error";
    }

    public boolean isSignificant () {
        return true;
    }
}

