package com.jiazy.aoptools.runtime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 作者： jiazy
 * 日期： 2018/3/16.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class ReflectionUtils {

    public static Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        try {
            return methodSignature.getDeclaringType().getDeclaredMethod(methodSignature.getName(), methodSignature.getParameterTypes());
//			return joinPoint.getTarget().getClass().getDeclaredMethod(
//					methodSignature.getName(), methodSignature.getParameterTypes());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

}
