package com.keys.chats.contacts;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keys.R;
import com.keys.chats.model.Contact;
import com.keys.chats.model.User;
import com.bumptech.glide.Glide;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.contact_item)
public class contactView extends FrameLayout {

    //    @ViewById(R.id.progressBar)
//    ProgressBar progressBar;
    @ViewById(R.id.root)
    LinearLayout root;
    @ViewById(R.id.name)
    TextView name;
    @ViewById(R.id.profile_pic)
    ImageView profile_pic;

    Context context;

    public contactView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(Contact s) {
        name.setText(s.getFullName());
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
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
    }
}
