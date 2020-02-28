package ssinai.scribbler.actions;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import ssinai.scribbler.utils.ScribUtils;
import ssinai.scribbler.gui.ScribFrame;
import ssinai.scribbler.gui.ScribOps;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 12, 2009
 * Time: 3:52:28 PM
 * To change this template use File | Settings | File Templates.
 */


public class ListFunctionsAction extends ScribAction {
    public ListFunctionsAction() {
        super("List Functions", "/images/messagebox_info.png");

    }

    private InfoTable opsTable;
    private JEditorPane htmlPane;

    class InfoTable extends JTable {

        InfoTable (Vector rowData, Vector<String> columnNames) {
            super(rowData, columnNames);
            setShowHorizontalLines(false);
            setShowVerticalLines(false);
        }

        public boolean isCellEditable (int row, int column) {
            return false;
        }
    }

    JSplitPane infoPane;

    public void actionPerformed (ActionEvent e) {

        ScribFrame frame = ScribFrame.FRAME;

        try {
            if (frame.getLeftTabbedPane().getRightComponent() != null) {
                frame.getLeftTabbedPane().setRightComponent(null);
                return;
            }
            System.out.println("actionPerformed list");
            if (opsTable == null) {
                Vector list = ScribOps.getList();
                System.out.println("MPOps.getList="+list);
                int maxParams = ScribOps.getMaxParams();
                Vector<String> columnNames = new Vector<String>(maxParams);
                Vector<Vector> mainVector = new Vector<Vector>(list.size());
                columnNames.add("Name");

                columnNames.add("Out");
                columnNames.add("In");


                for (Object aList : list) {
                    Vector v = (Vector) aList;
                    mainVector.add(v);
                }

                opsTable = new InfoTable(mainVector, columnNames);

                infoPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                JScrollPane scrollPane = new JScrollPane(opsTable);
                infoPane.setTopComponent(scrollPane);

                JButton closeButton = new JButton(ScribUtils.getImageIcon("/images/icon-close.gif"));
                closeButton.setPreferredSize(new Dimension(16,16));
                closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed (ActionEvent evt) {
                    }
                }) ;
                JPanel p = new JPanel();
                p.add(closeButton);
                scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, p);
                String infoUrl =  "information.html";
                System.out.println("URL="+infoUrl);

                try {

                    File file = new File(infoUrl);
                    System.out.println("Information  file="+file);
                    if (file.exists()) {
                        System.out.println("file existed");
                        FileReader fis = new FileReader(file);
                        BufferedReader ois = new BufferedReader(fis);
                        StringBuilder info = new StringBuilder();
                        String s = ois.readLine();
                        while (s != null) {
                            info.append(s);
                            s = ois.readLine();
                        }
                        fis.close();
                        ois.close();
                        System.out.println("info="+info);
                        htmlPane = new JEditorPane("text/html", info.toString());
                    } else {
                        System.out.println("file didn't exist");
                    }

                } catch (Exception ex) {
                    System.out.println("htmlPane exception");
                    JOptionPane.showMessageDialog(null,
                            "Could not find information file "+infoUrl,
                            "Problem", JOptionPane.ERROR_MESSAGE);
                }

                htmlPane.setEditable(false);

                infoPane.setBottomComponent(new JScrollPane(htmlPane));
                int height = frame.getContentPane().getHeight();
                infoPane.setDividerLocation(height/2);
            }
            frame.getLeftTabbedPane().setRightComponent(infoPane);
            int width =frame.getContentPane().getWidth();
            frame.getLeftTabbedPane().setDividerLocation(width-300);
            DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
            selectionModel.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged (ListSelectionEvent evt) {
                    if (!evt.getValueIsAdjusting()) {
                        int row = opsTable.getSelectedRow();
                        System.out.println("row="+row);
                        String method = (String)opsTable.getValueAt(row,0);
                        System.out.println("method="+method);
                        String params = (String)opsTable.getValueAt(row,2);
                        htmlPane.scrollToReference(method+params);
                    }
                }
            });
            opsTable.setSelectionModel(selectionModel);
        } catch (Exception ex) {
            System.out.println("table create exception:"+ex);
        }
    }

    /*
    public static void printAnnotationsForClass(Class classObj) {

        System.out.println("Package Notes:");
        printExample(classObj.getPackage());

        System.out.println("\nClass Notes:");
        printExample(classObj);

        System.out.println("\nField Notes:");
        printExample(classObj.getDeclaredFields());

        System.out.println("\nConstructor Notes:");
        printExample(classObj.getDeclaredConstructors());

        System.out.println("\nMethod Notes:");
        printExample(classObj.getDeclaredMethods());
    }

    public static void printExample(AnnotatedElement[] elems) {
        for (AnnotatedElement elem : elems) {
            printExample(elem);
        }
    }

    public static void printExample(AnnotatedElement elem) {
        if (elem == null || !elem.isAnnotationPresent(Example.class)) {
            return;
        }
        Example annotation = elem.getAnnotation(Example.class);
        String annotationValue = annotation.value();
        System.out.println(elem.toString() + " - Example: '" + annotationValue);
    }
      */
}
