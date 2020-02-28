package ssinai.scribbler.parser;

public class PreProcessor {

    Parser parser;
    StringBuilder buf;
    int commentCounter;

    public PreProcessor () {
        parser = getClearAnswerParser();
    }

    public String clearAnswers (String candidate) {
        System.out.println("in clearAnswersParser");
        buf = new StringBuilder();
        commentCounter = 0;
        Assembly a = new Assembly(candidate);
        boolean b = parser.match(a);
        if (b) {
            return buf.toString();
        }
        return "clearAnswers screwed up";
    }

    Sequence clearAnswerParser;
    public Parser getClearAnswerParser () {
        if (clearAnswerParser == null) {
            clearAnswerParser = new Sequence("ClearAnswerSeq");
            Alternation alt = new Alternation();
            alt.add(getLineCommentParser());
            alt.add(getStartCommentParser());
            alt.add(getEndCommentParser());
            alt.add(getAnswerParser());
            alt.add(getCharParser());
            clearAnswerParser.add(new Repetition(alt));
            clearAnswerParser.add(getEof());
        }
        return clearAnswerParser;
    }

    Terminal startCommentParser;
    Parser getStartCommentParser () {
        if (startCommentParser == null) {
            startCommentParser = new Terminal("[/][*]");
            startCommentParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    commentCounter++;

                    Object o = a.pop();
                    buf.append(o);
                }
            })  ;
        }
        return startCommentParser;
    }

    Terminal endCommentParser;
    Parser getEndCommentParser () {
        if (endCommentParser == null) {
            endCommentParser = new Terminal("[*][/]");
            endCommentParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    commentCounter--;
                    Object o = a.pop();
                    buf.append(o);
                }
            })  ;
        }
        return endCommentParser;
    }

    Terminal lineCommentParser;
    Parser getLineCommentParser () {
        if (lineCommentParser == null) {
            lineCommentParser = new Terminal("[/][/].*[^\\$]");

            lineCommentParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    buf.append(o);
                }
            }) ;
        }
        return lineCommentParser;
    }

    private Terminal answerParser;


    Parser getAnswerParser () {
        // need the [^;(>>)] so that if a ';' is left out, it doesn't erase multiple lines
        // until the next ';'
        final String answerPattern = ">>[^;(>>)]*;";
        if (answerParser == null) {
            answerParser = new Terminal(answerPattern);

            answerParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    if (commentCounter == 0) {
                        String s = o.toString();
                        s = s.replaceFirst(answerPattern, ">>;");
                        buf.append(s);

                    } else if (commentCounter > 0) {
                        buf.append(o);
                    }
                }
            });
        }
        return answerParser;
    }

    Terminal charParser;
    Parser getCharParser () {
        if (charParser == null) {
            charParser = new Terminal(".");
            charParser.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    buf.append(o);
                }
            });
        }
        return charParser;
    }

    Terminal eof;
    public Parser getEof () {
        if (eof == null) {
            eof = new Terminal("\\z");
            eof.addAssembler(new Assembler () {
                public void workOn (Assembly a) {
                    Object o = a.pop();
                    buf.append(o);
                }
            });
        }
        return eof;
    }

}

