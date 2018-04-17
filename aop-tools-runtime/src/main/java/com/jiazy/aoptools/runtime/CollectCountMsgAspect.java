package com.jiazy.aoptools.runtime;

import com.jiazy.aoptools.runtime.utils.BroadcastUtils;
import com.jiazy.aoptools.runtime.utils.ReflectionUtils;
import com.jiazy.testmode.annotation.CollectCountMsg;
import com.jiazy.testmode.annotation.CollectCountMsgs;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;

import static com.jiazy.aoptools.runtime.utils.Constant.POINTCUT_PACKAGE;

@Aspect
public class CollectCountMsgAspect extends TagAspect {
    private static final String POINTCUT_METHOD =
            "execution(@" + POINTCUT_PACKAGE + ".CollectCountMsgs * *(..))";

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

        CollectCountMsgs annotation = method.getAnnotation(CollectCountMsgs.class);
        if (annotation != null) {
            CollectCountMsg[] collectCountMsgs = annotation.value();

            for (CollectCountMsg collectCountMsg : collectCountMsgs) {
                BroadcastUtils.sendCountMsg(collectCountMsg.target(), method.getName(), collectCountMsg.description(), getTag(joinPoint));
            }
        }
    }
}