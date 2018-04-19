package com.jiazy.testmode.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LiXiaoFeng
 * @date 2018/4/4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(CollectValueMsgs.class)
public @interface CollectValueMsg {
    String target();

    int parameterIndex();

    String description() default "";
}
