package com.jiazy.aoptools.runtime;

import com.jiazy.aoptools.runtime.utils.ReflectionUtils;
import com.jiazy.testmode.annotation.Tag;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LiXiaoFeng
 * @date 2018/4/14
 */
public abstract class TagAspect {

    protected String getTag(ProceedingJoinPoint joinPoint) {
        if (joinPoint == null) {
            return null;
        }

        List<Pair> tags = new ArrayList<>();
        Annotation parameterAnnotations[][] = ReflectionUtils.getMethod(joinPoint).getParameterAnnotations();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (int j = 0; j < annotations.length; j++) {
                if (annotations[j] instanceof Tag) {
                    final String name = ((Tag) annotations[j]).name();
                    final String info = (String) args[i];
                    tags.add(new Pair(name, info));
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            stringBuilder.append(tags.get(i) + (i == tags.size() - 1 ? "" : "\n"));
        }
        return stringBuilder.toString();
    }

    private static class Pair {
        private String name;
        private String info;

        public Pair(String name, String info) {
            this.name = name;
            this.info = info;
        }

        @Override
        public String toString() {
            return "<" + name + "," + info + ">";
        }
    }
}
