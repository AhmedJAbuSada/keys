package com.keys.chats.contacts;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.R;
import com.keys.chats.ChattingActivity;
import com.keys.chats.model.Contact;
import com.keys.chats.model.User;
import com.keys.chats.utilities.Constant;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

@EFragment(R.layout.fragment_contacts)
public class ContactsFragment extends Fragment {

    @ViewById(R.id.list)
    RecyclerView recyclerView;
    @ViewById(R.id.search)
    SearchView search;

    @Bean
    ContactsRecyclerViewAdapter adapter;

    LinearLayoutManager mLayoutManager;
    Realm realm;
    private ProgressDialog pDialog;
    List<Contact> userList = new ArrayList<>();
    List<String> phoneList = new ArrayList<>();
    List<Contact> filterList = new ArrayList<>();

    ImageView ic_refresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @AfterViews
    void afterView() {
        inintDialog();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter.setCanChat(true);

        search.setActivated(true);
        search.setQueryHint(getString(R.string.search_key));
        search.onActionViewExpanded();
        search.setIconified(false);
        search.clearFocus();

//        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                adapter.getFilter().filter(newText);
//
//                return false;
//            }
//        });
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
                        filterList.add(userList.get(i));
                    }
                }
                adapter.setItems(filterList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        listContact();

        ic_refresh = (ImageView) getActivity().findViewById(R.id.ic_refresh);
        ic_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpDialog();
                new MyTask(new TaskListener() {
                    @Override
                    public void finished(boolean result) {
                        hidepDialog();
                        getAllUsersFromFirebase();
                    }
                }).execute();
            }
        });
    }

    public void listContact() {
        RealmResults<Contact> users = realm.where(Contact.class).findAll();
        for (Contact contact : users) {
            if (!ChattingActivity.getUid().equals(contact.getObjectId())) {
                if (!userList.contains(contact))
                    userList.add(contact);
            }
        }
        adapter.users = userList;
        adapter.setItems(userList);
        if (recyclerView != null)
            recyclerView.setAdapter(adapter);
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
        inintDialog();
        showpDialog();
        FirebaseDatabase.getInstance().getReference().child(Constant.TABLE_USER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (realm.isInTransaction()) {
                            realm.delete(User.class);
                            realm.delete(Contact.class);
                        }
//                        else
//                            realm.executeTransaction(new Realm.Transaction() {
//                                @Override
//                                public void execute(Realm realm) {
//                                    realm.delete(User.class);
//                                    realm.delete(Contact.class);
//                                }
//                            });
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
                                if (!realm.isInTransaction())
                                    realm.beginTransaction();
                                User person = realm.createObject(User.class);
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

                                mobileNo = FilterMobileNo(mobileNo);
                                for (int i = 0; i < phoneList.size(); i++) {
                                    if (mobileNo.equals(phoneList.get(i))
                                            && !ChattingActivity.getUid().equals(objectId)) {
//                                        Contact contact = new Contact(country, displayStatus, deviceToken, email, fullName,
//                                                latitude, longitude, mobileNo, objectId, os, picture,
//                                                "userQRCode", updatedAt, createdAt, statusId);
                                        phoneList.remove(phoneList.get(i));
//                                        userList.add(person);
                                        if (!realm.isInTransaction())
                                            realm.beginTransaction();
                                        Contact contact = realm.createObject(Contact.class);
                                        contact.setCountry(country);
                                        contact.setDisplayStatus(displayStatus);
                                        contact.setDeviceToken(deviceToken);
                                        contact.setEmail(email);
                                        contact.setFullName(fullName);
                                        contact.setLatitude(latitude);
                                        contact.setLongitude(longitude);
                                        contact.setMobileNo(mobileNo);
                                        contact.setObjectId(objectId);
                                        contact.setOs(os);
                                        contact.setPicture(picture);
                                        contact.setUserQRCode("userQRCode");
                                        contact.setUpdatedAt(updatedAt);
                                        contact.setCreatedAt(createdAt);
                                        contact.setStatusId(statusId);
                                        realm.commitTransaction();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                hidepDialog();
                            }
                        }
                        hidepDialog();
                        adapter.notifyDataSetChanged();
//                        adapter.users = userList;
//                        adapter.setItems(userList);
//                        if (recyclerView != null)
//                            recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hidepDialog();
                    }
                });
    }

    private void getContacts() {
//        showpDialog();
        ArrayList<String> alContacts = new ArrayList<>();
        ContentResolver cr = getActivity().getContentResolver();
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
//        hidepDialog();
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

    private interface TaskListener {
        void finished(boolean result);
    }

    private class MyTask extends AsyncTask<Object, Integer, Boolean> {

        private TaskListener mListener;


        public MyTask(TaskListener mListener) {
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
