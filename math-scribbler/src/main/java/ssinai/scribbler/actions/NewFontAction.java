package ssinai.scribbler.actions;

import ssinai.scribbler.gui.*;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.*;

import ssinai.scribbler.utils.Props;
import ssinai.scribbler.utils.ScribUtils;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 4, 2009
 * Time: 11:32:22 PM
 * To change this template use File | Settings | File Templates.
 */

public class NewFontAction extends ScribAction {
    private ChooseFontPanel chooseFontPanel;
    private JComboBox fontBox;
    private JSpinner fontSizeSpinner;

    private String initialFamily;
    private int initialSize;
    private boolean initialItalic, initialBold;
    private JToggleButton boldButton, italicButton;

    public NewFontAction() {
        super("New Font...", "/images/stock_font.png");
    }

    public void closeChooser () {
        chooseFontPanel.setVisible(false);
        JPanel toolbarPanel = ScribFrame.FRAME.getToolbarPanel();
        toolbarPanel.remove(chooseFontPanel);
        toolbarPanel.revalidate();
        chooseFontPanel = null;
    }

    public void actionPerformed (ActionEvent evt) {
        System.out.println("New Font Action");

        if (chooseFontPanel == null) {
            chooseFontPanel = new ChooseFontPanel();

            JPanel toolbarPanel = ScribFrame.FRAME.getToolbarPanel();
            toolbarPanel.add(chooseFontPanel);
            chooseFontPanel.setVisible(true);
            toolbarPanel.revalidate();
        } else {
            System.out.println("CLOSE SPOT 1");
            closeChooser();
        }
    }


    //   private JSpinner tabSpinner;
    //   private int tabSize;

    class ChooseFontPanel extends JPanel implements ChangeListener, ActionListener, ItemListener {
        public ChooseFontPanel () {
            super(new FlowLayout(FlowLayout.LEFT));

            //       ScribEditor editor = ScribFrame.FRAME.getCurrentEditor();
            //       Font font = editor.getTextPane().getFont();

            Style baseStyle = DocStyles.getStyle(DocStyles.BASE_STYLE);
            Font font = DocStyles.getStyleContext().getFont(baseStyle);
            System.out.println("initial font for editror="+font);

            initialFamily = (String) Props.get(Props.FONT_FAMILY, font.getFamily());
            initialSize = (Integer) Props.get(Props.FONT_SIZE, font.getSize());
            initialItalic = (Boolean) Props.get(Props.FONT_ITALIC, font.isItalic());
            initialBold = (Boolean) Props.get(Props.FONT_BOLD, font.isBold());

            JButton closeButton = new JButton(ScribUtils.getImageIcon("/images/icon-close.gif"));
            closeButton.setPreferredSize(new Dimension(16,16));
            add(closeButton);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed (ActionEvent evt) {
                    System.out.println("CLOSE SPOT 2");
                    closeChooser();
                }
            }) ;

            String [] fontNames =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            add(new Separator(20));

            fontBox = new JComboBox(fontNames);
            add(new JLabel("Font:"));
            System.out.println("setting fontBox selectedItem to "+initialFamily);
            fontBox.setSelectedItem(initialFamily);
            add(fontBox);

            fontBox.addItemListener(this);
            add(new Separator(20));

            add(new JLabel("Size:"));
            fontSizeSpinner = new JSpinner(new SpinnerNumberModel(initialSize,1,36,1));

            add(fontSizeSpinner);
            fontSizeSpinner.addChangeListener(this);
            add(new Separator(20));


            italicButton = new JToggleButton(ScribAction.getImageIcon("/images/Italic16.gif"));
            italicButton.addActionListener(this);
            italicButton.setSelected(initialItalic);

            boldButton = new JToggleButton(ScribAction.getImageIcon("/images/Bold16.gif"));
            boldButton.addActionListener(this);
            boldButton.setSelected(initialBold);

            add(boldButton);
            add(italicButton);
            boldButton.setMargin(new Insets(1,1,1,1));
            italicButton.setMargin(new Insets(1,1,1,1));

            add(new Separator(20));

            //  add(new JLabel("Tab:"));
            //    tabSize = 4;
            //   tabSpinner = new JSpinner(new SpinnerNumberModel(tabSize,1,16,1));
            //   tabSpinner.addChangeListener(this);

            //   add(tabSpinner);


            add(new Separator(50));

            JButton resetButton = new JButton("Reset");
            add(resetButton);

            resetButton.addActionListener(new ActionListener () {
                public void actionPerformed (ActionEvent evt) {
                    shouldApplyFont = false;
                    fontBox.setSelectedItem(initialFamily);
                    fontSizeSpinner.setValue(initialSize);
                    boldButton.setSelected(initialBold);
                    italicButton.setSelected(initialItalic);
                    shouldApplyFont = true;
                    applyFont();
                }
            });
            shouldApplyFont = true;
        }


        private boolean shouldApplyFont = false;

        public void actionPerformed (ActionEvent evt) {
            applyFont();
        }

        public void stateChanged (ChangeEvent evt) {
            applyFont();
        }

        public void itemStateChanged (ItemEvent evt) {
            applyFont();
        }

        public void applyFont () {
            System.out.println("appyFont");
            if (!shouldApplyFont) return;

            String selectedFont = (String)fontBox.getSelectedItem();
            System.out.println("selectedFont="+selectedFont);
            Number n = (Number)fontSizeSpinner.getValue();

            System.out.println("font size="+n);
            /*

            //      Number tabValue = (Number)tabSpinner.getValue();
            //      scribPad.getDocument().setTabValue(tabValue.intValue());

            */

            System.out.println("applying font styles");
            Style baseStyle = DocStyles.getStyle(DocStyles.BASE_STYLE);
            StyleConstants.setFontFamily(baseStyle, selectedFont);
            StyleConstants.setBold(baseStyle, boldButton.isSelected());
            StyleConstants.setItalic(baseStyle,italicButton.isSelected());
            StyleConstants.setFontSize(baseStyle, n.intValue());
        }
    }

}
