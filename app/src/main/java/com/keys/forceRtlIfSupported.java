package com.keys;

import android.app.Activity;
import android.os.Build;
import android.view.View;


public class forceRtlIfSupported {
    public static void forceRtlIfSupported(Activity activity) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

    }
}
