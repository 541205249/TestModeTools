package com.jiazy.aoptools.runtime.utils;

import android.content.Intent;
import android.util.Log;

/**
 * 作者： jiazy
 * 日期： 2018/3/12.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class BroadcastUtils {
    private static final String TAG = BroadcastUtils.class.getSimpleName();

    public static void sendElapsedTime(String target, String methodName, long spentTime, String description, String tag) {
        Intent intent = getAction(target);
        intent.putExtra("spentTime", spentTime);
        intent.putExtra("methodName", methodName);
        intent.putExtra("description", description);
        intent.putExtra("tag", tag);
        ContextInstance.getInstance().getContext().sendBroadcast(intent);
    }

    public static void sendCountMsg(String target, String methodName, String description, String tag) {
        Intent intent = getAction(target);
        Log.d(TAG, intent.getAction());
        intent.putExtra("description", description);
        intent.putExtra("methodName", methodName);
        intent.putExtra("tag", tag);
        ContextInstance.getInstance().getContext().sendBroadcast(intent);
    }

    public static void sendValueMsg(String target, String methodName, String value, String description, String tag) {
        Intent intent = getAction(target);

        intent.putExtra("value", value);
        intent.putExtra("description", description);
        intent.putExtra("methodName", methodName);
        intent.putExtra("tag", tag);
        ContextInstance.getInstance().getContext().sendBroadcast(intent);
    }

    private static Intent getAction(String target) {
        Intent intent = new Intent(getAction());
        intent.putExtra("target", target);
        Log.i("jiazy", "target=" + target);

        return intent;
    }

    private static String getAction() {
        return ContextInstance.getInstance().getContext().getPackageName() + ".testmode.action";
    }
}
