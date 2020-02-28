package ssinai.scribbler.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 9, 2010
 * Time: 2:12:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class PaneSplitter {

    ArrayList<JComponent> newList;

    public JComponent layoutPanes () {
        int index = 0;

        newList = new ArrayList<JComponent>(ScribTabbedPane.tabbedPaneList);

        System.out.println(index+" newList size="+newList.size());
        index++;
        while (newList.size() > 1) {
            newList = createList(newList);
            System.out.println(index+" newList size="+newList.size());
            index++;
        }

        if (newList.size() == 0) {
            return ScribTabbedPane.createNewPane();
        }
        JComponent c = newList.remove(0);
        System.out.println("got final component "+c+", type="+c.getClass().getName());
        System.out.println("newList size="+newList.size());

        return c;
    }

    public ArrayList<JComponent> createList (ArrayList<JComponent> originalList) {
        ArrayList<JComponent> copiedList = new ArrayList<JComponent>(originalList);
        ArrayList<JComponent> newList = new ArrayList<JComponent>();
        while (copiedList.size() > 0) {
            System.out.println("PaneSplitter createList = ScribTabbedPane.count() ="+ScribTabbedPane.count());
     //       JComponent centerComponent = ScribFrame.FRAME.getCenterComponent();
    //        Dimension size = centerComponent.getSize();
            JComponent mainSplitPane = ScribFrame.FRAME.getMainSplitPane();
            Dimension size = mainSplitPane.getSize();
            System.out.println("centerComponent size="+size);
            double w = size.getWidth() / ScribTabbedPane.count();

            if (copiedList.size() > 1) {
                JComponent left = copiedList.remove(0);
                System.out.println("leftComponent type="+left.getClass().getName()+", dim="+left.getPreferredSize());
                /*
                 if (left instanceof JSplitPane) {
                    ((JSplitPane)left).setDividerLocation(150);
                }
                */
                JComponent right = copiedList.remove(0);
                System.out.println("rightComponent type="+right.getClass().getName()+", dim="+right.getPreferredSize());

                /*
                if (right instanceof JSplitPane) {
                    ((JSplitPane)right).setDividerLocation(150);
                }
                */


                JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);

                sp.setResizeWeight(.5);

                newList.add(sp);
                Dimension dim = left.getPreferredSize();
                System.out.println("left preferred size="+dim);
                dim.width = 150;
                left.setMinimumSize(dim);
                left.setPreferredSize(dim);
                dim = right.getPreferredSize();
                System.out.println("right preferred size="+dim);
                dim.width = 150;
                right.setMinimumSize(dim);
                right.setPreferredSize(dim);

                Dimension spDim = sp.getPreferredSize();
                System.out.println("split pane size="+sp.getPreferredSize());
                sp.resetToPreferredSizes();
        //        int split = (int)(spDim.width / 2);
             //   sp.resetToPreferredSizes();
           //     sp.setDividerLocation(split);
            }  else {
                JComponent c = copiedList.remove(0);
                newList.add(c);
            }

        }
        System.out.println("createList returning list of size "+newList.size());
        return newList;
    }

}

