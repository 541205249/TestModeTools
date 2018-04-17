package com.jiazy.testmode.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LiXiaoFeng
 * @date 2018/4/17
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CollectSpentTimeSyncs {
    CollectSpentTimeSync[] value();
}
