package ssinai.scribbler.actions;

import ssinai.scribbler.gui.*;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ssinai.scribbler.utils.ScribUtils;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 4, 2009
 * Time: 11:25:32 PM
 * To change this template use File | Settings | File Templates.
 */

public class FindAction extends ScribAction {
    private JPanel findPanel;
    private JPanel replacePanel;
    private JTextField findText;
    private JTextField replaceText;
    private StyleContext defaultStyleContext;
    private ArrayList<GroupPosition> textGroupArray;

    private JButton findNextButton;
    private JButton findPrevButton;
    private JButton replaceButton;
    private JButton replaceAllButton;
    private JLabel replaceLabel;

    public FindAction() {
        super("Find&Replace...", "/images/binoculars.png");

        defaultStyleContext = StyleContext.getDefaultStyleContext();

        Style highlightStyle = defaultStyleContext.addStyle("HighlightStyle", null);
        highlightStyle.addAttribute(StyleConstants.Foreground, Color.black);
        highlightStyle.addAttribute(StyleConstants.Background, Color.cyan);

        Style currentHighlightStyle = defaultStyleContext.addStyle("CurrentHighlightStyle", null);
        currentHighlightStyle.addAttribute(StyleConstants.Foreground, Color.cyan);
        currentHighlightStyle.addAttribute(StyleConstants.Background, Color.black);
    }

    class FindButton extends JButton {
        public FindButton (String text) {
            super(text);
            Dimension dim = getPreferredSize();
            setPreferredSize(new Dimension(100, dim.height));
        }
    }

    public void actionPerformed (ActionEvent evt) {
        if (findPanel == null) {
            findPanel = new FindPanel();
            replacePanel = new ReplacePanel();
            JPanel toolbarPanel = ScribFrame.FRAME.getToolbarPanel();
            toolbarPanel.add(findPanel);
            toolbarPanel.add(replacePanel);
            toolbarPanel.revalidate();
            findText.requestFocusInWindow();
            ScribEditor editor =  ScribTabbedPane.getCurrentEditor();
            JTextPane textPane = editor.getTextPane();
            findText.getDocument().addDocumentListener(new FindPatternListener());
            textPane.setCaretPosition(0);
            replaceText.setEnabled(false);
            replaceText.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate (DocumentEvent evt) {
                    enableComponents();
                }
                public void removeUpdate (DocumentEvent evt) {
                    enableComponents();
                }

                public void changedUpdate (DocumentEvent evt) {

                }
            }) ;
        } else {
            close();
        }
    }

    public void close () {

        if (findPanel != null) {
            findPanel.setVisible(false);
        }
        if (replacePanel != null) {
            replacePanel.setVisible(false);
        }
        findText(null);

        JPanel toolbarPanel = ScribFrame.FRAME.getToolbarPanel();
        toolbarPanel.remove(findPanel);
        toolbarPanel.remove(replacePanel);
        toolbarPanel.revalidate();
        findPanel = null;
        replacePanel = null;

        ScribEditor editor =  ScribTabbedPane.getCurrentEditor();
        editor.getDocument().highlightDocument();
    }


    class FindPanel extends JPanel {

        public FindPanel () {
            super(new FlowLayout(FlowLayout.LEFT));

            JButton closeButton = new JButton(ScribUtils.getImageIcon("/images/icon-close.gif"));
            closeButton.setPreferredSize(new Dimension(16,16));
            add(closeButton);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed (ActionEvent evt) {
                    close();
                }
            }) ;

            add(new Separator(16));

            findNextButton = new FindButton("Next");
            findPrevButton = new FindButton("Previous");

            JLabel findLabel = new JLabel("Search For:");
            add(findLabel);
            findText = new JTextField(15);
            add(findText);

            add(findNextButton);
            findNextButton.addActionListener(new FindNext());
            add(findPrevButton);

            findNextButton.setEnabled(false);
            findPrevButton.setEnabled(false);
            findPrevButton.addActionListener(new FindPrevious());

            literalButton = new JRadioButton("Literal", true);
            wildcardButton = new JRadioButton("Wildcard");

            ButtonGroup bg = new ButtonGroup();
            bg.add(literalButton);
            bg.add(wildcardButton);

            JPanel matchPanel = new JPanel();
            matchPanel.add(literalButton);
            matchPanel.add(wildcardButton);
            add(matchPanel);
            MatchTypeListener matchTypeListener = new MatchTypeListener();
            literalButton.addActionListener(matchTypeListener);
            wildcardButton.addActionListener(matchTypeListener);
        }
    }

    class MatchTypeListener implements ActionListener {

        public void actionPerformed (ActionEvent evt) {
            /*
            if (literalButton.isSelected()) {
                System.out.println("literal button selected");
            }   else {
                System.out.println("wildcard button selected");
            }
            */
            update();

        }
    }

    JRadioButton literalButton;
    JRadioButton wildcardButton;

    class ReplacePanel extends JPanel {

        public ReplacePanel () {
            super(new FlowLayout(FlowLayout.LEFT));

            add(new Separator(26));

            replaceLabel = new JLabel("Replace With:");
            add(replaceLabel);
            replaceText = new JTextField(15);
            add(replaceText);

            replaceButton = new FindButton("Replace");
            replaceAllButton = new FindButton("Replace All");
            replaceButton.addActionListener(new ReplaceNext());
            add(replaceButton);
            replaceAllButton.addActionListener(new ReplaceAll());
            add(replaceAllButton);
            replaceLabel.setEnabled(false);
            replaceButton.setEnabled(false);
            replaceAllButton.setEnabled(false);
        }
    }


    class FindNext implements ActionListener {
        public void actionPerformed (ActionEvent evt) {
            ScribEditor editor = ScribTabbedPane.getCurrentEditor();
            String findPattern = findText.getText();

            if (findText(findPattern)) {
                highlightNext();
            } else {
                JOptionPane.showMessageDialog(ScribFrame.FRAME,
                        "Couldn't find '"+findPattern+"'",
                        "Find Next",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }


    class FindPrevious implements ActionListener {
        public void actionPerformed (ActionEvent evt) {
            ScribEditor editor = ScribTabbedPane.getCurrentEditor();
            String findPattern = findText.getText();

            if (findText(findPattern)) {
                highlightPrev();
            } else {
                JOptionPane.showMessageDialog(ScribFrame.FRAME,
                        "Couldn't find '"+findPattern+"'",
                        "Find Previous",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    class ReplaceNext implements ActionListener {
        public void actionPerformed (ActionEvent evt) {
            ScribEditor editor = ScribTabbedPane.getCurrentEditor();
            final String findPattern = findText.getText();
            String replacePattern = replaceText.getText();

            if (findText(findPattern)) {
                replaceNext(replacePattern);
                SwingUtilities.invokeLater(new Runnable () {
                    public void run () {
                        findText(findPattern);
                        highlightNext();
                    }
                });
            } else {
                JOptionPane.showMessageDialog(ScribFrame.FRAME,
                        "Couldn't find '"+findPattern+"'",
                        "Replace",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }


    class ReplaceAll implements ActionListener {
        public void actionPerformed (ActionEvent evt) {
            ScribEditor editor = ScribTabbedPane.getCurrentEditor();
            String findPattern = findText.getText();
            final String replacePattern = replaceText.getText();
            if (findText(findPattern)) {
                replaceAll(findPattern, replacePattern);
                SwingUtilities.invokeLater(new Runnable () {
                    public void run () {
                        findText(replacePattern);
                    }
                });

            } else {
                JOptionPane.showMessageDialog(ScribFrame.FRAME,
                        "Couldn't find '"+findPattern+"'",
                        "Replace",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }


    public boolean findText (String findPattern) {
        textGroupArray = new ArrayList<GroupPosition>();
        ScribEditor editor = ScribTabbedPane.getCurrentEditor();

        final ScribDocument document = editor.getDocument();

        try {
            String text = document.getText(0, document.getLength());

            Style defaultStyle = defaultStyleContext.getStyle(StyleContext.DEFAULT_STYLE);
            document.setCharacterAttributes(0, document.getLength(), defaultStyle, true);
            document.firePropertyChange("highlight", true, false);
            if (findPattern == null || findPattern.length() == 0) {
                return false;
            }

            int flags = 0;
            if (literalButton.isSelected()) {
                flags = Pattern.LITERAL;
            }

            Pattern p = Pattern.compile(findPattern, flags);
            final Matcher m = p.matcher(text);
            while (m.find()){
                Position startPosition = document.createPosition(m.start());
                Position endPosition = document.createPosition(m.end());
                GroupPosition gp = new GroupPosition(startPosition,endPosition);
                textGroupArray.add(gp);
            }
            enableComponents();

            if (textGroupArray.size() == 0) {
                return false;
            }

            try {
                Style highlightStyle = defaultStyleContext.getStyle("HighlightStyle");
                for (GroupPosition groupPosition : textGroupArray) {
                    int start = groupPosition.getStartPosition().getOffset();
                    int end = groupPosition.getEndPosition().getOffset();
                    document.setCharacterAttributes(start, end - start, highlightStyle, false);
                }

            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } catch (BadLocationException e) {
            System.out.println(new StringBuilder().append("findText BadLocationException:").append(e).toString());
        }
        return textGroupArray.size() > 0;
    }

    class GroupPosition {
        Position startPosition;
        Position endPosition;

        GroupPosition(Position startPosition, Position endPosition) {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }

        public Position getStartPosition () {
            return startPosition;
        }

        public Position getEndPosition () {
            return endPosition;
        }

        public String toString () {
            return startPosition + ","+endPosition;
        }

    }

    public void highlightPrev () {
        ScribEditor editor = ScribTabbedPane.getCurrentEditor();
        int caretPos = editor.getTextPane().getCaretPosition();
        int i = textGroupArray.size()-1;
        GroupPosition gp = textGroupArray.get(i);

        while (caretPos <= gp.getStartPosition().getOffset()) {
            if (i < 0) {
                int res = JOptionPane.showConfirmDialog(ScribFrame.FRAME,
                        "Find has reached the top of the file.\nContinue from bottom?",
                        "Find",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    i=textGroupArray.size()-1;
                    gp = textGroupArray.get(i);
                }
                break;
            }
            gp = textGroupArray.get(i);
            i--;
        }

        highlightPattern(gp);
    }

    public void highlightPattern (GroupPosition gp) {
        ScribEditor editor = ScribTabbedPane.getCurrentEditor();
        ScribDocument document = editor.getDocument();
        int start = gp.getStartPosition().getOffset();
        int end = gp.getEndPosition().getOffset();

        Style currentHighlightStyle = defaultStyleContext.getStyle("CurrentHighlightStyle");
        document.setCharacterAttributes(start, end-start, currentHighlightStyle, false);

        editor.getTextPane().setCaretPosition(gp.getStartPosition().getOffset());
    }

    public void replaceNext (String replacePattern) {
        ScribEditor editor = ScribTabbedPane.getCurrentEditor();
        int caretPos = editor.getTextPane().getCaretPosition();
        int i = 0;
        GroupPosition gp = textGroupArray.get(i);
        while (caretPos > gp.getStartPosition().getOffset()) {
            i++;
            if (i >= textGroupArray.size()) {
                i = 0;
                gp = textGroupArray.get(i);
                break;
            }
            gp = textGroupArray.get(i);
        }
        ScribDocument doc = editor.getDocument();
        doc.replace(gp.getStartPosition().getOffset(),
                gp.getEndPosition().getOffset()-gp.getStartPosition().getOffset(),
                replacePattern,
                null);
        editor.getTextPane().setCaretPosition(gp.getStartPosition().getOffset());
    }

    public void replaceAll (String findPattern, String replacePattern)  {
        ScribEditor editor = ScribTabbedPane.getCurrentEditor();
        ScribDocument doc = editor.getDocument();
        try {
            String text = doc.getText(0, doc.getLength());
            String newText = text.replaceAll(findPattern, replacePattern);
            doc.replace(0, doc.getLength(), newText, null);
        } catch (BadLocationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void highlightNext () {
        ScribEditor editor = ScribTabbedPane.getCurrentEditor();

        int caretPos = editor.getTextPane().getCaretPosition();

        int i =0;
        enableComponents();
        if (textGroupArray.size() == 0) {
            return;
        }
        GroupPosition gp = textGroupArray.get(i);
        while (caretPos >= gp.getStartPosition().getOffset()) {
            i++;
            if (i >= textGroupArray.size()) {
                int res = JOptionPane.showConfirmDialog(ScribFrame.FRAME,
                        "Find has reached the end of the file.\nContinue from top?",
                        "Find",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    i=0;
                    gp = textGroupArray.get(i);
                }
                break;

            }
            gp = textGroupArray.get(i);
        }

        highlightPattern(gp);
    }


    public void highlight () {
        enableComponents();

        if (textGroupArray == null || textGroupArray.size() == 0) {
            return;
        }

        ScribEditor editor = ScribTabbedPane.getCurrentEditor();
        ScribDocument document = editor.getDocument();
        int caretPos = editor.getTextPane().getCaretPosition();

        int i =0;
        GroupPosition gp = textGroupArray.get(i);
        while (caretPos > gp.getStartPosition().getOffset()) {

            i++;
            if (i >= textGroupArray.size()) {
                break;
            }
            gp = textGroupArray.get(i);
        }

        int start = gp.getStartPosition().getOffset();
        int end = gp.getEndPosition().getOffset();

        Style currentHighlightStyle = defaultStyleContext.getStyle("CurrentHighlightStyle");
        document.setCharacterAttributes(start, end-start, currentHighlightStyle, false);

        editor.getTextPane().setCaretPosition(gp.getStartPosition().getOffset());
    }


    class FindPatternListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) {
            update();
        }

        public void removeUpdate(DocumentEvent e) {
            update();
        }

        public void changedUpdate(DocumentEvent e) {
        }
    }

    private void update () {
        final String findPattern = findText.getText();
        enableComponents();

        SwingUtilities.invokeLater(new Runnable () {
            public void run () {
                if (findText(findPattern)) {
                    highlight();
                }
            }
        }) ;
    }

    private void enableComponents () {
        String s = findText.getText();
        boolean enabled = true;
        if (s == null || s.length() == 0) {
            enabled = false;
        }
        findNextButton.setEnabled(enabled);
        findPrevButton.setEnabled(enabled);

        replaceText.setEnabled(enabled);
        replaceLabel.setEnabled(enabled);

        s = replaceText.getText();
        enabled = !(s == null || s.length() == 0);
        replaceButton.setEnabled(enabled && replaceText.isEnabled());
        replaceAllButton.setEnabled(enabled && replaceText.isEnabled());
    }
}

