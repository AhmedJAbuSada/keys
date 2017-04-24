package com.keys.chats.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.MyApplication;
import com.keys.R;
import com.keys.activity.LoginActivity;
import com.keys.chats.chat.FullScreenImageActivity_;
import com.keys.chats.groups.GroupRecyclerViewAdapter;
import com.keys.chats.model.Group;
import com.keys.chats.model.PublicAdvs;
import com.keys.chats.utilities.Constant;
import com.keys.chats.utilities.RecyclerTouchListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

@EFragment(R.layout.fragment_home_)
public class HomeFragment extends Fragment {

    @ViewById(R.id.list)
    RecyclerView recyclerView;

    @ViewById(R.id.header)
    RecyclerViewHeader header;

    @ViewById(R.id.listH)
    RecyclerView listH;

//    @Bean
//    HomeRecyclerViewAdapter adapter;

    @Bean
    HomeAdvsRecyclerViewAdapter adapterH;

    @Bean
    GroupRecyclerViewAdapter adapter;

    LinearLayoutManager mLayoutManager;
    List<PublicAdvs> list = new ArrayList<>();
    List<Group> groupList = new ArrayList<>();
    private ProgressDialog pDialog;
    Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @AfterViews
    void afterView() {
        inintDialog();

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
            return;
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);

        header.attachTo(recyclerView);

        getAllPublicAdvsFromFirebase();
        getAllTopGroupsFromFirebase();

        listH.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), listH,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(final View view, final int position) {
//                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        i.setData(Uri.parse(list.get(position).getUrl()));
//                        startActivity(i);
                        FullScreenImageActivity_.intent(getActivity())
                                .nameUser("اعلان").urlPhotoUser(list.get(position).getUrl()).start();
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
    }

    public void getAllPublicAdvsFromFirebase() {
        showpDialog();
        FirebaseDatabase.getInstance().getReference().child(Constant.TABLE_PUBLIC_ADVS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                            JSONObject jUserData = new JSONObject((Map) dataSnapshotChild.getValue());
                            try {
                                Log.e("jUserData", MyApplication.gson.toJson(jUserData));
                                int id = jUserData.optInt("id");
                                String date1 = jUserData.optString("date1");
                                String date2 = jUserData.optString("date2");
                                String time1 = jUserData.optString("time1");
                                String time2 = jUserData.optString("time2");
                                String title = jUserData.optString("title");
                                String url = jUserData.optString("url");
                                boolean checked = jUserData.optBoolean("checked");
                                boolean isActive = jUserData.optBoolean("isActive");
                                list.add(new PublicAdvs(checked, isActive, id, date1, date2, time1, time2, title, url));
                            } catch (Exception e) {
                                e.printStackTrace();
                                hidepDialog();
                            }

                        }
                        hidepDialog();
                        LinearLayoutManager mLayoutManagerHorizontl = new LinearLayoutManager(getActivity());
                        mLayoutManagerHorizontl.setOrientation(GridLayoutManager.HORIZONTAL);
                        if (listH != null) {
                            listH.setLayoutManager(mLayoutManagerHorizontl);
                            listH.setHasFixedSize(true);
                            adapterH.setItems(list);
                            listH.setAdapter(adapterH);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hidepDialog();
                    }
                });
    }

    public void getAllTopGroupsFromFirebase() {
        showpDialog();
        FirebaseDatabase.getInstance().getReference().child(Constant.TABLE_TOP_GROUPS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
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
                                List<String> list = new ArrayList<>();
                                JSONArray members = jUserData.optJSONArray("members");
                                for (int i = 0; i < members.length(); i++) {
                                    list.add(members.optString(i));
                                }
                                groupList.add(new Group(name, objectId, picture, userId, createdAt, updatedAt, list));
                            } catch (Exception e) {
                                e.printStackTrace();
                                hidepDialog();
                            }

                        }
                        hidepDialog();
                        if (recyclerView != null) {
                            adapter.setRealm(realm);
                            adapter.setItems(groupList);
                            recyclerView.setAdapter(adapter);
                        }
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
}
