package ssinai.scribbler.annote;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sinai
 * Date: Apr 15, 2010
 * Time: 4:43:05 AM
 * To change this template use File | Settings | File Templates.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PACKAGE, ElementType.FIELD})
public @interface Example {
    String value () default "No Example available";
}
