package com.jiazy.aoptools.runtime;


import com.jiazy.aoptools.runtime.utils.BroadcastUtils;
import com.jiazy.aoptools.runtime.utils.ReflectionUtils;
import com.jiazy.testmode.annotation.CollectCountMsg;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;

import static com.jiazy.aoptools.runtime.utils.Constant.POINTCUT_PACKAGE;

@Aspect
public class CollectCountMsgAspect extends TagAspect {
    private static final String POINTCUT_METHOD =
            "execution(@" + POINTCUT_PACKAGE + ".CollectCountMsg * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotated() {
    }

    @Around("methodAnnotated()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        sendMsg(joinPoint);
        return result;
    }

    private void sendMsg(ProceedingJoinPoint joinPoint) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        if (method == null) {
            return;
        }

        CollectCountMsg annotation = method.getAnnotation(CollectCountMsg.class);
        if (annotation != null) {
            BroadcastUtils.sendCountMsg(annotation.target(), method.getName(), annotation.description(), getTag(joinPoint));
        }
    }
}