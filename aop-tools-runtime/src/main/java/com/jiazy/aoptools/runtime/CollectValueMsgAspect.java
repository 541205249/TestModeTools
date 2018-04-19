package com.jiazy.aoptools.runtime;

import android.util.Log;

import com.jiazy.aoptools.runtime.utils.BroadcastUtils;
import com.jiazy.aoptools.runtime.utils.ReflectionUtils;
import com.jiazy.testmode.annotation.CollectValueMsg;
import com.jiazy.testmode.annotation.CollectValueMsgs;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;

import static com.jiazy.aoptools.runtime.utils.Constant.POINTCUT_PACKAGE;

/**
 * @author LiXiaoFeng
 */
@Aspect
public class CollectValueMsgAspect extends TagAspect {
    private static String TAG = CollectValueMsgAspect.class.getSimpleName();
    private static final String POINTCUT_METHOD_SINGLE =
            "execution(@" + POINTCUT_PACKAGE + ".CollectValueMsg * *(..))";
    private static final String POINTCUT_METHOD_MULTIPLE =
            "execution(@" + POINTCUT_PACKAGE + ".CollectValueMsgs * *(..))";

    @Pointcut(POINTCUT_METHOD_SINGLE)
    public void methodSingleAnnotated() {
    }

    @Pointcut(POINTCUT_METHOD_MULTIPLE)
    public void methodMultipleAnnotated() {

    }

    @Around("methodSingleAnnotated()")
    public Object weaveSingleAnnotatedJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        sendMsg(joinPoint);
        return result;
    }

    @Around("methodMultipleAnnotated()")
    public Object weaveMultipleAnnotatedJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        sendMsgs(joinPoint);
        return result;
    }

    private void sendMsg(ProceedingJoinPoint joinPoint) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        if (method == null) {
            Log.i(TAG, "method == null");
            return;
        }

        CollectValueMsg annotation = method.getAnnotation(CollectValueMsg.class);
        if (annotation == null) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        Object value = args[annotation.parameterIndex()];
        BroadcastUtils.sendValueMsg(annotation.target(), method.getName(), value == null ? "null" : value.toString(), annotation.description(), getTag(joinPoint));
    }


    private void sendMsgs(ProceedingJoinPoint joinPoint) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        if (method == null) {
            Log.i(TAG, "method == null");
            return;
        }

        CollectValueMsgs annotation = method.getAnnotation(CollectValueMsgs.class);
        if (annotation == null) {
            return;
        }
        CollectValueMsg[] collectValueMsgs = annotation.value();
        Object[] args = joinPoint.getArgs();
        for (CollectValueMsg collectValueMsg : collectValueMsgs) {
            Object value = args[collectValueMsg.parameterIndex()];
            BroadcastUtils.sendValueMsg(collectValueMsg.target(), method.getName(), value == null ? "null" : value.toString(), collectValueMsg.description(), getTag(joinPoint));
        }
    }
}

