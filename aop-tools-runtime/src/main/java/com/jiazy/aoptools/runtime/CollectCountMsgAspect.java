package com.jiazy.aoptools.runtime;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.jiazy.testmode.annotation.CollectCountMsg;
import com.jiazy.testmode.annotation.DebugTraceLog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class CollectCountMsgAspect {
    private static final String POINTCUT_METHOD =
            "execution(@com.jiazy.testmode.annotation.CollectCountMsg * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithCollectCountMsg() {}

    @Around("methodAnnotatedWithCollectCountMsg()")
    public void weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        joinPoint.proceed();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        sendMsg(methodSignature);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void sendMsg(MethodSignature methodSignature) {
        CollectCountMsg collectCountMsg =
                methodSignature.getMethod().getDeclaredAnnotation(CollectCountMsg.class);
        if (collectCountMsg != null) {
            BroadcastUtils.sendCountMsg(collectCountMsg.target());
        }
    }

}