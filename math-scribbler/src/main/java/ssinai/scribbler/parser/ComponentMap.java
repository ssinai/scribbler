package ssinai.scribbler.parser;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Dec 8, 2009
 * Time: 1:48:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class ComponentMap extends HashMap<String, MPComponent> {

    private static ComponentMap map;

    private ComponentMap () {}

    public static ComponentMap getComponentMap () {
        if (map == null) {
            map = new ComponentMap();
        }
        return map;
    }

    public static MPComponent putComponent (String key, MPComponent c) {
        return getComponentMap().put(key, c);
    }

    public static MPComponent getComponent (String key) {
        return getComponentMap().get(key);
    }

}
