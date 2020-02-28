package ssinai.scribbler.parser;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Feb 3, 2010
 * Time: 7:33:27 PM
 * To change this template use File | Settings | File Templates.
 */

public class MatrixMap extends HashMap<String, ScribMatrix> {

    private static MatrixMap map;

    private MatrixMap () {}

    public static MatrixMap getMatrixMap () {
        if (map == null) {
            map = new MatrixMap();
        }
        return map;
    }

    public static ScribMatrix putMatrix (String key, ScribMatrix c) {
        return getMatrixMap().put(key, c);
    }

    public static ScribMatrix getMatrix (String key) {
        return getMatrixMap().get(key);
    }
}
