package com.keys.chats.chats;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.MyApplication;
import com.keys.R;
import com.keys.chats.ChattingActivity;
import com.keys.chats.model.Group;
import com.keys.chats.utilities.Constant;

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
    String name_chat;
    public chatView(Context context) {
        super(context);
        this.context = context;
    }

    public void bind(Group s) {
        if (MyApplication.myPrefs.user().get().equals(s.getName())) {
            String objId;
            for (int i = 0; i < s.getMembers().size(); i++) {
                if (!s.getMembers().get(i).equals(ChattingActivity.getUid())) {
                    objId = s.getMembers().get(i);
                    getUser(objId);
                }
            }
        }else{
            name.setText(s.getName());
        }

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

    private void getUser(String objectId) {
        FirebaseDatabase.getInstance().getReference().child(Constant.TABLE_USER)
                .child(objectId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.e("dataSnapshot", MyApplication.gson.toJson(dataSnapshot.child("fullName").getValue())
//                                + "     " + MyApplication.gson.toJson(dataSnapshot.getValue()));
                        name_chat = String.valueOf(dataSnapshot.child("fullName").getValue().toString());
                        name.setText(name_chat);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
