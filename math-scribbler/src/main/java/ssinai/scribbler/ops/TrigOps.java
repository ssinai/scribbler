package ssinai.scribbler.ops;

import org.apache.log4j.Logger;
import ssinai.scribbler.parser.Parser;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Nov 18, 2009
 * Time: 6:46:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class TrigOps {

    static private Logger logger = Parser.getLogger();
    public static void init () {
        logger.info("In TrigOps init");
    }

    public static Number sin (Number n) {
        return Math.sin(n.doubleValue());
    }

    public static Number cos (Number n) {
        return Math.cos(n.doubleValue());
    }
}
