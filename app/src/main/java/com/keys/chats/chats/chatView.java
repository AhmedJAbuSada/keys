package com.keys.chats.chats;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.keys.R;
import com.keys.chats.ChattingActivity;
import com.keys.chats.model.Group;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.chat_item)
public class chatView extends FrameLayout {

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

    public chatView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(Group s) {
        name.setText(s.getName());
//        if (s.getMembers().size() > 2)
//            name.setText(s.getName());
//        else {
//            String nam = "";
//            for (int i = 0; i < s.getMembers().size(); i++) {
//                if (!s.getMembers().get(i).equals(ChattingActivity.getUid()))
//                    nam = s.getMembers().get(i);
//            }
//            name.setText(nam);
//        }
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
