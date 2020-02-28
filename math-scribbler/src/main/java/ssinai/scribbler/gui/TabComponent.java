package ssinai.scribbler.gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Feb 28, 2010
 * Time: 1:24:59 AM
 * To change this template use File | Settings | File Templates.
 */
public   class TabComponent extends JPanel {
    JLabel label;
    ScribTabbedPane pane;

    public TabComponent (String tabTitle, final ScribTabbedPane tabbedPane) {
        setLayout(new BorderLayout());
        pane = tabbedPane;
        label = new JLabel();
        add(label, BorderLayout.WEST);
        label.setOpaque(false);

        setTabTitle(tabTitle);
        button = new TabButton();
        add(button, BorderLayout.EAST);
        setOpaque(false);
        Dimension dim = getPreferredSize();
        dim.width = 100;
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);
        System.out.println("tabComponent minSize="+dim);
    }

    public void setTabTitle (String title) {
        label.setText(title);
    }

    public String getTabTitle () {
        return label.getText();
    }

    public void setText (String title) {
        label.setText(title);
    }

    private TabButton button;

    public TabButton getTabButton() {
        return button;
    }

    private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
      //      setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(TabComponent.this);
            if (i != -1) {
                pane.removeTab(i);
            }

            if (pane.getTabCount() == 0) {
                System.out.println("EMPTY TAB PANE");
                pane.firePropertyChange("removeTab", null, pane);
                //            PropertyListener.firePropertyChange("removeTab", null, pane);
            }
        }

        //we don't want to update UI for this button
        public void updateUI() {
        }



        //paint the cross
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            //       g2.setStroke(new BasicStroke(2));
            //    g2.setColor(Color.BLACK);

            int delta = 6;

            if (getModel().isRollover()) {
                g2.setColor(Color.RED);
                g2.fillOval(2,2,getWidth()-5,getHeight()-5);
                g2.setColor(Color.WHITE);
            } else {
               g2.setColor(Color.BLACK);
            }
            g2.setStroke(new BasicStroke(2));

            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);

            g2.dispose();
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}


