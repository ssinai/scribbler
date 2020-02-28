package ssinai.scribbler.parser;

public interface MPComponent {
    public MPComponent copy ();
    public void  setUseColor (boolean useColor);
    public boolean getUseColor ();
}
