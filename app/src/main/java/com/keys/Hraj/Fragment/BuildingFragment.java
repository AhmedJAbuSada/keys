package com.keys.Hraj.Fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keys.DatabaseSQLite.DBHandler;
import com.keys.Interface.DoSearch;
import com.keys.Interface.OnLoadMoreListener;
import com.keys.R;
import com.keys.Hraj.Adapter.CustomGridAdapter;
import com.keys.Hraj.Model.Ads;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuildingFragment extends Fragment implements DoSearch, SwipeRefreshLayout.OnRefreshListener {

    private TabLayout tabs_icon;
    private ArrayList<Ads> adsMainsList;
    private CustomGridAdapter adapter;
    private ArrayList<Ads> adList;
    private RecyclerView recyclerView;
    private DBHandler db;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private Handler handler;
    private RelativeLayout relativeLayout;
    private Query query;
    private ValueEventListener value;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.card_recycler_view);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_main);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.no_data_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        handler = new Handler();

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        adsMainsList = new ArrayList<>();
        adapter = new CustomGridAdapter(adsMainsList, recyclerView, getActivity());
        recyclerView.setAdapter(adapter);

        tabs_icon = (TabLayout) getActivity().findViewById(R.id.tabs_icon1);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                adsMainsList.add(null);
                //adapter.notifyItemInserted(adsMainsList.size() - 1);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int start = adsMainsList.size();
                        int end = start + 10;
                        search(end);
                        adapter.setLoaded();
                    }
                }, 500);
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

        if (isNetworkAvailable()) {
            search(10);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "لا يوجد انترنت!" + "\n" +
                    "يجب الاتصال الانترنت لعرض الاعلانات", Toast.LENGTH_LONG).show();
        }
        return v;
    }

    private void getAllTask(DataSnapshot dataSnapshot) {
        adsMainsList.clear();
        progressBar.setVisibility(View.VISIBLE);
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            Log.e("ads", singleSnapshot.getValue() + "");
            Ads ads = singleSnapshot.getValue(Ads.class);
            if (adapter == null) {
                adapter = new CustomGridAdapter(adsMainsList, recyclerView, getActivity());
            }
            adapter.add(ads);
            swipeRefreshLayout.setRefreshing(false);
            // recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            query.removeEventListener(value);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void doSearch(String tag) {
        db = new DBHandler(getActivity());
        if (tag.equals(db.getAllCities().get(0).getName())) {
            adapter = new CustomGridAdapter(getActivity(), adsMainsList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            adList = new ArrayList<>();
            if (adsMainsList.size() > 0) {
                for (Ads object : adsMainsList) {
                    if (object.getCity().equals(tag)) {
                        adList.add(object);
                    }
                }
            }
            if (adList.size() > 0) {
                adapter = new CustomGridAdapter(getActivity(), adList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "لا يوجد اعلانات في هذه المدينة!", Toast.LENGTH_SHORT).show();
                adapter = new CustomGridAdapter(getActivity(), adsMainsList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            adapter.notifyDataSetChanged();
        }
    }

    public static boolean containsId(List<Ads> list, String city) {
        for (Ads object : list) {
            if (object.getCity().equals(city)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        search(10);
    }

    public void search(int limit) {
        query = FirebaseDatabase.getInstance().getReference().child("Ads").orderByChild("department").equalTo("مواد البناء").limitToLast(limit);
        value = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    getAllTask(dataSnapshot);
                else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    applyFont(getActivity(), relativeLayout);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addListenerForSingleValueEvent(value);
    }

    public void applyFont(final Context context, final View v) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "frutigerltarabic_roman.ttf");
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    applyFont(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(font);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(font);
            } else if (v instanceof RadioButton) {
                ((RadioButton) v).setTypeface(font);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

}
