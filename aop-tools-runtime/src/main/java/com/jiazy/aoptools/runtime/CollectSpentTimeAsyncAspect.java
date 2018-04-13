package com.jiazy.aoptools.runtime;

import android.util.Log;

import com.jiazy.aoptools.runtime.bean.StartMethodInfo;
import com.jiazy.aoptools.runtime.utils.BroadcastUtils;
import com.jiazy.aoptools.runtime.utils.ReflectionUtils;
import com.jiazy.testmode.annotation.CollectSpentTimeAsync;
import com.jiazy.testmode.annotation.Tag;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.jiazy.aoptools.runtime.utils.Constant.POINTCUT_PACKAGE;

@Aspect
public class CollectSpentTimeAsyncAspect {
    private static final String POINTCUT_METHOD =
            "execution(@" + POINTCUT_PACKAGE + ".CollectSpentTimeAsync * *(..))";

    private static HashMap<String, StartMethodInfo> sStartMethodList = new HashMap<>();

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotated() {
    }

    @Around("methodAnnotated()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.i("weaveJoinPoint", "!!!!!!!!!!");
        Method method = ReflectionUtils.getMethod(joinPoint);
        if (method == null) {
            Log.i("weaveJoinPoint", "method == null");
            return joinPoint.proceed();
        }

        CollectSpentTimeAsync annotation = method.getAnnotation(CollectSpentTimeAsync.class);
        if (annotation == null) {
            Log.i("weaveJoinPoint", "annotation == null");
            return joinPoint.proceed();
        }

        if (annotation.isEndPoint()) {
            Object result = joinPoint.proceed();
            sendMsg(joinPoint);

            return result;
        } else {
            putStartInfo2Map(joinPoint);
            return joinPoint.proceed();
        }
    }

    private void putStartInfo2Map(ProceedingJoinPoint joinPoint) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        CollectSpentTimeAsync annotation = method.getAnnotation(CollectSpentTimeAsync.class);
        long startTime = System.currentTimeMillis();
        sStartMethodList.put(annotation.target(), new StartMethodInfo(method.getName(), startTime, annotation.description(), getTag(joinPoint)));
    }

    private void sendMsg(ProceedingJoinPoint joinPoint) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        CollectSpentTimeAsync annotation = method.getAnnotation(CollectSpentTimeAsync.class);

        long endTime = System.currentTimeMillis();
        String target = annotation.target();
        StartMethodInfo startMethodInfo = sStartMethodList.get(target);
        if (startMethodInfo == null) {
            return;
        }

        long startTime = startMethodInfo.getStartTime();
        long spentTime = endTime - startTime;
        String endTag = getTag(joinPoint);
        String startTag = startMethodInfo.getTag();
        String tag = startTag + (startTag == null || startTag.length() == 0 || endTag == null || endTag.length() == 0 ? "" : "\n") + endTag;
        String startDescription = startMethodInfo.getDescription();
        String endDescription = annotation.description();
        String description = startDescription + (startDescription == null || startDescription.length() == 0 || endDescription == null || endDescription.length() == 0 ? "" : "\n") + endDescription;
        String methodName = "startName=" + startMethodInfo.getMethodName() + ",endName=" + method.getName();
        BroadcastUtils.sendElapsedTime(target, methodName, spentTime, description, tag);
        sStartMethodList.remove(target);
    }

    private String getTag(ProceedingJoinPoint joinPoint) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        Annotation parameterAnnotations[][] = method.getParameterAnnotations();
        List<Annotation> tagAnnotations = new ArrayList<>();
        List<Integer> tagIndexes = new ArrayList<>();
        for (Annotation[] annotations : parameterAnnotations) {
            for (int i = 0; i < annotations.length; i++) {
                if (annotations[i].getClass().equals(Tag.class)) {
                    tagAnnotations.add(annotations[i]);
                    tagIndexes.add(i);
                }
            }
        }

        Object[] args = joinPoint.getArgs();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tagAnnotations.size(); i++) {
            String k = ((Tag) tagAnnotations.get(i)).name();
            String v = (String) args[i];
            stringBuilder.append("<" + k + "," + v + ">" + (i == tagAnnotations.size() - 1 ? "" : "\n"));
        }
        return stringBuilder.toString();
    }
}
