package com.keys.widgets;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


public class myBadgeView extends TextView {

    private View target;

    public myBadgeView(Context context) {
        super(context);
    }

    public myBadgeView(Context context, View target) {
        super(context);
        init(target);
    }

    private void init(View target) {
        this.target = target;
    }

    public void updateTabBadge(int badgeNumber) {
        if (badgeNumber > 0) {
            target.setVisibility(View.VISIBLE);
            ((TextView) target).setText(Integer.toString(badgeNumber));
        } else {
            target.setVisibility(View.GONE);
        }
    }
}