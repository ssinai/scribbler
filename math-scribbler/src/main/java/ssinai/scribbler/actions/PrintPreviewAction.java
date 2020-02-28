package ssinai.scribbler.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;

import ssinai.scribbler.gui.ScribEditor;
import ssinai.scribbler.gui.ScribTabbedPane;
import ssinai.scribbler.gui.ScribFrame;
import ssinai.scribbler.gui.PreviewDialog;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 12, 2009
 * Time: 3:38:52 PM
 * To change this template use File | Settings | File Templates.
 */


public class PrintPreviewAction extends ScribAction {
    public PrintPreviewAction() {
       // super("Print...", "/images/stock_print.png");
        super("Print...", "/images/document-print.png");

    }

    public void actionPerformed (ActionEvent evt) {
        System.out.println("Print");

        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                ScribEditor editor = ScribTabbedPane.getCurrentEditor();

                final Cursor savedCursor = ScribFrame.FRAME.getCursor();
                ScribFrame.FRAME.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                final PreviewDialog previewDialog = new PreviewDialog(editor);
                previewDialog.setLocation(100, 10);
                previewDialog.pack();
                previewDialog.setVisible(true);
                ScribFrame.FRAME.setCursor(savedCursor);
            }
        });
    }
}
