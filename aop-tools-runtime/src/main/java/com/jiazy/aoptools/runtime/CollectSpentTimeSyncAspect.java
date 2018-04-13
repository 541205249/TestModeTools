package com.jiazy.aoptools.runtime;

import android.util.Log;

import com.jiazy.aoptools.runtime.utils.BroadcastUtils;
import com.jiazy.aoptools.runtime.utils.ReflectionUtils;
import com.jiazy.testmode.annotation.CollectSpentTimeSync;
import com.jiazy.testmode.annotation.Tag;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.jiazy.aoptools.runtime.utils.Constant.POINTCUT_PACKAGE;

@Aspect
public class CollectSpentTimeSyncAspect {
    private static final String POINTCUT_METHOD =
            "execution(@" + POINTCUT_PACKAGE + ".CollectSpentTimeSync * *(..))";

    private static final String POINTCUT_CONSTRUCTOR =
            "execution(@" + POINTCUT_PACKAGE + ".CollectSpentTimeSync *.new(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotated() {
    }

    @Pointcut(POINTCUT_CONSTRUCTOR)
    public void constructorAnnotated() {
    }

    @Around("methodAnnotated() || constructorAnnotated()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.i("weaveJoinPoint", "!!!!!!!!!!");

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        sendMsg(joinPoint, end - start);

        return result;
    }

    private void sendMsg(ProceedingJoinPoint joinPoint, long spentTime) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        if (method == null) {
            Log.i("weaveJoinPoint", "method == null");
            return;
        }

        CollectSpentTimeSync annotation = method.getAnnotation(CollectSpentTimeSync.class);
        if (annotation != null) {
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
            String tag = stringBuilder.toString();

            BroadcastUtils.sendElapsedTime(annotation.target(), method.getName(), spentTime, annotation.description(), tag);
        }
    }
}
