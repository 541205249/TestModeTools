package com.jiazy.aoptools.runtime;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.jiazy.aoptools.runtime.bean.MethodTraceInfo;
import com.jiazy.testmode.annotation.CollectSpentTimeAsync;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;

import java.lang.reflect.Method;
import java.util.HashMap;

@Aspect
public class CollectSpentTimeAsyncAspect {
	private HashMap<String,MethodTraceInfo> traceMap = new HashMap<>();


	private static final String POINTCUT_METHOD =
			"execution(@com.jiazy.testmode.annotation.CollectSpentTimeAsync * *(..))";

	private static final String POINTCUT_CONSTRUCTOR =
			"execution(@com.jiazy.testmode.annotation.CollectSpentTimeAsync *.new(..))";

	@Pointcut(POINTCUT_METHOD)
	public void methodAnnotatedWithCollectSpentTimeAsync() {}

	@Around("methodAnnotatedWithCollectSpentTimeAsync()")
	public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		Log.i("weaveJoinPoint", "!!!!!!!!!!");

		long start = System.currentTimeMillis();
		if(isStartPoint(joinPoint)) {

		}
		Object result = joinPoint.proceed();
		long end = System.currentTimeMillis();
		long spentTime = end-start;

		sendMsg(joinPoint, spentTime);

		return result;
	}

	@TargetApi(Build.VERSION_CODES.N)
	private boolean isStartPoint(ProceedingJoinPoint joinPoint) {
		Method method = ReflectionUtils.getMethod(joinPoint);
		if (method == null) {
			Log.i("weaveJoinPoint", "method == null");
			return false;
		}

		CollectSpentTimeAsync annotation = method.getDeclaredAnnotation(CollectSpentTimeAsync.class);
		if (annotation != null) {
			Log.i("weaveJoinPoint", "annotation == " + annotation.getClass().getName());
			return annotation.isStartPoint();
		}

		return false;
	}

	@TargetApi(Build.VERSION_CODES.N)
	private void sendMsg(ProceedingJoinPoint joinPoint, long spentTime) {
		Method method = ReflectionUtils.getMethod(joinPoint);
		if (method == null) {
			Log.i("weaveJoinPoint", "method == null");
			return;
		}

        CollectSpentTimeAsync annotation = method.getDeclaredAnnotation(CollectSpentTimeAsync.class);
        if (annotation != null) {
            BroadcastUtils.sendElapsedTime(annotation.target(), spentTime);
            Log.i("weaveJoinPoint", "annotation == " + annotation.getClass().getName());
        }
	}

	private static void enterMethod(JoinPoint joinPoint){
		CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

		Class<?> cls = codeSignature.getDeclaringType();
		String methodName = codeSignature.getName();
		String[] parameterNames = codeSignature.getParameterNames();
		Object[] parameterValues = joinPoint.getArgs();
		StringBuilder builder = new StringBuilder("-->  ");
		builder.append(methodName).append("(");
		for(int i = 0;i < parameterValues.length;i++){
			if(i > 0){
				builder.append(", ");
			}
			builder.append(parameterNames[i]).append("=").append(parameterValues[i]);
		}
		builder.append(")");
		Log.e(cls.getSimpleName(),builder.toString());
	}

	private static void endMethod(JoinPoint joinPoint,long spentTime){
		Signature signature = joinPoint.getSignature();

		Class<?> cls = signature.getDeclaringType();
		String methodName = signature.getName();
		String builder = "<--  " +
				methodName +
				" [" +
				spentTime +
				"ms]";
		Log.e(cls.getSimpleName(), builder);
	}

}
