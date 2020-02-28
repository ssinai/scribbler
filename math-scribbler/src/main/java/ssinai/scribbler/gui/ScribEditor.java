package ssinai.scribbler.gui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 13, 2010
 * Time: 3:14:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScribEditor extends JScrollPane {

    private ScribTabbedPane parent;
    private String name;
    private File file;
    private ScribTextPane textPane;
    private MonitorableUndoManager undoManager;


    public ScribEditor (ScribTabbedPane parent) {
        this.parent = parent;
        ScribDocument doc = new ScribDocument();
        textPane = new ScribTextPane(doc);
        setViewportView(textPane);
        undoManager = new MonitorableUndoManager();
        doc.setUndoableEditListener(undoManager);
    }

    public MonitorableUndoManager getUndoManager () {
        return undoManager;
    }

    public ScribTabbedPane getParentTabbedPane() {
        return parent;
    }

    public ScribTextPane getTextPane() {
        return textPane;
    }

    public void setName (String name) {
        this.name = name;
        ScribFrame.FRAME.setTitle(name);
        int index = parent.indexOfComponent(this);
        if (index < 0) {
            return;
        }

        TabComponent tabComponent = (TabComponent)parent.getTabComponentAt(index);
        if (tabComponent != null) {
            tabComponent.setTabTitle(name);
        }
    }

    public String getName () {
        return name;
    }

    public boolean isChanged () {
        return false;
    }

    public ScribDocument getDocument () {
        return (ScribDocument)textPane.getDocument();
    }

    public void setFile (File file) {
        this.file = file;
        setName(file.getName());
    }

    public File getFile () {
        return file;
    }

    public boolean isAvailable () {
        try {
            String text = getDocument().getText(0, getDocument().getLength());
            text = text.trim();
            if (text.length() == 0 && getFile() == null) {
                return true;
            }

        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return false;

    }


    /*
    public MonitorableUndoManager getUndoManager () {
        ScribDocument doc = getDocument();
        return doc.getUndoManager();
    }
    */
}

