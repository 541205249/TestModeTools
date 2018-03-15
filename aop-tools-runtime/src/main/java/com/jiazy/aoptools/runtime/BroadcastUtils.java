package com.jiazy.aoptools.runtime;

import android.content.Intent;
import android.util.Log;

/**
 * 作者： jiazy
 * 日期： 2018/3/12.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class BroadcastUtils {
    private static final String BASE_ACTION = "com.testmode.action";

    public static void sendElapsedTime(String target, long timeDifference) {
        Intent intent = getAction(target);
        intent.putExtra("timeDifference", timeDifference);
        ContextInstance.getInstance().getContext().sendBroadcast(intent);
    }

    public static void sendCountMsg(String target) {
        Intent intent = getAction(target);
        ContextInstance.getInstance().getContext().sendBroadcast(intent);
    }

    private static Intent getAction(String target) {
        Intent intent = new Intent(BASE_ACTION);
        intent.putExtra("target", target);
        Log.i("jiazy", "target=" + target);

        return intent;
    }
}
