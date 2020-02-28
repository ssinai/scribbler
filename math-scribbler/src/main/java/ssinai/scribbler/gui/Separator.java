package ssinai.scribbler.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 4, 2009
 * Time: 11:34:11 PM
 * To change this template use File | Settings | File Templates.
 */

public class Separator extends JComponent {

    public Separator(int width) {
        this(width, 1);
    }

    public Separator(int width, int height) {
        Dimension dim = new Dimension(width, height);
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);
    }
}
