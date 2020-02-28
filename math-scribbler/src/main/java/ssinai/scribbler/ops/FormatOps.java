package ssinai.scribbler.ops;

import ssinai.scribbler.utils.Props;

import java.text.DecimalFormat;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 2, 2009
 * Time: 6:46:08 PM
 * To change this template use File | Settings | File Templates.
 */


public class FormatOps {

    private static Number tabspace = 0;
    private static boolean useCommas = false;

    private static DecimalFormat defaultFormat = new DecimalFormat();
    private static DecimalFormat numFormat = new DecimalFormat();

    static {
        System.out.println("in FormatOps init");
    }

    public static void init () {
        if (Props.PROPS.get("defaultFormat") == null) {
            System.out.println("Couldn't find defaultFormat");
            defaultFormat = new DecimalFormat();
            defaultFormat.setMinimumFractionDigits(0);
            defaultFormat.setMaximumFractionDigits(4);
            Props.PROPS.put("defaultFormat", defaultFormat);
        } else {
            defaultFormat = (DecimalFormat)Props.PROPS.get("defaultFormat");
        }
        numFormat = (DecimalFormat)defaultFormat.clone();
        System.out.println("FormatOps.init numFormat="+numFormat.getMinimumFractionDigits()+","+numFormat.getMaximumFractionDigits());
        mp_compactmat(Boolean.TRUE);
    }

    public static Number mp_tabspace (Number n) {
        tabspace = n;
        return tabspace;
    }

    public static int getTabSpace () {
        return tabspace.intValue();
    }

    public static void mp_compactmat (Boolean b) {
        // TODO	ScribMatrix.setCompact(b.booleanValue());
    }


    public static boolean getUseCommas () {
        return useCommas;
    }

    public static String format (Object o) {
        //       System.out.println("in format o type="+o.getClass().getName());
        //       System.out.println("numFormat.min="+numFormat.getMinimumFractionDigits()+", max="+numFormat.getMaximumFractionDigits());

        if (o instanceof Double) {
            Double d = (Double)o;
            if (d.isNaN()) {
                return Double.NaN+"";
            }
            return numFormat.format(o);
        }
        return o.toString();
    }

    public static DecimalFormat numformat (String s) {
//System.out.println("Setting formatString to " + s);
        numFormat.applyPattern(s);
        return numFormat;
    }

    public static void mp_setUseComma (boolean commas) {
//System.out.println("Setting formatString to " + s);
        useCommas = commas;
    }

    public static DecimalFormat getNumFormat () {
//System.out.println("returning format from FormatOps");
        return numFormat;
    }

    public static void mp_fraclen (Number n1, Number n2) {
//System.out.println("in mp_fraction n1="+n1+", n2="+n2);
        int _n1 = n1.intValue();
        int _n2 = n2.intValue();
        if (_n1 < 0) _n1 = 0;
        if (_n2 < 0) _n2 = 0;
        if (_n1 < _n2) {
            numFormat.setMinimumFractionDigits(_n1);
            numFormat.setMaximumFractionDigits(_n2);
        } else {
            numFormat.setMinimumFractionDigits(_n2);
            numFormat.setMaximumFractionDigits(_n1);
        }
/*
		System.out.println("exiting mp_fraclen min="+numFormat.getMinimumFractionDigits()+
			","+numFormat.getMaximumFractionDigits());
*/
    }

    public static void mp_default_fraclen (Number n1, Number n2) {
//        System.out.println("in mp_default_fraction n1="+n1+", n2="+n2);
        int _n1 = n1.intValue();
        int _n2 = n2.intValue();
        if (_n1 < 0) _n1 = 0;
        if (_n2 < 0) _n2 = 0;
        if (_n1 < _n2) {
            defaultFormat.setMinimumFractionDigits(_n1);
            defaultFormat.setMaximumFractionDigits(_n2);
        } else {
            defaultFormat.setMinimumFractionDigits(_n2);
            defaultFormat.setMaximumFractionDigits(_n1);
        }
//        System.out.println("defaultFormat="+defaultFormat.getMinimumFractionDigits()+", "+defaultFormat.getMaximumFractionDigits());
        Props.PROPS.put("defaultFormat", defaultFormat);
        numFormat.setMaximumFractionDigits(defaultFormat.getMaximumFractionDigits());
        numFormat.setMinimumFractionDigits(defaultFormat.getMinimumFractionDigits());
    }

    public static DecimalFormat getDefaultFormat () {
        return defaultFormat;
    }

}

