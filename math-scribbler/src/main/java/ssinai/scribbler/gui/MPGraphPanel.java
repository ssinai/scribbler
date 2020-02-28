package ssinai.scribbler.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;

import org.jfree.chart.JFreeChart;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 9, 2009
 * Time: 1:59:42 AM
 * To change this template use File | Settings | File Templates.
 */


public abstract class MPGraphPanel extends JPanel {

//    Target target;
    Number width, height;
    Paint bg;
    protected boolean useColor = true;
    JFreeChart chart;
    Map target;

    public MPGraphPanel(Map target) {
        this.target = target;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        setBackground(Color.white);
        setBorder(BorderFactory.createLineBorder(Color.black));
        width = (Number)target.get("width");
        height = (Number)target.get("height");
        setFocusable(false);
        //	setSize(getWidth(500), getHeight(300));
    }

    public boolean isFocusable () {
        return false;
    }

    public Paint setBackground () {
        try {
            String background = (String)get("bg");
            if (background != null) {
                bg = Color.decode(background.trim());
                chart.setBackgroundPaint(bg);
                return bg;
            }
        } catch (Exception e) {
            System.out.println("setBackground exception:"+e);
        }
        chart.setBackgroundPaint(Color.lightGray);
        return Color.lightGray;
    }

    public Object get (String key) {
        System.out.println("key='"+key+"'");
        if (target.containsKey(key)) {
            System.out.println("containsKey "+key);
        }
        return target.get(key.trim());
    }


    public Object get (String key, Object def) {
        Object o = target.get(key);
        if (o == null) {
            return def;
        } else {
            return o;
        }
    }

    public void setSize (int width, int height) {
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        super.setSize(width, height);
    }

    public int getWidth (int defwidth) {
        if (width == null) return defwidth;
        return width.intValue();
    }

    public int getHeight (int defheight) {
        if (height == null) return defheight;
        return height.intValue();
    }

    public static String [] toStringArray (Collection c) {
        System.out.println("in toStringArray");
        ArrayList al = new ArrayList(c);
        System.out.println("al="+al);
        String [] data = new String[al.size()];
        for (int i = 0 ; i < al.size() ; i++) {
            data[i] = (String)al.get(i);
        }
        return data;
    }

    public static float [] toFloatArray (Collection c) {
        ArrayList al = new ArrayList(c);
        float [] data = new float[al.size()];
        for (int i = 0 ; i < al.size() ; i++) {
            Number d = (Number)al.get(i);
            data[i] = d.floatValue();
        }
        return data;
    }

    public void setUseColor (boolean useColor) {
        this.useColor = useColor;
    }

    public boolean getUseColor () {
        return useColor;
    }

}


