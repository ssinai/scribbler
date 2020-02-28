package ssinai.scribbler.gui;

import javax.swing.text.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Jan 20, 2010
 * Time: 12:00:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class UnderlineHighlighter extends DefaultHighlighter {
    public UnderlineHighlighter (Color c) {
        painter = (c == null ? sharedPainter : new UnderlineHighlightPainter(c));
    }

    public Object addHighlight (int p0, int p1) throws BadLocationException {
        return addHighlight(p0, p1, painter);
    }

    public void setDrawsLayeredHighlights (boolean newValue) {
        if (newValue == false) {
            throw new IllegalArgumentException(
                    "UnderlineHighlighter only draws layered highlights");
        }
        super.setDrawsLayeredHighlights(true);
    }

    public static class UnderlineHighlightPainter extends LayeredHighlighter.LayerPainter {
        public UnderlineHighlightPainter (Color c) {
            color = c;
        }

        public void paint (Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
            // Do nothing: this method will never be called
        }

        public Shape paintLayer (Graphics g, int offs0, int offs1, Shape bounds,
                                 JTextComponent c, View view) {
            g.setColor(color == null ? c.getSelectionColor() : color);
            Rectangle alloc;
            if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
                if (bounds instanceof Rectangle) {
                    alloc = (Rectangle)bounds;
                } else {
                    alloc = bounds.getBounds();
                }
            } else {
                try {
                    Shape shape = view.modelToView (
                            offs0, Position.Bias.Forward,
                            offs1, Position.Bias.Backward,
                            bounds);
                    alloc = (shape instanceof Rectangle) ?
                            (Rectangle)shape : shape.getBounds();

                } catch (BadLocationException e) {
                    return null;
                }
            }

            // draw a little squiggy line underneath the error
            FontMetrics fm = c.getFontMetrics(c.getFont());
            int baseline = alloc.y + alloc.height - fm.getDescent() + 2;
            int yoff = 1;
            for (int i = alloc.x ; i < alloc.x + alloc.width; i += 2) {
                g.drawLine(i, baseline+yoff, i+2, baseline-yoff);
                yoff = -yoff;
            }
            return alloc;
        }

        protected Color color;  // The color for the underline
    }

    protected static final
        Highlighter.HighlightPainter sharedPainter = new UnderlineHighlightPainter(null);

    protected Highlighter.HighlightPainter painter;
}

