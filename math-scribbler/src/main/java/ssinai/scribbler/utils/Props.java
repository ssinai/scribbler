package ssinai.scribbler.utils;

import ssinai.scribbler.gui.ScribDocument;
import ssinai.scribbler.gui.DocStyles;
import ssinai.scribbler.gui.ScribFrame;
import ssinai.scribbler.gui.ScribParagraphView;

import javax.swing.text.StyleContext;
import javax.swing.text.Style;
import javax.swing.*;
import java.util.Hashtable;
import java.util.Properties;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Feb 11, 2010
 * Time: 1:46:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class Props {

    public final static Hashtable<String, Object> PROPS;

    private static final Color defaultRegularFG = Color.black;
    private static final Color defaultErrorFG = Color.red;
    private static final Color defaultCommentFG = Color.magenta;
    private static final Color defaultAnswerFG = Color.blue;
    private static final Color defaultHighlightColor = new Color(250,230,200);
    private static final Color defaultBackground = Color.white;
    
    private static final String defaultRegularFGString = Integer.toString(defaultRegularFG.getRGB());
    private static final String defaultErrorFGString = Integer.toString(defaultErrorFG.getRGB());
    private static final String defaultCommentFGString = Integer.toString(defaultCommentFG.getRGB());
    private static final String defaultAnswerFGString = Integer.toString(defaultRegularFG.getRGB());
    private static final String defaultHighlightColorString = Integer.toString(defaultHighlightColor.getRGB());
    private static final String defaultBackgroundString = Integer.toString(defaultBackground.getRGB());

    public static final String FONT_FAMILY = "FontFamily";
    public static final String FONT_SIZE = "FontSize";
    public static final String FONT_ITALIC = "FontItalic";
    public static final String FONT_BOLD = "FontBold";

    public static final String BACKGROUND = "Background";

    public static final String FRAME_LOCATION = "FrameLocation";
    public static final String FRAME_SIZE = "FrameSize";
    
    public static final String FRAME_X = "FrameX";
    public static final String FRAME_Y = "FrameY";
    
    public static final String FRAME_WIDTH = "FrameWidth";
    public static final String FRAME_HEIGHT = "FrameHeight";

    public static final String CURRENT_LINE_HIGHLIGHT = "CurrentLineHighlight";

    public static Properties savedProps = new Properties();
    
    static {
        PROPS = new Hashtable<String, Object>();
        PROPS.put(DocStyles.REGULAR_STYLE, defaultRegularFG);
        PROPS.put(DocStyles.COMMENT_STYLE, defaultCommentFG);
        PROPS.put(DocStyles.ERROR_STYLE, defaultErrorFG);
        PROPS.put(DocStyles.ANSWER_STYLE, defaultAnswerFG);
        PROPS.put(CURRENT_LINE_HIGHLIGHT, defaultHighlightColor);
        PROPS.put(BACKGROUND, defaultBackground);   
    }


    private Props() {}

    public static Object put (String key, Object value) {
        return PROPS.put(key, value);
    }

    public static Object get (String key) {
        return PROPS.get(key);
    }

    public static Object get (String key, Object defaultValue) {
        if (PROPS.containsKey(key)) {
            return PROPS.get(key);
        } else {
            return defaultValue;
        }
    }
    
    
    public static void readProperties () {
    	try {
    		
            System.out.println("Working Directory = " +
                    System.getProperty("user.dir"));
    		
    		System.out.println("in readProperties");
    		
    		
    		File propertyFile = new File("my_user.props");
    		if (!propertyFile.exists()) {
    			System.out.println("propertyFile does not exist");
                savedProps.setProperty(DocStyles.REGULAR_STYLE, defaultRegularFGString);
                savedProps.setProperty(DocStyles.COMMENT_STYLE, defaultCommentFGString);
                savedProps.setProperty(DocStyles.ERROR_STYLE, defaultErrorFGString);
                savedProps.setProperty(DocStyles.ANSWER_STYLE, defaultAnswerFGString);
                savedProps.setProperty(CURRENT_LINE_HIGHLIGHT, defaultHighlightColorString);
                savedProps.setProperty(BACKGROUND, defaultBackgroundString);
    			
    		} else {
    			System.out.println("propertiesFile exists");
    			FileInputStream in = new FileInputStream(propertyFile);
    			savedProps.load(in);
    			in.close();
    		}
    		
    		System.out.println("savedProps="+savedProps);
    		String colorString = savedProps.getProperty(DocStyles.REGULAR_STYLE);
    		System.out.println("colorString="+colorString);
    		System.out.println("colorString class="+colorString.getClass());
    		Color color1 = Color.decode(colorString);
    		System.out.println("color1="+color1);
            PROPS.put(DocStyles.REGULAR_STYLE, color1);
            
    		colorString = savedProps.getProperty(DocStyles.COMMENT_STYLE);
            color1 = Color.decode(colorString);
            PROPS.put(DocStyles.COMMENT_STYLE, color1);
            
    		colorString = savedProps.getProperty(DocStyles.ERROR_STYLE);
            color1 = Color.decode(colorString);
            PROPS.put(DocStyles.ERROR_STYLE, color1);
            
    		colorString = savedProps.getProperty(DocStyles.ANSWER_STYLE);
            color1 = Color.decode(colorString);
            PROPS.put(DocStyles.ANSWER_STYLE, color1);
            
    		colorString = savedProps.getProperty(CURRENT_LINE_HIGHLIGHT);
            color1 = Color.decode(colorString);
            PROPS.put(CURRENT_LINE_HIGHLIGHT, color1);
            
    		colorString = savedProps.getProperty(BACKGROUND);
            color1 = Color.decode(colorString);
            PROPS.put(BACKGROUND, color1);	
            
            if (savedProps.containsKey(FONT_FAMILY)) {
            	System.out.println("Setting font");
	            String fontFamily = savedProps.getProperty(FONT_FAMILY);
	            Integer size = Integer.valueOf(savedProps.getProperty(FONT_SIZE));
	            Boolean isItalic = Boolean.valueOf(savedProps.getProperty(FONT_ITALIC));
	            Boolean isBold = Boolean.valueOf(savedProps.getProperty(FONT_BOLD));
	            ScribDocument.setFont(fontFamily, size, isItalic, isBold);
            }
            
            if (savedProps.containsKey(FRAME_X)) {
            	System.out.println("setting bounds");
            	int x = Integer.valueOf(savedProps.getProperty(FRAME_X));
            	System.out.println("x="+x);
            	int y = Integer.valueOf(savedProps.getProperty(FRAME_Y));
            	System.out.println("y="+y);
            	int width = Integer.valueOf(savedProps.getProperty(FRAME_WIDTH));
            	System.out.println("width="+width);
            	int height = Integer.valueOf(savedProps.getProperty(FRAME_HEIGHT));
            	System.out.println("height="+height);
            	PROPS.put(FRAME_LOCATION, new Point(x,y));
            	PROPS.put(FRAME_SIZE, new Dimension(width, height));
            }

    		
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    }
    
    public static void writeProperties() {
    	
    	System.out.println("in writeProperties");
        StyleContext styleContext = DocStyles.getStyleContext();

        Style baseStyle = styleContext.getStyle(DocStyles.BASE_STYLE);
        Font font = styleContext.getFont(baseStyle);

        savedProps.setProperty(FONT_FAMILY, font.getFamily().toString());
        savedProps.setProperty(FONT_SIZE, Integer.toString(font.getSize()));
        savedProps.setProperty(FONT_ITALIC, Boolean.toString(font.isItalic()));
        savedProps.setProperty(FONT_BOLD, Boolean.toString(font.isBold()));
        
        Color color = (Color)PROPS.get(DocStyles.REGULAR_STYLE);
        savedProps.setProperty(DocStyles.REGULAR_STYLE, Integer.toString(color.getRGB()));
        
        color = (Color)PROPS.get(DocStyles.ANSWER_STYLE);
        savedProps.setProperty(DocStyles.ANSWER_STYLE, Integer.toString(color.getRGB()));
        
        color = (Color)PROPS.get(DocStyles.COMMENT_STYLE);
        savedProps.setProperty(DocStyles.COMMENT_STYLE, Integer.toString(color.getRGB()));
        
        color = (Color)PROPS.get(DocStyles.ERROR_STYLE);
        savedProps.setProperty(DocStyles.ERROR_STYLE, Integer.toString(color.getRGB()));
        
        color = (Color)PROPS.get(BACKGROUND);
        savedProps.setProperty(BACKGROUND, Integer.toString(color.getRGB()));


        savedProps.setProperty(CURRENT_LINE_HIGHLIGHT, Integer.toString(ScribParagraphView.lineColor.getRGB()));
      
        JFrame frame = ScribFrame.FRAME;
        Point frameLocation = frame.getLocation();
        savedProps.setProperty(FRAME_X, Integer.toString(frameLocation.x ));
        savedProps.setProperty(FRAME_Y, Integer.toString(frameLocation.y ));
        
        Dimension frameSize = frame.getSize();
        savedProps.setProperty(FRAME_WIDTH, Integer.toString(frameSize.width ));
        savedProps.setProperty(FRAME_HEIGHT, Integer.toString(frameSize.height ));
        
    	try {
    		System.out.println("writing to my_user.props");
			FileOutputStream out = new FileOutputStream("my_user.props");
			savedProps.store(out, "---Math Scribbler Properties File---");
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
    	
    }


}

