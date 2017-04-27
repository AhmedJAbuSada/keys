package com.keys.chats.chats;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.R;
import com.keys.chats.ChattingActivity;
import com.keys.chats.model.Group;
import com.keys.chats.model.GroupRealm;
import com.keys.chats.utilities.Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

@EFragment(R.layout.fragment_chats_)
public class ChatsFragment extends Fragment {

    @ViewById(R.id.list)
    RecyclerView recyclerView;

    @Bean
    ChatsRecyclerViewAdapter adapter;

    LinearLayoutManager mLayoutManager;
    Realm realm;
    List<Group> groupList = new ArrayList<>();
    RealmResults<GroupRealm> groupRealms;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        groupRealms = realm.where(GroupRealm.class).equalTo("members.memberId", ChattingActivity.getUid()).findAll();
        for (GroupRealm groupRealm : groupRealms) {
            List<String> memberList = new ArrayList<>();
            for (int i = 0; i < groupRealm.getMembers().size(); i++) {
                memberList.add(groupRealm.getMembers().get(i).getMemberId());
            }
            Group group = new Group(groupRealm.getName(), groupRealm.getObjectId(),
                    groupRealm.getPicture(), groupRealm.getUserId(), groupRealm.getCreatedAt(),
                    groupRealm.getUpdatedAt(), memberList);
            if (!groupList.contains(group))
                groupList.add(group);
        }
    }

    @AfterViews
    void afterView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        adapter.setRealm(realm);
        adapter.setItems(groupList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
