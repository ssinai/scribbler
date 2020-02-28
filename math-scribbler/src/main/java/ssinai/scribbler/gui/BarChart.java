package ssinai.scribbler.gui;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;

import java.awt.*;
import java.awt.print.PageFormat;
import java.util.Map;


import ssinai.scribbler.parser.MPComponent;
import ssinai.scribbler.parser.ScribMatrix;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 12, 2009
 * Time: 8:49:31 AM
 * To change this template use File | Settings | File Templates.
 */


public class BarChart extends MPGraphPanel implements MPComponent {

    private CategoryDataset createDataset () {
        String legend = (String)get("legend", "Series");
        String axisLabel = (String)get("axislabel", "Factor");
        ScribMatrix matrix = (ScribMatrix)get("matrix");
        double [][] data;
        if (matrix == null) {
            data = new double[][] {
                    {1.0, 43.0, 35.0, 58.0, 54.0, 77.0, 71.0, 89.0},
                    {54.0, 75.0, 63.0, 83.0, 43.0, 46.0, 27.0, 13.0},
                    {41.0, 33.0, 22.0, 34.0, 62.0, 32.0, 42.0, 34.0}
            };
        } else {
            data = matrix.getData();
        }
        return DatasetUtils.createCategoryDataset(legend, axisLabel, data);
    }


    public BarChart (Map target) {
        super(target);
        CategoryDataset dataset = createDataset();
        chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setFocusable(false);

        chartPanel.setPreferredSize(new Dimension(400,290));
        chartPanel.setMinimumSize(new Dimension(400,290));
        chartPanel.setMaximumSize(new Dimension(400,290));

        chartPanel.setMaximumDrawHeight(290);
        chartPanel.setMaximumDrawWidth(400);
        add(chartPanel);

        setPreferredSize(new Dimension(410, 300));
        setMaximumSize(new Dimension(410, 300));
        setMinimumSize(new Dimension(410, 300));
    }

    private JFreeChart createChart(final CategoryDataset dataset) {

        final String title = (String)get("title","");
        final String domainLabel = (String)get("domainlabel","");
        final String rangeLabel = (String)get("rangelabel", "");

        chart = ChartFactory.createBarChart(
                title,							// title
                domainLabel,					// domain axis label
                rangeLabel,						// range axis label
                dataset,                    // data
                PlotOrientation.HORIZONTAL, // orientation
                true,                       // include legend
                false,
                false
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        setBackground();

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();

        String rangeAxisLoc =  (String)get("rangeaxisloc");
        if (rangeAxisLoc != null && rangeAxisLoc.equals("tr")) {
            plot.setRangeAxisLocation(AxisLocation.TOP_OR_RIGHT);
        } else {
            plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        }


        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 100.0);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;

    }

    public BarChart copy () {
        System.out.println("in BarChart copy");
        BarChart o = new BarChart(target);
        o.setUseColor(useColor);
        return o;
    }

    public void print (Graphics g) {
        System.out.println("BarChart print started");
        Graphics2D g2 = (Graphics2D)g.create();
        PageFormat pageFormat = new PageFormat();
        g2.setClip(0,0,(int)pageFormat.getWidth(), (int)pageFormat.getHeight());
        super.print(g2);
        g2.dispose();
        System.out.println("BarChart print ended");
    }

    public void paint (Graphics g) {
        super.paint(g);
    }

}
