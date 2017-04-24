package com.keys.chats.contacts;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.keys.R;
import com.keys.chats.ChattingActivity;
import com.keys.chats.model.User;
import com.keys.chats.utilities.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

@EActivity(R.layout.activity_contacts)
public class ContactActivity extends AppCompatActivity {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.list)
    RecyclerView recyclerView;

    @Bean
    ContactsNonMemberRecyclerViewAdapter adapter;

    LinearLayoutManager mLayoutManager;
    Realm realm;
    private ProgressDialog pDialog;
    List<User> userList = new ArrayList<>();
    List<String> phoneList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @AfterViews
    void afterView() {
        toolbar.setTitle(getString(R.string.title_contacts));
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
        inintDialog();
        getContacts();
        getAllUsersFromFirebase();
        mLayoutManager = new LinearLayoutManager(ContactActivity.this);
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter.setCanChat(false);
    }

    private void getContacts() {
        showpDialog();
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
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        phoneList = getContacts(alContacts);
        hidepDialog();
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

    private boolean isExist(String number) {
        number = number.trim();
        if (number.contains("-"))
            number = number.replace("-", "");
        if (number.startsWith("00")) {
            if (number.length() >= 13)
                number = number.substring(2);
        } else if (number.startsWith("+")) {
            if (number.length() >= 12)
                number = number.substring(1);
        }

        return phoneList.contains(number);
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

                                User person = new User(country, displayStatus, deviceToken, email, fullName,
                                        latitude, longitude, mobileNo, objectId, os, picture,
                                        "userQRCode", updatedAt, createdAt, statusId);
                                if (!isExist(mobileNo) && !objectId.equals(ChattingActivity.getUid()))
                                    userList.add(person);

                            } catch (Exception e) {
                                e.printStackTrace();
                                hidepDialog();
                            }
                        }
                        hidepDialog();
                        adapter.setItems(userList);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hidepDialog();
                    }
                });
    }


    private ProgressDialog inintDialog() {
        pDialog = new ProgressDialog(ContactActivity.this);
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
}
