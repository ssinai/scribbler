package ssinai.scribbler.parser;

public interface Executable {
    public void eval (Parser parser) throws InterruptedException;
    public int getIndex ();
    public String toString();
    public void setAnswer(Object answer);
    public Object getAnswer();
    public void setShowAnswer(boolean val);
    public boolean getShowAnswer();
    public Object getLastAnswer();
    public void setLastAnswer (Object lastAnswer);
}
