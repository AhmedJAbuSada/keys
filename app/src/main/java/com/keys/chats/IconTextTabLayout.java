package com.keys.chats;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

import com.keys.R;


public class IconTextTabLayout extends TabLayout {
    int[] icons = {
            R.drawable.ic_person,
            R.drawable.ic_chat,
            R.drawable.ic_group,
            R.drawable.ic_home,
    };

    String[] titles = {
            "جهات الاتصال",
            "المحادثات",
            "المجموعات",
            "الرئيسية",
    };

    public IconTextTabLayout(Context context) {
        super(context);
    }

    public IconTextTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconTextTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTabsFromPagerAdapter(@NonNull PagerAdapter adapter) {
        this.removeAllTabs();
        int i = 0;
        for (int count = adapter.getCount(); i < count; ++i) {
            this.addTab(this.newTab().setCustomView(R.layout.custom_tab)
                    .setIcon(icons[i])
                    .setText(titles[i]));
        }
    }
}