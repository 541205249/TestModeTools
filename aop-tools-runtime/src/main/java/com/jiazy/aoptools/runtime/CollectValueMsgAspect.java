package com.jiazy.aoptools.runtime;

import android.util.Log;

import com.jiazy.aoptools.runtime.utils.BroadcastUtils;
import com.jiazy.aoptools.runtime.utils.ReflectionUtils;
import com.jiazy.testmode.annotation.CollectValueMsg;
import com.jiazy.testmode.annotation.ValueParameter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.jiazy.aoptools.runtime.utils.Constant.POINTCUT_PACKAGE;

/**
 * @author LiXiaoFeng
 */
@Aspect
public class CollectValueMsgAspect extends TagAspect {
    private static String TAG = CollectValueMsgAspect.class.getSimpleName();
    private static final String POINTCUT_METHOD =
            "execution(@" + POINTCUT_PACKAGE + ".CollectValueMsg * *(..))";

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
            Log.i(TAG, "method == null");
            return;
        }

        CollectValueMsg annotation = method.getAnnotation(CollectValueMsg.class);
        Float value = null;
        Object[] args = joinPoint.getArgs();
        if (annotation != null) {
            Annotation parameterAnnotations[][] = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                boolean isFoundValue = false;
                for (int j = 0; j < annotations.length; j++) {
                    if (annotations[j] instanceof ValueParameter) {
                        value = (Float) args[i];
                        isFoundValue = true;
                        break;
                    }
                }
                if (isFoundValue) {
                    break;
                }
            }

            if (value == null) {
                return;
            }

            BroadcastUtils.sendValueMsg(annotation.target(), method.getName(), value, annotation.description(), getTag(joinPoint));
        }
    }
}

