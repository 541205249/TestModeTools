package com.jiazy.aoptools.runtime;

import com.jiazy.aoptools.runtime.utils.BroadcastUtils;
import com.jiazy.aoptools.runtime.utils.ReflectionUtils;
import com.jiazy.testmode.annotation.CollectSpentTimeSync;
import com.jiazy.testmode.annotation.CollectSpentTimeSyncs;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;

import static com.jiazy.aoptools.runtime.utils.Constant.POINTCUT_PACKAGE;

@Aspect
public class CollectSpentTimeSyncAspect extends TagAspect {
    private static final String POINTCUT_METHOD_SINGLE =
            "execution(@" + POINTCUT_PACKAGE + ".CollectSpentTimeSync * *(..))";
    private static final String POINTCUT_METHOD_MULTIPLE =
            "execution(@" + POINTCUT_PACKAGE + ".CollectSpentTimeSyncs * *(..))";
    private static final String POINTCUT_CONSTRUCTOR_SINGLE =
            "execution(@" + POINTCUT_PACKAGE + ".CollectSpentTimeSync *.new(..))";
    private static final String POINTCUT_CONSTRUCTOR_MULTIPLE =
            "execution(@" + POINTCUT_PACKAGE + ".CollectSpentTimeSyncs *.new(..))";


    @Pointcut(POINTCUT_METHOD_SINGLE)
    public void methodSingleAnnotated() {

    }

    @Pointcut(POINTCUT_METHOD_MULTIPLE)
    public void methodMultipleAnnotated() {

    }

    @Pointcut(POINTCUT_CONSTRUCTOR_SINGLE)
    public void constructorSingleAnnotated() {
    }

    @Pointcut(POINTCUT_CONSTRUCTOR_MULTIPLE)
    public void constructorMultipleAnnotated() {
    }

    @Around("methodSingleAnnotated() || constructorSingleAnnotated()")
    public Object weaveSingleAnnotatedJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        sendMsg(joinPoint, end - start);

        return result;
    }

    @Around("methodMultipleAnnotated() || constructorMultipleAnnotated()")
    public Object weaveMultipleAnnotatedJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        sendMsgs(joinPoint, end - start);

        return result;
    }

    private void sendMsg(ProceedingJoinPoint joinPoint, long spentTime) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        if (method == null) {
            return;
        }

        CollectSpentTimeSync annotation = method.getAnnotation(CollectSpentTimeSync.class);
        if (annotation == null) {
            return;
        }
        BroadcastUtils.sendElapsedTime(annotation.target(), method.getName(), spentTime, annotation.description(), getTag(joinPoint));
    }


    private void sendMsgs(ProceedingJoinPoint joinPoint, long spentTime) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        if (method == null) {
            return;
        }

        CollectSpentTimeSyncs annotation = method.getAnnotation(CollectSpentTimeSyncs.class);
        if (annotation == null) {
            return;
        }
        CollectSpentTimeSync[] collectSpentTimeSyncs = annotation.value();
        for (CollectSpentTimeSync collectSpentTimeSync : collectSpentTimeSyncs) {
            BroadcastUtils.sendElapsedTime(collectSpentTimeSync.target(), method.getName(), spentTime, collectSpentTimeSync.description(), getTag(joinPoint));
        }
    }
}
