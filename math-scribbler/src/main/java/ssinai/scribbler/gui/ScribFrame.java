package ssinai.scribbler.gui;

import ssinai.scribbler.actions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ssinai.scribbler.parser.MPException;
import ssinai.scribbler.utils.ScribUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 8, 2010
 * Time: 11:37:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScribFrame extends JFrame {
    public static ScribFrame FRAME;

    public static final String title = "Scribbler";

    public ScribFrame() throws HeadlessException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        new ActionHandler();
        FRAME = this;

        JMenuBar menuBar = ActionHandler.getMenuBar();
        setJMenuBar(menuBar);

        toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.Y_AXIS));
        JPanel tPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tPanel.add(ActionHandler.getToolBar());
        toolbarPanel.add(tPanel);
        add(toolbarPanel, BorderLayout.NORTH);

        initButtonPanel();

        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftTabbedPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        leftTabbedPane.setBackground(Color.green);
        mainSplitPane.setBackground(Color.blue);
        add(leftTabbedPane, BorderLayout.CENTER);
        leftTabbedPane.setLeftComponent(mainSplitPane);


        mainTabbedPane = ScribTabbedPane.createNewPane();
        mainSplitPane.setTopComponent(mainTabbedPane);
    }

    private JSplitPane leftTabbedPane;
    private JSplitPane mainSplitPane;

    private JComponent mainTabbedPane;

    public void setMainTabbedPane (JComponent mainTabbedPane) {
        this.mainTabbedPane = mainTabbedPane;
        int dividerLocation = mainSplitPane.getDividerLocation();
        mainSplitPane.setTopComponent(mainTabbedPane);
        mainSplitPane.setDividerLocation(dividerLocation);
    }

    public JComponent getMainSplitPane () {
        return mainSplitPane;
    }

    public JSplitPane getLeftTabbedPane () {
        return leftTabbedPane;
    }

    public void setTitle (String title) {
        String newTitle = "Scribbler";
        if (title != null && title.trim().length() > 0) {
            newTitle += " - " +title.trim();
        }
        super.setTitle(newTitle);
    }

    private JPanel toolbarPanel;
    public JPanel getToolbarPanel () {
        return toolbarPanel;
    }

    private JPanel errorPanel;

    public void removeErrorPanel () {
         lastException = null;
        if (errorPanel == null) {
            return;
        }
        mainSplitPane.remove(errorPanel);
        errorPanel = null;

    }

    MPException lastException;
    public void displayErrorPanel (MPException error) {
     System.out.println("in displayErrorPanel error="+error);
        error.printStackTrace();
        String errorString = error.toString();
        System.out.println("in displayErrorPanel errorString="+errorString);
        errorPanel = new JPanel(new BorderLayout());
        JTextPane errorTextPane = new JTextPane();
        errorTextPane.setEditable(false);
        errorPanel.add(new JScrollPane(errorTextPane));
        lastException = error;
        errorTextPane.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                System.out.println("error Pane mouseClicked");
                if (lastException != null) {
                    int index = lastException.getMatchIndex();
                    ScribTabbedPane.getCurrentEditor().getTextPane().setCaretPosition(index);
                }
            }
        }) ;
        errorTextPane.setText(error.toString());
        System.out.println("error.getMatchIndex()="+error.getMatchIndex()+",errorString="+error.getErrorString());
        ScribTabbedPane.getCurrentEditor().getTextPane().setCaretPosition(error.getMatchIndex());

        ScribFrame.FRAME.setGrumpy();
        mainSplitPane.setBottomComponent(errorPanel);
        mainSplitPane.setDividerLocation(.9);
    }


    public void setGrumpy () {
        smileyLabel.setIcon(ScribUtils.getImageIcon("/images/sad.gif"));
        smileyLabel.revalidate();
    }

    public void setEmpty () {
        smileyLabel.setIcon(emptyIcon);
        smileyLabel.revalidate();
    }

    private JLabel smileyLabel = new JLabel();
    ImageIcon smileyIcon = ScribUtils.getImageIcon("/images/stock_smiley.png");
    public void setSmiley () {
        smileyLabel.setIcon(smileyIcon);
        smileyLabel.revalidate();
    }

    public void setResultIcon (MPException error) {
        if (error == null) {
            System.out.println("setResultIcon setSmiley");
            setSmiley();
        } else {
            setGrumpy();
        }
    }

    public static void setProgress (int progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > 100) {
            progress = 100;
        }
        progressBar.setValue(progress);
        if (progress == 0) {
            FRAME.setEmpty();
        }
    }

    private static JProgressBar progressBar;
    public static CalculateAction submitAction = new CalculateAction();
    public static CancelAction cancelAction = new CancelAction();



    public void initButtonPanel () {
        JPanel buttonPanel = new JPanel();
        JButton calcButton = new JButton(submitAction);

        buttonPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK, true), "Calculate" );
        ActionMap actionMap = buttonPanel.getActionMap();
        actionMap.put("Calculate", submitAction);
        buttonPanel.setActionMap(actionMap);
        buttonPanel.add(calcButton);

        ClearAction clearAction = new ClearAction();
        JButton clearButton = new JButton(clearAction);
        buttonPanel.add(clearButton);

        JButton cancelButton = new JButton(cancelAction);
        buttonPanel.add(cancelButton);
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        progressBar = new JProgressBar();

        progressPanel.add(progressBar);
        progressBar.setValue(0);
        smileyLabel.setIcon(emptyIcon);
        progressPanel.add(smileyLabel);
        progressPanel.setMinimumSize(new Dimension(200,10));
        progressPanel.setPreferredSize(new Dimension(200,10));

        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(progressPanel, BorderLayout.EAST);
        JLabel statusLabel = new JLabel("Status");
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private static EmptyIcon emptyIcon = new EmptyIcon();
    JPanel bottomPanel;
}