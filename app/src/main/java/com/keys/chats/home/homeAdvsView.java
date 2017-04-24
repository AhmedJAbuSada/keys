package com.keys.chats.home;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.keys.R;
import com.keys.chats.model.PublicAdvs;
import com.bumptech.glide.Glide;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.home_advs_item)
public class homeAdvsView extends FrameLayout {

    //    @ViewById(R.id.progressBar)
//    ProgressBar progressBar;
    @ViewById(R.id.root)
    LinearLayout root;
    @ViewById(R.id.url)
    ImageView url;

    Context context;

    public homeAdvsView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(PublicAdvs s) {
        if (!s.getUrl().equals(""))
            Glide.with(context).load(s.getUrl())
                    .override(100, 100)
                    .fitCenter()
                    .into(url);
    }

//    public void showProgress() {
//        progressBar.setVisibility(VISIBLE);
//        root.setVisibility(GONE);
//    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        ViewGroup.LayoutParams layoutParams = getLayoutParams();
//        layoutParams.width =  LinearLayout.LayoutParams.MATCH_PARENT;
//    }
}
