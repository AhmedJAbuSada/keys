package com.keys.Hraj.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.keys.Interface.OnLoadMoreListener;
import com.keys.R;
import com.keys.Hraj.Activity.AddAdsActivity;
import com.keys.Hraj.Adapter.AdsMainAdapter;
import com.keys.Hraj.Model.Ads;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainAdsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<Ads> adsMainsList;
    private AdsMainAdapter adsMainAdapter;
    private TabLayout tabs_icon;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_ads, container, false);
        getActivity().findViewById(R.id.add_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddAdsActivity.class));
            }
        });

        TextView textView = (TextView) getActivity().findViewById(R.id.app_text);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_main);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);

        textView.setText(getString(R.string.app_name));
        textView.setGravity(Gravity.RIGHT);
        handler = new Handler();
        adsMainsList = new ArrayList<Ads>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Ads");

        recyclerView = (RecyclerView) v.findViewById(R.id.main_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adsMainAdapter = new AdsMainAdapter(adsMainsList, recyclerView, getActivity());
        recyclerView.setAdapter(adsMainAdapter);

        adsMainAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                adsMainsList.add(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int start = adsMainsList.size();
                        int end = start + 34;
                        search(end);
                        adsMainAdapter.setLoaded();
//                        adsMainAdapter.notifyItemInserted(adsMainsList.size() - 1);
                    }
                }, 2000);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (isNetworkAvailable()) {
            search(3);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "لا يوجد انترنت!" + "\n" +
                    "يجب الاتصال الانترنت لعرض الاعلانات", Toast.LENGTH_LONG).show();
        }

        tabs_icon = (TabLayout) getActivity().findViewById(R.id.tabs_icon1);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean hideToolBar = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                hideToolBar = dy > 0;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (hideToolBar) {
                    tabs_icon.setVisibility(View.GONE);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                } else {
                    tabs_icon.setVisibility(View.VISIBLE);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();

                }
            }
        });

        return v;
    }


    private void getAllTask(DataSnapshot dataSnapshot) {
        adsMainsList.clear();
        progressBar.setVisibility(View.VISIBLE);

        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            Log.e("ads", singleSnapshot.getValue() + "");
            Ads ads = singleSnapshot.getValue(Ads.class);
            if (adsMainAdapter == null) {
                adsMainAdapter = new AdsMainAdapter(adsMainsList, recyclerView, getActivity());
            }
            adsMainAdapter.add(ads);
            swipeRefreshLayout.setRefreshing(false);
            //recyclerView.smoothScrollToPosition(adsMainAdapter.getItemCount() - 1);
            adsMainAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        search(10);
    }

    public void search(int limit) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Ads").orderByChild("date").limitToLast(limit);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getAllTask(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
