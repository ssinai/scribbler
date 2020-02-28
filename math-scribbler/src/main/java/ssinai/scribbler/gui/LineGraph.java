package ssinai.scribbler.gui;

import org.apache.commons.math3.linear.RealVector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import ssinai.scribbler.parser.MPComponent;
import ssinai.scribbler.parser.ScribMatrix;

import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 11, 2009
 * Time: 11:11:55 PM
 * To change this template use File | Settings | File Templates.
 */


public class LineGraph extends MPGraphPanel implements MPComponent {

    public LineGraph(Map target) {
        super(target);
        System.out.println("in LineGraph target="+target);
        CategoryDataset dataset = createDataset();
        chart = createChart(dataset);

        double width = (Double)get("width");
        double height = (Double)get("height");
        ChartPanel chartPanel = new ChartPanel(chart);


        Dimension chartDim = new Dimension((int)Math.round(width-10), (int)Math.round(height-10));
        chartPanel.setPreferredSize(new Dimension((int)Math.round(width-10),270));
        add(chartPanel);
        //setSize(310, 280);
        setSize((int)Math.round(width), (int)Math.round(height));


      //  chartPanel.setPreferredSize(new Dimension((int)Math.round(width-10),270));
        //       chartPanel.setPreferredSize(new Dimension(790, 290));
      //  add(chartPanel);
    }

    /**
     * Creates a sample dataset.
     *
     * @return The dataset.
     */


    private CategoryDataset createDataset () {
        System.out.println("in createDataset");
/*
		String legend = (String)get("legend", "Series");
		String axisLabel = (String)get("axislabel", "Factor");
*/
        System.out.println("getting matrix");
//		ScribMatrix matrix = null;
        Object matrix = null;
        try {
            matrix = get("matrix");
        } catch (Exception ex){
            System.out.println("matrix exception:"+ex);
        }
        System.out.println("createDataset matrix="+matrix);
        System.out.println("matrix type="+matrix.getClass().getName());
        double [][] data = new double[0][0];
        if (matrix == null) {
            data = new double[][] {
                    {1.0, 4.0, 3.0, 5.0, 5.0, 7.0, 7.0, 8.0},
                    {5.0, 7.0, 6.0, 8.0, 4.0, 4.0, 2.0, 1.0},
                    {4.0, 3.0, 2.0, 3.0, 6.0, 3.0, 4.0, 3.0}
            };
        } else {
            if (matrix instanceof ScribMatrix) {
                //   data = new double[((ScribMatrix)matrix).getRowDimension()][((ScribMatrix)matrix).getColumnDimension()];
                data = ((ScribMatrix)matrix).getData();
            } else if (matrix instanceof RealVector) {
                data = new double[1][];
         //       data[0] = ((RealVector)matrix).getData();
                      data[0] = ((RealVector)matrix).toArray();
            }
        }
        String [] series = {"First", "Second", "Third"};
        /*
        String [] types = {"Type 1", "Type 2", "Type 3",
                "Type 4", "Type 5", "Type 6", "Type 7", "Type 8"};
        */
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0 ; i < data.length ; i++) {
            for (int j = 0 ; j < data[0].length ; j++) {
                dataset.addValue(data[i][j], series[i], j+"");
            }
        }
        return dataset;
    }


    /**
     * Creates a sample chart.
     *
     * @param dataset  a dataset.
     *
     * @return The chart.
     */
    private JFreeChart createChart(final CategoryDataset dataset) {

        final String title = (String)get("title","");
        final String domainLabel = (String)get("domainlabel","");
        final String rangeLabel = (String)get("rangelabel", "");


        // create the chart...
        chart = ChartFactory.createLineChart(
                title,							// title
                domainLabel,					// domain axis label
                rangeLabel,						// range axis label
                dataset,                   // data
                PlotOrientation.VERTICAL,  // orientation
                true,                      // include legend
                true,                      // tooltips
                false                      // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);
        //    legend.setShapeScaleX(1.5);
        //  legend.setShapeScaleY(1.5);
        //legend.setDisplaySeriesLines(true);

//        chart.setBackgroundPaint(Color.white);
        setBackground();

        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);

        // customise the range axis...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(true);

        // ****************************************************************************
        // * JFREECHART DEVELOPER GUIDE                                               *
        // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
        // * to purchase from Object Refinery Limited:                                *
        // *                                                                          *
        // * http://www.object-refinery.com/jfreechart/guide.html                     *
        // *                                                                          *
        // * Sales are used to provide funding for the JFreeChart project - please    *
        // * support us so that we can continue developing free software.             *
        // ****************************************************************************

        // customise the renderer...
        final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
//        renderer.setDrawShapes(true);

        renderer.setSeriesStroke(
                0, new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[] {10.0f, 6.0f}, 0.0f
                )
        );
        renderer.setSeriesStroke(
                1, new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[] {6.0f, 6.0f}, 0.0f
                )
        );
        renderer.setSeriesStroke(
                2, new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[] {2.0f, 6.0f}, 0.0f
                )
        );
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }


    public LineGraph copy () {
        System.out.println("in LineGraph copy");
        LineGraph o = new LineGraph(target);
        o.setUseColor(useColor);
        return o;
    }
}
