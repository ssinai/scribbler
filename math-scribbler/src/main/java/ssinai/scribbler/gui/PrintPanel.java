package ssinai.scribbler.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 12, 2009
 * Time: 3:40:41 PM
 * To change this template use File | Settings | File Templates.
 */


public class PrintPanel extends JPanel {

    PrintableTextPane textPane;
    ScribEditor editor;

    public PrintPanel (ScribEditor editor) {

        System.out.println("in PrintPanel constructor");
        ScribDocument doc = editor.getDocument();
        System.out.println("PrintPanel doc title="+doc.getName());
        this.editor = editor;
        textPane = new PrintableTextPane(editor);
        add(textPane);
    }

    public void setPage (int page) {
        textPane.setPage(page);
    }

    public int next () {
        return textPane.next();
    }

    public int prev () {
        return textPane.prev();
    }

    public int first () {
        return textPane.first();
    }

    public int last () {
        return textPane.last();
    }

    /*
    public void toPDF () {
        textPane.toPDF();
    }
    */


    public PrintableTextPane getTextPane () {
        return textPane;
    }

    public ScribDocument getDocument () {
        return (ScribDocument)textPane.getDocument();
    }

    public void setOrientation (int orientation) {
        System.out.println("in setOrientation");
        textPane.setOrientation(orientation);
    }


    public void setMargins (float top, float left, float bottom, float right) {
        System.out.println("in setMargins");
        textPane.setMargins(top, left, bottom, right);
    }

    public void setPageSize (float width, float height) {
        System.out.println("in setPageSize");

        textPane.setPageSize(width, height);
    }


    public void paint (Graphics g) {
        System.out.println("PrintPanel paint");
        Graphics2D g2 = (Graphics2D)g;
        System.out.println("PrintPanel paint transform="+g2.getTransform());
        g2.clearRect(0, 0, getWidth(), getHeight());
        super.paint(g2);
    }


    public void paintComponent (Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        System.out.println("PrintPanel paintComponent "+g2.getTransform());
        super.paintComponent(g2);
    }

    public void paintChildren (Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        System.out.println("PrintPanel paintChildren "+g2.getTransform());
        System.out.println("childCount="+getComponentCount());
        for (int i = 0 ; i < getComponentCount() ; i++) {
            Component c = getComponent(i);
            System.out.println("child type="+c.getClass().getName()+", c.PreferredSize="+c.getPreferredSize()+", c.size="+c.getSize());
        }
        super.paintChildren(g2);
    }

    public void paintBorder (Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        System.out.println("PrintPanel paintBorder "+g2.getTransform());
        super.paintBorder(g2);
    }

    public void validate () {
        System.out.println("PrintPanel VALIDATE");

        super.validate();
    }

    public void doLayout () {
        super.doLayout();
        System.out.println("PrintPanel doLayout");
    }

}
