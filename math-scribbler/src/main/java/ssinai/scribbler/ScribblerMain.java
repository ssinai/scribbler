package ssinai.scribbler;

import ssinai.scribbler.gui.ScribFrame;

import javax.swing.*;
import java.awt.*;

import ssinai.scribbler.utils.Props;
import ssinai.scribbler.gui.ScribOps;

public class ScribblerMain {
	
    public static ScribblerMain APP;


     public static void main (String [] arg) {
         SwingUtilities.invokeLater (new Runnable () {
             public void run () {
                

                 try {
                     boolean found = false;

                     for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    	 System.out.println("LandF info="+info.getName());
                         if ("Windows".equals(info.getName())) {
                             UIManager.setLookAndFeel(info.getClassName());
                             found = true;
                             break;
                         }
                     }
                     found = false;

                     if (!found) {
                         UIManager.setLookAndFeel(
                                 UIManager.getSystemLookAndFeelClassName());
                     }

                 } catch (Exception e) {

                     // If Nimbus is not available, you can set the GUI to another look and feel.
                 }

                 Props.readProperties();

                 APP = new ScribblerMain();
             }
         });
	}
	     
     public ScribblerMain () {
         ScribFrame frame = new ScribFrame();

         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

         Dimension d = new Dimension((int)(screenSize.width * .8), (int)(screenSize.height * .7));
         frame.setMinimumSize(new Dimension(525,300));


         Dimension frameSize = (Dimension) Props.PROPS.get(Props.FRAME_SIZE);
         if (frameSize != null) {
             frame.setSize(frameSize);
         } else {
             frame.setSize(d);
         }

         Point frameLocation = (Point) Props.PROPS.get(Props.FRAME_LOCATION);
         if (frameLocation == null) {
             int xPos = (screenSize.width - frame.getSize().width) / 2;
             int yPos = (screenSize.height - frame.getSize().height) / 2;
             frameLocation = new Point(xPos, yPos);
         }
         frame.setLocation(frameLocation);
         frame.setVisible(true);
         new ScribOps();
     }

}
