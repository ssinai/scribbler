package ssinai.scribbler.gui;

import org.jfree.data.function.Function2D;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.general.DatasetUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import ssinai.scribbler.parser.MPComponent;

import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 12, 2009
 * Time: 9:44:35 AM
 * To change this template use File | Settings | File Templates.
 */



public class NormalDistChart extends MPGraphPanel implements MPComponent {

    public NormalDistChart (Map target) {

        super(target);
        Function2D normal = new NormalDistributionFunction2D(0.0, 1.0);
        XYDataset dataset = DatasetUtils.sampleFunction2D(normal, -5.0, 5.0, 100, "Normal");
        chart = ChartFactory.createXYLineChart(
            "XY Series Demo",
            "X",
            "Y",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(490, 290));
			add(chartPanel);
    }

	public NormalDistChart copy () {
System.out.println("in NormalDistChart copy");
		NormalDistChart o = new NormalDistChart(target);
		o.setUseColor(useColor);
		return o;
	}
}


