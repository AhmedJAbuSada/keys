package com.keys.chats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.R;
import com.keys.activity.LoginActivity;
import com.keys.chats.chats.AddChatActivity_;
import com.keys.chats.chats.AddChatGroupActivity_;
import com.keys.chats.chats.ChatsFragment_;
import com.keys.chats.contacts.ContactsFragment_;
import com.keys.chats.groups.GroupsFragment_;
import com.keys.chats.home.HomeFragment_;
import com.keys.chats.model.GroupRealm;
import com.keys.chats.model.Members;
import com.keys.chats.model.RecentRealm;
import com.keys.chats.utilities.Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import io.realm.Realm;

@EFragment(R.layout.activity_chatting)
public class ChattingActivity extends Fragment {

//    private final static int EXTERNAL_STORAGE_PERMISSION_REQUEST = 100;

    //    @ViewById(R.id.navigation)
//    BottomNavigationView navigation;
    @ViewById(R.id.toolbar)
    LinearLayout toolbar;
    @ViewById(R.id.navigation_contacts)
    LinearLayout navigation_contacts;
    @ViewById(R.id.navigation_chats)
    LinearLayout navigation_chats;
    @ViewById(R.id.navigation_groups)
    LinearLayout navigation_groups;
    @ViewById(R.id.navigation_home)
    LinearLayout navigation_home;
    @ViewById(R.id.ic_home)
    ImageView ic_home;
    @ViewById(R.id.ic_groups)
    ImageView ic_groups;
    @ViewById(R.id.ic_chats)
    ImageView ic_chats;
    @ViewById(R.id.ic_contacts)
    ImageView ic_contacts;
    @ViewById(R.id.title_home)
    TextView title_home;
    @ViewById(R.id.title_groups)
    TextView title_groups;
    @ViewById(R.id.title_chats)
    TextView title_chats;
    @ViewById(R.id.title_contacts)
    TextView title_contacts;

    ImageView image_toolbar;
    ImageView ic_refresh;

    int menu_icon = R.drawable.item;
    ListPopupWindow popupWindowList;
    Realm realm;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @AfterViews
    void afterView() {
        inintDialog();
//        requestExternalStoragePermission();
//        title_toolbar.setText(getString(R.string.app_name));
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return;
        }

        getAllGroupssFromFirebase();
        getAllRecentFromFirebase();

//        allTabs.addTab(allTabs.newTab().setText("ONE"),true);
//        allTabs.addTab(allTabs.newTab().setText("ONE"));
//        allTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getPosition())
//                {
//                    case 0 :
////                        replaceFragment(fragmentOne);
//                        break;
//                    case 1 :
////                        replaceFragment(fragmentTwo);
//                        break;
//                }
//            }
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//            }
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });

//        navigation.getMenu().getItem(3).setChecked(true);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        makeFragment(HomeFragment_.builder().build());
        clickItem(R.id.navigation_home);
        navigation_contacts.setOnClickListener(clickListener);
        navigation_chats.setOnClickListener(clickListener);
        navigation_groups.setOnClickListener(clickListener);
        navigation_home.setOnClickListener(clickListener);
        image_toolbar = (ImageView) getActivity().findViewById(R.id.image_toolbar);
        ic_refresh = (ImageView) getActivity().findViewById(R.id.ic_refresh);

        getActivity().findViewById(R.id.add_icon).setVisibility(View.GONE);
        getActivity().findViewById(R.id.ic_group).setVisibility(View.GONE);
        getActivity().findViewById(R.id.chat_icon).setVisibility(View.GONE);
        getActivity().findViewById(R.id.refresh_icon).setVisibility(View.GONE);
        getActivity().findViewById(R.id.add_user).setVisibility(View.GONE);
        getActivity().findViewById(R.id.image_toolbar).setVisibility(View.VISIBLE);
        image_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (menu_icon) {
                    case R.drawable.ic_chat_bubble:
                        openMenu(view);
                        break;
                    case R.drawable.ic_group_add_:
//                        ContactActivity_.intent(ChattingActivity.this).start();
                        Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                        startActivity(intent);
                        break;
                    case R.drawable.ic_add_:
                        AddChatGroupActivity_.intent(getActivity()).start();
                        break;
                }
            }
        });


    }


//    @Override
//    public void onBackPressed() {
//        if (getFragmentManager().getBackStackEntryCount() == 1) {
//            finish();
//        } else {
//            super.onBackPressed();
//        }
//    }

    public void getAllGroupssFromFirebase() {
        showpDialog();
        FirebaseDatabase.getInstance().getReference().child(Constant.TABLE_GROUP)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!realm.isInTransaction())
                            realm.beginTransaction();
                        realm.delete(GroupRealm.class);

                        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                            // Person user = dataSnapshotChild.getValue(Person.class);
                            JSONObject jUserData = new JSONObject((Map) dataSnapshotChild.getValue());
                            try {
                                String name = jUserData.optString("name");
                                String objectId = jUserData.optString("objectId");
                                String userId = jUserData.optString("userId");
                                String picture = jUserData.optString("picture");
                                double createdAt = jUserData.optDouble("createdAt");
                                double updatedAt = jUserData.optDouble("updatedAt");
                                boolean deleted = jUserData.optBoolean("deleted");
                                boolean statusId = jUserData.optBoolean("statusId");
                                boolean checked = jUserData.optBoolean("checked");
                                boolean isActive = jUserData.optBoolean("isActive");
                                JSONArray members = jUserData.optJSONArray("members");
                                if (!realm.isInTransaction())
                                    realm.beginTransaction();
                                GroupRealm group = realm.createObject(GroupRealm.class);
                                group.setName(name);
                                group.setObjectId(objectId);
                                group.setUserId(userId);
                                group.setPicture(picture);
                                group.setCreatedAt(createdAt);
                                group.setUpdatedAt(updatedAt);
                                group.setDeleted(deleted);
                                group.setStatusId(statusId);
                                group.setChecked(checked);
                                group.setActive(isActive);
                                for (int i = 0; i < members.length(); i++) {
                                    Members memb = realm.createObject(Members.class);
                                    memb.setMemberId(members.optString(i));
                                    group.getMembers().add(memb);
                                }
                                realm.commitTransaction();
                            } catch (Exception e) {
                                e.printStackTrace();
                                hidepDialog();
                            }

                        }
                        hidepDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hidepDialog();
                    }
                });
    }

    public void getAllRecentFromFirebase() {
        showpDialog();
        FirebaseDatabase.getInstance().getReference().child(Constant.TABLE_RECENT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!realm.isInTransaction())
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.delete(RecentRealm.class);
                                }
                            });

                        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                            JSONObject jUserData = new JSONObject((Map) dataSnapshotChild.getValue());
                            try {
                                double lastMessageDate = jUserData.optDouble("lastMessageDate");
                                double createdAt = jUserData.optDouble("createdAt");
                                double updatedAt = jUserData.optDouble("updatedAt");
                                String groupId = jUserData.optString("groupId");
                                String userId = jUserData.optString("userId");
                                String lastMessage = jUserData.optString("lastMessage");
                                String objectId = jUserData.optString("objectId");
                                String type = jUserData.optString("type");
                                String description = jUserData.optString("description");
                                String initials = jUserData.optString("initials");
                                String picture = jUserData.optString("picture");
                                int counter = jUserData.optInt("counter");
                                boolean archived = jUserData.optBoolean("archived");
                                boolean deleted = jUserData.optBoolean("deleted");
                                JSONArray members = jUserData.optJSONArray("members");

                                realm.beginTransaction();
                                RecentRealm recent = realm.createObject(RecentRealm.class);
                                recent.setLastMessageDate(lastMessageDate);
                                recent.setCreatedAt(createdAt);
                                recent.setUpdatedAt(updatedAt);
                                recent.setGroupId(groupId);
                                recent.setUserId(userId);
                                recent.setLastMessage(lastMessage);
                                recent.setObjectId(objectId);
                                recent.setType(type);
                                recent.setDescription(description);
                                recent.setInitials(initials);
                                recent.setPicture(picture);
                                recent.setCounter(counter);
                                recent.setArchived(archived);
                                recent.setDeleted(deleted);

                                for (int i = 0; i < members.length(); i++) {
                                    Members memb = realm.createObject(Members.class);
                                    memb.setMemberId(members.optString(i));
                                    recent.getMembers().add(memb);
                                }
                                realm.commitTransaction();
                            } catch (Exception e) {
                                e.printStackTrace();
                                hidepDialog();
                            }

                        }
                        hidepDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hidepDialog();
                    }
                });
    }

    private ProgressDialog inintDialog() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.pleaseWait));

        return pDialog;
    }

    private void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
        }
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

//    private void requestExternalStoragePermission() {
//        if ((ContextCompat.checkSelfPermission(getActivity(),
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(getActivity(),
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED)) {
//
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_CONTACTS},
//                    EXTERNAL_STORAGE_PERMISSION_REQUEST);
//        }
//    }


    public static String getUid() {
        String uid = "";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            uid = user.getUid();
        return uid;
    }

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    menu_icon = R.drawable.item;
//                    image_toolbar.setImageResource(0);
//                    getFragmentManager().popBackStack();
//                    makeFragment(HomeFragment_.builder().build());
//                    return true;
//                case R.id.navigation_groups:
//                    menu_icon = R.drawable.ic_add_;
//                    image_toolbar.setImageResource(R.drawable.ic_add_);
//                    getFragmentManager().popBackStack();
//                    makeFragment(GroupsFragment_.builder().build());
//                    return true;
//                case R.id.navigation_chats:
//                    menu_icon = R.drawable.ic_chat_bubble;
//                    image_toolbar.setImageResource(R.drawable.ic_chat_bubble);
//                    getFragmentManager().popBackStack();
//                    makeFragment(ChatsFragment_.builder().build());
//                    return true;
//                case R.id.navigation_contacts:
//                    menu_icon = R.drawable.ic_group_add_;
//                    image_toolbar.setImageResource(R.drawable.ic_group_add_);
//                    getFragmentManager().popBackStack();
//                    makeFragment(ContactsFragment_.builder().build());
//                    return true;
//            }
//            return false;
//        }
//
//    };

    private void clickItem(int id_icon) {
        switch (id_icon) {
            case R.id.navigation_home:
                ic_home.setImageResource(R.drawable.ic_home);
                ic_groups.setImageResource(R.drawable.ic_users_outline);
                ic_chats.setImageResource(R.drawable.ic_chat_outline);
                ic_contacts.setImageResource(R.drawable.ic_person_outline);
                title_home.setTypeface(null, Typeface.BOLD);
                title_groups.setTypeface(null, Typeface.NORMAL);
                title_chats.setTypeface(null, Typeface.NORMAL);
                title_contacts.setTypeface(null, Typeface.NORMAL);
                break;
            case R.id.navigation_groups:
                ic_home.setImageResource(R.drawable.ic_home_outline);
                ic_groups.setImageResource(R.drawable.ic_users);
                ic_chats.setImageResource(R.drawable.ic_chat_outline);
                ic_contacts.setImageResource(R.drawable.ic_person_outline);
                title_home.setTypeface(null, Typeface.NORMAL);
                title_groups.setTypeface(null, Typeface.BOLD);
                title_chats.setTypeface(null, Typeface.NORMAL);
                title_contacts.setTypeface(null, Typeface.NORMAL);
                break;
            case R.id.navigation_chats:
                ic_home.setImageResource(R.drawable.ic_home_outline);
                ic_groups.setImageResource(R.drawable.ic_users_outline);
                ic_chats.setImageResource(R.drawable.ic_chat);
                ic_contacts.setImageResource(R.drawable.ic_person_outline);
                title_home.setTypeface(null, Typeface.NORMAL);
                title_groups.setTypeface(null, Typeface.NORMAL);
                title_chats.setTypeface(null, Typeface.BOLD);
                title_contacts.setTypeface(null, Typeface.NORMAL);
                break;
            case R.id.navigation_contacts:
                ic_home.setImageResource(R.drawable.ic_home_outline);
                ic_groups.setImageResource(R.drawable.ic_users_outline);
                ic_chats.setImageResource(R.drawable.ic_chat_outline);
                ic_contacts.setImageResource(R.drawable.ic_person_);
                title_home.setTypeface(null, Typeface.NORMAL);
                title_groups.setTypeface(null, Typeface.NORMAL);
                title_chats.setTypeface(null, Typeface.NORMAL);
                title_contacts.setTypeface(null, Typeface.BOLD);
                break;
        }

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.navigation_home:
                    menu_icon = R.drawable.item;
                    image_toolbar.setImageResource(0);
                    getFragmentManager().popBackStack();
                    makeFragment(HomeFragment_.builder().build());
                    ic_refresh.setVisibility(View.GONE);
                    clickItem(R.id.navigation_home);
                    break;
                case R.id.navigation_groups:
                    menu_icon = R.drawable.ic_add_;
                    image_toolbar.setImageResource(R.drawable.ic_add_);
                    getFragmentManager().popBackStack();
                    makeFragment(GroupsFragment_.builder().build());
                    ic_refresh.setVisibility(View.GONE);
                    clickItem(R.id.navigation_groups);
                    break;
                case R.id.navigation_chats:
                    menu_icon = R.drawable.ic_chat_bubble;
                    image_toolbar.setImageResource(R.drawable.ic_chat_bubble);
                    getFragmentManager().popBackStack();
                    makeFragment(ChatsFragment_.builder().build());
                    clickItem(R.id.navigation_chats);
                    ic_refresh.setVisibility(View.GONE);
                    break;
                case R.id.navigation_contacts:
                    menu_icon = R.drawable.ic_group_add_;
//                    ic_refresh
                    image_toolbar.setImageResource(R.drawable.ic_group_add_);
                    getFragmentManager().popBackStack();
                    makeFragment(ContactsFragment_.builder().build());
                    clickItem(R.id.navigation_contacts);
                    ic_refresh.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public void makeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openMenu(View view) {
        popupWindowList = new ListPopupWindow(getActivity());
        popupWindowList.setAnchorView(view);
        popupWindowList.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        String[] list = {getString(R.string.private_message), getString(R.string.puplic_message)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.setting, list);
        popupWindowList.setAdapter(adapter);
        popupWindowList.setContentWidth(measureContentWidth(adapter));
        popupWindowList.setModal(true);
        popupWindowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        AddChatActivity_.intent(getActivity()).start();
                        popupWindowList.dismiss();
                        break;
                    case 1:
                        AddChatGroupActivity_.intent(getActivity()).start();
                        popupWindowList.dismiss();
                        break;
                }
            }
        });
        popupWindowList.show();
    }

    private int measureContentWidth(ListAdapter listAdapter) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ListAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(getActivity());
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }
}
