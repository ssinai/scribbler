package ssinai.scribbler.gui;

import javax.swing.text.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 28, 2009
 * Time: 2:16:04 AM
 * To change this template use File | Settings | File Templates.
 */

public class ScribEditorKit extends StyledEditorKit {

    public Object clone () {
        return new ScribEditorKit();
    }


    public ViewFactory getViewFactory () {
        return defaultFactory;
    }

    static class ScribViewFactory implements ViewFactory {
        public View create (Element elem) {
            String elementName = elem.getName();
     //       System.out.println("creating elemName="+elementName);

            if (elementName != null) {
                if (elementName.equals(AbstractDocument.ParagraphElementName)) {
                    return new ScribParagraphView(elem);
                }
                if (elementName.equals(AbstractDocument.SectionElementName)) {
                    return new ScribBoxView(elem, View.Y_AXIS);
                }
            }
            return styledEditorKitFactory.create(elem);
        }
    }

    private static final ViewFactory styledEditorKitFactory =
            (new StyledEditorKit()).getViewFactory();

    private static final ViewFactory defaultFactory =
            new ScribViewFactory();
}

