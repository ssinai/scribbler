package ssinai.scribbler.gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 13, 2010
 * Time: 3:08:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScribTabbedPane extends JTabbedPane {

    public static ArrayList<ScribTabbedPane> tabbedPaneList = new ArrayList<ScribTabbedPane>();
    public static ScribTabbedPane currentTabbedPane;
    public static ScribEditor currentEditor;

    public ScribTabbedPane () {
        setOpaque(true);
        tabbedPaneList.add(this);
        currentTabbedPane = this;
    }

    public static int count () {
        return tabbedPaneList.size();
    }

    public ScribEditor addTab () {
        final ScribEditor editor = new ScribEditor(this);
        String name = editor.getDocument().getName();
        add(name, editor);
        editor.setName(name);

        TabComponent tabComponent = new TabComponent(name, this);
        setTabComponentAt(getTabCount()-1,tabComponent);

        //TODO fix this to get focus to work right
        SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                setSelectedIndex(getTabCount()-1);
            }
        }) ;
        editor.getTextPane().addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                int index = indexOfComponent(editor);
                setSelectedIndex(index);

        //        MonitorableUndoManager undoManager= editor.getUndoManager();
         //       ActionHandler.updateUndoRedo(undoManager);

            }

            public void focusLost(FocusEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        return editor;
    }

    public void removeTab (int index) {
        int prevTabCount = getTabCount();
        int selectedIndex = getSelectedIndex();
        System.out.println("removeTab index="+index);
        remove(index);
        int newTabCount=getTabCount();
        System.out.println("prevTabCount="+prevTabCount+", newTabCount="+newTabCount);


        if ((index == selectedIndex) && (getTabCount() > 0 && index >= getTabCount())) {
            System.out.println("removeTab selecting index "+(getTabCount()-1));
            setSelectedIndex(getTabCount()-1);
        } else if (getTabCount() == 0) {
            ScribFrame.FRAME.setTitle("");
  //          ActionHandler.updateUndoRedo(null);
        }

    }

    public static ScribTabbedPane getCurrentTabbedPane () {

        if (currentTabbedPane != null && tabbedPaneList.contains(currentTabbedPane)) {
            return currentTabbedPane;
        }

        if (tabbedPaneList.size() == 1) {
            return tabbedPaneList.get(0);
        }
        return tabbedPaneList.get(tabbedPaneList.size()-1);
    }


 //   public static ScribTabbedPane createNewPane () {
    public static JComponent createNewPane () {
        ScribTabbedPane tabbedPane = new ScribTabbedPane();
        PaneSplitter paneSplitter = new PaneSplitter();
        JComponent c = paneSplitter.layoutPanes();
        tabbedPane.addTab();
        return c;
  //      ScribFrame.FRAME.setMainComponent(c);
  //      tabbedPane.addTab();
  //      return tabbedPane;
    }

    public static void removePane (ScribTabbedPane tabbedPane) {
        if (tabbedPaneList.size() == 1) {
            return;
        }

        tabbedPaneList.remove(tabbedPane);
        PaneSplitter paneSplitter = new PaneSplitter();
        JComponent c = paneSplitter.layoutPanes();
 //       ScribFrame.FRAME.setMainComponent(c);
        ScribFrame.FRAME.setMainTabbedPane(c);

        ScribTabbedPane lastTabbedPane = tabbedPaneList.get(tabbedPaneList.size()-1);
        lastTabbedPane.setSelectedIndex(lastTabbedPane.getTabCount()-1);

    }

    public void firePropertyChange (String propertyName, Object oldValue, Object newValue) {
        if ("removeTab".equals(propertyName)) {
            ScribTabbedPane tabbedPane = (ScribTabbedPane)newValue;
            removePane(tabbedPane);
        } else {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    public static ScribEditor getCurrentEditor () {
        return currentEditor;
    }

    public void setSelectedIndex (int index) {
        System.out.println("setSelectedIndex "+index);


        super.setSelectedIndex(index);
        ScribEditor editor = (ScribEditor)getComponentAt(index);
        String title = editor.getName();
        System.out.println("tab title="+title);
        ScribFrame.FRAME.setTitle(title);
        currentTabbedPane = this;
        currentEditor = editor;
        if (!editor.getTextPane().hasFocus()) {
            editor.getTextPane().requestFocusInWindow();

        }
    }

    public static ScribEditor getAvailableEditor (File f) {
        for (ScribTabbedPane tabbedPane : tabbedPaneList) {
            for (int i = 0 ; i < tabbedPane.getTabCount() ; i++) {
                ScribEditor editor = (ScribEditor)tabbedPane.getComponentAt(i);
                File editorFile = editor.getFile();
                if (f.equals(editorFile)) {
                    return editor;
                }
            }
        }

        for (ScribTabbedPane tabbedPane : tabbedPaneList) {
            for (int i = 0 ; i < tabbedPane.getTabCount() ; i++) {
                ScribEditor editor = (ScribEditor)tabbedPane.getComponentAt(i);
                if (editor.isAvailable()) {
                    return editor;
                }
            }
        }

        ScribTabbedPane tabbedPane = tabbedPaneList.get(tabbedPaneList.size()-1);

        return tabbedPane.addTab();
    }

}

