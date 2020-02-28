package ssinai.scribbler.ops;

import java.awt.*;
import java.util.HashMap;

import org.apache.log4j.Logger;
import ssinai.scribbler.parser.Parser;
import ssinai.scribbler.gui.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 7, 2009
 * Time: 7:56:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChartOps {


    static private Logger logger = Parser.getLogger();
    public static void init () {
    }


    //    public static Component circle () {
    public static Component circle (Double radius) {
        Circle circle = new Circle(radius.intValue());
        return circle;
    }

    public static Component barchart (HashMap map) {
        return new BarChart(map);
    }

    public static Component piechart (HashMap map) {
        return new PieChart(map);
    }

    public static Component normchart (HashMap map) {
        return new NormalDistChart(map);
    }

    public static Component int_barchart (HashMap map) {
        return new IntervalBarChart(map);
    }

    public static Component linegraph (HashMap map) {
        return new LineGraph(map);
    }

    public static Component xychart (HashMap map) {
        return new XYChart(map);
    }
}

