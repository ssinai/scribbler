package ssinai.scribbler.gui;

import ssinai.scribbler.actions.ScribAction;

import javax.swing.*;
import javax.swing.text.View;
import javax.swing.text.BoxView;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.DocFlavor;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.Media;
import java.util.Vector;
import java.awt.event.*;
import java.awt.*;
import java.awt.print.PrinterJob;
import java.awt.print.PageFormat;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 18, 2009
 * Time: 2:01:04 AM
 * To change this template use File | Settings | File Templates.
 */

public class PreviewDialog extends JDialog {

    private JList printServiceList;
    private JList paperSizeList;
    private JTextField pageField;
    private JLabel ofLabel;
    private Vector<Media> paperSizeNames;
    private PrintService[] printServices;
    private PrintPanel printPanel;
    private JTextField topField, bottomField, leftField, rightField;




    public PreviewDialog(ScribEditor editor) {
        super(ScribFrame.FRAME);
        System.out.println("PreviewDialog constructor");
        System.out.println("PreviewDialog printableDoc title="+editor.getDocument().getName());
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        printPanel = new PrintPanel(editor);

   //     ScribDocument printableDoc = printPanel.getDocument();
        setTitle("Preview - ["+editor.getName()+"]");
        printServices
                = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);

        Vector v = new Vector ();

        for (PrintService service : printServices) {
            v.add(service.getName());
        }

        printServiceList = new JList(v);
        printServiceList.setVisibleRowCount(4);

        PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

        printServiceList.setSelectedValue(defaultPrintService.getName(), true);
        printServiceList.addListSelectionListener(new PrinterListSelectionListener());

        paperSizeList = new JList();
        paperSizeList.setVisibleRowCount(4);
        getPaperSizes(defaultPrintService);

        Object defaultPaperSize = defaultPrintService.getDefaultAttributeValue(Media.class);

        paperSizeList.setSelectedValue(defaultPaperSize, true);

        paperSizeList.addListSelectionListener(new PaperSizeSelectionListener());


        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));


        JScrollPane printServiceListScrollPane = new JScrollPane(printServiceList);
        printServiceListScrollPane.setBorder(BorderFactory.createTitledBorder(("Select Printer")));
        settingsPanel.add(printServiceListScrollPane);


        JScrollPane paperSizeScrollPane = new JScrollPane(paperSizeList);
        paperSizeScrollPane.setBorder(BorderFactory.createTitledBorder("Select Paper Size"));
        settingsPanel.add(paperSizeScrollPane);



        JPanel buttonPanel = new JPanel();

        PageButton firstButton = new PageButton(ScribAction.getImageIcon("/images/stock_first-page.png"));
        buttonPanel.add(firstButton);
        firstButton.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent evt) {
                int pageIndex = printPanel.first();
                pageField.setText(Integer.toString(pageIndex+1));
            }
        });


        PageButton prevButton = new PageButton(ScribAction.getImageIcon("/images/stock_previous.png"));
        buttonPanel.add(prevButton);

        pageField = new JTextField("1", 3);
        pageField.setHorizontalAlignment(JTextField.CENTER);

        pageField.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent evt) {
                String s = pageField.getText().trim();
                System.out.println("s='"+s+"'");

                if (s != null && s.length() > 0) {
                    Integer page = Integer.getInteger(s);
                    int newPageIndex = page.intValue()-1;
                    System.out.println("going to page "+newPageIndex);
                    printPanel.setPage(page);
                    System.out.println("back from setPage");
                }
            }
        });

        Dimension size = prevButton.getPreferredSize();
        Dimension textSize = pageField.getPreferredSize();
        pageField.setPreferredSize(new Dimension(textSize.width, size.height));
        buttonPanel.add(pageField);

        ofLabel = new JLabel("of 10");
        buttonPanel.add(ofLabel);

        PageButton nextButton = new PageButton(ScribAction.getImageIcon("/images/stock_next.png"));
        buttonPanel.add(nextButton);
        nextButton.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent evt) {
                System.out.println("nextButton pressed pageIndex");
                int pageIndex = printPanel.next();
                System.out.println("nextButton returned="+pageIndex);
                pageField.setText(Integer.toString(pageIndex+1));
            }
        });

        PageButton lastButton = new PageButton(ScribAction.getImageIcon("/images/stock_last-page.png"));
        buttonPanel.add(lastButton);

        lastButton.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent evt) {
                int pageIndex = printPanel.last();
                pageField.setText(Integer.toString(pageIndex+1));
            }
        });


        prevButton.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent evt) {
                int pageIndex = printPanel.prev();
                pageField.setText(Integer.toString(pageIndex+1));
            }
        });

        settingsPanel.add(buttonPanel);


        JPanel marginPanel = new JPanel(new GridLayout(2,2));

        MarginListener marginListener = new MarginListener();

        JPanel tPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tPanel.add(new JLabel("Top:"));
        topField = new JTextField(5);
        topField.setText("1");
        tPanel.add(topField);
        marginPanel.add(tPanel);
        topField.getDocument().addDocumentListener(marginListener);
        System.out.println("topField initialized");

        tPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tPanel.add(new JLabel("Bottom:"));
        bottomField = new JTextField(5);
        bottomField.setText("1");
        tPanel.add(bottomField);
        bottomField.getDocument().addDocumentListener(marginListener);
        marginPanel.add(tPanel);

        tPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tPanel.add(new JLabel("Left:"));
        leftField = new JTextField(5);
        leftField.setText("1");
        tPanel.add(leftField);
        leftField.getDocument().addDocumentListener(marginListener);
        marginPanel.add(tPanel);

        tPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tPanel.add(new JLabel("Right:"));
        rightField = new JTextField(5);
        rightField.setText("1");
        tPanel.add(rightField);
        rightField.getDocument().addDocumentListener(marginListener);
        marginPanel.add(tPanel);



        marginPanel.setOpaque(true);
        marginPanel.setBorder(BorderFactory.createTitledBorder("Margins"));
        settingsPanel.add(marginPanel);

        JPanel orientationPanel = new JPanel();
        JRadioButton portraitButton = new JRadioButton("Portrait",ScribAction.getImageIcon("/images/portrait.gif"));
        JRadioButton landscapeButton = new JRadioButton("Landscape", ScribAction.getImageIcon("/images/landscape.gif"));
        ButtonGroup bg = new ButtonGroup();
        bg.add(portraitButton);
        bg.add(landscapeButton);
        portraitButton.setSelected(true);
        orientationPanel.add(portraitButton);
        orientationPanel.add(Box.createHorizontalStrut(20));
        orientationPanel.add(landscapeButton);
        orientationPanel.setBorder(BorderFactory.createTitledBorder("Orientation"));
        settingsPanel.add(orientationPanel);

        portraitButton.addActionListener(new ActionListener() {
            public void actionPerformed (ActionEvent evt) {
                printPanel.setOrientation(PageFormat.PORTRAIT);
            }
        });

        landscapeButton.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent evt) {
                printPanel.setOrientation(PageFormat.LANDSCAPE);
            }
        });

        final JCheckBox headersFootersCheckBox = new JCheckBox("Show Headers & Footers", true);
        settingsPanel.add(headersFootersCheckBox);
        headersFootersCheckBox.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent evt) {
                getPrintableTextPane().setShowHeadersAndFooters(headersFootersCheckBox.isSelected());
            }
        }) ;

        JButton printButton = new JButton("Print");

        printButton.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent evt) {
                try {
                    setVisible(false);

                    System.out.println("selected print service="+printServiceList.getSelectedValue());
                    PrinterJob job = PrinterJob.getPrinterJob();
                    job.setPrintService(printServices[printServiceList.getSelectedIndex()]);
                    job.setPrintable(printPanel.getTextPane(), printPanel.getTextPane().getPageFormat());
                    boolean ok = job.printDialog();
                    if (ok) {
                        job.print();
                    }
                } catch (Exception e) {
                    dispose();
                }
            }
        });


        /*
        JButton pdfButton = new JButton("PDF");

        pdfButton.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent evt) {
                setVisible(false);
                System.out.println("selected print service="+PrintServiceList.getSelectedValue());
                printPanel.toPDF();
            }
        });
        */

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener () {
            public void actionPerformed (ActionEvent evt) {
                setVisible(false);
                dispose();
            }
        }) ;


        JPanel printButtonPanel = new JPanel();
        printButtonPanel.setBorder(BorderFactory.createEmptyBorder(15,0,0,0));
        printButtonPanel.add(printButton);
 //       printButtonPanel.add(pdfButton);
        printButtonPanel.add(closeButton);
        settingsPanel.add(printButtonPanel);


        int maxWidth = 0;
        int totalHeight = 0;
        for (Component c : settingsPanel.getComponents()) {
            Dimension prefSize = c.getPreferredSize();
            if (prefSize.width > maxWidth) {
                maxWidth = prefSize.width;
            }
            totalHeight += prefSize.height;
        }
        Dimension newDim = new Dimension(maxWidth, totalHeight);

        settingsPanel.setPreferredSize(newDim);
        settingsPanel.setOpaque(false);

        NullEventsGlassPane glassPane = new NullEventsGlassPane();
        setGlassPane(glassPane);
        getGlassPane().setVisible(true);

        glassPane.add(settingsPanel, BorderLayout.EAST);

        Dimension settingsPanelSize = settingsPanel.getPreferredSize();
        JPanel newPanel = new JPanel();
        newPanel.setPreferredSize(settingsPanelSize);
        getContentPane().setLayout(new BorderLayout());



        JPanel mainPanel = new JPanel(new BorderLayout());


        mainPanel.add(newPanel, BorderLayout.EAST);

        /*
        JTextPane tp = new JTextPane();
        tp.setText("Hello");
        tp.setBackground(Color.cyan);
        */
        mainPanel.add(printPanel, BorderLayout.CENTER);

        add(mainPanel);

        printPanel.setPreferredSize(new Dimension(400, newDim.height+80));



        addComponentListener(new ComponentAdapter() {
            public void componentShown (ComponentEvent evt) {
                //               System.out.println("PreviewDialog component shown");
                PrintableTextPane textPane = printPanel.getTextPane();
                Dimension dim = textPane.getPreferredSize();
                System.out.println("previewDialog textPane dim="+dim);
                View rootView = printPanel.getTextPane().getUI().getRootView(printPanel.getTextPane());
                BoxView boxView = (BoxView)rootView.getView(0);
                //               System.out.println("boxView.getViewCount()="+boxView.getViewCount());
                boxView.setSize(dim.width, dim.height);
            }
        }) ;

        getPrintableTextPane().addPropertyChangeListener("layoutPrintPages", new MyPropertyChangeListener());


    }

    class MyPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            System.out.println("MyPropertyChangeListener got propertyChange "+evt.getNewValue());
            Integer numPages = (Integer)evt.getNewValue();
            ofLabel.setText("of "+numPages);
        }
    }

    public PrintableTextPane getPrintableTextPane () {
        return printPanel.getTextPane();
    }

    public void doLayout () {
        System.out.println("PreviewDialog3 doLayout");
        super.doLayout();
    }

    public void validate () {
        System.out.println("PreviewDialog3 validate");
        super.validate();
    }


    private class PrinterListSelectionListener implements ListSelectionListener {
        private PrinterListSelectionListener() {
        }

        public void valueChanged(ListSelectionEvent evt) {
            //To change body of implemented methods use File | Settings | File Templates.
            System.out.println("in PrintServiceList valueChanged evt="+evt);
            int selectedIndex = printServiceList.getSelectedIndex();
            final PrintService printService = printServices[selectedIndex];
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    getPaperSizes(printService);
                }
            });
        }
    }

    private class PaperSizeSelectionListener implements ListSelectionListener {
        private PaperSizeSelectionListener() {
        }

        public void valueChanged (ListSelectionEvent evt) {
            int selectedIndex = paperSizeList.getSelectedIndex();
            MediaSizeName value = (MediaSizeName)paperSizeNames.get(selectedIndex);
            MediaSize mediaSize = MediaSize.getMediaSizeForName(value);
            final float [] paperSize = mediaSize.getSize(MediaSize.INCH);
            System.out.println("selected MediaSize ="+mediaSize);
            System.out.println("paper size="+paperSize[0]+","+paperSize[1]);
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    System.out.println("run involing setPageSize");
                    printPanel.setPageSize(paperSize[0], paperSize[1]);
                }
            });
        }
    }

    class NullEventsGlassPane extends JComponent {
        public NullEventsGlassPane () {
            setLayout(new BorderLayout());

            addMouseListener(new MouseAdapter() {});
            addMouseMotionListener(new MouseMotionAdapter() {});
            addKeyListener(new KeyAdapter() {});


            addComponentListener(new ComponentAdapter() {
                public void componentShown(ComponentEvent evt) {
                    requestFocusInWindow();
                }
            }) ;

            setFocusTraversalKeysEnabled(false);
        }
    }

    public void getPaperSizes (PrintService printService) {
        Cursor oldCursor = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        paperSizeList.setEnabled(false);
        try {
            Media[] res = (Media [])printService.getSupportedAttributeValues(Media.class, null, null);
            paperSizeNames = new Vector<Media>();
            Vector<Media> v = new Vector<Media>();

            for (int i = 0 ; i < res.length ; i++) {
                Media me = res[i];
                System.out.println("me="+me+", class="+me.getClass().getName());
                if (me instanceof MediaSizeName) {
                    paperSizeNames.add(me);
                    //  v.add(me.getName());
                }
            }

            for (Media media : paperSizeNames) {
                v.add(media);
            }


            paperSizeList.setListData(v);
        } catch (Exception e) {
            System.out.println("Problem retrieving paper sizes:"+e);
        } finally {
            paperSizeList.setEnabled(true);
            setCursor(oldCursor);
        }
    }

    class PageButton extends JButton {

        private Insets insets;

        public PageButton (Icon icon) {
            super(icon);
            insets = new Insets(0,0,0,0);
            setFocusPainted(false);
            setRolloverEnabled(true);
            setRolloverIcon(null);
            setPressedIcon(null);
            setBorderPainted(false);
            setContentAreaFilled(false);
        }

        public Insets getInsets () {
            return insets;
        }
    }

    class MarginListener implements DocumentListener {
        float top = 72f, left = 72f, bottom = 72f, right = 72f;

        public void changedUpdate (DocumentEvent evt) {
        }

        public void insertUpdate (DocumentEvent evt) {
            parseMargins();
        }

        public void parseMargins () {

            String s = topField.getText();
            try {
                top = Float.parseFloat(s);
                top *= 72f;
            } catch (Exception e) {
                System.out.println("top parseMargins exception:"+e);
                top = 0;
            }

            s = leftField.getText();
            try {
                left = Float.parseFloat(s);
                left *= 72f;
            } catch (Exception e) {
                System.out.println("left parseMargins exception:"+e);
                left = 0;
            }

            s = bottomField.getText();
            try {
                bottom = Float.parseFloat(s);
                bottom *= 72f;
            } catch (Exception e) {
                System.out.println("bottom parseMargins exception:"+e);
                bottom = 0;
            }

            s = rightField.getText();
            try {
                right = Float.parseFloat(s);
                right *= 72f;
            } catch (Exception e) {
                System.out.println("right parseMargins exception:"+e);
                right = 0;
            }

            System.out.println("top="+top+", left="+left+", bottom="+bottom+", right="+right);
            printPanel.setMargins(top, left, bottom, right);
        }

        public void removeUpdate (DocumentEvent evt) {
            parseMargins();
        }
    }

}


