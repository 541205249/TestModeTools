package com.jiazy.aoptools.runtime;

import android.view.View;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * AvoidDoubleClickAspect
 */

@Aspect
public class AvoidDoubleClickAspect {

	private static final int TIME_TAG = 111222333;
	private static final int CLICK_DELAY_TIME = 2000;

	private static final String POINTCUT_METHOD =
			"execution(@com.jiazy.testmode.annotations.AvoidDoubleClick * *(..))";

	@Pointcut(POINTCUT_METHOD)
	public void methodAnnotatedWithAvoidDoubleClick() {}

	@Around("methodAnnotatedWithAvoidDoubleClick()")
	public void weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		Object obj = joinPoint.getArgs()[0];
		String na = joinPoint.getSignature().getName();
		if(obj == null || !(obj instanceof View)){
			joinPoint.proceed();
		} else {
			View clickView = (View) obj;
			Object timeTag = clickView.getTag(TIME_TAG);
			long lastClickTime = 0;
			if(timeTag != null && timeTag instanceof Long){
				lastClickTime = (long) timeTag;
			}
			long currentTime = System.currentTimeMillis();
			if(currentTime - lastClickTime < CLICK_DELAY_TIME){
				return;
			}
			clickView.setTag(TIME_TAG,currentTime);
			joinPoint.proceed();
		}

	}
}
