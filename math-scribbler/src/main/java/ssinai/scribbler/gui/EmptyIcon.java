package ssinai.scribbler.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 28, 2009
 * Time: 1:56:01 AM
 * To change this template use File | Settings | File Templates.
 */


public class EmptyIcon implements Icon {
    public EmptyIcon () {}

    public int getIconHeight () {
        return 15;
    }

    public int getIconWidth () {
        return 15;
    }

    public void paintIcon (Component c, Graphics g, int x, int y) {
    }
}
