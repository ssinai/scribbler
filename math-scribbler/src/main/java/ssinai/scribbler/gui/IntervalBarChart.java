package ssinai.scribbler.gui;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.renderer.category.IntervalBarRenderer;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.chart.ui.TextAnchor;

import java.awt.*;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.EventListener;
import java.util.Map;

import ssinai.scribbler.parser.MPComponent;
import ssinai.scribbler.parser.ScribVector;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 11, 2009
 * Time: 2:28:46 PM
 * To change this template use File | Settings | File Templates.
 */



public class IntervalBarChart extends MPGraphPanel implements MPComponent {

    /** The categories. */
    private static final String[] CATEGORIES = {"1", "3", "5", "10", "20"};

    /** The label font. */
    private static Font labelFont = null;

    /** The title font. */
    private static Font titleFont = null;

    /** The chart. */
    private JFreeChart chart = null;

    static {
        labelFont = new Font("Helvetica", Font.PLAIN, 10);
        titleFont = new Font("Helvetica", Font.BOLD, 14);
    }

    /**
     * Creates a new demo.
     */
    public IntervalBarChart (Map target) {

        super(target);
        System.out.println("in IntervalBarChart");
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(Color.white);
        setFocusable (false);

        double[][] lows = {{-.0315, .0159, .0306, .0453, .0557}};
        double[][] highs = {{.1931, .1457, .1310, .1163, .1059}};
        DefaultIntervalCategoryDataset data = null;

        ScribVector lowMatrix = (ScribVector)get("lows");

        if (lowMatrix != null) {
            lows[0] = lowMatrix.toArray();
        }

        ScribVector highMatrix = (ScribVector)get("highs");
        if (highMatrix != null) {
            highs[0] = highMatrix.toArray();
        }

        double width = (Double)get("width");
        double height = (Double)get("height");


        data = new DefaultIntervalCategoryDataset(lows, highs);
        data.setCategoryKeys(CATEGORIES);

        final String title = (String)get("title", "");
        String xTitle = (String)get("xtitle","");
        String yTitle = (String)get("ytitle","");
        final CategoryAxis xAxis = new CategoryAxis(xTitle);
        xAxis.setLabelFont(titleFont);
        xAxis.setTickLabelFont(labelFont);
        xAxis.setTickMarksVisible(false);
        final NumberAxis yAxis = new NumberAxis(yTitle);
        yAxis.setLabelFont(titleFont);
        yAxis.setTickLabelFont(labelFont);
        yAxis.setRange(-0.2, 0.40);
        final DecimalFormat formatter = new DecimalFormat("0.##%");
        yAxis.setTickUnit(new NumberTickUnit(0.05, formatter));

        final IntervalBarRenderer renderer = new IntervalBarRenderer();
        renderer.setSeriesPaint(0, new Color(51, 102, 153));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelPaint(Color.white);
        final ItemLabelPosition p = new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER
        );
        renderer.setDefaultPositiveItemLabelPosition(p);

        final CategoryPlot plot = new CategoryPlot(data, xAxis, yAxis, renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setOutlinePaint(Color.white);
        plot.setOrientation(PlotOrientation.VERTICAL);

        this.chart = new JFreeChart(title, titleFont, plot, false);
        this.chart.setBackgroundPaint(Color.white);
        ChartPanel chartPanel = new ChartPanel(chart);
    //    chartPanel.setPreferredSize(new Dimension(300,270));
        Dimension chartDim = new Dimension((int)Math.round(width-10), (int)Math.round(height-10));
        chartPanel.setPreferredSize(new Dimension((int)Math.round(width-10),270));
        add(chartPanel);
        //setSize(310, 280);
        setSize((int)Math.round(width), (int)Math.round(height));

        EventListener[] listeners = getListeners(MouseListener.class);
        for (EventListener listener : listeners) {
            removeMouseListener((MouseListener)listener);
        }
    }


    public IntervalBarChart copy ()  {
        System.out.println("in IntervalBarChart copy");
        IntervalBarChart o = new IntervalBarChart (target);
        o.setUseColor(useColor);
        return o;
    }

    public void setUseColor (boolean useColor) {
        this.useColor = useColor;
    }

    public boolean getUseColor () {
        return useColor;
    }
}

