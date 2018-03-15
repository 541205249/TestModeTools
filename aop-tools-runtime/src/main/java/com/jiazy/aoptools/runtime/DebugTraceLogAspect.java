package com.jiazy.aoptools.runtime;

import android.text.TextUtils;
import android.util.Log;

import com.jiazy.aoptools.runtime.bean.MethodTraceInfo;
import com.jiazy.testtools.annotation.DebugTraceLog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * DebugTraceLogAspect
 */

@Aspect
public class DebugTraceLogAspect {
	private HashMap<String,LinkedList<MethodTraceInfo>> traceMap = new HashMap<>();

	private static final String POINTCUT_METHOD =
			"execution(@com.jiazy.testmode.annotations.DebugTraceLog * *(..))";

	@Pointcut(POINTCUT_METHOD)
	public void methodAnnotatedWithDebugTraceLog() {}

	@Around("methodAnnotatedWithDebugTraceLog()")
	public void weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		DebugTraceLog debugTraceLog = methodSignature.getMethod().getAnnotation(DebugTraceLog.class);
		String traceTag = debugTraceLog.value();
		if(TextUtils.isEmpty(traceTag)){
			joinPoint.proceed();
			return ;
		}
		long start,end;
		if(isStartMethod(traceTag,joinPoint)){
			StringBuilder builder = new StringBuilder("-->");
			start = System.currentTimeMillis();
			Object result = joinPoint.proceed();
			LinkedList<MethodTraceInfo> traceInfoList = traceMap.get(traceTag);
			if(traceInfoList == null){
				traceInfoList = new LinkedList<>();
			}
			MethodTraceInfo info = new MethodTraceInfo();
			info.setMethodName(joinPoint.getSignature().getName());
			info.setInvokeTime(start);
			traceInfoList.add(info);
			traceMap.put(traceTag,traceInfoList);
			builder.append(joinPoint.getSignature().getName());
			Log.e(traceTag,builder.toString());
		} else {
			Object result = joinPoint.proceed();
			StringBuilder builder = new StringBuilder("<--");
			end = System.currentTimeMillis();
			LinkedList<MethodTraceInfo> traceInfoList = traceMap.get(traceTag);
			long startTime = traceInfoList.removeFirst().getInvokeTime();
			builder.append(joinPoint.getSignature().getName()).append("use time").append(end-startTime);
			Log.e(traceTag,builder.toString());
		}
	}

	private boolean isStartMethod(String traceTag,JoinPoint joinPoint){
		LinkedList<MethodTraceInfo> traceInfoList = traceMap.get(traceTag);
		if(traceInfoList == null || traceInfoList.size() == 0){
			return true;
		} else {
			MethodTraceInfo info = traceInfoList.getFirst();
			if(info.getMethodName().equals(joinPoint.getSignature().getName())){
				return true;
			} else {
				return false;
			}
		}
	}

}
