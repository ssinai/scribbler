package ssinai.scribbler.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;


import org.apache.commons.math3.linear.RealVector;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot;
import ssinai.scribbler.parser.MPComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 9, 2009
 * Time: 1:58:18 AM
 * To change this template use File | Settings | File Templates.
 */

public class PieChart extends MPGraphPanel implements MPComponent {
    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private static PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("One", 43.2);
        dataset.setValue("Two", 10.0);
        dataset.setValue("Three", 27.5);
        dataset.setValue("Four", 17.5);
        dataset.setValue("Five", 11.0);
        dataset.setValue("Six", 19.4);
        return dataset;
    }

    public PieChart(Map target) {
        super(target);
        System.out.println("Pie chart target="+target);
        DefaultPieDataset dataset = new DefaultPieDataset();
        RealVector data = (RealVector)target.get("v1");
        System.out.println("data type="+data.getClass().getName());
        ArrayList labels = (ArrayList)target.get("labels");
        System.out.println("labels="+labels);
        System.out.println("labels type="+labels.getClass().getName());



        JFreeChart chart = null;
        if (data == null || labels == null) {
            chart = createChart(createDataset());
        } else {
            for (int i = 0 ; i < data.getDimension() ; i++) {
                String label = (String)labels.get(i);
                double value = data.getEntry(i);
                dataset.setValue(label, value);
            }
            chart = createChart(dataset);
        }
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(350,350));

        add(chartPanel);
        setSize(360,360);
    }

    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return A chart.
     */
    private JFreeChart createChart(PieDataset dataset) {

        final String title = (String)get("title", "");
        chart = ChartFactory.createPieChart(
                title,  // chart title
                dataset,             // data
                true,               // include legend
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(false);
        plot.setLabelGap(0.02);
        return chart;

    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
/*
    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }
*/

    public PieChart copy () {
        System.out.println("in PieChart copy");
        PieChart o = new PieChart(target);
        o.setUseColor(useColor);
        return o;
    }

    public void print (Graphics g) {
        System.out.println("Printing pie chart");
        super.print(g);
    }
}
