package com.jiazy.aoptools.runtime;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.jiazy.testmode.annotation.CollectElapsedTime;
import com.jiazy.testmode.annotation.DebugTraceLog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * CollectElapsedTimeAspect
 */

@Aspect
public class CollectElapsedTimeAspect {
	private static final String POINTCUT_METHOD =
			"execution(@com.jiazy.testmode.annotation.CollectElapsedTime * *(..))";

	private static final String POINTCUT_CONSTRUCTOR =
			"execution(@com.jiazy.testmode.annotation.CollectElapsedTime *.new(..))";

	@Pointcut(POINTCUT_METHOD)
	public void methodAnnotatedWithCollectElapsedTime() {}

	@Pointcut(POINTCUT_CONSTRUCTOR)
	public void constructorAnnotatedWithCollectElapsedTime() {}

	@Around("methodAnnotatedWithCollectElapsedTime() || constructorAnnotatedWithCollectElapsedTime")
	public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {

		enterMethod(joinPoint);
		long start = System.currentTimeMillis();
		Object result = joinPoint.proceed();
		long end = System.currentTimeMillis();
		long timeDifference = end-start;
		endMethod(joinPoint,timeDifference);

		sendMsg(joinPoint, timeDifference);

		return result;
	}

	@TargetApi(Build.VERSION_CODES.N)
	private void sendMsg(ProceedingJoinPoint joinPoint, long timeDifference) {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		CollectElapsedTime collectElapsedTime =
				methodSignature.getMethod().getDeclaredAnnotation(CollectElapsedTime.class);
		if (collectElapsedTime != null) {
			BroadcastUtils.sendElapsedTime(collectElapsedTime.target(), timeDifference);
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
		StringBuilder builder = new StringBuilder("<--  ")
				.append(methodName)
				.append(" [")
				.append(spentTime)
				.append("ms]");
		Log.e(cls.getSimpleName(),builder.toString());
	}

}
