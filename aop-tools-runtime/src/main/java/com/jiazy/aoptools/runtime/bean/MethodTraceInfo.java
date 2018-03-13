package com.jiazy.aoptools.runtime.bean;

/**
 * Created by Administrator on 2018/2/8.
 */

public class MethodTraceInfo {
	private String methodName;
	private long invokeTime;

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public long getInvokeTime() {
		return invokeTime;
	}

	public void setInvokeTime(long invokeTime) {
		this.invokeTime = invokeTime;
	}
}
