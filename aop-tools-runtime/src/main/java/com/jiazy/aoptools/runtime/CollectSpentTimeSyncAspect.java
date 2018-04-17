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
    private static final String POINTCUT_METHOD =
            "execution(@" + POINTCUT_PACKAGE + ".CollectSpentTimeSyncs * *(..))";

    private static final String POINTCUT_CONSTRUCTOR =
            "execution(@" + POINTCUT_PACKAGE + ".CollectSpentTimeSyncs *.new(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotated() {
    }

    @Pointcut(POINTCUT_CONSTRUCTOR)
    public void constructorAnnotated() {
    }

    @Around("methodAnnotated() || constructorAnnotated()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long end = System.currentTimeMillis();

        sendMsg(joinPoint, end - start);

        return result;
    }

    private void sendMsg(ProceedingJoinPoint joinPoint, long spentTime) {
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
