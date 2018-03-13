package com.jiazy.aoptools.runtime;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * 作者： jiazy
 * 日期： 2018/3/12.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class ContextInstance {
    private volatile static ContextInstance mInstance;
    private Context context;

    private ContextInstance() throws Throwable {
        if(context == null){
            Class clz = Class.forName("android.app.ActivityThread");
            Method method = clz.getMethod("currentApplication");
            context = (Context) method.invoke(clz);
        }
    }

    public static ContextInstance getInstance() {
        if (mInstance == null) {
            synchronized (ContextInstance.class) {
                if (mInstance == null) {
                    try {
                        mInstance = new ContextInstance();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        Log.i("ContextInstance", "ContextInstance is down!!!");
                    }
                }
            }
        }
        return mInstance;
    }

    public Context getContext() {
        return context;
    }
}
