package ssinai.scribbler.actions;

import ssinai.scribbler.utils.ScribUtils;

import javax.swing.*;
import java.awt.event.InputEvent;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 28, 2009
 * Time: 1:02:25 AM
 * To change this template use File | Settings | File Templates.
 */

public abstract class ScribAction extends AbstractAction {

    ScribAction(String title, String iconURL) {
        super(title, getImageIcon(iconURL));
    }

    ScribAction(String title) {
        super(title);
    }


    static public ImageIcon getImageIcon (String name) {
        return ScribUtils.getImageIcon(name);
    }


    public void setKeystroke (int key) {
        putValue(Action.MNEMONIC_KEY, key);
        KeyStroke ctrlKeyStroke = KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK);
        putValue(Action.ACCELERATOR_KEY, ctrlKeyStroke);
    }

}
