package ssinai.scribbler.gui;

import javax.swing.undo.*;
import javax.swing.event.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultStyledDocument;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 29, 2009
 * Time: 3:27:45 AM
 * To change this template use File | Settings | File Templates.
 */


public class MonitorableUndoManager extends UndoManager {
//    protected EventListenerList listenerList = new EventListenerList();

//    protected ChangeEvent changeEvent;
    protected CompoundEdit compoundEdit;
    public MonitorableUndoManager () {
        super();
    }

    public synchronized void setLimit (int l) {
        super.setLimit(l);
        ActionHandler.updateUndoRedo(this);
    }

    public synchronized void discardAllEdits () {
        super.discardAllEdits();
        ActionHandler.updateUndoRedo(this);
    }

    public synchronized void undo () throws CannotUndoException {
//        System.out.println("in MonitorableUndoManager undo compoundEdit="+compoundEdit);
        try {

            if (compoundEdit != null && compoundEdit.isInProgress()) {
                //              System.out.println("undo closing compoundEdit");
                compoundEdit.end();
                if (compoundEdit.isSignificant()) {
                    addEdit(compoundEdit);
                }
                compoundEdit = null;
            }


  //          System.out.println("calling super.undo()");
            super.undo();
  //          System.out.println("Calling fireChangeEvent");
            ActionHandler.updateUndoRedo(this);

 //           System.out.println("past fireChangeEvent");
        } catch (Exception e ) {
            System.out.println("undo exception:"+e);
        }
    }

    public synchronized void redo () throws CannotRedoException {
        try {
            if (compoundEdit != null && compoundEdit.isInProgress()) {
   //             System.out.println("redo closing compoundEdit");
                compoundEdit.end();
                /*
                          if (compoundEdit.isSignificant()) {
                             //                  System.out.println("adding compoundEdit");
                       //      super.addEdit(compoundEdit);
                              addEdit(compoundEdit);
                         }
                */

                compoundEdit = null;
            }

            super.redo();
            ActionHandler.updateUndoRedo(this);
        } catch (Exception e) {
            System.out.println("redo exception:"+e);
        }
    }

    public boolean canUndo () {
  //      System.out.println("in MonitorableUndoManager canRedo");

 //      System.out.println("in MonitorableUndoManager canUndo superCanUndo()="+super.canUndo()+", compoundEdit="+compoundEdit);
        if (super.canUndo() || compoundEdit != null) {
            return true;
        }

//        System.out.println("canUndo returning false");
        return false;
    }

    public boolean canRedo () {
   //            System.out.println("in MonitorableUndoManager canRedo");
   //            System.out.println("super.canRedo="+super.canRedo()+", compoundEdit="+compoundEdit);

        if (super.canRedo() && compoundEdit == null) {
            //           System.out.println("canRedo returning true");
            return true;
        }
//        System.out.println("canRedo returning false");
        return false;
    }

    public synchronized void closeEdit () {
        if (compoundEdit == null) return;

        compoundEdit.end();
        addEdit(compoundEdit);
        compoundEdit = null;
    }

    public synchronized boolean addEdit (UndoableEdit anEdit) {
        boolean retval = super.addEdit(anEdit);

   //     System.out.println("undoManager size="+edits.size());
        return retval;
    }

    public String getUndoPresentationName () {
        return super.getUndoPresentationName();
        //      return "Hee Hee";
    }

    public String getRedoPresentationName () {
        return super.getRedoPresentationName();
        //       return "Haw Haw";
    }


    DocumentEvent.EventType lastAction = null;
    boolean lastSpecial = false;
    public void undoableEditHappened (UndoableEditEvent evt) {
        UndoableEdit undoableEdit = evt.getEdit();

        if (undoableEdit instanceof DefaultStyledDocument.AttributeUndoableEdit) {
            return;
        }

        if (undoableEdit instanceof AbstractDocument.DefaultDocumentEvent) {
            if (((AbstractDocument.DefaultDocumentEvent)undoableEdit).getType() == DocumentEvent.EventType.CHANGE) {
                return;
            }

            try {
                if (((AbstractDocument.DefaultDocumentEvent)undoableEdit).getType() == DocumentEvent.EventType.REMOVE) {

                    if (lastAction == DocumentEvent.EventType.INSERT) {
                        if (compoundEdit == null) {
                            compoundEdit = new CompoundEdit();
                            compoundEdit.addEdit(undoableEdit);
                        } else {
                            compoundEdit.end();
                            addEdit(compoundEdit);
                            compoundEdit = new CompoundEdit();
                            compoundEdit.addEdit(undoableEdit);
                        }
                    } else {
                        if (compoundEdit == null) {
                            compoundEdit = new CompoundEdit();
                        }
                        compoundEdit.addEdit(undoableEdit);
                    }
                    lastAction = DocumentEvent.EventType.REMOVE;
                    ActionHandler.updateUndoRedo(this);

                    return;

                } else if (((AbstractDocument.DefaultDocumentEvent)undoableEdit).getType() == DocumentEvent.EventType.INSERT) {
                    AbstractDocument.DefaultDocumentEvent edit = (AbstractDocument.DefaultDocumentEvent)evt.getEdit();

                    DefaultStyledDocument doc = (DefaultStyledDocument)edit.getDocument();
                    int offset = edit.getOffset();
                    int length = edit.getLength();
                    String editText = doc.getText(offset, length);

                    char c = editText.charAt(0);
                    boolean isSpecial = Character.isJavaIdentifierPart(c);


       //             System.out.println("c="+c+", isSpecial="+isSpecial+", lastSpecial="+lastSpecial);

                    if ((isSpecial != lastSpecial) || lastAction == DocumentEvent.EventType.REMOVE) {

                        lastSpecial = isSpecial;
                        if (compoundEdit == null) {
                            compoundEdit = new CompoundEdit();
                            compoundEdit.addEdit(undoableEdit);
                        } else {
                            compoundEdit.end();
                            addEdit(compoundEdit);
                            compoundEdit = new CompoundEdit();
                            compoundEdit.addEdit(undoableEdit);
                        }
                    }  else {
                        lastSpecial = isSpecial;
                        if (compoundEdit == null) {
                            compoundEdit = new CompoundEdit();
                        }

                        compoundEdit.addEdit(undoableEdit);
                    }
                    lastAction = DocumentEvent.EventType.INSERT;
                    ActionHandler.updateUndoRedo(this);

                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.undoableEditHappened(evt);
        ActionHandler.updateUndoRedo(this);
    }

}
