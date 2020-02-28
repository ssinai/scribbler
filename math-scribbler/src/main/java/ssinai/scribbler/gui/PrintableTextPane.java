package ssinai.scribbler.gui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 12, 2009
 * Time: 3:41:50 PM
 * To change this template use File | Settings | File Templates.
 */


public class PrintableTextPane extends JTextPane implements Printable {

    ArrayList<PageSize> pageArray;
    boolean previewable = false;
    private PageFormat pageFormat;
    int pageIndex;
    Date date;
    SimpleDateFormat sdf;
    public boolean showHeaders = true;
    private ScribEditor editor;


    public PrintableTextPane(ScribEditor editor) {
  //      super(editor.getDocument());
        ScribDocument doc = editor.getDocument();
        System.out.println("in PrintableTextPane constructor with title="+doc.getName());
        ScribDocument printableDoc = doc.copy();
        this.editor = editor;
        setDocument(printableDoc);
        System.out.println("in PrintableTextPane constructor");
        PageFormat pageFormat = getPageFormat();
        setPreferredSize(new Dimension((int)pageFormat.getWidth(), (int)pageFormat.getHeight()));
        setMaximumSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        date = new Date();
        sdf = new SimpleDateFormat();
        System.out.println("preferredSize="+getPreferredSize());
        System.out.println("font="+getFont());
        setFocusable(false);
        getCaret().setVisible(false);

        setBackground(Color.white);

        editor.getDocument().highlightDocument();
    }

    public PageFormat getPageFormat () {
        if (pageFormat == null) {
            pageFormat = new PageFormat();
        }
        return pageFormat;
    }


    public void setShowHeadersAndFooters (boolean val) {
        showHeaders = val;
        getTopLevelAncestor().repaint();
    }

    public void setOrientation (int orientation) {
        if (orientation != getPageFormat().getOrientation()) {
            pageIndex = 0;
            pageFormat.setOrientation(orientation);
            setPreferredSize(new Dimension((int)pageFormat.getWidth(), (int)pageFormat.getHeight()));
            setMaximumSize(getPreferredSize());
            setMinimumSize(getPreferredSize());
            System.out.println("new textPane size="+getPreferredSize());
            invalidate();
            getParent().invalidate();
            getTopLevelAncestor().validate();
            System.out.println("new textPane size="+getPreferredSize());
            getParent().repaint();
        }
    }

    public void setMargins (float top, float left, float bottom, float right) {
        System.out.println("in setMargins top="+top+", left="+left+", bottom="+bottom+", right="+right);
        insets = new Insets(Math.round(top), Math.round(left),
                Math.round(bottom), Math.round(right));
        invalidate();
        getParent().validate();
        getTopLevelAncestor().repaint();
    }

    public void setPageSize (float width, float height) {
        System.out.println("in setPageSize width="+width+", height="+height);
        Paper paper = pageFormat.getPaper();
        paper.setSize((double)width*72f, (double)height*72f);
        pageFormat.setPaper(paper);
        setPreferredSize(new Dimension((int)pageFormat.getWidth(), (int)pageFormat.getHeight()));
        setMaximumSize(getPreferredSize());
        setMinimumSize(getPreferredSize());
        invalidate();
        getParent().validate();
        getTopLevelAncestor().repaint();

        /*
        System.out.println("new textPane size="+getPreferredSize()+
                ", paper.imageableArea x="+paper.getImageableX()+", y="+paper.getImageableY()
                +", width="+paper.getImageableWidth()+", height="
                +paper.getImageableHeight());
        System.out.println("paper size="+paper.getWidth()+","+ paper.getHeight());
       */
    }


    public void setInsets (Insets insets) {
        this.insets = insets;
    }

    private Insets insets;

    public Insets getInsets () {
        if (insets == null) {
            insets = new Insets(72, 72, 72, 72);
        }
        //   System.out.println("returning insets="+insets);
        return insets;
    }

    public void paint (Graphics g) {
        System.out.println("textPane.paint isPaintingForPrint()="+isPaintingForPrint()+", pageIndex="+pageIndex);
        if (pageArray == null || pageArray.size() == 0) {
            System.out.println("page array was null, returning from text pane paint");
            return;
        }

        Graphics2D g2 = (Graphics2D)g;
        System.out.println("PrintableTextPane paint g2 type="+g2.getClass().getName());


        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        super.paint(g2);
        //       getCaret().setVisible(false);
    }

    public void paintComponent (Graphics g) {
        System.out.println("textPane.paintComponent isPaintingForPrint()="+isPaintingForPrint()+", pageIndex="+pageIndex);
        Graphics2D g2 = (Graphics2D)g;
        PageSize pageSize = pageArray.get(getPageIndex());

        if (!isPrinting()) {
            System.out.println("parent preferred size="+getParent().getPreferredSize());
            System.out.println("parent size="+getParent().getSize());
            System.out.println("textPane size="+getSize());
            float scale_w = ((float)getParent().getSize().width / getSize().width);
            System.out.println("scale_w="+scale_w);
            float scale_h = ((float)getParent().getSize().height / getSize().height);
            System.out.println("scale_h="+scale_h);
            Rectangle r2 = new Rectangle(0,0,getWidth()+1, getHeight()+1);
            g2.setColor(getBackground());

            scale_w *= 0.95f;
            scale_h *= 0.95f;
            float scale = 1f;
            if (scale_w < scale_h) {
                scale = scale_w;
            } else {
                scale = scale_h;
            }
            System.out.println("scale="+scale);

            float scaledWidth = getSize().width * scale;
            System.out.println("scaledWidth="+scaledWidth);
            float scaledHeight = getSize().height * scale;
            System.out.println("scaledHeight="+scaledHeight);
            float scaledTranslate = 1f;
            AffineTransform at = new AffineTransform(1f, 0f, 0f, 1f, 0f, 5f);
            g2.setTransform(at);
            System.out.println("initial transform="+g2.getTransform());
            scaledTranslate = getParent().getSize().width/2f - scaledWidth/2f;
            System.out.println("final scaledTranslate="+scaledTranslate);
   //         g2.setClip(null);
            g2.setColor(getBackground());
            g2.scale(scale, scale);
            System.out.println("filling rect "+r2);
            System.out.println("yPos="+pageSize.getYPos());
            g2.translate(scaledTranslate/scale, 0);
            System.out.println("second transform="+g2.getTransform());
            g2.fill(r2);
        }


        g2.translate(0, -pageSize.getYPos());



        Rectangle clipRect = new Rectangle(0, pageSize.getYPos()+getInsets().top,
                (int)pageFormat.getWidth()+1, pageSize.getHeight()-1);
        System.out.println("clipRect="+clipRect);
        g2.setClip(clipRect);
          
        super.paintComponent(g2);
    }

    public boolean isPrinting () {
        return isPrinting;
    }

    private boolean isPrinting = false;

    public void setPrinting (boolean printing) {
        isPrinting = printing;
    }

    public int getPageIndex () {
        if (pageIndex < 0) {
            pageIndex = 0;
        }

        if (pageIndex >= pageArray.size()) {
            pageIndex = pageArray.size()-1;
        }

        return pageIndex;
    }

    public void paintChildren (Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        System.out.println("printabletextPane paintChildren");

        PageSize pageSize = pageArray.get(getPageIndex());

        int height = (int)pageFormat.getHeight()-getInsets().top-getInsets().bottom;

        int clipHt = height < pageSize.getHeight()-1 ? height : pageSize.getHeight()-1;



        Rectangle2D.Float clipRect = new Rectangle2D.Float(getInsets().left, pageSize.getYPos()+getInsets().top,
                (int)pageFormat.getWidth()-getInsets().left-getInsets().right, clipHt);



        g2.setClip(clipRect);
          
        super.paintChildren(g2);


        g2.setClip(new Rectangle(0,pageSize.getYPos(), (int)pageFormat.getWidth(), (int)pageFormat.getHeight()));

        if (showHeaders) {
            drawHeaders(g2);
        }

        if (!isPrinting()) {
            drawMargin(g2);
        }
    }


    Stroke stroke = new BasicStroke(1f,  // Width of stroke
            BasicStroke.CAP_ROUND,  // End cap style
            BasicStroke.JOIN_ROUND, // Join style
            0f,                  // Miter limit
            new float[] {0,5}, // Dash pattern
            0);                   // Dash phase


    public void drawMargin (Graphics2D g2) {
        Paint savedColor = g2.getPaint();
        Stroke savedStroke = g2.getStroke();
        g2.setStroke(stroke);
        g2.setPaint(Color.black);

        PageSize pageSize = pageArray.get(getPageIndex());
        int width = (int)pageFormat.getWidth()-getInsets().left-getInsets().right;
        int height = (int)pageFormat.getHeight()-getInsets().top-getInsets().bottom;
        g2.drawRect(getInsets().left, pageSize.getYPos()+getInsets().top,width,height);

        g2.setStroke(savedStroke);
        g2.setPaint(savedColor);
    }
    public void drawHeaders (Graphics g) {

        Graphics2D g2 = (Graphics2D)g;
        System.out.println("in drawHeaders printing="+isPaintingForPrint());
        Paint oldColor = g2.getPaint();
        PageSize pageSize = pageArray.get(getPageIndex());
        g2.setPaint(Color.black);
        g2.setClip(new Rectangle(0,pageSize.getYPos(), (int)pageFormat.getWidth()+1, (int)pageFormat.getHeight()+1));

        System.out.println("set header clip to "+g2.getClip());

 //       ScribDocument doc = (ScribDocument)getDocument();

   //     String title = doc.getTitle();
        String title = editor.getName();
        g2.drawString(title, 72, pageSize.getYPos()+40);

        int yPos = pageSize.getYPos() + (int)pageFormat.getHeight()-40;

        g2.drawString(sdf.format(date), 72, yPos);

        String s = "Page "+(getPageIndex()+1);
        FontMetrics fontMetrics = g2.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth(s);
        System.out.println("yPos for Page "+(getPageIndex()+1)+"="+yPos);

        g2.drawString(s, (int)pageFormat.getWidth()-getInsets().right - stringWidth, yPos);

        g2.setPaint(oldColor);
        System.out.println("leaving drawHeaders printing="+isPaintingForPrint());
    }



    public int print (Graphics g, PageFormat pf, int index) {
        System.out.println("in print B with index="+index);

        if (index >= pageArray.size()) {
            getTopLevelAncestor().repaint();
            return NO_SUCH_PAGE;
        }

        setPrinting(true);
        pageIndex = index;
        paint(g);
        setPrinting(false);
        return PAGE_EXISTS;
    }

/*
    public int toPDF () {
        com.lowagie.text.Document pdfDocument = null;
        try {

            System.out.println("in toPDF");
            JFrame frame = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this);
            FileDialog fileDialog = new FileDialog(frame,
                    "Save As PDF", FileDialog.SAVE);
         //   ScribDocument doc = (ScribDocument)getDocument();
            String title = editor.getName();
            System.out.println("pdf doc title='"+title+"'");
            String displayedTitle= title+".pdf";

            System.out.println("displayedTitle='"+displayedTitle+"'");
            fileDialog.setFile(displayedTitle);

            fileDialog.setVisible(true);

            String fileName = fileDialog.getFile();
            String directory = fileDialog.getDirectory();

            System.out.println("save as file "+fileName+", dir="+directory);

         //   fileName="pie.pdf";
            if (fileName == null && directory == null) {
                return JOptionPane.CANCEL_OPTION;
            }

            if (!fileName.endsWith(".pdf")) {
                fileName += ".pdf";
            }
            System.out.println("SAVING!! "+directory+", "+fileName);

            setPrinting(true);

            com.lowagie.text.Rectangle rect = new com.lowagie.text.Rectangle((float)pageFormat.getWidth(), (float)pageFormat.getHeight());
            pdfDocument = new com.lowagie.text.Document(rect,0,0,0,0);

            PdfWriter writer = PdfWriter.getInstance(pdfDocument,
                    new FileOutputStream(directory+fileName));
            writer.setPdfVersion(PdfWriter.VERSION_1_6);
            pdfDocument.open();
            PdfContentByte cb = writer.getDirectContent();
            com.lowagie.text.Rectangle pageSize = pdfDocument.getPageSize();
            System.out.println("pdf page size="+pageSize);

            for (int i = 0 ; i < pageArray.size() ; i++) {
                pdfDocument.newPage();
                pageIndex = i;
                Graphics2D g2 = cb.createGraphics((float)pageFormat.getWidth(),(float)pageFormat.getHeight());
                print(g2);
                g2.dispose();
            }

            setPrinting(false);
            System.out.println("PrintableTextPane pdf generated");

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (pdfDocument != null && pdfDocument.isOpen()) {
                pdfDocument.close();
            }
        }
         return JOptionPane.YES_OPTION;
    }
    */


    public void doLayout () {
        System.out.println("in doLayout");
        super.doLayout();

        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                initPages();
                getTopLevelAncestor().repaint();
            }
        });
    }

    public void validate () {
        System.out.println("PrintableTextPane VALIDATE");
        super.validate();
    }

    public boolean isFocusable () {
        return false;
    }

    public void initPages () {
        System.out.println("PrintableTextPane initPages "+getParent().getSize());
        ArrayList<View> paraArray = new ArrayList<View>();
        View rootView = getUI().getRootView(this);
        System.out.println("rootView="+rootView);
        System.out.println("rootView.getViewCount()="+rootView.getViewCount());

        for (int i = 0 ; i < rootView.getViewCount() ; i++) {
            View v = rootView.getView(i) ;
            System.out.println("childView="+v);
        }


        BoxView boxView = (BoxView)rootView.getView(0);
        System.out.println("boxView = "+boxView);

        Shape boxAlloc = rootView.getChildAllocation(0, new Rectangle());
        System.out.println("boxAlloc="+boxAlloc);
        System.out.println("boxView.getViewCount()="+boxView.getViewCount());
        for (int i = 0 ; i < boxView.getViewCount() ; i++) {
            View paraView = boxView.getView(i);

            System.out.println("paraView "+i+" type="+paraView.getClass().getName()+", numChildren="+paraView.getViewCount()+", y span="+paraView.getPreferredSpan(View.Y_AXIS)+", visible="+paraView.isVisible()+", paraView="+paraView);
            Element element = paraView.getElement();
            int startOffset = element.getStartOffset();
            int endOffset = element.getEndOffset();
            Document doc = element.getDocument();
            try {
                String text = doc.getText(startOffset, endOffset-startOffset);
                System.out.println("element text='"+text+"'");
            } catch (BadLocationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }



            System.out.println("paraView.getViewCount()="+paraView.getViewCount());
            for (int j = 0 ; j < paraView.getViewCount() ; j++) {
                View rowView = paraView.getView(j);
                element = rowView.getElement();
                startOffset = element.getStartOffset();
                endOffset = element.getEndOffset();
                try {
                    String text = doc.getText(startOffset, endOffset-startOffset);
                    System.out.println("row element text='"+text+"'");
                } catch (BadLocationException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                System.out.println("rowView "+i+","+j+"="+rowView);
                int flowStart = ((ParagraphView)paraView).getFlowStart(j);
                System.out.println("flowStart="+flowStart);



                /*
                Shape alloc = paraView.getChildAllocation(j, new Rectangle());
				System.out.println("alloc="+alloc);
				System.out.println("\trowView "+j+" type="+rowView.getClass().getName()+" "+rowView.getPreferredSpan(View.X_AXIS)+", "+rowView.getPreferredSpan(View.Y_AXIS));
				for (int k = 0 ; k < rowView.getViewCount() ; k++) {
					View childView = rowView.getView(k);
					System.out.println("\t\tchildView "+k+" type="+childView.getClass().getName());
				}
				*/

                float span = rowView.getPreferredSpan(View.Y_AXIS);
                System.out.println("span for rowView "+i+","+j+"="+span+", startOffset="+rowView.getStartOffset());

                paraArray.add(rowView);
            }

        }


        int rowIndex = 0;
        //      System.out.println("allocating pageArray");
        pageArray = new ArrayList<PageSize>();

        Dimension size = new Dimension((int)pageFormat.getWidth(),
                (int)pageFormat.getHeight());
        //     System.out.println("initPages imageablePageSize="+size);
        int ht = size.height - getInsets().top - getInsets().bottom;
        int startLoc = 0;
        int endLoc = 0;
        //     System.out.println("paraArray.size()="+paraArray.size());
        while (rowIndex < paraArray.size()) {
            View rowView = paraArray.get(rowIndex);
            float span = rowView.getPreferredSpan(View.Y_AXIS);
            //          System.out.println("span="+span);
            System.out.println("span="+span+", rowIndex="+rowIndex+", endLoc+span="+(endLoc+span)+", ht="+ht);
            if (endLoc + span > ht) {
                System.out.println("ADDING A PAGE to page array");

                pageArray.add(new PageSize(startLoc, endLoc));
                startLoc += endLoc;
                endLoc = 0;
            }
            // endLoc += (int)span;
            endLoc += Float.valueOf(span).intValue();
            rowIndex++;
            if (rowIndex == paraArray.size()) {
                pageArray.add(new PageSize(startLoc, endLoc));
            }
        }
        System.out.println("pageArray="+pageArray);

        System.out.println("firing property change layoutPrintPages "+pageArray.size());
        firePropertyChange("layoutPrintPages", null, pageArray.size());
    }


    public int next () {
        System.out.println("next");
        if (getPageIndex() < pageArray.size()-1) {
            pageIndex++;
        }
        System.out.println("pageIndex="+pageIndex);
        getTopLevelAncestor().repaint();
        return pageIndex;
    }

    public int first () {
        System.out.println("first");
        pageIndex = 0;
        System.out.println("pageIndex="+pageIndex);
        getTopLevelAncestor().repaint();
        return pageIndex;
    }

    public int last () {
        System.out.println("first");
        pageIndex = pageArray.size()-1;
        System.out.println("pageIndex="+pageIndex);
        getTopLevelAncestor().repaint();
        return pageIndex;
    }

    public int prev () {
        System.out.println("prev");
        if (getPageIndex() > 0) {
            pageIndex--;
        }
        System.out.println("pageIndex="+pageIndex);
        getTopLevelAncestor().repaint();
        return pageIndex;
    }

    public void setPage (int page) {
        System.out.println("in setPage with pageIndex="+page);

        if (page > 0 && page <= pageArray.size()) {
            pageIndex = page-1;
            getTopLevelAncestor().repaint();
        }
    }
}