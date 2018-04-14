package com.jiazy.aoptools.runtime;

import com.jiazy.aoptools.runtime.utils.BroadcastUtils;
import com.jiazy.aoptools.runtime.utils.ReflectionUtils;
import com.jiazy.testmode.annotation.CollectSpentTimeSync;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;

import static com.jiazy.aoptools.runtime.utils.Constant.POINTCUT_PACKAGE;

@Aspect
public class CollectSpentTimeSyncAspect extends TagAspect {
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

        CollectSpentTimeSync annotation = method.getAnnotation(CollectSpentTimeSync.class);
        if (annotation != null) {
            BroadcastUtils.sendElapsedTime(annotation.target(), method.getName(), spentTime, annotation.description(), getTag(joinPoint));
        }
    }
}
