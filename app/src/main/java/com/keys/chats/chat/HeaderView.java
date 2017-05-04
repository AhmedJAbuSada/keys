package com.keys.chats.chat;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keys.R;
import com.keys.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HeaderView extends LinearLayout{

    @Bind(R.id.header_view_title)
    TextView title;

    @Bind(R.id.header_view_sub_title)
    TextView subTitle;

//    @Bind(R.id.img_header_edit)
//    ImageView img_header_edit;


    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void bindTo(String title) {
        bindTo(title, "");
    }

    public void bindTo(String title, String subTitle) {
        hideOrSetText(this.title, title);
        hideOrSetText(this.subTitle, subTitle);
    }

    private void hideOrSetText(TextView tv, String text) {
        if (text == null || text.equals(""))
            tv.setVisibility(GONE);
        else
            tv.setText(text);
    }


//    public void click(final Activity activity, String objectId) {
//        this.img_header_edit.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Utils.showCustomToast(activity,"rrrr");
//            }
//        });
//    }
}
