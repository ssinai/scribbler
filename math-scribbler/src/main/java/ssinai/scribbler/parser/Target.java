package ssinai.scribbler.parser;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Jun 22, 2009
 * Time: 2:46:30 PM
 * To change this template use File | Settings | File Templates.
 */

public class Target {

	public ConcurrentHashMap<String, Object> table;

	public static final String negate = "negate";
	public static final String showAnswer = "showAnswer";
	public static final String arrayList = "arrayList";
	public static final String defineName = "defineName";

	public Target () {
		table = new ConcurrentHashMap<String, Object>();
	}

	public Target copy () {
		Target newTarget = new Target();
		for (Enumeration<String> keys = table.keys() ; keys.hasMoreElements() ;) {
			String key = keys.nextElement();
			Object value = table.get(key);
			newTarget.table.put(key, value);
		}
		return newTarget;
	}

	public void put (String key, Object value) {
		table.put(key, value);
	}

	public void putAll (ConcurrentHashMap<String, Object> c) {
		table.putAll(c);
	}

	public Object get (String key) {
		return table.get(key);
	}

	public boolean containsKey (String key) {
		return table.containsKey(key);
	}

	public Enumeration keys () {
		return table.keys();
	}

	public Object remove (String key) {
		Object v = null;
		if (table.containsKey(key)) {
			v = table.get(key);
			table.remove(key);
		}

		return v;
	}

	public String toString () {
		return table.toString();
	}
}
