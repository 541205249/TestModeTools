package com.jiazy.aoptools.runtime;

import com.jiazy.aoptools.runtime.bean.StartMethodInfo;
import com.jiazy.aoptools.runtime.utils.BroadcastUtils;
import com.jiazy.aoptools.runtime.utils.ReflectionUtils;
import com.jiazy.testmode.annotation.CollectSpentTimeAsync;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;
import java.util.HashMap;

import static com.jiazy.aoptools.runtime.utils.Constant.POINTCUT_PACKAGE;

@Aspect
public class CollectSpentTimeAsyncAspect extends TagAspect {
    private static final String POINTCUT_METHOD =
            "execution(@" + POINTCUT_PACKAGE + ".CollectSpentTimeAsync * *(..))";

    private static HashMap<String, StartMethodInfo> sStartMethodList = new HashMap<>();

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotated() {
    }

    @Around("methodAnnotated()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ReflectionUtils.getMethod(joinPoint);
        if (method == null) {
            return joinPoint.proceed();
        }

        CollectSpentTimeAsync annotation = method.getAnnotation(CollectSpentTimeAsync.class);
        if (annotation == null) {
            return joinPoint.proceed();
        }

        if (annotation.isEndPoint()) {
            Object result = joinPoint.proceed();
            sendMsg(joinPoint);

            return result;
        } else {
            putStartInfo2Map(joinPoint);
            return joinPoint.proceed();
        }
    }

    private void putStartInfo2Map(ProceedingJoinPoint joinPoint) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        CollectSpentTimeAsync annotation = method.getAnnotation(CollectSpentTimeAsync.class);
        long startTime = System.currentTimeMillis();
        sStartMethodList.put(annotation.target(), new StartMethodInfo(method.getName(), startTime, annotation.description(), getTag(joinPoint)));
    }

    private void sendMsg(ProceedingJoinPoint joinPoint) {
        Method method = ReflectionUtils.getMethod(joinPoint);
        CollectSpentTimeAsync annotation = method.getAnnotation(CollectSpentTimeAsync.class);

        long endTime = System.currentTimeMillis();
        String target = annotation.target();
        StartMethodInfo startMethodInfo = sStartMethodList.get(target);
        if (startMethodInfo == null) {
            return;
        }

        long startTime = startMethodInfo.getStartTime();
        long spentTime = endTime - startTime;
        String endTag = getTag(joinPoint);
        String startTag = startMethodInfo.getTag();
        String tag = startTag + (startTag == null || startTag.length() == 0 || endTag == null || endTag.length() == 0 ? "" : "\n") + endTag;
        String startDescription = startMethodInfo.getDescription();
        String endDescription = annotation.description();
        String description = startDescription + (startDescription == null || startDescription.length() == 0 || endDescription == null || endDescription.length() == 0 ? "" : "\n") + endDescription;
        String methodName = "startName=" + startMethodInfo.getMethodName() + ",endName=" + method.getName();
        BroadcastUtils.sendElapsedTime(target, methodName, spentTime, description, tag);
        sStartMethodList.remove(target);
    }
}
