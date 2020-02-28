package ssinai.scribbler.gui;

import ssinai.scribbler.parser.*;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ssinai.scribbler.ops.FormatOps;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 30, 2009
 * Time: 1:38:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class CalculateWorker extends SwingWorker<ScribDocument, Void> {

    private MPException error = null;

    private static CalculateWorker calculateWorker;
    private Point viewPosition;

    private boolean clearOnly = false;
    private ScribDocument oldDoc;
    private ScribDocument newDoc;
    private String newString;

    private ProgressListener progressListener;

    private Cursor savedFrameCursor;
    private Cursor savedTextPaneCursor;
    private String oldText;

    private ScribEditor editor;

    private static boolean isCalculating = false;

    public static boolean isCalculating () {
        return isCalculating;
    }

    public CalculateWorker () {
        this(false);
    }

    public CalculateWorker(boolean clearOnly) {
        System.out.println("CalculateWorker clearOnly");
        this.clearOnly = clearOnly;
        isCalculating = true;

        ScribFrame.submitAction.setEnabled(false);
        ScribFrame.FRAME.setEmpty();
        ScribFrame.FRAME.removeErrorPanel();

        editor = ScribTabbedPane.getCurrentEditor();
        editor.getUndoManager().closeEdit();

        FormatOps.init();

        oldDoc = editor.getDocument();
        oldText = oldDoc.getText();
        JViewport viewport = editor.getViewport();
        viewPosition = viewport.getViewPosition();

        ComponentMap.getComponentMap().clear();
        MatrixMap.getMatrixMap().clear();


        getCaretRow();
        savedFrameCursor = ScribFrame.FRAME.getCursor();
        ScribFrame.FRAME.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        savedTextPaneCursor = editor.getTextPane().getCursor();
        editor.getTextPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));

        removePropertyChangeListener(progressListener);
        progressListener = new ProgressListener();

        addPropertyChangeListener(progressListener);
        newDoc = new ScribDocument();
        String title = oldDoc.getName();

        newDoc.setName(title);
        newDoc.disableListeners();


        calculateWorker = this;
    }


    public static CalculateWorker getCalculateWorker () {
        return calculateWorker;
    }


    public void updateProgress(int progress) {
        System.out.println("update progress "+progress);
        super.setProgress(progress);
        ScribFrame.setProgress(progress);
    }


    class ProgressListener implements PropertyChangeListener {
        public void propertyChange (PropertyChangeEvent evt) {
            String strPropertyName = evt.getPropertyName();
            //         System.out.println("CalcWorker propertyName='"+strPropertyName+"'");
            if ("progress".equals(strPropertyName)) {
                int progress = (Integer)evt.getNewValue();
                System.out.println("progress 2 "+progress);
                ScribFrame.setProgress(progress);
            }  else if ("cancelled".equals(strPropertyName)) {
                ScribFrame.setProgress(0);
                ScribFrame.FRAME.setEmpty();
                ScribFrame.FRAME.setCursor(savedFrameCursor);
                editor.getTextPane().setCursor(savedTextPaneCursor);
                editor.getDocument().setChanged(true);
                editor.getTextPane().requestFocusInWindow();
            }  else if ("completed".equals(strPropertyName)) {
                /*
                scribPad.setSaveEnabled(true);
                scribPad.setSaveAsEnabled(true);
                */
                ScribFrame.FRAME.setCursor(savedFrameCursor);
                editor.getTextPane().setCursor(savedTextPaneCursor);
                editor.getDocument().setChanged(true);
                editor.getTextPane().requestFocusInWindow();


                if (error == null) {
                    SwingUtilities.invokeLater (new Runnable () {
                        public void run () {
                            JViewport viewport = editor.getViewport();
                            viewport.setViewPosition(viewPosition);
                        }
                    });
                }

            }
        }
    }


    private int getCaretRow () {
        int caretPos = editor.getTextPane().getCaretPosition();
        Pattern pattern = Pattern.compile("\n");
        String text = oldDoc.getText();
        int caretRow = 0;
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            if (m.start() >= caretPos) {
                return caretRow;
            } else {
                caretRow++;
            }
        }
        return 0;
    }

    long startTime;
    long endTime;

    protected ScribDocument doInBackground() {

        startTime = System.currentTimeMillis();
        ScribParser scribParser = ScribParser.getScribParser();
        scribParser.clearVariables();
        updateProgress(0);


        try {
            PreProcessor preProcessor = new PreProcessor();
            newString = preProcessor.clearAnswers(oldText);


            if (!clearOnly) {
                newString = scribParser.calculate(newString);
            }

        } catch (Exception e) {
            System.out.println("doInBackground exception "+e);
            if (e instanceof InterruptedException) {
                System.out.println("Interrupted Exception");
            }

            if (e instanceof MPException) {
                error = (MPException)e;
            } else {
                error = new MPException(e);
            }
        }

        if (!clearOnly) {
            error = scribParser.getError();
        }
        System.out.println("SPOT 2 error="+error);
        return newDoc;
    }

    protected void done () {
        System.out.println("in done newString="+newString);
        System.out.println("error="+error);
        ScribDocument newDoc = null;
        try {

            /*
            if (isCancelled()) {
                clearProgress();
                wrapUp(true);
                return;
            }
            */

            newDoc = get();

            if (!clearOnly) {
                ScribFrame.FRAME.setResultIcon(error);
            }

            newDoc.insertString(0, newString, null);

            newDoc.copyMatrixMap(MatrixMap.getMatrixMap());
            newDoc.copyComponentMap(ComponentMap.getComponentMap());
            newDoc.parseForMatrices();

            editor.getTextPane().setDocument(newDoc);

            String oldText = oldDoc.getText();
            String newText = newDoc.getText();

//            System.out.println("Calculate Worker oldText='"+oldText+"'");
//            System.out.println("Calculate Worker newText='"+newText+"'");
//            System.out.println("error="+error);


            newDoc.enableListeners();
            newDoc.addUndoableEditListener(editor.getUndoManager());

            if (!oldText.equals(newText) || error != null) {

                if (error != null) {
                    ErrorUndoEdit errorEdit = new ErrorUndoEdit(editor, error);
                    UndoableEditEvent euee = new UndoableEditEvent(this, errorEdit);
                    editor.getUndoManager().undoableEditHappened(euee);
                    newDoc.setError(error);
                    ScribFrame.FRAME.displayErrorPanel(error);
                }
                DocumentUndoEdit edit = new DocumentUndoEdit(editor, oldDoc, newDoc);

                UndoableEditEvent uee = new UndoableEditEvent(this, edit);
                editor.getUndoManager().undoableEditHappened(uee);
            }

        } catch (InterruptedException e) {
            System.out.println("CalculateWorker caught InterruptedException");
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        wrapUp(false);
        endTime = System.currentTimeMillis();

        long time = endTime - startTime ;
        System.out.println("total time = "+time);


    }

    private void wrapUp (boolean cancelled) {
        ScribFrame.submitAction.setEnabled(true);
        isCalculating = false;

        if (cancelled) {
            getPropertyChangeSupport().firePropertyChange("cancelled", true, false);
        } else {
            getPropertyChangeSupport().firePropertyChange("completed", true, false);
        }
        getPropertyChangeSupport().firePropertyChange("highlightDoc", true, false);

    }

}
