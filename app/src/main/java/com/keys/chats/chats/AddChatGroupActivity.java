package com.keys.chats.chats;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.MyApplication;
import com.keys.R;
import com.keys.chats.ChattingActivity;
import com.keys.chats.chat.ChatActivity_;
import com.keys.chats.model.Group;
import com.keys.chats.model.GroupRealm;
import com.keys.chats.model.Members;
import com.keys.chats.model.User;
import com.keys.chats.utilities.Constant;
import com.keys.emoji.EmojiconRecentsManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.realm.Realm;

@EActivity(R.layout.activity_add_chat_group)
public class AddChatGroupActivity extends AppCompatActivity {
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.list)
    RecyclerView recyclerView;
    @ViewById(R.id.search)
    SearchView search;
    @ViewById(R.id.group_name)
    EditText group_name;

//    @Bean
//    ChatGroupRecyclerViewAdapter adapter;
    FilterAdapter adapter;
    private DatabaseReference mFirebaseDatabaseReference;
    LinearLayoutManager mLayoutManager;
    Realm realm;
    private ProgressDialog pDialog;
    List<User> userList = new ArrayList<>();
    List<String> phoneList = new ArrayList<>();
    Map<String, Boolean> list = new HashMap<>();
    List<String> checklist = new ArrayList<>();
    List<User> filterList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @AfterViews
    void afterView() {
        toolbar.setTitle(getString(R.string.add_chat_group));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        search.setActivated(true);
        search.setQueryHint(getString(R.string.search_key));
        search.onActionViewExpanded();
        search.setIconified(false);
        search.clearFocus();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
             filterList.clear();
                for (int i = 0; i < userList.size(); i++) {
                    if ((userList.get(i).getFullName().toUpperCase()).contains(newText.toString().toUpperCase())) {
//                        Log.i("/////",userList.get(i).getCheck()+" hhh");
                        filterList.add(userList.get(i));
//                        Log.i("/////",filterList.get(i).getCheck()+" aaa");
                    }
                }
//                adapter.setItems(filterList);
//                recyclerView.setAdapter(adapter);
                setRVAdapter(filterList);
                // adapter.getFilter().filter(newText);

                return true;
            }
        });

        inintDialog();
        showpDialog();
        new MyTask(new TaskListener() {
            @Override
            public void finished(boolean result) {
                hidepDialog();
                getAllUsersFromFirebase();
            }
        }).execute();

        mLayoutManager = new LinearLayoutManager(AddChatGroupActivity.this);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void setRVAdapter(final List<User> filterList) {
        adapter = new FilterAdapter(this, filterList, new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                User filterClass = filterList.get(position);
                CheckBox cb = (CheckBox) v;
                filterClass = (User) cb.getTag();
                filterClass.setCheck(cb.isChecked());
                if (filterClass.getCheck()) {
                    checklist.add(filterClass.getObjectId());
                } else {
                    checklist.remove(filterClass.getObjectId());
                }
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (!TextUtils.isEmpty(group_name.getText().toString()) && !checklist.isEmpty())
                    writeNewGroup();
                else {
                    if (TextUtils.isEmpty(group_name.getText().toString()))
                        Toast.makeText(AddChatGroupActivity.this, getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
                    if (checklist.isEmpty())
                        Toast.makeText(AddChatGroupActivity.this, getString(R.string.list_empty), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // [START basic_write]
    private void writeNewGroup() {
        List<String> members = new ArrayList<>();
//        for (String key : list.keySet()) {
//            if (list.get(key))
//                members.add(key);
//        }
        for (int i=0;i<checklist.size();i++){
            members.add(checklist.get(i));
        }
        members.add(getUid());
        long time = System.currentTimeMillis();
        String groupId = getGroupID();
        String name = group_name.getText().toString();
        Group group = new Group(name, groupId, "", getUid(), time, time, members);
        mFirebaseDatabaseReference.child(Constant.TABLE_GROUP).child(groupId).setValue(group);
        if (!realm.isInTransaction())
            realm.beginTransaction();
        GroupRealm groupm = realm.createObject(GroupRealm.class);
        groupm.setName(name);
        groupm.setObjectId(groupId);
        groupm.setUserId(getUid());
        for (int i = 0; i < members.size(); i++) {
            Members memb = realm.createObject(Members.class);
            memb.setMemberId(members.get(i));
            groupm.getMembers().add(memb);
        }
        realm.commitTransaction();
        ChatActivity_.intent(AddChatGroupActivity.this).group(group).start();
        finish();
    }

    public String getUid() {
        String uid = "";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            uid = user.getUid();
        return uid;
    }

    public String getGroupID() {
        int length = 24;
        Random random = new Random(System.currentTimeMillis());
        String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890-_";
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return token.toString();
    }

    private void getContacts() {
        ArrayList<String> alContacts = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            do {

                if (Integer.parseInt(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    assert pCur != null;
                    if (pCur.moveToFirst()) {
                        do {
                            String contactNumber = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
                            alContacts.add(contactNumber);
                            break;
                        } while (pCur.moveToNext());
                    }
                    pCur.close();
                    Log.e("COntacts", MyApplication.gson.toJson(alContacts));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        phoneList = getContacts(alContacts);
    }

    public List<String> getContacts(List<String> mobileNo) {
        List<String> listNo = new ArrayList<>();
        for (String number : mobileNo) {
            number = number.trim();
            if (number.contains("-"))
                number = number.replace("-", "");
            if (number.startsWith("00")) {
                if (number.length() >= 13)
                    listNo.add(number.substring(2));
            } else if (number.startsWith("+")) {
                if (number.length() >= 12)
                    listNo.add(number.substring(1));
            } else {
                if (number.length() >= 11)
                    listNo.add(number);
            }
        }

        return listNo;
    }

    private String FilterMobileNo(String number) {
        number = number.trim();
        if (number.contains("-"))
            number = number.replace("-", "");
        if (number.startsWith("00")) {
            if (number.length() >= 13)
                number = number.substring(2);
        } else if (number.startsWith("+")) {
            if (number.length() >= 12)
                number = number.substring(1);
        } /*else {
            if (number.length() >= 11)
                number = number;
        }*/
        return number;
    }

    public void getAllUsersFromFirebase() {
        showpDialog();
        FirebaseDatabase.getInstance().getReference().child(Constant.TABLE_USER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                            JSONObject jUserData = new JSONObject((Map) dataSnapshotChild.getValue());
                            try {
                                String country = jUserData.optString("country");
                                String displayStatus = jUserData.optString("displayStatus");
                                String deviceToken = jUserData.optString("deviceToken");
                                String email = jUserData.optString("email");
                                String fullName = jUserData.optString("fullName");
                                String latitude = jUserData.optString("latitude");
                                String longitude = jUserData.optString("longitude");
                                String mobileNo = jUserData.optString("mobileNo");
                                String objectId = jUserData.optString("objectId");
                                String os = jUserData.optString("os");
                                String picture = jUserData.optString("picture");
//                                String userQRCode = jUserData.optString("userQRCode");
                                double updatedAt = jUserData.optDouble("updatedAt");
                                double createdAt = jUserData.optDouble("createdAt");
                                String statusId = jUserData.optString("statusId");
                                mobileNo = FilterMobileNo(mobileNo);

                                for (int i = 0; i < phoneList.size(); i++) {
                                    if (mobileNo.equals(phoneList.get(i)) && !ChattingActivity.getUid().equals(objectId)) {
                                        User person = new User(country, displayStatus, deviceToken, email, fullName,
                                                latitude, longitude, mobileNo, objectId, os, picture,
                                                "userQRCode", updatedAt, createdAt, statusId);
                                        person.setCheck(false);
                                        userList.add(person);
//                                        list.put(person.getObjectId(), false);
                                        realm.beginTransaction();
                                        person = realm.createObject(User.class);
                                        person.setCountry(country);
                                        person.setDisplayStatus(displayStatus);
                                        person.setDeviceToken(deviceToken);
                                        person.setEmail(email);
                                        person.setFullName(fullName);
                                        person.setLatitude(latitude);
                                        person.setLongitude(longitude);
                                        person.setMobileNo(mobileNo);
                                        person.setObjectId(objectId);
                                        person.setOs(os);
                                        person.setPicture(picture);
                                        person.setUserQRCode("userQRCode");
                                        person.setUpdatedAt(updatedAt);
                                        person.setCreatedAt(createdAt);
                                        person.setStatusId(statusId);
                                        realm.commitTransaction();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                hidepDialog();
                            }
                        }
                        hidepDialog();
//                        adapter.setList(list);
//                        adapter.users = userList;
//                        adapter.setItems(userList);
//                        recyclerView.setAdapter(adapter);
                        setRVAdapter(userList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hidepDialog();
                    }
                });
    }


    private ProgressDialog inintDialog() {
        pDialog = new ProgressDialog(AddChatGroupActivity.this);
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

    private interface TaskListener {
        void finished(boolean result);
    }

    private class MyTask extends AsyncTask<Object, Integer, Boolean> {

        private TaskListener mListener;


        MyTask(TaskListener mListener) {
            super();
            this.mListener = mListener;
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            // long running background operation
            getContacts();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mListener.finished(result);
            super.onPostExecute(result);
        }
    }
}
