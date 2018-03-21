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

    public static void sendElapsedTime(String target, String methodName, long spentTime) {
        Intent intent = getAction(target);
        intent.putExtra("spentTime", spentTime);
        intent.putExtra("methodName", methodName);
        ContextInstance.getInstance().getContext().sendBroadcast(intent);
    }

    public static void sendCountMsg(String target, String methodName, boolean isSuccess, String description) {
        Intent intent = getAction(target);

        intent.putExtra("successCount", isSuccess ? 1 : 0);
        intent.putExtra("failCount", isSuccess ? 0 : 1);
        intent.putExtra("description", description);
        intent.putExtra("methodName", methodName);
        ContextInstance.getInstance().getContext().sendBroadcast(intent);
    }

    private static Intent getAction(String target) {
        Intent intent = new Intent(getAction());
        intent.putExtra("target", target);
        Log.i("jiazy", "target=" + target);

        return intent;
    }

    private static String getAction() {
        return "com.testmode.action." + ContextInstance.getInstance().getContext().getPackageName();
    }
}
