package ssinai.scribbler.parser;

public class KeyWord extends Terminal {
    public KeyWord() {
    }

    public KeyWord(String s) {
      super("\\b"+s+"\\s+");
    }
}
