package ssinai.scribbler.gui;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import java.util.Map;
import java.awt.*;

import ssinai.scribbler.parser.MPComponent;
import ssinai.scribbler.parser.ScribMatrix;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 12, 2009
 * Time: 9:11:48 AM
 * To change this template use File | Settings | File Templates.
 */


public class XYChart extends MPGraphPanel implements MPComponent {

    public XYChart(Map target) {

        super(target);
        System.out.println("in XYChart constructor target="+target);


        XYDataset dataset = createDataset();
        chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(490, 290));
        add(chartPanel);

    }

    /**
     * Creates a sample dataset.
     *
     * @return a sample dataset.
     */
    private XYDataset createDataset() {

        ScribMatrix xarray = (ScribMatrix)get("xdata");
        ScribMatrix yarray = (ScribMatrix)get("ydata");
        double [][] xData = xarray.getData();
        double [][] yData = yarray.getData();

        final XYSeries series3 = new XYSeries("Third");
        for (int i = 0 ; i < xData[0].length ; i++) {
            series3.add(xData[0][i], yData[0][i]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series3);

        return dataset;
    }

    /**
     * Creates a chart.
     *
     * @param dataset  the data for the chart.
     *
     * @return a chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {

        // create the chart...
        chart = ChartFactory.createXYLineChart(
                "XY Chart Demo",      // chart title
                "X",                      // x axis label
                "Y",                      // y axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,                     // tooltips
                false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        setBackground();
//        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
/*
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
*/
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }


    public XYChart copy () {
        System.out.println("in XYChart copy");
        XYChart o = new XYChart(target);
        o.setUseColor(useColor);
        return o;
    }
}

