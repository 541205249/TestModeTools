package com.jiazy.aoptools.runtime;

import android.content.Intent;

/**
 * 作者： jiazy
 * 日期： 2018/3/12.
 * 公司： 步步高教育电子有限公司
 * 描述：
 */
public class BroadcastUtils {

    public static void sendA(long timeDifference) {
        Intent intent = new Intent("com.eebbk.test.aoptest");
        intent.putExtra("test","method spend time"+ (timeDifference));
        ContextInstance.getInstance().getContext().sendBroadcast(intent);
    }
}
