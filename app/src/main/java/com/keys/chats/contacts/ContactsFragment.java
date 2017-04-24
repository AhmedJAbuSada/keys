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

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);

                return false;
            }
        });

        listContact();
    }

    public void listContact() {
        RealmResults<Contact> users = realm.where(Contact.class).findAll();
        for (Contact contact : users) {
            if (!ChattingActivity.getUid().equals(contact.getObjectId())) {
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
}
