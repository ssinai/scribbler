package ssinai.scribbler.utils;

import ssinai.scribbler.gui.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 28, 2009
 * Time: 1:08:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {

    public static final String SAVE = "Save";
    public static final String SAVE_AS = "Save As";
    public static final String EXITING = "Exiting";
    public static final String NEW = "New";
    public static final String OPEN = "Open";

    public static int checkForSave (ScribEditor editor, String title) {
        int res = JOptionPane.YES_OPTION;

        if (title.equals(SAVE)) {
            preSave(editor, title);
        } else if (title.equals(SAVE_AS)) {
            preSave(editor, title);
        } else {
            if (editor.isChanged()) {
                String docName = editor.getName();

                res = JOptionPane.showConfirmDialog (null,
                        docName + " has changed. Save changes?", "Save File",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (res == JOptionPane.YES_OPTION) {
                    res = preSave(editor, title);
                }
            }
        }
        return res;
    }

    public static void load (ScribEditor editor) {
        load(editor, null);
    }

    public static File selectFile () {
        FileDialog fileDialog = new FileDialog(ScribFrame.FRAME,
                "Open File", FileDialog.LOAD);
        fileDialog.setVisible(true);
        String fileName = fileDialog.getFile();
        if (fileName == null) {
            fileDialog.setVisible(false);
            fileDialog.dispose();
            return null;
        }

        String directory = fileDialog.getDirectory();
        return new File(directory, fileName);
    }

    public static void load (ScribEditor editor, File fileToLoad) {
        File file = fileToLoad;

        if (fileToLoad == null) {
            JFrame frame = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, editor);
            FileDialog fileDialog = new FileDialog(frame,
                    "Open File", FileDialog.LOAD);
            fileDialog.setVisible(true);
            String fileName = fileDialog.getFile();
            if (fileName == null) {
                fileDialog.setVisible(false);
                fileDialog.dispose();
                return;
            }

            String directory = fileDialog.getDirectory();
            file = new File(directory, fileName);
        }

        loadFile(editor, file);

    }

    public static void loadFile (final ScribEditor editor, File file) {
        BufferedReader bufferedReader = null;
        try {

            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            StringBuilder buf = new StringBuilder();
            String text = bufferedReader.readLine();
            while (text != null) {
                buf.append(text);
                buf.append("\n");
                text = bufferedReader.readLine();
            }

            ScribDocument doc = editor.getDocument();
            System.out.println("loading file to doc="+doc);

            doc.replace(0, doc.getLength(), buf.toString(), null);
            editor.setFile(file);
            System.out.println("FileUtils.load set editor to "+file);

            //       System.out.println("FileUtils load calling setDocument for "+doc.getTitle());

            FileHistoryList.FILE_HISTORY_LIST.addFile(file);

//            doc.setChanged(false);

            ScribTabbedPane tabbedPane = editor.getParentTabbedPane();
            tabbedPane.setSelectedComponent(editor);
            doc.firePropertyChange("highlight", true, false);
            SwingUtilities.invokeLater (new Runnable () {
                public void run () {
                    JViewport viewport = editor.getViewport();
                    viewport.setViewPosition(new Point(0,0));

                }
            });

        } catch (Exception e) {
            System.err.println("Can't load file "+file);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception ex) {
                    System.err.println("Can't close fileReaders "+ex);
                }
            }
        }
    }

    public static int preSave (ScribEditor editor, String title) {

        File savedFile = editor.getFile();

        if (title.equals(SAVE_AS) || savedFile == null) {
            JFrame frame = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, editor);
            FileDialog fileDialog = new FileDialog(frame,
                    title, FileDialog.SAVE);
            fileDialog.setFile("*.txt");

            fileDialog.setVisible(true);

            String fileName = fileDialog.getFile();
            String directory = fileDialog.getDirectory();
            System.out.println("save as file "+fileName+", dir="+directory);

            if (fileName == null && directory == null) {
                return JOptionPane.CANCEL_OPTION;
            }
            System.out.println("SAVING!! "+directory+", "+fileName);

            savedFile = new File(directory, fileName);
        }
        FileUtils.save(editor, savedFile);
        editor.setFile(savedFile);
        //   scribPad.setSaveEnabled(false);

        return JOptionPane.YES_OPTION;
    }

    public static void save (ScribEditor editor, File file) {

        System.out.println("in save for "+file);
        BufferedWriter bufferedWriter = null;
        try {
            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            JTextPane textPane = editor.getTextPane();
            ScribDocument doc = editor.getDocument();
            textPane.getEditorKit().write(bufferedWriter, doc, 0, doc.getLength());
            System.out.println("adding "+file+" to FileHistoryList");
            FileHistoryList.FILE_HISTORY_LIST.addFile(file);
            System.out.println("Saved "+file);
        } catch (Exception e) {
            System.err.println("Can't save file "+file);
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception ex) {
                System.err.println("Can't close fileWriters "+ex);
            }
        }
    }

    public static void openFile (ScribEditor editor, File file) {
        try {
            System.out.println("in openFile file="+file.getCanonicalPath());

            if (file.exists()) {
                openFile(editor, file.getCanonicalPath());
            } else {
                openFile(editor, "");
            }

        } catch (Exception ex) {
            System.out.println("openFile exception:"+ex);
        }
    }

    public static void openFile (ScribEditor editor, String fileName) {
        ScribDocument doc = editor.getDocument();
        System.out.println("in openFile with fileName='"+fileName+"'");


        if (fileName != null && fileName.trim().length() > 0) {
            File selectedFile = new File(fileName.trim());
            StringBuilder buf = openFile(selectedFile);
            doc.replace(0, doc.getLength(), buf.toString(), null);
            editor.setFile(selectedFile);

        } else {
            load(editor);
        }

        doc.setChanged(false);


        doc.firePropertyChange("highlight", true, false);
    }

    public static StringBuilder openFile (File f) {
        System.out.println("in openFile f="+f);
        StringBuilder buf = new StringBuilder();

        if (f.exists() && f.isFile()) {
            BufferedReader br = null;

            try {
                FileReader fr = new FileReader(f);
                br = new BufferedReader(fr);

                int ch = br.read();
                while (ch > -1) {
                    // strip out ignorable control characters
//System.out.println("ch="+ch+", "+(char)ch);
                    if (ch != 13) {
//System.out.println("appending ch "+ch);
                        buf.append((char)ch);
                    }
                    ch = br.read();
                }
            } catch (Exception e) {
                System.out.println("read exception:"+e);
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (Exception ex) {
                    System.err.println("Can't close file "+ex);
                }
            }

        }
        return buf;
    }

}

