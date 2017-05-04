package com.keys.chats.chat;

import android.app.Activity;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import com.keys.R;
import com.keys.Utils;
import com.keys.chats.model.GroupRealm;
import com.keys.chats.model.Members;
import com.keys.chats.model.Message;

public class GroupInfoActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_header_view)
    HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    HeaderView floatHeaderView;

    private boolean isHideToolbarView = false;
    ImageView img_header_edit;
    ImageView profile_img;
    RecyclerView rv_groupMember;
    Activity activity;
    private Realm realm;
    private String groupId;
    private String createdAt;
    private MemberAdapter memberAdapter;
    private RealmList<Members> memberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        ButterKnife.bind(this);
        activity = this;
        realm = Realm.getDefaultInstance();
        groupId = getIntent().getStringExtra("groupId");
        Log.i("/////* groupId : ", groupId + " aa");
        GroupRealm group = realm.where(GroupRealm.class)
                .equalTo("objectId", groupId).findFirst();
//        resultMessages.get(0).getCreatedAt();
        createdAt = Utils.getDate((long) group.getCreatedAt());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().
        collapsingToolbarLayout.setTitle(" ");

//        toolbarHeaderView.click(activity,group.getObjectId());
        toolbarHeaderView.bindTo(group.getName(), createdAt);
        floatHeaderView.bindTo(group.getName(), createdAt);
        img_header_edit = (ImageView) findViewById(R.id.img_header_edit);
        profile_img = (ImageView) findViewById(R.id.profile_img);

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             Utils.showCustomToast(activity, "llll");
            }
        });
        Utils.applyFont(activity, findViewById(R.id.parentPanel));
        appBarLayout.addOnOffsetChangedListener(this);
        rv_groupMember=(RecyclerView)findViewById(R.id.rv_groupMember);
        rv_groupMember.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,true));
        memberList=group.getMembers();
        memberAdapter=new MemberAdapter(activity, memberList, new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });
        rv_groupMember.setAdapter(memberAdapter);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Utils.showCustomToast(activity, "ddddd");
            return true;
        }else  if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}