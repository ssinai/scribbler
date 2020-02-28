package ssinai.scribbler.parser;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Jun 22, 2009
 * Time: 3:05:46 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CollectionParser extends Parser {

	ArrayList<Parser> al;

	CollectionParser () {
		super();
		al = new ArrayList<Parser>();
	}

	CollectionParser (String s) {
		super(s);
		al = new ArrayList<Parser>();
	}

	public void add (Parser p) {
		al.add(p);
	}
}
