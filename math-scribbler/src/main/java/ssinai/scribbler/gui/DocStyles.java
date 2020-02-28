package ssinai.scribbler.gui;

import ssinai.scribbler.utils.Props;

import javax.swing.text.StyleContext;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Mar 6, 2010
 * Time: 5:06:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class DocStyles {

    private static StyleContext styleContext;

    public static final String BASE_STYLE = "BaseStyle";
    public static final String MONO_STYLE = "MonoStyle";
    public static final String REGULAR_STYLE = "RegularStyle";
    public static final String COMMENT_STYLE = "CommentStyle";
    public static final String ANSWER_STYLE = "AnswerStyle";
    public static final String ERROR_STYLE = "ErrorStyle";

    static {
        styleContext = new StyleContext();
        Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
        Style baseStyle = styleContext.addStyle(BASE_STYLE, defaultStyle);

        Style monoStyle = styleContext.addStyle(MONO_STYLE, null);
        StyleConstants.setForeground(monoStyle, Color.black);
        StyleConstants.setBackground(monoStyle, Color.white);

        Style regularStyle = styleContext.addStyle(REGULAR_STYLE, baseStyle);
        Color c = (Color) Props.get(REGULAR_STYLE);
        StyleConstants.setForeground(regularStyle, c);

        Style commentStyle = styleContext.addStyle(COMMENT_STYLE, null);
        c = (Color) Props.get(COMMENT_STYLE);
        StyleConstants.setForeground(commentStyle, c);

        Style answerStyle = styleContext.addStyle(ANSWER_STYLE, null);
        c = (Color) Props.get(ANSWER_STYLE);
        StyleConstants.setForeground(answerStyle, c);

        Style errorStyle = styleContext.addStyle(ERROR_STYLE, null);
        c = (Color) Props.get(ERROR_STYLE);
        StyleConstants.setForeground(errorStyle, c);
    }

    public static StyleContext getStyleContext () {
        return styleContext;
    }

    public static Style getStyle (String styleName) {
        return styleContext.getStyle(styleName);
    }
}

