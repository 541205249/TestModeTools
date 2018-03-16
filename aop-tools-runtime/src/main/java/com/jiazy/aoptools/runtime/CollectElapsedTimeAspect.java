package com.jiazy.aoptools.runtime;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.jiazy.testmode.annotation.CollectElapsedTime;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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

	@Around("methodAnnotatedWithCollectElapsedTime()")
	public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		Log.i("weaveJoinPoint", "!!!!!!!!!!");
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
		BroadcastUtils.sendElapsedTime("sssss", 111111);

		Method method = getMethod(joinPoint);
		if (method == null) {
			Log.i("weaveJoinPoint", "method == null");
			return;
		}

//		CollectElapsedTime annotation = method.getDeclaredAnnotation(CollectElapsedTime.class);
//		if (annotation != null) {
//			BroadcastUtils.sendElapsedTime(annotation.target(), timeDifference);
//			Log.i("weaveJoinPoint", "annotation == " + annotation.getClass().getName());
//		}

		Annotation annotation1= method.getAnnotation(CollectElapsedTime.class);
		Annotation annotation2 = method.getDeclaredAnnotation(CollectElapsedTime.class);
		Annotation[] annotation3 = method.getAnnotations();
		Annotation[] methodAnnotation = method.getDeclaredAnnotations();
		for (Annotation aMethodAnnotation : methodAnnotation) {
			if (! (aMethodAnnotation instanceof CollectElapsedTime)) {
				continue;
			}

			CollectElapsedTime annotation = method.getDeclaredAnnotation(CollectElapsedTime.class);
			if (annotation != null) {
				BroadcastUtils.sendElapsedTime(annotation.target(), timeDifference);
				Log.i("weaveJoinPoint", "annotation == " + annotation.getClass().getName());
				break;
			}

		}
	}

	private Method getMethod(ProceedingJoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();

//		if (! (signature instanceof MethodSignature)) {
//			throw new IllegalArgumentException("该注解只能用于方法");
//		}

		MethodSignature methodSignature = (MethodSignature) signature;
		try {
			return methodSignature.getDeclaringType().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
//			return joinPoint.getTarget().getClass().getDeclaredMethod(
//					methodSignature.getName(), methodSignature.getParameterTypes());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
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
