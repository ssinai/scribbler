package ssinai.scribbler.gui;

import javax.swing.text.Element;
import javax.swing.text.BoxView;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 28, 2009
 * Time: 2:16:49 AM
 * To change this template use File | Settings | File Templates.
 */

public class ScribBoxView extends BoxView {

    public ScribBoxView (Element elem, int axis) {
        super(elem, axis);
    }

    public Shape getChildAllocation (int index, Shape allocation) {
        return super.getChildAllocation(index, allocation);
    }

    public void paint (Graphics g, Shape alloc) {
         //   System.out.println("in boxView paint alloc="+alloc);
        super.paint(g, alloc);
    }

    public void paintChild (Graphics g, Rectangle alloc, int index) {
        //    System.out.println("in boxView paintChild alloc="+alloc+", index="+index);
        super.paintChild(g, alloc, index);
    }
}
