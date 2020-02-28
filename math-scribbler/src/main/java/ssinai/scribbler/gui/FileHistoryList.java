package ssinai.scribbler.gui;

import javax.swing.*;
import java.util.ArrayList;
import java.io.*;
import java.net.URL;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import ssinai.scribbler.actions.ScribAction;
import ssinai.scribbler.utils.FileUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 12, 2009
 * Time: 2:59:14 PM
 * To change this template use File | Settings | File Templates.
 */

public class FileHistoryList extends JMenu {

    public static FileHistoryList FILE_HISTORY_LIST;
    private LoadFileListener loadFileListener;
    private ArrayList<File> fileList;

    private String historyFile = "fileHistory.ser";


    public ArrayList<File> getList () {
        if (fileList == null) {
            try {
                File file = new File(historyFile);
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    fileList = (ArrayList)ois.readObject();
                    ois.close();
                    fis.close();
                    for (int i = fileList.size()-1 ; i >= 0 ; i--) {
                        File f = fileList.get(i);
                        if (!f.exists() || (!f.isFile())) {
                            fileList.remove(i);
                        }
                    }
                } else {
                    fileList = new ArrayList<File>();
                }
            } catch (Exception e) {
                System.out.println("Exception during deserialization: " + e);
            }
        }

        return fileList;
    }

    public void saveList () {
        try {
            FileOutputStream fos = new FileOutputStream(historyFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(fileList);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            System.out.println("Exception during serialization: " +e);
        }
    }

    public void addFile (File f) {
        if (f == null || (!f.isFile())) {
            return;
        }

        String f1Path = getCanonicalPath(f);
        System.out.println("flPath="+f1Path);
        if (fileList == null) fileList = new ArrayList<File>();

        for (int i = 0 ; i < fileList.size() ; i++) {
            File f2 = fileList.get(i);
            String f2Path = getCanonicalPath(f2);
            if ((f2Path).equals(f1Path)) {
                fileList.remove(f2);
                break;
            }
        }

        fileList.add(0, f);
        if (fileList.size() > 10) {
            fileList.remove(10);
        }
        saveList();
        reloadMenu();
    }

    public void deleteFile (File f) {
        String f1Path = getCanonicalPath(f);
        for (int i = 0 ; i < fileList.size() ; i++) {
            File f2 = fileList.get(i);
            String f2Path = getCanonicalPath(f2);
            if ((f2Path).equals(f1Path)) {
                fileList.remove(f2);
                break;
            }
        }
        saveList();
        reloadMenu();
    }

    public String getCanonicalPath (File f) {
        String path = "";
        try {
            path = f.getCanonicalPath();
        } catch (Exception e) {
            System.out.println("getCanonicalPath exception:"+e);
        }
        return path;
    }


    public FileHistoryList () {
        super("File History");
        URL url = getClass().getResource("/images/history24.gif");
        Image img = Toolkit.getDefaultToolkit().getImage(url);
        setIcon(new ImageIcon(img));
        loadFileListener = new LoadFileListener();
        reloadMenu();
        FILE_HISTORY_LIST = this;
    }

    // overriding this to fix cascading menu bug where cascade menu item wouldn't detect mouse entry
 /*
    protected Point getPopupMenuOrigin () {
        Point origin = super.getPopupMenuOrigin();
        return new Point(origin.x, 0); // default returns y of -3, which is bad
    }
    */

    public void reloadMenu () {
        getList();
        removeAll();

        for (File file : fileList) {
            String s = getCanonicalPath(file);
            HistoryMenuItem mi = new HistoryMenuItem(file, s);
            add(mi);
            mi.addActionListener(loadFileListener);
        }
    }


    class LoadFileListener implements ActionListener {
        public void actionPerformed (ActionEvent evt) {
            HistoryMenuItem source = (HistoryMenuItem) evt.getSource();
            File f = source.getFile();
            if (!f.exists()) {
                return;
            }

            ScribEditor editor = ScribTabbedPane.getAvailableEditor(f);
            FileUtils.loadFile(editor, f);

        }
    }


    class HistoryMenuItem extends JMenuItem {
        File f;

        public HistoryMenuItem(File f, String title) {
            super(title, ScribAction.getImageIcon("/images/stock_new-formula-24.png"));
            this.f = f;
        }

        public void setFile (File f) {
            this.f = f;
        }

        public File getFile () {
            return f;
        }
    }
}

