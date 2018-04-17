package com.jiazy.testmode.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CollectCountMsg
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Repeatable(CollectCountMsgs.class)
public @interface CollectCountMsg {
    String target();

    String description() default "";
}
