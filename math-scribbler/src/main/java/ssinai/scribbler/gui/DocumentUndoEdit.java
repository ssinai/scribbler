package ssinai.scribbler.gui;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 2, 2009
 * Time: 6:41:06 PM
 * To change this template use File | Settings | File Templates.
 */

public class DocumentUndoEdit extends AbstractUndoableEdit {

    ScribEditor editor;
    ScribDocument oldDoc;
    ScribDocument newDoc;
    int caretPos;

    public DocumentUndoEdit(ScribEditor editor, ScribDocument oldDoc, ScribDocument newDoc) {
        super();
        this.editor = editor;
        this.oldDoc = oldDoc;
        this.newDoc = newDoc;
    }


    public void undo () throws CannotUndoException {
        super.undo();
        try {
            editor.getTextPane().setDocument(oldDoc);
            int pos = oldDoc.getLength();
            editor.getTextPane().setCaretPosition(pos);
            ScribFrame.FRAME.setTitle(oldDoc.getName());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void redo () throws CannotRedoException {
        super.redo();
        try {
            editor.getTextPane().setDocument(newDoc);
            int pos = newDoc.getLength();
            editor.getTextPane().setCaretPosition(pos);
            ScribFrame.FRAME.setTitle(newDoc.getName());

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public boolean isSignificant () {
        return !oldDoc.getText().equals(newDoc.getText());
    }

    public String getPresentationName () {
        return "document";
    }
}
