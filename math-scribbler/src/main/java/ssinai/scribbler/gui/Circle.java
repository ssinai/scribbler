package ssinai.scribbler.gui;

import ssinai.scribbler.parser.MPComponent;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 7, 2009
 * Time: 7:57:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Circle extends JPanel implements MPComponent {

    int radius;
    int diam;
    public boolean useColor = true;

    public Circle (int radius) {
        System.out.println("in Circle constructor");
        this.radius = radius;
        diam = radius*2;
        Dimension size = new Dimension(diam,diam);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }

    public void paint (Graphics g) {
        System.out.println("Circle paint diam="+diam+", g type="+g.getClass().getName());
        Graphics2D g2 = (Graphics2D)g;

        if (useColor) {
            g2.setPaint(Color.red);
        } else {
            g2.setPaint(Color.black);
        }
        g.drawOval(1,1,diam-2,diam-2);
    }

    public MPComponent copy () {
        System.out.println("in circle copy");
        Circle c = new Circle(radius);
        c.setUseColor(useColor);
        return c;
    }

    public void setUseColor (boolean useColor) {
        this.useColor = useColor;
    }

    public boolean getUseColor () {
        return useColor;
    }
}

