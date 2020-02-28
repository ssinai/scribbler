package ssinai.scribbler.utils;

import javax.swing.ImageIcon;
import java.awt.*;
import java.net.URL;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 6, 2010
 * Time: 3:41:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScribUtils {
    public static ImageIcon getImageIcon (String name) {

        URL url = ScribUtils.class.getResource(name);
        Image img = Toolkit.getDefaultToolkit().getImage(url);

        return new ImageIcon(img);
    }

}
