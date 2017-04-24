package com.keys.chats.groups;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keys.R;
import com.keys.chats.model.Group;
import com.bumptech.glide.Glide;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.group_item)
public class groupView extends FrameLayout {

//    @ViewById(R.id.progressBar)
//    ProgressBar progressBar;
    @ViewById(R.id.root)
    LinearLayout root;
    @ViewById(R.id.name)
    TextView name;
    @ViewById(R.id.recent)
    TextView recent;
    @ViewById(R.id.unseen)
    TextView unseen;
    @ViewById(R.id.profile_pic)
    ImageView profile_pic;

    Context context;

    public groupView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(Group s) {
        name.setText(s.getName());
        if (s.getPicture() != null && !s.getPicture().equals(""))
            Glide.with(context).load(s.getPicture())
                    .override(100, 100)
                    .fitCenter()
                    .into(profile_pic);
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
