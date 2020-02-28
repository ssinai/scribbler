package ssinai.scribbler.actions;


import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.text.StyleConstants;
import javax.swing.text.Style;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

import ssinai.scribbler.gui.*;
import ssinai.scribbler.utils.ScribUtils;
import ssinai.scribbler.utils.Props;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 5, 2009
 * Time: 10:45:55 PM
 * To change this template use File | Settings | File Templates.
 */


public class ColorChooserAction extends ScribAction {

    //  static private Logger logger = Parser.getLogger();
    JColorChooser chooser;
    JScrollPane scrollPane;

    public ColorChooserAction() {
        //       super("Colors...", "/images/stock_filters-pop-art.png");
        super("Colors...", "/images/color-browser.png");

    }

    public void actionPerformed (ActionEvent evt) {
        if (chooser == null) {
            chooser = new JColorChooser();
            AbstractColorChooserPanel[] panels = chooser.getChooserPanels();
            for (AbstractColorChooserPanel panel : panels) {
                if (panel.getDisplayName().equals("Swatches")) {
                    myPanel = panel;
                    break;
                }
            }

            if (myPanel == null) {
                //         logger.error("Couldn't create a Swatch Color Chooser");
                return;
            }

            ChooseColorPanel chooseColorPanel = new ChooseColorPanel();
            JPanel toolbarPanel = ScribFrame.FRAME.getToolbarPanel();
            scrollPane = new JScrollPane(chooseColorPanel);
            toolbarPanel.add(scrollPane);
            toolbarPanel.revalidate();
        } else {
            closeChooser();
        }
    }

    public void closeChooser () {
        chooser.setVisible(false);
        JPanel toolbarPanel = ScribFrame.FRAME.getToolbarPanel();
        toolbarPanel.remove(scrollPane);
        toolbarPanel.revalidate();
        chooser = null;
        scrollPane = null;
    }

    private AbstractColorChooserPanel myPanel;
    private ScribTextPane pane;
    private ConcurrentHashMap<String, JPanel> colorPanels;
    private JPanel radioPanel;
    private ScribEditor editor;

    class ChooseColorPanel extends JPanel {
        Color commentColor;
        Color errorColor;
        Color answerColor;
        Color regularColor;
        Color backgroundColor;
        Color lineHighlightColor;

        public ChooseColorPanel () {
            super(new FlowLayout(FlowLayout.LEFT));
            ScribTabbedPane tabbedPane = ScribTabbedPane.tabbedPaneList.get(0);
            editor = (ScribEditor)tabbedPane.getComponentAt(0);
            System.out.println("editor component 0 class="+editor.getClass());
            pane = editor.getTextPane();

            ScribDocument doc = editor.getDocument();
            commentColor = StyleConstants.getForeground(doc.getStyle(DocStyles.COMMENT_STYLE));
            errorColor = StyleConstants.getForeground(doc.getStyle(DocStyles.ERROR_STYLE));
            regularColor = StyleConstants.getForeground(doc.getStyle(DocStyles.REGULAR_STYLE));
            answerColor = StyleConstants.getForeground(doc.getStyle(DocStyles.ANSWER_STYLE));
            backgroundColor = pane.getBackground();
            lineHighlightColor = ScribParagraphView.lineColor;
            
            System.out.println("ColorChooserPanel backgroundColor="+backgroundColor);
            System.out.println("editor background="+editor.getBackground());

            JButton closeButton = new JButton(ScribUtils.getImageIcon("/images/icon-close.gif"));
            closeButton.setPreferredSize(new Dimension(16,16));
            add(closeButton);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed (ActionEvent evt) {
                    closeChooser();
                }
            }) ;

            add(new Separator(20));

            radioPanel = new JPanel(new GridLayout(3,2));
            final JRadioButton regularRb = new JRadioButton("Normal Text",true);
            final JRadioButton errorsRb = new JRadioButton("Error Text");
            final JRadioButton commentRb = new JRadioButton("Comment Text");
            final JRadioButton answersRb = new JRadioButton("Answer Text");
            final JRadioButton backgroundRb = new JRadioButton("Background");
            final JRadioButton currentLineRb = new JRadioButton("Line Highlight");
            ButtonGroup bg = new ButtonGroup();
            bg.add(regularRb);
            bg.add(errorsRb);
            bg.add(commentRb);
            bg.add(answersRb);
            bg.add(backgroundRb);
            bg.add(currentLineRb);

            add(radioPanel);

            colorPanels = new ConcurrentHashMap<String, JPanel>();
            createColorPanel(regularColor, DocStyles.REGULAR_STYLE, regularRb);
            createColorPanel(errorColor, DocStyles.ERROR_STYLE, errorsRb);
            createColorPanel(commentColor, DocStyles.COMMENT_STYLE, commentRb);
            createColorPanel(answerColor, DocStyles.ANSWER_STYLE, answersRb);
            createColorPanel(backgroundColor, Props.BACKGROUND, backgroundRb);
            createColorPanel(lineHighlightColor, Props.CURRENT_LINE_HIGHLIGHT, currentLineRb);


            add(new Separator(20));

            if (myPanel != null) {
                add(myPanel);
                myPanel.getColorSelectionModel().addChangeListener(new ChangeListener() {
                    public void stateChanged (ChangeEvent evt) {
                        if (commentRb.isSelected()) {
                            putColor(DocStyles.COMMENT_STYLE);
                        } else if (regularRb.isSelected()) {
                            putColor(DocStyles.REGULAR_STYLE);
                        } else if (answersRb.isSelected()) {
                            putColor(DocStyles.ANSWER_STYLE);
                        } else if (errorsRb.isSelected()) {
                            putColor(DocStyles.ERROR_STYLE);
                        } else if (backgroundRb.isSelected()) {
                            Color color = putColor(Props.BACKGROUND);
                            System.out.println("stateChanged backgroundColor="+color);
                            pane.setBackground(color);
                            
                        } else if (currentLineRb.isSelected()) {
                            ScribParagraphView.lineColor = putColor(Props.CURRENT_LINE_HIGHLIGHT);
                        }

                        ScribDocument doc = editor.getDocument();
                        doc.firePropertyChange("highlightDoc", true, false);
                    }
                }) ;
            }

            JButton resetButton = new JButton("Reset");
            resetButton.addActionListener(new ActionListener () {
                public void actionPerformed(ActionEvent evt) {

                    myPanel.getColorSelectionModel().setSelectedColor(Color.black);

                    regularColor = setColor (DocStyles.REGULAR_STYLE);
                    commentColor = setColor (DocStyles.COMMENT_STYLE);
                    errorColor = setColor (DocStyles.ERROR_STYLE);
                    answerColor = setColor (DocStyles.ANSWER_STYLE);
                    
                    backgroundColor = (Color) Props.PROPS.get(Props.BACKGROUND);
                    lineHighlightColor = (Color) Props.PROPS.get(Props.CURRENT_LINE_HIGHLIGHT);


                    if (backgroundColor != null) {
                        pane.setBackground(backgroundColor);
                    }

                    
                    if (lineHighlightColor != null) {
                        ScribParagraphView.lineColor = lineHighlightColor;
                    }
                    


                    ScribDocument doc = editor.getDocument();
                    doc.firePropertyChange("highlight", true, false);

                    JPanel panel = colorPanels.get(Props.BACKGROUND);
                    panel.setBackground(backgroundColor);
                    panel = colorPanels.get(Props.CURRENT_LINE_HIGHLIGHT);
                    panel.setBackground(lineHighlightColor);
                }
            });

            add(resetButton);

        }

    }

    private Color putColor (String id) {
    	System.out.println("putColor "+ id);
        Color color = myPanel.getColorSelectionModel().getSelectedColor();
        System.out.println("color="+color+", color.getRGB()="+color.getRGB());
        Props.put(id, color);

        Style style = pane.getStyledDocument().getStyle(id);

        if (style != null) {
            StyleConstants.setForeground(style, color);
        }

        JPanel panel = colorPanels.get(id);
        if (panel != null) {
            panel.setBackground(color);
        }

        return color;
    }

    private Color setColor (String key) {
  
        Color color = (Color) Props.PROPS.get(key);
      	System.out.println("setting color to "+color);
        if (color == null) {
            ScribDocument doc = editor.getDocument();
            StyleConstants.setForeground(doc.getStyle(key), color);
        }
        JPanel panel = colorPanels.get(key);
        panel.setBackground(color);
        return color;
    }

    private void createColorPanel (Color color, String style, JRadioButton rb) {
    	System.out.println("createColorPanel color="+color+", style="+style);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ColorPanel colorPanel = new ColorPanel(color);
        colorPanels.put(style, colorPanel);
        p.add(colorPanel);
        p.add(rb);
        radioPanel.add(p);
    }

    public class ColorPanel extends JPanel {
        public ColorPanel (Color color) {
            setPreferredSize(new Dimension(20,20));
            setBorder(BorderFactory.createLineBorder(Color.black));
            setOpaque(true);
            setBackground(color);
        }
    }

}
