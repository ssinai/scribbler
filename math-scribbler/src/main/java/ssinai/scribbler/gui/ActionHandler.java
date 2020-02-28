package ssinai.scribbler.gui;

import ssinai.scribbler.actions.*;

import javax.swing.*;


import ssinai.scribbler.utils.ScribUtils;

import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 9, 2010
 * Time: 12:00:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActionHandler {

    private static UndoAction undoAction;
    private static  RedoAction redoAction;
    private static JMenuBar menuBar;
    private static JToolBar toolBar;

    public ActionHandler() {
        menuBar = new JMenuBar();

        NewTabAction newTabAction = new NewTabAction();
        NewPaneAction newPaneAction = new NewPaneAction();
        OpenAction openAction = new OpenAction();
        SaveAction saveAction = new SaveAction();
        SaveAsAction saveAsAction = new SaveAsAction();
        FileHistoryList fileHistoryList = new FileHistoryList();

        ExitAction exitAction = new ExitAction();

        CopyAction copyAction = new CopyAction();
        copyAction.setKeystroke(KeyEvent.VK_C);


        CutAction cutAction = new CutAction();
        cutAction.setKeystroke(KeyEvent.VK_X);

        PasteAction pasteAction = new PasteAction();
        pasteAction.setKeystroke(KeyEvent.VK_V);

        SelectAllAction selectAllAction = new SelectAllAction();
        selectAllAction.setKeystroke(KeyEvent.VK_A);


        undoAction = new UndoAction();
        undoAction.setEnabled(false);
        redoAction = new RedoAction();
        redoAction.setEnabled(false);

        PrintPreviewAction printPreviewAction = new PrintPreviewAction();

        FindAction findAction = new FindAction();
        ColorChooserAction colorChooserAction = new ColorChooserAction();
        NewFontAction newFontAction = new NewFontAction();
        FormatNumbersAction formatNumbersAction = new FormatNumbersAction();
        ListFunctionsAction listFunctionsAction = new ListFunctionsAction();
        ReloadOpsAction reloadOpsAction = new ReloadOpsAction();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setIcon(ScribUtils.getImageIcon("/images/ball.red.gif"));

        fileMenu.add(newTabAction);
        fileMenu.add(newPaneAction);
        fileMenu.add(openAction);
        fileMenu.add(saveAction);
        fileMenu.add(saveAsAction);
        fileMenu.add(printPreviewAction);
        fileMenu.add(fileHistoryList);
        fileMenu.addSeparator();
        fileMenu.add(exitAction);
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setIcon(ScribUtils.getImageIcon("/images/ball.red.gif"));
        editMenu.add(findAction);
        editMenu.addSeparator();
        editMenu.add(cutAction);

        editMenu.add(copyAction);
        editMenu.add(pasteAction);
        editMenu.add(selectAllAction);

        editMenu.addSeparator();
        editMenu.add(undoAction);
        editMenu.add(redoAction);
        menuBar.add(editMenu);

        JMenu propertiesMenu = new JMenu("Properties");
        propertiesMenu.setIcon(ScribUtils.getImageIcon("/images/ball.red.gif"));
        propertiesMenu.add(colorChooserAction);
        propertiesMenu.add(newFontAction);
        propertiesMenu.add(formatNumbersAction);
        propertiesMenu.add(listFunctionsAction);
        propertiesMenu.add(reloadOpsAction);
        menuBar.add(propertiesMenu);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(newTabAction);
        toolBar.add(newPaneAction);
        toolBar.add(openAction);
        toolBar.add(saveAction);
        toolBar.addSeparator();
        toolBar.add(cutAction);
        toolBar.add(copyAction);
        toolBar.add(pasteAction);
        toolBar.addSeparator();
        toolBar.add(undoAction);
        toolBar.add(redoAction);
        toolBar.addSeparator();
        toolBar.add(printPreviewAction);
        toolBar.add(findAction);
        toolBar.add(colorChooserAction);
        toolBar.add(newFontAction);
        toolBar.add(formatNumbersAction);
        toolBar.add(reloadOpsAction);
        toolBar.add(listFunctionsAction);
    }

    public static JMenuBar getMenuBar() {
        return menuBar;
    }

    public static JToolBar getToolBar () {
        return toolBar;
    }

    public static void updateUndoRedo (MonitorableUndoManager m) {
        if (m == null) {
            undoAction.setEnabled(false);
            redoAction.setEnabled(false);
            return;
        }
        boolean canUndo = m.canUndo();
        boolean canRedo = m.canRedo();

        undoAction.setEnabled(canUndo);
        redoAction.setEnabled(canRedo);
    }
}