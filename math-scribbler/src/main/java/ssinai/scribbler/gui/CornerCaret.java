package ssinai.scribbler.gui;

import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 29, 2009
 * Time: 3:30:25 AM
 * To change this template use File | Settings | File Templates.
 */

public class CornerCaret extends DefaultCaret {

    public CornerCaret () {
        setBlinkRate(500);
    }

    protected synchronized void damage (Rectangle r) {
        if (r != null) {
            JTextComponent comp = getComponent();
            try {
                Rectangle2D r2 = comp.modelToView2D(getDot());
                int x = 0;
                int y = (int)r2.getY();
                int width = comp.getWidth();
                int height = (int)r2.getHeight();
                comp.repaint(x, y, width, height);

                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;

            } catch (BadLocationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void paint (Graphics g) {
        if (isVisible()) {
            g = g.create();
            JTextComponent comp = getComponent();
            try {
                Rectangle2D r2 = comp.modelToView2D(getDot());
                g.setColor(comp.getCaretColor());
                g.drawLine((int)(r2.getX()), (int)(r2.getY()+1),  (int)r2.getX(), (int)(height + r2.getY()-2));
                g.drawLine((int)(r2.getX()+1), (int)(r2.getY()+1),  (int)(r2.getX()+1), (int)(height + r2.getY()-2));

            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            g.dispose();
        }
    }

}

