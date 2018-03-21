package com.jiazy.aoptools.runtime.bean;

public class StartMethodInfo {
	private String methodName;
	private long startTime;

	public StartMethodInfo(String methodName, long startTime) {
		this.methodName = methodName;
		this.startTime = startTime;
	}

	public String getMethodName() {
		return methodName;
	}

	public long getStartTime() {
		return startTime;
	}

}
