package ssinai.scribbler.actions;

import ssinai.scribbler.parser.MatrixMap;
import ssinai.scribbler.parser.ScribParser;
import ssinai.scribbler.utils.ScribUtils;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.text.DecimalFormat;

import ssinai.scribbler.gui.ScribFrame;
import ssinai.scribbler.ops.FormatOps;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 5, 2009
 * Time: 10:39:56 PM
 * To change this template use File | Settings | File Templates.
 */


public class FormatNumbersAction extends ScribAction {
    public FormatNumbersAction() {
        super("Format Numbers...","/images/stock_add-decimal-place.png");
    }

    public void actionPerformed (ActionEvent evt) {
        //     System.out.println("Format Text Action");


        if (formatDecimalPanel == null) {
            formatDecimalPanel = new FormatDecimalPanel();
        }
        JPanel toolbarPanel = ScribFrame.FRAME.getToolbarPanel();
        toolbarPanel.add(formatDecimalPanel);
        formatDecimalPanel.setVisible(true);
        toolbarPanel.revalidate();
    }
    FormatDecimalPanel formatDecimalPanel;

    class FormatDecimalPanel extends JPanel implements ChangeListener {
        JSpinner minSizeSpinner;
        JSpinner maxSizeSpinner;

        public FormatDecimalPanel () {
            super();
            setLayout(new FlowLayout(FlowLayout.LEFT));
            showPanel();
        }

        boolean initialized = false;

        public void stateChanged (ChangeEvent evt) {
            Number min = (Number)minSizeSpinner.getValue();
            Number max = (Number)maxSizeSpinner.getValue();
            System.out.println("setting fraclen to min="+min+", max="+max);
            FormatOps.mp_default_fraclen(min,max);
            MatrixMap map = MatrixMap.getMatrixMap();

            System.out.println("matrixMap="+map);

            ScribParser.getScribParser().printAll();
        }

        public void showPanel () {
            if (initialized == false) {
                initialized = true;

                DecimalFormat format = FormatOps.getDefaultFormat();

                JButton closeButton = new JButton(ScribUtils.getImageIcon("/images/icon-close.gif"));
                closeButton.setPreferredSize(new Dimension(16,16));
                closeButton.addActionListener (new ActionListener() {
                    public void actionPerformed (ActionEvent evt) {
                        setVisible(false);
                        JPanel toolbarPanel = ScribFrame.FRAME.getToolbarPanel();
                        toolbarPanel.remove(formatDecimalPanel);
                        toolbarPanel.revalidate();
                    }
                });
                add(closeButton);

                JLabel label = new JLabel("  Format Decimals: ");
                Font labelFont = label.getFont();
                Font newFont = labelFont.deriveFont(Font.BOLD);
                label.setFont(newFont);
                add(label);


                minSizeSpinner = new JSpinner(
                        new SpinnerNumberModel(format.getMinimumFractionDigits(),0,10,1));
                JPanel p = new JPanel();
                p.add(new JLabel("Minimum:"));
                p.add(minSizeSpinner);
                minSizeSpinner.addChangeListener(this);


                JPanel separatorPanel = new JPanel();
                separatorPanel.setMinimumSize(new Dimension(2,20));
                p.add(separatorPanel);

                maxSizeSpinner = new JSpinner(
                        new SpinnerNumberModel(format.getMaximumFractionDigits(),0,10,1));
                p.add(new JLabel("Maximum:"));
                p.add(maxSizeSpinner);
                maxSizeSpinner.addChangeListener(this);

                JPanel separatorPanel2 = new JPanel();
                separatorPanel2.setMinimumSize(new Dimension(2,20));
                p.add(separatorPanel2);
                add(p);

                JButton resetButton = new JButton("Reset");
                add(resetButton);
            }
        }


    }


}