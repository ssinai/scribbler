package ssinai.scribbler.gui;

import ssinai.scribbler.annote.Example;
import ssinai.scribbler.ops.FormatOps;
import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;


import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.lang.reflect.Method;
import java.io.File;
import java.io.FileWriter;

import ssinai.scribbler.parser.MPException;
import ssinai.scribbler.parser.Parser;
import ssinai.scribbler.parser.ScribMatrix;
import ssinai.scribbler.parser.ScribVector;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Oct 4, 2009
 * Time: 10:39:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScribOps {


	public static String opsPath = "classes/ssinai/scribbler/ops";

    private static Logger logger = Parser.getLogger();
    private static ArrayList<Class> myClasses = new ArrayList<Class>();
    private static TreeSet<String> methodNames;
    private static TreeMap<String,Method> myMethods;

    private static Hashtable <String, ArrayList<Class>> methodTable;


    public ScribOps() {
   //     System.out.println("in ScribOps constructor");
        loadOps();
        FormatOps.init();
    }


    private static ArrayList<String> classList;
    private static ArrayList<Method> allMethods;

    public static void loadOps() {
    	System.out.println("in loadOps");
        Class [] types = new Class[0];
        
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        
        String workingDir = System.getProperty("user.dir");
        
		/*
		 * if workingDir does not contain "target", then we're running as a java app in
		 * the IDE. Otherwise, we're running a jar file.
		 */ 
        
        if (!workingDir.contains("target")) {
        	// determine if running jar or as java app in IDE.
        	opsPath = "target/classes/ssinai/scribbler/ops";
        }

        logger.setLevel(Level.INFO);
        File directory = new File(opsPath);
        System.out.println("File directory = "+directory);
        File[] files = directory.listFiles();
        classList = new ArrayList<String>();
        for (File file : files) {
            String s = file.getName();
            logger.info("file="+s);
            int index = s.indexOf(".class");
            if (index == -1) continue;
            if (s.indexOf(".class") != -1) {
                s = "ssinai.scribbler.ops."+s.substring(0, index);
                logger.info("A fileName='"+s+"'");
                classList.add(s);
            }
        }
        logger.info("classList="+classList);

        methodTable = new Hashtable<String, ArrayList<Class>>();

        methodNames = new TreeSet<String>();
        myMethods = new TreeMap<String, Method>();
        allMethods = new ArrayList<Method>();
        for (String className : classList) {
            Class myClass;
            try {
                logger.info("SPOT 1 LOAD className="+(className));
                myClass = ClassLoader.getSystemClassLoader().loadClass(className);
                logger.info("got class "+myClass);
                myClasses.add(myClass);

                Method init = myClass.getDeclaredMethod("init", types);
                logger.info("MPOps invoking init for class "+myClass);
                init.invoke(null, (java.lang.Object [])types);
                Method [] methodNamesArray = myClass.getDeclaredMethods();


                TreeSet<String> nameSet = new TreeSet<String>();
                for (Method aMethod : methodNamesArray) {
                    allMethods.add(aMethod);
                    myMethods.put(aMethod.getName(), aMethod);
                    methodNames.add(aMethod.getName());
                    nameSet.add(aMethod.getName());
                    ArrayList<Class> value = methodTable.get(aMethod.getName());
                    if (value == null) {
                        value = new ArrayList<Class>();
                    }
                    value.add(myClass);
                    methodTable.put(aMethod.getName(), value);
                }
            } catch (Exception ex) {
                logger.info("ScribOps exception "+ex);
            }
        }

        Collections.sort(allMethods, new MethodComparator());
        logger.info("methods="+myMethods);

        logger.setLevel(Level.OFF);
    }


    static public TreeMap<String, Method> getMethods() {
        return myMethods;
    }

    static public TreeSet<String> getMethodNames() {
        return methodNames;
    }

    public static Object invoke (String methodName, Object [] params) {
   //     System.out.println("in invoke methodName="+methodName);
        Class [] paramClasses = new Class[params.length];

        for (int i = 0 ; i < paramClasses.length ; i++) {
   //         System.out.println("params["+i+"] type="+params[i].getClass().getName());
            if (params[i] instanceof Number) {
                paramClasses[i] = Number.class;
                continue;
            } else if (params[i] instanceof ScribMatrix) {

                ScribMatrix m = (ScribMatrix)params[i];
                if (m.isVector()) {
                    paramClasses[i] = RealVector.class;
                    double [] row = m.getRow(0);
                    params[i] = new ScribVector(row);
                    continue;
                }
            } else if (params[i] instanceof RealVector) {
                paramClasses[i] = RealVector.class;
                continue;
            }

            paramClasses[i] = params[i].getClass();
        }

        try {
            ArrayList<Class> al = methodTable.get(methodName);
            if (al == null) {
      //          System.out.println(methodName +" doesn't exist");
                NoSuchMethodException ex = new NoSuchMethodException("A Couldn't find "+methodName);
                throw ex;
            }

            for (Class myClass : al) {
                try {
                    Method myMethod = myClass.getMethod(methodName, paramClasses);
            //        System.out.println("calling myMethod Invoke myClass="+myClass.getName()+", methodName="+methodName);
                    Object retval = myMethod.invoke(myClass, params);
           //         System.out.println("returning retval="+retval);
                    return retval;

                } catch (NoSuchMethodException nsme) {
                    System.out.println("No such method "+methodName);
                }
            }

        } catch (Exception e) {
            System.out.println("caught Exception cause:"+e);
            System.out.println("caught Exception cause:"+e.getCause());
            if (e.getCause() != null) {
                System.out.println("caught Exception message:"+e.getCause().getMessage());
            }

            System.out.println("caught Exception message:"+e.getMessage());


            if (e.getCause() != null) {
                throw new MPException(e.getCause());
            } else {
                throw new MPException(e);
            }


        }

        NoSuchMethodException nsme = new NoSuchMethodException("B Couldn't find '"+methodName+"'");
        throw new MPException(nsme);
    }


    public static Vector getList () {

        //    TreeMap<String, Method> myMethods = getMethods();
        StringBuilder infoBuf = new StringBuilder();
        infoBuf.append("<html><head><title>Information</title></head><body>\n");
        Vector<Vector<String>> vector = new Vector<Vector<String>>();

        //      Set<String> keys = myMethods.keySet();
        for (Method aMethod : allMethods) {
            Vector<String> row = new Vector<String>();
            row.add(aMethod.getName());

            Class returnType = aMethod.getReturnType();
            //  row.add(returnType.getSimpleName());
            row.add(getSimpleTypeName(returnType));
            Class [] params = aMethod.getParameterTypes();
            StringBuilder paramBuf = new StringBuilder();
            for (int i = 0 ; i < params.length ; i++) {
                if (i > 0) {
                    paramBuf.append(",");
                }

                paramBuf.append(getSimpleTypeName(params[i]));
            }

            row.add(paramBuf.toString());

            vector.add(row);


            infoBuf.append("<p><A NAME='")
                    .append(aMethod.getName())
                    .append(paramBuf.toString())
                    .append("'>\n")
                    .append("<b>method</b> : ").append(aMethod.getName()).append("<br>\n")
                    .append("<b>class</b> : ")
                    .append(aMethod.getDeclaringClass().getSimpleName())
                    .append("<br>\n")
                    .append("<b>in</b> : ")
                    .append(paramBuf.toString())
                    .append("<br>\n")
                    .append("<b>out</b> : ")
                            //   .append(returnType.getSimpleName())
                    .append(getSimpleTypeName(returnType))

                    .append("<br>\n")
                            //         .append("My description\n")
                    .append(printExample(aMethod))
                    .append("</A>\n");

        }
        infoBuf.append("</body></html>");

        String infoString = infoBuf.toString();
        try {
            File outFile = new File(".", "information.html");
            FileWriter writer = new FileWriter(outFile);
            writer.write(infoString,0,infoString.length());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println("writer exception"+e);
        }

        return vector;
    }

    public static String getSimpleTypeName (Class c) {
        if (c == ScribMatrix.class) {
            return "Matrix";
        } else if (c == RealVector.class) {
            return "Vector";
        } else {
            return c.getSimpleName();
        }
    }


    static public int getMaxParams () {
        return 3;
    }

    public static String printExample(AnnotatedElement elem) {
        if (elem == null || !elem.isAnnotationPresent(Example.class)) {
            return "No annotation";
        }
        Example annotation = elem.getAnnotation(Example.class);
        String annotationValue = annotation.value();
        return annotationValue;
        //      System.out.println(elem.toString() + " - Example: '" + annotationValue);
    }


}

class MethodComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        String name1 = ((Method)o1).getName();
        String name2 = ((Method)o2).getName();
        return name1.compareTo(name2);
    }
}

