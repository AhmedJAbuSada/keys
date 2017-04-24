package com.keys.chats.home;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keys.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.home_item)
public class homeView extends FrameLayout {

//    @ViewById(R.id.progressBar)
//    ProgressBar progressBar;
    @ViewById(R.id.root)
    LinearLayout root;
    @ViewById(R.id.name)
    TextView name;

    Context context;

    public homeView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(String s) {
        name.setText(s);
    }

//    public void showProgress() {
//        progressBar.setVisibility(VISIBLE);
//        root.setVisibility(GONE);
//    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width =  LinearLayout.LayoutParams.MATCH_PARENT;
    }
}
