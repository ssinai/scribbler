package ssinai.scribbler.gui;

import ssinai.scribbler.utils.Props;

import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 28, 2009
 * Time: 2:17:46 AM
 * To change this template use File | Settings | File Templates.
 */

public class ScribParagraphView extends ParagraphView {

    public static Color lineColor;

    public ScribParagraphView (Element elem) {
        super(elem);
        lineColor = (Color) Props.get(Props.CURRENT_LINE_HIGHLIGHT);
    }

    public void paint (Graphics g, Shape a) {
 //       System.out.println("ScribParagraphView paint shape="+a);
        JTextComponent component = (JTextComponent)getContainer();

        int caretPos = component.getCaretPosition();
        try {

            if (caretPos >= getStartOffset() && caretPos < getEndOffset()) {
                Rectangle2D rect = component.modelToView2D(caretPos);
                g.setColor(lineColor);
                float width = getMaximumSpan(View.X_AXIS);
                float height = getPreferredSpan(View.Y_AXIS);

                g.fillRect(0, (int) rect.getY(), (int)width, (int)height);
                g.setColor(component.getForeground());
            }
        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        super.paint(g,a);
    }

}
