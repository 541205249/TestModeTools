package com.jiazy.aoptools.runtime.bean;

public class StartMethodInfo {
    private String methodName;
    private long startTime;
    private String description;
    private String tag;

    public StartMethodInfo(String methodName, long startTime, String description, String tag) {
        this.methodName = methodName;
        this.startTime = startTime;
        this.description = description;
        this.tag = tag;
    }

    public String getMethodName() {
        return methodName;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getDescription() {
        return description;
    }

    public String getTag() {
        return tag;
    }
}
