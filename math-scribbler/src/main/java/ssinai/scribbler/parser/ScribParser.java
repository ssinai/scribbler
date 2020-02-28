package ssinai.scribbler.parser;

import java.text.ParseException;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import ssinai.scribbler.ops.MatrixOps;
import ssinai.scribbler.gui.*;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 30, 2009
 * Time: 2:06:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScribParser {

//    private static Logger logger;
    private final Stack<ArrayList> listStack = new Stack<ArrayList>();

    private ArrayList currentList = new ArrayList();
    private final ArrayList <Executable> mainList = new ArrayList<Executable>();
    private Parser parser;
    private TreeMap<Integer, MatchResult> commentMap = new TreeMap<Integer, MatchResult>();
    private String candidate;
    private ArrayList<String> failedTestList;
    private int componentCounter;
    private int matrixCounter;
    Object lastAnswer;


    private static ScribParser scribParser;

    private static TreeSet<String> reservedWords;

    static {
        reservedWords = new TreeSet<String>();
        reservedWords.add("true");
        reservedWords.add("false");
        reservedWords.add("while");
        reservedWords.add("do");
        reservedWords.add("if");
        reservedWords.add("else");
        reservedWords.add("for");
        reservedWords.add("testEquals");
        reservedWords.add("map");
        reservedWords.add("loop");
        reservedWords.add("Math.PI");
        reservedWords.add("Math.E");

        scribParser = new ScribParser();
    }

    private ScribParser() {
        //  new ScribOps();
        //      logger = Parser.getLogger();
        init();
    }



    public static ScribParser getScribParser () {
        return scribParser;
    }

    public void printAll() {
        System.out.println("printAll");
        System.out.println(mainList.toString());
    }

    private void init () {
        PreProcessor preProcessor = new PreProcessor();
        parser = getMainParser();
    }


    public boolean checkIfCancelled () {

        if (CalculateWorker.getCalculateWorker() != null) {
            boolean cancelled = CalculateWorker.getCalculateWorker().isCancelled();
            //        System.out.println("checkIfCancelled returning "+cancelled);
            return cancelled;
            //      return CalculateWorker.getCalculateWorker().isCancelled();
        }
        return false;

    }
      
    public void updateProgress (int progress) {
        if (CalculateWorker.getCalculateWorker() != null) {
            CalculateWorker.getCalculateWorker().updateProgress(progress);
        }
    }

    MPException error;
    public MPException getError () {
        return error;
    }


    public String calculate (String c) {

        this.candidate = c;

        mainList.clear();

        error = null;
        StringBuilder buf = new StringBuilder();
        failedTestList = new ArrayList<String>();
        int start = 0;
        componentCounter = 0;
        matrixCounter = 0;

//        Level savedLevel = logger.getLevel();
//        logger.setLevel(Level.INFO);

        //       logger.setLevel(savedLevel);

        currentList = mainList;
        Parser.pass = Parser.Pass.PARSE;

//        findDefinedMethods(candidate);
        findComments(candidate);
        Assembly a = new Assembly(candidate);

        parser.matchIndex=0;
        boolean b = parser.match(a);
        //      savedLevel = logger.getLevel();
        //      logger.setLevel(Level.INFO);

//        ArrayList<Executable> newList = (ArrayList<Executable>) mainList.clone();
        ArrayList<Statement> newList = (ArrayList<Statement>) mainList.clone();

        if (!b) {
            System.out.println("Parse Error");
            int matchIndex = parser.matchIndex;
            //      String errorString = getErrorString(matchIndex);
            //        error = new MPError();

            ParseException pe = new ParseException("Parsing error", matchIndex);
            //   error = new MPException(errorString);
            //         error.setErrorString(errorString);
            error = new MPException(pe);
            error.setMatchIndex(matchIndex);
            System.out.println("parseError matchIndex="+matchIndex);

            System.out.println("setting error to pass "+Parser.pass);
            error.setPass(Parser.pass);
            System.out.println("parse errorString='"+getErrorString(matchIndex)+"'");
            //        System.out.println("parse errror startOfLineIndex="+startOfLineIndex+", endofLineIndex="+endOfLineIndex);
            error.setErrorString(getErrorString(matchIndex));
            //    error.setLineBounds(startOfLineIndex, endOfLineIndex);
            return candidate;
        } else {

            if (definedMethodMap == null) {
                definedMethodMap = new TreeMap<String, DefinedMethod>();
            }

            //      System.out.println("");
            //      System.out.println("Starting PARSER CALC");

            //      System.out.println("definedMethodMap="+definedMethodMap);

            Parser.pass = Parser.Pass.CALC;
            System.out.println("Set Parser.pass to CALC");
            processingIndex = 0;
            parser.matchIndex = 0;
            int matchIndex= parser.matchIndex;
            int counter = 0;
            int maxIndex = 0;
            //        System.out.println("newList="+newList);
            setIndexes(newList);
            for (Statement o : newList) {

                System.out.println("executing '"+o+"'");
                error = null;
                if (checkIfCancelled()) {
                    return candidate;
                }

                //    System.out.println("first maxIndex="+maxIndex);
                maxIndex += o.getIndex();
                //        System.out.println("next maxIndex="+maxIndex);
                if (o.getIndex() >= processingIndex) {
                    processingIndex = o.getIndex();
                }
                System.out.println("processingIndex="+processingIndex);
                currentStatement = o;
                System.out.println("currentStatement="+currentStatement);
                System.out.println("o instanceof "+o.getClass().getName());
                try {
                    System.out.println("processing index="+o.getIndex());
                    System.out.println("pre variableMap="+variableMap);
                    o.eval(parser);
                    System.out.println("post variableMap="+variableMap);
                    //      //        o.exec(parser);
                    //               System.out.println("past o");
                } catch (Exception e) {
                    //               System.out.println("Caught processException e="+e);
                    //               System.out.println("o index = "+o.getIndex());
                    if (e instanceof MPException) {
                        error = (MPException)e;
                    } else {
                        error = new MPException(e);
                    }
                    //               System.out.println("error matchIndex="+o.getIndex());

                    //               System.out.println("error executing "+o);
                    if (error.getMatchIndex() == -1) {
                        error.setMatchIndex(o.getIndex());
                    }
                    error.setErrorString(getErrorString(error.getMatchIndex()));
                    error.setPass(Parser.pass);
                    //               System.out.println("setting error to pass 2 "+Parser.pass);
                    throw error;

                }
                int progress = 100 * (counter+1) / newList.size();
                updateProgress(progress);
                counter++;
            }
        }
        for (Object o : newList) {
            //    System.out.println("appending "+o.toString());
            buf.append(o.toString());
        }

        mainList.clear();

        return buf.toString();

    }


    private void setIndexes (ArrayList<Statement> newList) {
        for (Statement s : newList) {
            if (s instanceof WhileConditionalStatement) {
                System.out.println("WhileConditionalList="+s);
                for (Object s1 : s) {
                    if (s1 instanceof BraceStatements) {
                        System.out.println("BraceStatements found");
                        BraceStatements braceStatements = (BraceStatements)s1;
                        for (Object s2 : braceStatements) {
                            System.out.println("braceStatement='"+s2+"'");
                        }
                    } else {
                        System.out.println("s1="+s1);
                    }
                }
            } else {
                System.out.println("Plain Statement="+s);
            }
        }
    }





    int processingIndex = 0;
    Executable currentStatement;


    int endOfLineIndex = -1;
    int startOfLineIndex = -1;
    public String getErrorString (int matchIndex) {
        System.out.println("error in parsing at "+matchIndex);
        if (matchIndex >= candidate.length()) {
            matchIndex = candidate.length()-1;
        }
        char c = candidate.charAt(matchIndex);
        System.out.println("c="+c);

        endOfLineIndex = -1;
        startOfLineIndex = -1;

        System.out.println("getErrorString candidate="+candidate);

        if (c == '\n') {
            endOfLineIndex = matchIndex;
            startOfLineIndex = candidate.lastIndexOf("\n", matchIndex-1);
        } else {
            endOfLineIndex = candidate.indexOf("\n", matchIndex);
            startOfLineIndex = candidate.lastIndexOf("\n", matchIndex);
        }

        if (endOfLineIndex == -1) {
            endOfLineIndex=candidate.length();
        }

        if (startOfLineIndex == -1) {
            startOfLineIndex = 0;
        }

        String tmpString = candidate.substring(startOfLineIndex, endOfLineIndex);
        tmpString = tmpString.trim();

        return tmpString;
    }

    public void findComments (String candidate) {
        Pattern pattern = Pattern.compile("([/][*])|([*][/])");
        Matcher matcher = pattern.matcher(candidate);
        commentMap.clear();

        while (matcher.find()) {
            commentMap.put(matcher.start(), matcher.toMatchResult());
        }
    }


    Sequence commentParser;
    public Parser getCommentParser () {
        if (commentParser == null) {
            commentParser = new Sequence("Comment Seq");
            commentParser.add(new Terminal("\\s*[/][*]"));

            commentParser.addAssembler(new Assembler() {
                public void workOn (Assembly a) {

                    if (Parser.pass == Parser.Pass.PARSE) {
                        Object o = a.pop();
                        String s = o.toString();
                        int len = s.length();
                        int position = a.getIndex()-len;


                        int endPosition = findEndComment(position);
                        String assemblyString = a.getInitialString();
                        a.setIndex(endPosition);

                        String commentString = assemblyString.substring(position, endPosition);

                        System.out.println("commentString is '"+commentString+"'");
                        Comment lc = new Comment(commentString);
                        lc.setIndex(endPosition);
                        currentList.add(lc);
                    }
                    a.getStack().clear();
                }
            });
        }
        return commentParser;
    }



    public int findEndComment (int startPosition) {
        SortedMap<Integer, MatchResult> tailMap = commentMap.tailMap(startPosition);
        Set<Integer> keySet = tailMap.keySet();
        Integer lastPosition = -1;

        for (Integer key : keySet) {
            MatchResult matchResult = tailMap.get(key);
            if (matchResult.group().equals("/*")) {
                if (lastPosition != -1) {
                    break;
                }
            } else if (matchResult.group().equals("*/")) {
                lastPosition = key ;
            }
        }
        return  lastPosition+2;
    }



    class Comment extends Statement {

        public Comment (String s) {
            super(s);
        }

        public String toString () {
            return s;
        }


        public void eval (Parser parser) {
            System.out.println("Comment eval");
        }
    }



    Sequence lineCommentParser;
    public Parser getLineCommentParser () {
        if (lineCommentParser == null) {
            lineCommentParser = new Sequence("LineComment Seq");
            lineCommentParser.add(new Terminal("[/][/][^\n]*([\n]|\\z)"));

            lineCommentParser.addAssembler(new Assembler() {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.PARSE) {
                        Object o = a.pop();
                        Comment lc = new Comment(o.toString());

                        currentList.add(lc);
                    }
                    a.getStack().clear();
                }
            });

        }
        return lineCommentParser;
    }


    private Sequence mainParser;
    public  Parser getMainParser () {
        if (mainParser == null) {
            mainParser = new Sequence("Main Parser Seq");
            mainParser.add(getStatementList());
            mainParser.add(getEof());
        }
        return mainParser;
    }

    public static final TreeMap<Object,Object>variableMap = new TreeMap<Object,Object>();
    //   private final TreeMap<Object,Object>internalVariableMap = new TreeMap<Object,Object>();

    public void clearVariables () {
        if (variableMap != null) {
            variableMap.clear();
        }

        if (Statement.internalVariableMap != null) {
            Statement.internalVariableMap.clear();
        }
    }


    private Sequence postfix;
    Parser getPostfix () {
        if (postfix == null) {
            postfix = new Sequence("Postfix Seq");
            postfix.add(new Terminal(variableString));
            Alternation alt = new Alternation("Postfix Alt");

            alt.add(new Terminal("[-][-]"));
            alt.add(new Terminal("[+][+]"));
            postfix.add(alt);
            postfix.addAssembler(new Assembler() {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {

                        Object operator = a.pop().toString().trim();
                        Object variable = a.pop().toString().trim();

                        Object value = variableMap.get(variable);
                        if (value == null) {
                            value = 0;
                        }
                        double d = Double.valueOf(value.toString());
                        double d2 = d;
                        if (operator.toString().equals("--")) {
                            d2 -= 1.0;
                        } else if (operator.toString().equals("++")) {
                            d2 += 1.0;
                        }
                        variableMap.put(variable, d2);
                        a.push(value);

                        int x = 1;
                        int y = x++;
                    }
                }
            });
        }
        return postfix;
    }



    private final String variableString = "[_a-zA-Z]\\w*";
    private Alternation statement;
    public Parser getStatement() {
        if (statement == null) {
            statement = new Alternation("Statement Alt");
            statement.add(getAssignableStatement());
            statement.add(getSimpleStatement());
            statement.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.PARSE) {
                        Statement al = new Statement(a);
                        //           System.out.println("Created new Statement at "+al.getIndex()+", al='"+al+"'");
                        currentList.add(al);
                    } else if (Parser.pass == Parser.Pass.CALC) {
                        //     lastAnswer = a.pop();
                        lastAnswer = a.pop();
                        currentStatement.setLastAnswer(lastAnswer);
                        Target target = a.getTarget();
                        Object showAnswer = target.get(Target.showAnswer);
                        if (showAnswer instanceof Boolean && ((Boolean) showAnswer)) {
                            currentStatement.setShowAnswer(true);
                        }
                        if (lastAnswer instanceof MPComponent) {
                            String key = "__JComponent:"+(componentCounter++)+":";
                            ComponentMap.putComponent(key, (MPComponent)lastAnswer);

                            currentStatement.setAnswer(key);
                        } else if (lastAnswer instanceof ScribMatrix) {

                            if (currentStatement.getShowAnswer()) {
                                ScribMatrix m = (ScribMatrix)lastAnswer;
                                String key = "__Matrix:"+(matrixCounter++)+":";
                                MatrixMap.putMatrix(key, m);
                                currentStatement.setAnswer(key);
                            }  else {
                                currentStatement.setAnswer(lastAnswer);
                            }
                        } else if (lastAnswer instanceof DefinedMethod) {
                            //             System.out.println("Defined method lastAnswer "+currentStatement.getAnswer());
                        } else {
                            currentStatement.setAnswer(lastAnswer);
                        }
                    }
                    a.getStack().clear();
                }
            }) ;
        }
        return statement;
    }

    Alternation assignableVar;
    public Parser getAssignableVar () {
        if (assignableVar == null) {
            assignableVar = new Alternation("Asgn Var Alt");
            Sequence seq = new Sequence();
            seq.add(getMatrixElementPattern());
            assignableVar.add(seq);
            assignableVar.add(new Terminal(variableString));
            seq.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        try {
                            Stack indexStack = new Stack();
                            while (true) {
                                Object o = a.pop();
                                if (!o.toString().trim().equals("[")) {
                                    indexStack.push(o);
                                } else {
                                    break;
                                }
                            }

                            Number rowIndex = (Number)indexStack.pop();
                            Number columnIndex;
                            if (!indexStack.isEmpty()) {
                                columnIndex = (Number)indexStack.pop();
                            } else {
                                columnIndex=rowIndex;
                                rowIndex=0;
                            }

                            Object varName = a.pop();
                            varName = varName.toString().trim();
                            Object o = variableMap.get(varName);

                            if (o == null) {
                                ScribMatrix mat = new ScribMatrix(rowIndex, columnIndex);

                                variableMap.put(varName, mat);
                                MatrixHolder mh = new MatrixHolder((String)varName, mat, rowIndex, columnIndex);
                                a.push(mh);
                            } else if (o instanceof ScribMatrix) {
                                ScribMatrix mat = (ScribMatrix)o;

                                if (mat.getColumnDimension() < (columnIndex.intValue()+1)
                                        || mat.getRowDimension() < rowIndex.intValue()+1) {
                                    int colSize = Math.max(mat.getColumnDimension(), columnIndex.intValue()+1);
                                    int rowSize = Math.max(mat.getRowDimension(), rowIndex.intValue()+1);


                                    ScribMatrix newMatrix = new ScribMatrix(rowSize, colSize);
                                    newMatrix.setSubMatrix(mat.getData(),0,0);

                                    mat = newMatrix;
                                    variableMap.put(varName, mat);
                                }
                                MatrixHolder mh = new MatrixHolder((String)varName, mat, rowIndex, columnIndex);
                                a.push(mh);

                            }  else if (o instanceof RealVector) {
                                ScribVector v = (ScribVector)o;

                                if (v.getDimension() < (columnIndex.intValue()+1)) {
                                    int vectorSize = Math.max(v.getDimension(), columnIndex.intValue()+1);

                                    //     RealVector newVector = new ScribVector(vectorSize);
                                    ScribVector newVector = new ScribVector(vectorSize);


                                    v = newVector;
                                    variableMap.put(varName,v);
                                }
                                //    VectorHolder vh = new VectorHolder((String)varName, v, columnIndex);
                                VectorHolder vh = new VectorHolder((String)varName, v, columnIndex);

                                a.push(vh);
                            }

                        } catch (Exception ex) {
                            a.push(ex);
                        }
                    }
                }
            }) ;


        }
        return assignableVar;
    }

    Sequence assignableStatement;
    public Parser getAssignableStatement () {
        if (assignableStatement == null) {
            assignableStatement = new Sequence("Assignable Seq");
            assignableStatement.add(getAssignableVar());
            assignableStatement.add(getAssignableOperator());
            assignableStatement.add(getSimpleStatement());
            assignableStatement.addAssembler(new Assembler() {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        Object o = a.pop();

                        Object operator = a.pop().toString().trim();

                        Object variableObject = a.pop();
                        Object variable = variableObject.toString().trim();
                        if (operator.equals("=")) {
                            if (variableObject instanceof MatrixHolder) {
                                MatrixHolder mh = (MatrixHolder)variableObject;
                                ScribMatrix m = mh.getMatrix();

                                if (m.getColumnDimension() < (mh.getColIndex().intValue()+1)
                                        || m.getRowDimension() < mh.getRowIndex().intValue()+1) {
                                    int colSize = Math.max(m.getColumnDimension(), mh.getColIndex().intValue()+1);
                                    int rowSize = Math.max(m.getRowDimension(), mh.getRowIndex().intValue()+1);

                                    ScribMatrix newMatrix = new ScribMatrix(rowSize, colSize);
                                    newMatrix.setSubMatrix(m.getData(),0,0);
                                    m = newMatrix;
                                }

                                ScribMatrix newMatrix = new ScribMatrix(m.getData());
                                newMatrix.setEntry(mh.getRowIndex(), mh.getColIndex(), Double.valueOf(o.toString().trim()));
                                o = newMatrix;
                                variable = mh.getVarName();
                                a.push(newMatrix.getEntry(mh.getRowIndex().intValue(), mh.getColIndex().intValue()));
                                a.push(o);
                                variableMap.put(variable, o);
                                return;
                            } else if (variableObject instanceof VectorHolder) {
                                VectorHolder vh = (VectorHolder)variableObject;
                                // RealVector v = vh.getVector();
                                ScribVector v = vh.getVector();
                                if (v.getDimension() < vh.getIndex().intValue()+1) {
                                    int indexSize = Math.max(v.getDimension(), vh.getIndex().intValue()+1);
                                    v = new ScribVector(indexSize);
                                }
                                //    RealVector newVector = new ScribVector(v.getData());
                                ScribVector newVector = new ScribVector(v.toArray());

                                //          newVector.setEntry(vh.getIndex().intValue(), Double.valueOf(o.toString().trim()));
                                newVector.setEntry (vh.getIndex(), Double.valueOf(o.toString().trim()));
                                o = newVector;
                                variable = vh.getVarName();
                                a.push(newVector.getEntry(vh.getIndex().intValue()));
                                a.push(o);
                                variableMap.put(variable, o);
                                return;
                            } else {
                                variableMap.put(variable, o);
                            }

                        } else if (operator.equals("+=")) {
                            Object old = variableMap.get(variable);
                            double d = Double.parseDouble(o.toString());
                            double val = d + Double.parseDouble(old.toString());
                            variableMap.put(variable, val);
                        } else if (operator.equals("-=")) {
                            Object old = variableMap.get(variable);
                            double d = Double.parseDouble(o.toString());
                            double val = Double.parseDouble(old.toString())-d;
                            variableMap.put(variable, val);
                        } else if (operator.equals("/=")) {
                            Object old = variableMap.get(variable);
                            double d = Double.parseDouble(o.toString());
                            double val = Double.parseDouble(old.toString())/d;
                            variableMap.put(variable, val);
                        } else if (operator.equals("*=")) {
                            Object old = variableMap.get(variable);
                            double d = Double.parseDouble(o.toString());
                            double val = Double.parseDouble(old.toString())*d;
                            variableMap.put(variable, val);
                        } else if (operator.equals("%=")) {
                            Object old = variableMap.get(variable);
                            double d = Double.parseDouble(o.toString());
                            double val = Double.parseDouble(old.toString())%d;
                            variableMap.put(variable, val);
                        } else if (operator.equals("^=")) {
                            Object old = variableMap.get(variable);
                            double d = Double.parseDouble(o.toString());
                            double val = Math.pow(Double.parseDouble(old.toString()),d);
                            variableMap.put(variable, val);
                        }
                        a.push(o);
                    }
                }
            });
        }
        return assignableStatement;
    }


    Sequence simpleStatement;
    public Parser getSimpleStatement () {
        if (simpleStatement == null) {
            simpleStatement = new Sequence("Simple Statement Seq");
            simpleStatement.add(getExpression());
            simpleStatement.add(new Repetition(getPrintSymbol(),0,1));
            simpleStatement.add(getEos().discard());
        }
        return simpleStatement;
    }


    Alternation blockStatement;
    public Parser getBlockParser () {
        if (blockStatement == null) {
            blockStatement = new Alternation("Block Alt");

            blockStatement.add(getDoWhileStatement());
            blockStatement.add(getWhileStatement());
            blockStatement.add(getIfElseStatement());
            blockStatement.add(getForStatement());
        }
        return blockStatement;
    }



    Sequence statementList;
    public Parser getStatementList () {
        if (statementList == null) {
            statementList = new Sequence("StatementList Seq");
            Alternation alt = new Alternation("StatementList Alt");

            alt.add(getCommentParser());
            alt.add(getLineCommentParser());
            alt.add(getDefineStatement());
            alt.add(getBlockParser());


            alt.add(getStatement());
            Repetition rep = new Repetition(alt);
            rep.setName("StatementList Rep");
            statementList.add(rep);
        }
        return statementList;
    }

    Alternation assignableOperator;
    public Parser getAssignableOperator () {
        if (assignableOperator == null) {
            assignableOperator = new Alternation("Assignable Operator Alt");
            assignableOperator.add(new Terminal("[=]"));
            assignableOperator.add(new Terminal("[+][=]"));
            assignableOperator.add(new Terminal("[-][=]"));
            assignableOperator.add(new Terminal("[*][=]"));
            assignableOperator.add(new Terminal("[/][=]"));
            assignableOperator.add(new Terminal("[%][=]"));
            assignableOperator.add(new Terminal("\\^[=]"));
        }
        return assignableOperator;
    }



    Sequence braceBlock;
    public Parser getBraceBlock () {
        if (braceBlock == null) {
            braceBlock = new Sequence("BraceBlock Seq");
            Terminal startBrace = new Terminal("[{]");
            braceBlock.add(startBrace.discard());
            startBrace.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    listStack.push(currentList);
                    currentList = new BraceStatements();
                    Object o = a.pop();
                    //             System.out.println("Created new BraceStatements at "+a.getIndex());

                    currentList.add(o);
                }

            }) ;

            braceBlock.add(getStatementList());


            Terminal endBrace = new Terminal("[}]");
            endBrace.addAssembler(new Assembler () {
                public void workOn (Assembly a) {

                    Object o = a.pop();
                    if (!a.getStack().isEmpty()) {
                        currentList.add(a.getStack());
                    }
                    currentList.add(o);

                    restoreList();
                    a.getStack().clear();
                }
            }) ;
            braceBlock.add(endBrace.discard());
        }
        return braceBlock;
    }

    /*
    class DoWhileConditionalList extends Statement {
        public void eval (Parser parser) throws InterruptedException {
            internalVariableMap.clear();

            while (true) {
                System.out.println("DoWhileConditionalList loop");
                BraceStatements braceStatements = (BraceStatements)get(1);
                //           braceStatements.process();
                braceStatements.eval(parser);

                Object done = internalVariableMap.get("break");

                if (done != null && done instanceof Boolean) {
                    if ((Boolean)done) {
                        return;
                    }
                }

                BooleanParenStatement boolParenStatement = (BooleanParenStatement)get(3);
                boolean val = boolParenStatement.getConditionalTest(parser);

                if (!val) {
                    System.out.println("internalVariableMap="+internalVariableMap);
                    System.out.println("variableMap"+variableMap);
                    return;
                }
            }
        }
    }
    */


    Sequence defineStatement;
    public Parser getDefineStatement() {
        if (defineStatement == null) {
            defineStatement = new Sequence("Define Seq");
            Terminal t = new Terminal("\\s*[#]\\s*");
            defineStatement.add(t);

            t.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    listStack.push(currentList);
                    currentList = new DefinedMethod();
                    Object o = a.pop();
                    currentList.add(o);
                }
            });

            Terminal methodName = new Terminal(variableString);
            defineStatement.add(methodName);
            methodName.addAssembler (new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    currentList.add(o);
                }
            });

            defineStatement.add(getDefinedMethodParameters());
            defineStatement.add(getBraceBlock());
            defineStatement.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.PARSE) {
                        DefinedMethod defMeth = (DefinedMethod)currentList;
                        defMeth.setMarker((String)defMeth.get(0));
                        defMeth.setName((String)defMeth.get(1));
                        defMeth.setParameters((ArrayList)defMeth.get(2));
                        defMeth.setBraceStatements((BraceStatements)defMeth.get(3));

                        currentList = listStack.pop();
                        currentList.add(defMeth);

                        if (definedMethodMap == null) {
                            definedMethodMap = new TreeMap<String, DefinedMethod>();
                        }
                        definedMethodMap.put(defMeth.getName(), defMeth);
                    }
                    //           System.out.println("Leaving DefinedMethod workOn");
                }

            })  ;
        }
        return defineStatement;
    }


    TreeMap<String, DefinedMethod> definedMethodMap;


    Sequence whileStatement;
    public Parser getWhileStatement() {
        if (whileStatement == null) {
            whileStatement = new Sequence("While Seq");
            KeyWord t = new KeyWord("while");
            whileStatement.add(t);
            t.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    listStack.push(currentList);
                    currentList = new WhileConditionalStatement();
                    Object o = a.pop();
                    //         System.out.println("Created new WhileConditionlList at "+a.getIndex()+", o='"+o+"'");
                    currentList.add(o);
                }
            });
            whileStatement.add(getBooleanParenStatement());
            whileStatement.add(getBraceBlock());
            whileStatement.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    restoreList();
                }
            })  ;
        }
        return whileStatement;
    }



    Sequence doWhileStatement;
    public Parser getDoWhileStatement() {
        if (doWhileStatement == null) {
            doWhileStatement = new Sequence("Do While Seq");
            KeyWord t = new KeyWord("do");
            doWhileStatement.add(t);
            t.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    listStack.push(currentList);
                    currentList = new DoWhileConditionalStatement();
                    Object o = a.pop();
                    currentList.add(o);
                    System.out.println("do currentList="+currentList);
                }
            });
            doWhileStatement.add(getBraceBlock());

            Sequence whileSeq = new Sequence("Do While Seq");
            KeyWord whileTerm = new KeyWord("while");

            whileTerm.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    currentList.add(o);
                    System.out.println("while currentList="+currentList);
                }
            }) ;

            doWhileStatement.add(whileTerm);
            whileSeq.add(getBooleanParenStatement());


            //      whileSeq.add(new Terminal("[;]").discard());
            /*
            whileSeq.addAssembler(new Assembler() {
                public void workOn (Assembly a) {
                    logger.info("in whileSeq workOn");
                }
            }) ;
            */
            doWhileStatement.add(whileSeq);

            Terminal semicolon = new Terminal("[;]");
            doWhileStatement.add(semicolon);
            semicolon.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    currentList.add(o);
                }
            }) ;
            //          System.out.println("final doWhileStatement="+doWhileStatement);

            doWhileStatement.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    System.out.println("doWhile workOn variableMap="+variableMap);
                    restoreList();
                }
            })  ;
        }
        return doWhileStatement;
    }


    Sequence forStatement;
    public Parser getForStatement () {
        if (forStatement == null) {
            forStatement = new Sequence();

            KeyWord t = new KeyWord("for");
            forStatement.add(t);

            t.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    System.out.println("in ForStatement workOn");
                    listStack.add(currentList);
                    ForConditionalStatement fcs = new ForConditionalStatement();
                    Object o = a.pop();
                    System.out.println("ForStatement o="+o);
                    fcs.add(o);

                    String s = findMatchingEndParen(a);
                    System.out.println("ForStatement s="+s);
                    fcs.add(s);
                    currentList = fcs;
                }
            });

            forStatement.add(getBraceBlock());
            forStatement.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    restoreList();
                }
            }) ;
        }
        return forStatement;
    }

    private String findMatchingEndParen (Assembly a) {
        String s = a.getInitialString();
        int index = a.getIndex();
        int matchCount = 0;
        StringBuilder buf = new StringBuilder();
        do {
            char c = s.charAt(index++);
            if (c == '(') {
                matchCount++;
            } else if (c == ')') {
                matchCount--;
            }
            buf.append(c);
        } while (matchCount > 0);
        a.setIndex(index);
        return buf.toString();
    }



    Sequence ifStatement;
    public Parser getIfStatement() {
        if (ifStatement == null) {
            ifStatement = new Sequence("If Seq");
            KeyWord t = new KeyWord("if");
            ifStatement.add(t);
            t.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    listStack.push(currentList);
                    currentList = new IfConditionalStatement();
                    Object o = a.pop();
                    currentList.add(o);
                }
            }) ;
            ifStatement.add(getBooleanParenStatement());
            ifStatement.add(getBraceBlock());
        }
        return ifStatement;
    }


    Sequence elseIfStatement;
    public Parser getElseIfStatement () {
        if (elseIfStatement == null) {
            elseIfStatement = new Sequence("Else If Seq");
            KeyWord t = new KeyWord("else\\s+if");
            elseIfStatement.add(t);
            t.addAssembler (new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    currentList.add(o);
                }
            }) ;
            elseIfStatement.add(getBooleanParenStatement());
            elseIfStatement.add(getBraceBlock());
            /*
            elseIfStatement.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                }
            })  ;
            */
        }
        return elseIfStatement;
    }


    public void restoreList () {
        if (listStack.isEmpty()) {
            return;
        }
        ArrayList tmpList = currentList;
        currentList = listStack.pop();
        currentList.add(tmpList);
        System.out.println("restoreList variableMap="+variableMap);
    }



    Sequence elseStatement;
    public Parser getElseStatement () {
        if (elseStatement == null) {
            elseStatement = new Sequence("Else Seq");
            KeyWord t = new KeyWord("else");
            elseStatement.add(t);
            t.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    currentList.add(o);
                }
            }) ;
            elseStatement.add(getBraceBlock());

            /*
            elseStatement.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    logger.info("end of else statement workOn break="+internalVariableMap.get("break")+", a="+a);
                    logger.info("end else statement a="+a+", currentList="+currentList);
                    logger.info("mainList="+mainList);
                }
            })  ;
            */
        }
        return elseStatement;
    }



    Sequence ifElseStatement;
    public Parser getIfElseStatement () {
        if (ifElseStatement == null) {
            ifElseStatement = new Sequence("IfElse Seq");
            ifElseStatement.add(getIfStatement());
            ifElseStatement.add(new Repetition(getElseIfStatement()));
            ifElseStatement.add(new Repetition(getElseStatement(),0,1));
            ifElseStatement.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    restoreList();
                }
            }) ;

        }
        return ifElseStatement;
    }

    private Object methodReturn;

    Sequence returnParser;
    public Sequence getReturnParser () {
        if (returnParser == null) {
            returnParser = new Sequence("ReturnParser");
            returnParser.add(new Terminal("return").discard());
            returnParser.add(getExpression());
            returnParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        methodReturn = a.pop();
                        Statement.internalVariableMap.add(Statement.ControlEnum.RETURN);
                    }
                }
            });
        }
        return returnParser;
    }

    Sequence continueParser;
    public Sequence getContinueParser () {
        if (continueParser == null) {
            continueParser = new Sequence ("ContinueParser");
            continueParser.add(new Terminal("continue").discard());
            continueParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        Statement.internalVariableMap.add(Statement.ControlEnum.CONTINUE);
                        a.push(true);
                    }
                }
            } );
        }
        return continueParser;
    }



    Sequence breakParser;
    public Sequence getBreakParser () {
        if (breakParser == null) {
            breakParser = new Sequence ("BreakParser");
            breakParser.add(new Terminal("break").discard());
            breakParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        System.out.println("breakParser workOn");
                        Statement.internalVariableMap.add(Statement.ControlEnum.BREAK);
                        a.push(true);
                    }
                }
            } );
        }
        return breakParser;
    }





    Sequence booleanParenStatement;

    public Parser getBooleanParenStatement () {
        if (booleanParenStatement == null) {
            booleanParenStatement = new Sequence();
            Terminal t = new Terminal("[(]");
            booleanParenStatement.add(t);

            t.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    listStack.push(currentList);
                    Object o = a.pop();
                    currentList = new BooleanParenStatement();
                    currentList.add(o);
                }
            });

            booleanParenStatement.add(getExpression());
            Terminal t2 = new Terminal("[)]");

            t2.addAssembler(new Assembler () {
                public void workOn (Assembly a) {

                    Stack tmpStack = new Stack();
                    Object endParen = a.pop();

                    while (!a.getStack().isEmpty()) {
                        Object o = a.pop();
                        tmpStack.push(o);
                    }
                    StringBuilder buf = new StringBuilder();

                    while (!tmpStack.isEmpty()) {
                        Object o = tmpStack.pop();
                        buf.append(o.toString());
                    }

                    currentList.add(buf.toString());
                    currentList.add(endParen);
                    restoreList();
                }
            });

            booleanParenStatement.add(t2);
        }
        return booleanParenStatement;
    }



    Sequence definedMethodParameters;
    public Parser getDefinedMethodParameters () {
        if (definedMethodParameters == null) {
            definedMethodParameters = new Sequence("Def Meth Param Seq");
            Terminal t = new Terminal("[(]");
            definedMethodParameters.add(t);

            t.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    listStack.push(currentList);
                    Object o = a.pop();
                    currentList = new ArrayList();
                    currentList.add(o);
                }
            });

            Sequence seq1 = new Sequence("Def Params Seq");
            Terminal param = new Terminal(variableString);
            seq1.add(param);
            param.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    currentList.add(o);
                }
            }) ;
            definedMethodParameters.add(seq1);
            Sequence seq2 = new Sequence();
            Terminal comma = new Terminal("[,]");
            comma.addAssembler (new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    currentList.add(o);
                }
            }) ;

            seq2.add(comma);
            seq2.add(param);
            Repetition rep = new Repetition(seq2);
            seq1.add(rep);


            Terminal t2 = new Terminal("[)]");

            t2.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    Object endParen = a.pop();
                    currentList.add(endParen);
                    restoreList();
                }
            });

            definedMethodParameters.add(t2);
        }
        return definedMethodParameters;

    }


    class DefinedMethod extends Statement {
        String name;
        String marker;
        ArrayList<String> parameters;
        BraceStatements braceStatements;
        String parameterString;

        public DefinedMethod () {
            super();
        }

        public void setName (String name) {
            this.name = name;
        }

        public String getName () {
            return name;
        }

        public void setParameters(ArrayList parameterList) {
            parameters = new ArrayList<String>(parameterList.size()-2);
            for (int i = 1 ; i < parameterList.size()-1; i+=2) {
                parameters.add(parameterList.get(i).toString().trim());
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0 ; i < parameterList.size() ; i++) {
                builder.append(parameterList.get(i));
            }
            parameterString = builder.toString();
        }

        public ArrayList<String> getParameters () {
            return parameters;
        }

        public int getParameterCount () {
            return parameters.size();
        }

        public void setBraceStatements (BraceStatements braceStatements) {
            this.braceStatements = braceStatements;
        }

        public BraceStatements getBraceStatements () {
            return braceStatements;
        }


        TreeMap savedVars;

        public void exec (ArrayList parameters) {

            try {
                savedVars = new TreeMap();

                for (int i = 0 ; i < parameters.size() ; i++) {
                    Object key = getParameters().get(i);
                    Object currentVarVal = variableMap.get(key);
                    if (currentVarVal != null) {
                        savedVars.put(key, currentVarVal);
                    }
                    Object val = parameters.get(i);
                    variableMap.put(key, val);
                }

                methodReturn = null;
                //         getBraceStatements().process();
                getBraceStatements().eval(parser);

                for (int i = 0 ; i < getParameters().size() ; i++) {
                    Object key = getParameters().get(i);
                    variableMap.remove(key);
                    Object val = savedVars.get(key);
                    if (val != null) {
                        variableMap.put(key, val);
                    }
                }

            } catch (Exception e) {
                System.out.println("DefinedMethod exec exception:"+e);
            }
            savedVars = null;
        }

        public void eval (Parser parser) {
            // empty method, otherwise the defined method will try to execute without valid parameters
        }

        public String getMarker () {
            return marker;
        }

        public void setMarker (String marker) {
            this.marker = marker;
        }

        public String toString () {
            StringBuilder buf = new StringBuilder();
            buf.append(getMarker() + getName() + parameterString + getBraceStatements());
            return buf.toString();
        }

    }


    Alternation phrase;
    public Parser getPhrase () {
        if (phrase == null) {
            phrase = new Alternation("Phrase Alt");


            Sequence seq = new Sequence("Neg Phrase Seq");
            seq.add(new Terminal("-").discard());
            seq.add(getPositivePhrase());
            seq.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        ArrayDeque<Object> params = new ArrayDeque<Object>();
                        Object param = a.pop();
                        params.add(param);
                        Object o = ScribOps.invoke("negate", params.toArray());
                        a.push(o);
                    }
                }
            }) ;
            phrase.add(seq);
            phrase.add(getPositivePhrase());
        }
        return phrase;
    }

    Alternation positivePhrase;
    public Parser getPositivePhrase () {
        if (positivePhrase == null) {
            positivePhrase = new Alternation();
            Sequence parenExpression = new Sequence("Phrase parenExpression");
            Terminal startParen = new Terminal("[(]");
            parenExpression.add(startParen.discard());
            parenExpression.add(getExpression());
            Terminal endParen = new Terminal("[)]");
            parenExpression.add(endParen.discard());
            positivePhrase.add(parenExpression);
            positivePhrase.add(getContinueParser());
            positivePhrase.add(getBreakParser());
            positivePhrase.add(getReturnParser());
            //       positivePhrase.add(getMapParser());
            positivePhrase.add(getValue());
        }
        return positivePhrase;
    }




    Sequence commaDelimitedSequence;
    public Parser getCommaDelimitedSequence () {
        if (commaDelimitedSequence == null) {
            commaDelimitedSequence = new Sequence("Comma Del First Seq");
            Sequence expressionSeq = new Sequence("Expression Seq");
            expressionSeq.add(getExpression());
            commaDelimitedSequence.add(expressionSeq);
            Sequence seq = new Sequence("Comma Del Second Seq");
            Terminal comma = new Terminal(",");
            seq.add(comma.discard());
            seq.add(expressionSeq);

            Repetition rep2 = new Repetition(seq);
            rep2.setName("Comma Del Second Rep");
            commaDelimitedSequence.add(rep2);
            expressionSeq.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        Object val = a.pop();
                        ArrayList parameterList = (ArrayList)a.pop();
                        parameterList.add(val);
                        a.push(parameterList);
                    }
                }
            }) ;

            /*
            commaDelimitedSequence.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                    }
                }
            }) ;
            */
        }
        return commaDelimitedSequence;
    }



    Sequence method;
    public Parser getMethod () {
        if (method == null) {
            method = new Sequence("Method Seq");
            Alternation methodNameAlt = new Alternation("Method Name Alt");
            Terminal testEqTerm = new Terminal("testeq");
            testEqTerm.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    Target target = a.getTarget();
                    a.put("testeq", true);
                    a.setTarget(target);
                }
            }) ;

            methodNameAlt.add(testEqTerm);
            Terminal methodName = new Terminal(variableString+"\\s*[(]");
            methodName.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        String n = a.pop().toString();
                        int index = n.indexOf("(");
                        String name = n.substring(0,index);
                        name = name.trim();
                        a.push(name);
                        ArrayList parameterList = new ArrayList();
                        a.push(parameterList);
                    }
                }
            });
            methodNameAlt.add(methodName);
            method.add(methodNameAlt);

            method.add(new Repetition(getCommaDelimitedSequence(),0,1));
            method.add(new Terminal("[)]").discard());
            method.addAssembler (new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        int caretIndex = a.getIndex();
                        //               System.out.println("caretIndex="+caretIndex);
                        ArrayList parameterList = (ArrayList)a.pop();
                        Object key = a.pop();
                        String keyString = key.toString().trim();
                        DefinedMethod val = definedMethodMap.get(keyString);
                        if (val != null) {
                            val.exec(parameterList);
                            if (methodReturn != null) {
                                a.push(methodReturn);
                            } else {
                                a.push(val);
                            }
                        } else {
                            //    try {
                            //             System.out.println("calling invoke keyString="+keyString);
                            try {
                                Object ans = ScribOps.invoke(keyString, parameterList.toArray());

                                //                 System.out.println("past invoke ans="+ans);
                                Target target = a.getTarget();
                                Object isTestEquals = target.get("testeq");

                                if (isTestEquals != null) {
                                    if (isTestEquals.equals(true)) {

                                        if (!((Boolean) ans)) {
                                            String failedString = "testEquals failed at index "
                                                    +processingIndex+" '"+getErrorString(processingIndex)+"'";
                                            System.out.println(failedString);
                                            failedTestList.add(failedString);
                                        }
                                    }
                                }

                                a.push(ans);
                            } catch (MPException e) {
                                System.out.println("invoke exception e="+e+", at "+a.getIndex()+", caretIndex="+caretIndex);
                                throw e;
                            }


                        }
                    }
                }
            });
        }
        return method;
    }

    Alternation booleanParser;
    public Parser getBooleanParser () {
        if (booleanParser == null) {
            booleanParser = new Alternation("Bool Alt");

            booleanParser.add(new Terminal("true"));
            booleanParser.add(new Terminal("false"));

            booleanParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        Object o = a.pop();
                        if (o.toString().trim().equals("true")) {
                            a.push(true);
                        } else if (o.toString().trim().equals("false")) {
                            a.push(false);
                        }
                    }
                }
            }) ;
        }
        return booleanParser;
    }



    Alternation preDef;
    public Parser getPredefinedValues () {
        if (preDef == null) {
            preDef = new Alternation("PreDef Alt");
            preDef.add(new Terminal("Math.PI"));
            preDef.add(new Terminal("Math.E"));

            preDef.addAssembler(new Assembler () {

                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        Object o = a.pop();
                        String s = o.toString().trim();
                        if (s.equals("Math.PI")) {
                            a.push(Math.PI);
                        } else if (s.equals("Math.E")) {
                            a.push(Math.E);
                        }
                    }
                }

            }) ;
        }
        return preDef;
    }


    Sequence value;
    public Parser getValue () {
        if (value == null) {
            value = new Sequence("Val Seq");
            Alternation alt = new Alternation("Val Alt");
            alt.add(getNum());

            alt.add(getPredefinedValues());

            alt.add(getPostfix());
            alt.add(getStringParser());
            alt.add(getBooleanParser());
            alt.add(getMapParser());
            alt.add(getMethod());
            alt.add(getStringList());
            alt.add(getMatrixElement());
            alt.add(getMatrixParser());
            alt.add(getVariable());
            value.add(alt);
            Repetition rep = new Repetition(getTranspose(),0,1);
            rep.setName("Val Tra Rep");
            value.add(rep);
        }
        return value;
    }


    Sequence matrixElementPattern;
    public Parser getMatrixElementPattern () {
        if (matrixElementPattern == null) {
            matrixElementPattern = new Sequence("Mat Elem Pat Seq");
            matrixElementPattern.add(new Terminal(variableString));
            matrixElementPattern.add(new Terminal("\\["));
            Sequence seq2 = new Sequence();
            seq2.add(getExpression());
            Sequence seq3 = new Sequence();
            seq3.add(new Terminal(",").discard());
            seq3.add(getExpression());
            seq2.add(new Repetition(seq3,0,1));
            matrixElementPattern.add(seq2);

            matrixElementPattern.add(new Terminal("\\]").discard());
        }
        return matrixElementPattern;
    }


    Sequence matrixElement;
    public Parser getMatrixElement () {
        if (matrixElement == null) {
            matrixElement = new Sequence("Mat2 El Seq");
            matrixElement.add(getMatrixElementPattern());
            matrixElement.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        System.out.println("in getMatrixElement workOn a="+a);
                        try {
                            Stack indexStack = new Stack();

                            while (true) {
                                Object o = a.pop();
                                if (!o.toString().trim().equals("[")) {
                                    System.out.println("pushing '"+o+"' onto indexStack");
                                    indexStack.push(o);
                                } else {
                                    break;
                                }
                            }
                            System.out.println("indexStack="+indexStack);

                            Number rowIndex = (Number)indexStack.pop();
                            Number columnIndex = null;
                            if (!indexStack.isEmpty()) {
                                columnIndex=(Number)indexStack.pop();
                            }

                            System.out.println("rowIndex="+rowIndex+", columnIndex="+columnIndex);
                            Object varName = a.pop();
                            //                    System.out.println("varName="+varName);
                            Object o = variableMap.get(varName.toString().trim());
                            System.out.println("o="+o);
                            if (o == null) {
                                ScribMatrix mat;
                                if (columnIndex != null) {
                                    mat = new ScribMatrix(rowIndex.intValue(),columnIndex.intValue());
                                } else {
                                    mat = new ScribMatrix(rowIndex.intValue(), 1);
                                }
                                variableMap.put(varName, mat);
                                o = mat;
                            }

                            if (o instanceof ScribMatrix) {
                                ScribMatrix mat = (ScribMatrix)o;
                                if (columnIndex != null) {
                                    o = mat.getEntry(rowIndex.intValue(), columnIndex.intValue());
                                } else {
                                    RealVector v = mat.getRowVector(rowIndex.intValue());
                                    o = new ScribVector(v);
                                }
                                a.push(o);
                            } else if (o instanceof RealVector) {
                                ScribVector v = (ScribVector)o;
                                o = v.getEntry(rowIndex.intValue());
                                a.push(o);
                            }
                        } catch (Exception ex) {
                            a.push(ex.getMessage());
                        }
                    }
                }
            }) ;
        }
        return matrixElement;
    }


    Sequence stringList;
    public Parser getStringList () {
        if (stringList == null) {
            stringList = new Sequence("Str List");
            Terminal start = new Terminal("\\[");
            stringList.add(start);
            Parser quotedString = getStringParser();
            stringList.add(quotedString);
            Sequence seq2 = new Sequence("Str Seq2");
            seq2.add(new Terminal(",").discard());
            seq2.add(quotedString);
            stringList.add(new Repetition(seq2));
            Terminal end = new Terminal("\\]");
            stringList.add(end.discard());
            stringList.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        ArrayList<String> al = new ArrayList<String>();
                        while (!a.getStack().isEmpty()) {
                            Object o = a.pop();

                            if (o.toString().trim().equals("[")) {
                                break;
                            }
                            al.add(0,o.toString());
                        }
                        a.push(al);
                    }
                }

            });
        }
        return stringList;
    }


    Sequence mapParser;
    public Parser getMapParser () {
        if (mapParser == null) {
            mapParser = new Sequence("Map Seq");
            mapParser.add(new KeyWord("map").discard());

            Sequence mapEntryParser = new Sequence();
            mapEntryParser.add(new Terminal(variableString));
            mapEntryParser.add(new Terminal("=").discard());
            mapEntryParser.add(getValue());
            mapEntryParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        Target target = a.getTarget();
                        if (!target.containsKey("map")) {
                            HashMap hashMap = new HashMap();
                            target.put("map", hashMap);
                        }

                        HashMap hashMap = (HashMap)target.get("map");

                        Object value = a.pop();
                        Object key = a.pop();
                        hashMap.put(key.toString().trim(), value);
                        target.put("map", hashMap);
                        a.setTarget(target);
                    }
                }
            }) ;
            mapParser.add(new Repetition(mapEntryParser));

            mapParser.addAssembler(new Assembler() {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        Target target = a.getTarget();
                        Object map = target.get("map");
                        a.push(map);
                    }
                }
            }) ;
        }
        return mapParser;
    }



    String stringString = "\"[^\"]*\"";
    Terminal stringParser;
    public Parser getStringParser () {
        if (stringParser == null) {
            stringParser = new Terminal(stringString);
            stringParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        String s = (String)a.pop();
                        s = s.replaceAll("\"", " ");
                        s = s.trim();
                        a.push(s);
                    }
                }
            });
        }
        return stringParser;
    }



    Sequence array;
    public Parser getMatrixParser () {
        if (array == null) {
            array = new Sequence("Mat Seq");

            Terminal start = new Terminal("\\[");
            array.add(start);

            Sequence arrayRow = new Sequence("Mat Arr Row Seq");
            arrayRow.add(getExpression());

            Sequence repSeq = new Sequence("Mat Rep Seq");
            repSeq.add(new Terminal(",").discard());
            repSeq.add(getExpression());
            Repetition rep = new Repetition(repSeq);
            rep.setName("Mat Rep");
            arrayRow.add(rep);

            array.add(arrayRow);

            Alternation alt = new Alternation("Mat Alt");
            Terminal semiColon = new Terminal(";");
            alt.add(semiColon);

            Sequence seq2 = new Sequence("Mat Seq2");
            seq2.add(arrayRow);
            alt.add(seq2);
            array.add(new Repetition(alt));


            Terminal end = new Terminal("\\]");
            array.add(end);


            array.addAssembler (new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        try {
                            System.out.println("in MatrixParser workOn a="+a);
                            Stack<Object> stack = new Stack<Object>();
                            Object o = a.pop();
                            String s = o.toString().trim();
                            while (!s.equals("[")) {
                                stack.push(o);
                                o = a.pop();
                                s = o.toString().trim();
                            }

                            ArrayList currentRow = new ArrayList();
                            ScribMatrix currentMatrix = new ScribMatrix();

                            while (!stack.isEmpty()) {
                                Object poppedObj = stack.pop();
                                System.out.println("popped "+poppedObj+", type="+poppedObj.getClass().getName());
                                s = poppedObj.toString().trim();
                                if (s.equals(";") || s.equals("]")) {
                                    Object obj = currentRow.get(0);
                                    // TODO check for null current row or obj
                                    if (obj instanceof Number) {
                                        ScribVector rowVector = createScribVector(currentRow);
                                        currentMatrix = currentMatrix.appendRow(rowVector.toArray());
                                    } else if (obj instanceof RealVector) {
                                        ScribVector rowVector = createScribVector(currentRow);
                                        System.out.println("SPOT A rowVector="+rowVector+", currentMatrix="+currentMatrix);
                                        currentMatrix = currentMatrix.appendRow(rowVector.toArray());
                                    } else if (obj instanceof ScribMatrix) {
                                        ScribMatrix tmpMatrix = (ScribMatrix)obj;
                                        for (int i = 1 ; i < currentRow.size() ; i++) {
                                            RealMatrix m = (RealMatrix)currentRow.get(i);
                                            tmpMatrix = tmpMatrix.appendColumns(m.getData());
                                        }
                                        currentMatrix = currentMatrix.appendRows(tmpMatrix.getData());
                                    }
                                    System.out.println("SPOT B currentMatrix="+currentMatrix);
                                    if (s.equals("]")) {
                                        System.out.println("currentMatrix.getRowDimension()="+currentMatrix.getRowDimension());
                                        if (currentMatrix.getRowDimension() == 1) {
                                            RealVector rv = currentMatrix.getRowVector(0);
                                            System.out.println("rv="+rv);
                                            ScribVector sv = new ScribVector(rv);
                                            System.out.println("pushing sv="+sv);
                                            a.push(sv);
                                        } else {
                                            System.out.println("pushing currentMatrix="+currentMatrix);
                                            a.push(currentMatrix);
                                        }
                                    }
                                    currentRow.clear();
                                } else {
                                    System.out.println("adding "+poppedObj+" to currentRow");
                                    currentRow.add(poppedObj);
                                    System.out.println("currentRow now="+currentRow);
                                }

                            }

                        } catch (Exception ex) {
                            System.out.println("MatrixParser workOn exception "+ex);
                            ex.printStackTrace();
                            //             System.exit(1);
                        }
                    }
                }
            });
        }

        return array;
    }

    private ScribVector createScribVector (ArrayList currentRow){
        RealVector rowVector = new ArrayRealVector();
        for (int i = 0 ; i < currentRow.size() ; i++) {
            Object element = currentRow.get(i);
            System.out.println("got element "+element);
            if (element instanceof Number) {
                rowVector = rowVector.append((Double)element);
            } else if (element instanceof RealVector) {
                rowVector = rowVector.append((ScribVector)element);
            }
            System.out.println("rowVector="+rowVector);
        }
        return new ScribVector(rowVector);
    }

    Sequence relation;
    public Parser getRelation () {
        if (relation == null) {
            relation = new Sequence("Relation Seq");
            relation.add(getTerm());
            Alternation alt = new Alternation("Relation alt");
            alt.add(getPlusTerm());
            alt.add(getMinusTerm());
            Repetition rep = new Repetition(alt);
            rep.setName("Expression Rep");
            relation.add(rep);
        }
        return relation;
    }

    Sequence lessThan;
    public Parser getLessThan () {
        if (lessThan == null) {
            lessThan = new Sequence("Less Than Seq");
            lessThan.add(new Terminal("<").discard());
            lessThan.add(getRelation());
            lessThan.addAssembler (new Assembler() {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        checkRelation(a, "lt");
                    }
                }
            });
        }
        return lessThan;
    }

    Sequence lessThanEquals;
    public Parser getLessThanEquals () {
        if (lessThanEquals == null) {
            lessThanEquals = new Sequence("Less Than Equals");
            lessThanEquals.add(new Terminal("<=").discard());
            lessThanEquals.add(getRelation());
            lessThanEquals.addAssembler (new Assembler() {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        checkRelation(a, "lte");
                    }
                }
            });
        }
        return lessThanEquals;
    }

    Sequence greaterThan;
    public Parser getGreaterThan () {
        if (greaterThan == null) {
            greaterThan = new Sequence("Greater Than");
            greaterThan.add(new Terminal(">").discard());
            greaterThan.add(getRelation());
            greaterThan.addAssembler (new Assembler() {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        checkRelation(a, "gt");
                    }
                }
            });
        }
        return greaterThan;
    }

    Sequence greaterThanEquals;
    public Parser getGreaterThanEquals () {
        if (greaterThanEquals == null) {
            greaterThanEquals = new Sequence("Greater Than Equals");
            greaterThanEquals.add(new Terminal(">=").discard());
            greaterThanEquals.add(getRelation());
            greaterThanEquals.addAssembler (new Assembler() {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        checkRelation(a, "gte");
                    }
                }
            });
        }
        return greaterThanEquals;
    }



    Sequence equality;
    public Parser getEquality () {
        if (equality == null) {
            equality = new Sequence("Equality Seq");
            equality.add(getRelation());
            Alternation alt = new Alternation("Equality Alt");
            alt.add(getLessThanEquals());
            alt.add(getGreaterThanEquals());
            alt.add(getLessThan());
            alt.add(getGreaterThan());
            Repetition rep = new Repetition(alt);
            rep.setName("Equality Rep");
            equality.add(rep);
        }
        return equality;
    }

    public void checkRelation (Assembly a, String methodName) {
        //   System.out.println("defined method flag set");
//        try {
        ArrayDeque<Object> params=pop3(a);
        //     System.out.println("in checkRelation methodName="+methodName+", params"+params);
        Object val = ScribOps.invoke(methodName, params.toArray());
        //      System.out.println("val="+val);
        a.push(val);

    }

    Sequence equals;
    public Parser getEquals() {
        if (equals == null) {
            equals = new Sequence("Equals Seq");
            equals.add(new Terminal("[=][=]").discard());
            equals.add(getEquality());
            equals.addAssembler (new Assembler() {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        System.out.println("getEquals workOn");
                        checkRelation(a, "isEquals");
                    }
                }
            });
        }
        return equals;
    }

    Sequence notEquals;
    public Parser getNotEquals() {
        if (notEquals == null) {
            notEquals = new Sequence("NotEquals Seq");
            notEquals.add(new Terminal("[!][=]").discard());
            notEquals.add(getEquality());
            notEquals.addAssembler (new Assembler() {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        System.out.println("getNotEquals workOn");
                        checkRelation(a, "notEquals");
                    }
                }
            });
        }
        return notEquals;
    }



    Sequence logical;
    public Parser getLogical () {
        if (logical == null) {
            logical = new Sequence("Logical Seq");
            logical.add(getEquality());
            Alternation alt = new Alternation("Logical Alt");
            alt.add(getEquals());
            alt.add(getNotEquals());
            Repetition rep = new Repetition(alt);
            rep.setName("Logical Rep");
            logical.add(rep);
        }
        return logical;
    }



    Sequence logicalAnd;
    public Parser getLogicalAnd () {
        if (logicalAnd == null) {
            logicalAnd = new Sequence("Logical And");
            logicalAnd.add(new Terminal("[&][&]").discard());
            logicalAnd.add(getLogical());
            logicalAnd.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    System.out.println("in getLogicalAnd workOn a="+a);
                    if (Parser.pass==Parser.Pass.CALC) {
                        checkRelation(a, "and");
                    }
                }
            });

        }
        return logicalAnd;
    }

    Sequence logicalOr;
    public Parser getLogicalOr () {
        if (logicalOr == null) {
            logicalOr = new Sequence("Logical Or");
            logicalOr.add(new Terminal("[|][|]").discard());
            logicalOr.add(getLogical());
            logicalOr.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    System.out.println("in getLogicalOr workOn a="+a);
                    if (Parser.pass==Parser.Pass.CALC) {
                        checkRelation(a, "or");
                    }
                }
            });
        }
        return logicalOr;
    }





    Sequence expression;
    public Parser getExpression () {
        if (expression == null) {
            expression = new Sequence("Expression Seq");

            expression.add(getLogical());
            Alternation alt = new Alternation("Expression Alt");
            alt.add(getLogicalOr());
            alt.add(getLogicalAnd());
            Repetition rep = new Repetition(alt);
            rep.setName("Expression Rep");

            expression.add(rep);
        }
        return expression;
    }




    Sequence term;
    public Parser getTerm () {
        if (term == null) {
            term = new Sequence("Term Seq");
            term.add(getFactor());
            Alternation alt = new Alternation("Term Alt");
            alt.add(getTimesFactor());
            alt.add(getDivideFactor());
            alt.add(getMatrixElementTimesFactor());
            alt.add(getMatrixRDivideFactor());
            Repetition rep = new Repetition(alt);
            rep.setName("Term Rep");
            term.add(rep);
        }
        return term;
    }

    Sequence factor;
    public Parser getFactor () {
        if (factor == null) {
            factor = new Sequence("Factor Seq");
            Alternation alt = new Alternation("Factor Alt");

            Sequence seq = new Sequence("Factor Seq");

            Terminal negate = new Terminal("[-]");
            seq.add(negate.discard());
            seq.add(getExpPhrase());
            alt.add(seq);

            seq.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        ArrayDeque<Object> params = new ArrayDeque<Object>();
                        Object param = a.pop();
                        params.add(param);
                        Object o = ScribOps.invoke("negate", params.toArray());
                        a.push(o);
                    }
                }
            }) ;

            alt.add(getExpPhrase());
            factor.add(alt);
        }
        return factor;
    }

    Sequence expPhrase;
    public Parser getExpPhrase () {
        if (expPhrase == null) {
            expPhrase = new Sequence("ExpPhrase Seq");
            expPhrase.add(getPhrase());
            Alternation alt = new Alternation();
            alt.add(getPowFactor());
            alt.add(getTranspose());
            alt.add(getMatrixElementPow());
            Repetition rep = new Repetition(alt);
            rep.setName("ExpPhrase Alt");
            expPhrase.add(rep);
        }
        return expPhrase;
    }


    Sequence timesFactor;
    public Parser getTimesFactor () {
        if (timesFactor == null) {
            timesFactor = new Sequence("Times Seq");
            Terminal t = new Terminal("[*]");
            timesFactor.add(t.discard());
            timesFactor.add(getFactor());
            timesFactor.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        checkRelation(a, "multiply");
                    }
                }
            });
        }
        return timesFactor;
    }

    public ArrayDeque<Object> pop3 (Assembly a) {
        ArrayDeque<Object> queue = new ArrayDeque<Object>();
        queue.addFirst(a.pop());
        queue.addFirst(a.pop());
        return queue;
    }


    Sequence divideFactor;
    public Parser getDivideFactor () {
        if (divideFactor == null) {
            divideFactor = new Sequence("Divide Seq");
            Terminal t = new Terminal("[/]");
            divideFactor.add(t.discard());
            divideFactor.add(getFactor());
            divideFactor.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        System.out.println("dividing a="+a);
                        checkRelation(a, "divide");
                    }
                }
            });
        }
        return divideFactor;
    }

    Sequence matrixRDivideFactor;
    public Parser getMatrixRDivideFactor () {
        if (matrixRDivideFactor == null) {
            matrixRDivideFactor = new Sequence("Mat RDiv Seq");
            Terminal t = new Terminal("\\\\"); // this is just a single backslash
            matrixRDivideFactor.add(t.discard());
            matrixRDivideFactor.add(getFactor());
            matrixRDivideFactor.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    checkRelation(a, "mrdivide");
                }
            });
        }
        return matrixRDivideFactor;
    }

    Sequence matrixElementTimesFactor;
    public Parser getMatrixElementTimesFactor () {
        if (matrixElementTimesFactor == null) {
            matrixElementTimesFactor = new Sequence("Mat Ele Times Seq");
            Terminal t = new Terminal("[.][*]"); //
            matrixElementTimesFactor.add(t.discard());
            matrixElementTimesFactor.add(getFactor());
            matrixElementTimesFactor.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        checkRelation(a, "ebeMultiply");
                    }
                }
            });
        }
        return matrixElementTimesFactor;
    }

    Sequence matrixElementPow;
    public Parser getMatrixElementPow () {
        if (matrixElementPow == null) {
            matrixElementPow = new Sequence("Mat Ele Pow Seq");
            Terminal t = new Terminal("[.]\\^"); //
            matrixElementPow.add(t.discard());
            matrixElementPow.add(getFactor());
            matrixElementPow.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        checkRelation(a, "ebePow");
                    }
                }
            });
        }
        return matrixElementPow;
    }



    Sequence powFactor;
    public Parser getPowFactor () {
        if (divideFactor == null) {
            powFactor = new Sequence("Pow Seq");
            Terminal t = new Terminal("\\^");
            powFactor.add(t.discard());
            powFactor.add(getFactor());
            powFactor.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        checkRelation(a, "pow");
                    }
                }
            });
        }
        return powFactor;
    }

    Sequence transpose;
    public Parser getTranspose () {
        if (transpose == null) {
            transpose = new Sequence("Trans Seq");
            Terminal transposeTerminal = new Terminal("\\'");
            transpose.add(transposeTerminal.discard());
            transpose.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        Object o = a.pop();

                        if (o instanceof ScribMatrix) {
                            ScribMatrix m = (ScribMatrix)o;
                            ScribMatrix newMatrix =  MatrixOps.transpose(m);
                            a.push(newMatrix);
                        } else {
                            a.push(o);
                        }
                    }
                }
            }) ;
        }
        return transpose;
    }


    Sequence plusTerm;
    public Parser getPlusTerm () {
        if (plusTerm == null) {
            plusTerm = new Sequence("Plus Term");
            plusTerm.add(new Terminal("[+]").discard());
            plusTerm.add(getTerm());
            plusTerm.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass==Parser.Pass.CALC) {
                        checkRelation(a, "add");
                    }
                }
            });

        }
        return plusTerm;
    }

    Sequence minusTerm;
    public Parser getMinusTerm () {
        if (minusTerm == null) {
            minusTerm = new Sequence("Minus Term");
            minusTerm.add(new Terminal("[-]").discard());

            minusTerm.add(getTerm());
            minusTerm.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    if (Parser.pass == Parser.Pass.CALC) {
                        checkRelation(a, "subtract");
                    }
                }
            });
        }
        return minusTerm;
    }

    Sequence variable;
    public Parser getVariable () {
        if (variable == null) {
            variable = new Sequence("Var Seq");
            variable.add(new Terminal(variableString));
            variable.addAssembler(new Assembler () {
                public void workOn (Assembly a) {


                    if (Parser.pass == Parser.Pass.CALC) {
                        Object o = a.pop();
                        String key = o.toString().trim();
                        if (reservedWords.contains(key)) {

                            a.push(o);
                            return;
                        }
                        Object val = variableMap.get(key);
                        if (val == null) {
                            val = 0d; // must be a double or kaboom
                            variableMap.put(key, val);
                        }
                        a.push(val);
                    }
                }
            });

        }
        return variable;
    }



    private String numString = "[0-9]*[.]?([0-9]+(e|E|e-|E-)?)?[0-9]+";
    Terminal num;
    public Parser getNum () {
        num = new Terminal(numString);

        num.addAssembler(new Assembler() {
            public void workOn (Assembly a) {
                if (Parser.pass == Parser.Pass.CALC) {
                    String s = (String)a.pop();
                    Double d = Double.valueOf(s.trim());
                    a.push(d);
                }
            }
        });

        return num;
    }

    Terminal eos;
    public Parser getEos () {
        if (eos == null) {
            eos = new Terminal(";");
            /*
            eos.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                }
            });
            */
        }
        return eos;
    }


    Terminal eof;
    public Parser getEof () {
        if (eof == null) {
            eof = new Terminal("\\z");
            /*
            eof.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    logger.info("getEof workOn");
                }
            });
            */
        }
        return eof;
    }


    String printString = ">>";
    Terminal printSymbol;
    public Terminal getPrintSymbol () {
        if (printSymbol == null) {
            printSymbol = new Terminal(printString);
            printSymbol.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    System.out.println("printSymbol workOn");

                    if (Parser.pass == Parser.Pass.CALC) {
                        System.out.println("printSymbol workOn Parser.Pass.CALC");
                        Object o = a.pop();
                        currentList.add(o);
                        a.put(Target.showAnswer, true);
                    }
                }
            });
        }
        return printSymbol;
    }


}
