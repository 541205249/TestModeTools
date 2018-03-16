package com.jiazy.aoptools.runtime;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.jiazy.testmode.annotation.CollectCountMsg;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;

@Aspect
public class CollectCountMsgAspect {
    private static final String POINTCUT_METHOD =
            "execution(@com.jiazy.testmode.annotation.CollectCountMsg * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithCollectCountMsg() {}

    @Around("methodAnnotatedWithCollectCountMsg()")
    public void weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();
        sendMsg(joinPoint);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void sendMsg(ProceedingJoinPoint joinPoint) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        if (method == null) {
            Log.i("weaveJoinPoint", "method == null");
            return;
        }

        CollectCountMsg annotation = method.getDeclaredAnnotation(CollectCountMsg.class);
        if (annotation != null) {
            BroadcastUtils.sendCountMsg(annotation.target(), annotation.isSuccess(), annotation.description());
            Log.i("weaveJoinPoint", "annotation == " + annotation.getClass().getName());
        }
    }

}