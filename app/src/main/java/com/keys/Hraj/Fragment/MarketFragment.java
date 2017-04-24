package com.keys.Hraj.Fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.keys.Interface.DoSearch;
import com.keys.Interface.OnLoadMoreListener;
import com.keys.R;
import com.keys.Hraj.Activity.AddMarketActivity;
import com.keys.activity.CustomTypefaceSpan;
import com.keys.Hraj.Activity.UpdateMarketActivity;
import com.keys.Hraj.Adapter.MarketAdapter;
import com.keys.Interface.OnPositionClickListener;
import com.keys.Hraj.Model.Ads;
import com.keys.Hraj.Model.Market;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MarketFragment extends Fragment implements DoSearch, SwipeRefreshLayout.OnRefreshListener {

    private List<Market> marketList;
    protected RecyclerView recyclerView;
    private MarketAdapter marketAdapter;
    private TabLayout tabs_icon;
    DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private android.app.AlertDialog alertDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_market, container, false);
        TextView textView = (TextView) getActivity().findViewById(R.id.app_text);
        textView.setText(getString(R.string.app_name));
        textView.setGravity(Gravity.RIGHT);
        getActivity().findViewById(R.id.market_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddMarketActivity.class));
            }
        });
        marketList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        handler = new Handler();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Shops");
        recyclerView = (RecyclerView) v.findViewById(R.id.market_recycler_view);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar_main);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        final Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "frutigerltarabic_roman.ttf");

        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        marketAdapter = new MarketAdapter(marketList, recyclerView, getActivity(), new OnPositionClickListener() {
            @Override
            public void onClick(View v, final int position) {
                PopupMenu popup = new PopupMenu(getActivity(), v);
                popup.setGravity(Gravity.RIGHT);
                popup.inflate(R.menu.more_menu);

                MenuPopupHelper menuPopupHelper = new MenuPopupHelper(getActivity(), (MenuBuilder) popup.getMenu(), v);
                menuPopupHelper.setGravity(Gravity.RIGHT);
                menuPopupHelper.setForceShowIcon(true);
                final Menu m = popup.getMenu();
                for (int i = 0; i < m.size(); i++) {
                    final MenuItem mi = m.getItem(i);
                    SubMenu subMenu = mi.getSubMenu();
                    if (subMenu != null && subMenu.size() > 0) {
                        for (int j = 0; j < subMenu.size(); j++) {
                            MenuItem subMenuItem = subMenu.getItem(j);
                            applyFontToMenuItem(subMenuItem);
                        }
                    }
                    applyFontToMenuItem(mi);
                }
                Drawable yourdrawable1 = m.getItem(0).getIcon(); // change 0 with 1,2 ...
                Drawable yourdrawable2 = m.getItem(1).getIcon();
                yourdrawable1.mutate();
                yourdrawable2.mutate();
                yourdrawable1.setColorFilter(getResources().getColor(R.color.colorGrayIcon), PorterDuff.Mode.SRC_IN);
                yourdrawable2.setColorFilter(getResources().getColor(R.color.colorGrayIcon), PorterDuff.Mode.SRC_IN);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.cancel:
                                View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.cancel_dialog_activity, null);
                                popupView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                                final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity());
                                dialog.setView(popupView);
                                TextView cancel_message = (TextView) popupView.findViewById(R.id.cancel_massage);
                                Button yes = (Button) popupView.findViewById(R.id.cancel_yes);
                                Button no = (Button) popupView.findViewById(R.id.cancel_no);
                                cancel_message.setTypeface(face);
                                yes.setTypeface(face);
                                no.setTypeface(face);
                                yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Query query = FirebaseDatabase.getInstance().getReference().child("Shops").
                                                orderByChild("shopId").equalTo(marketList.get(position).getId());
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot != null || dataSnapshot.getChildren().iterator().hasNext()) {
                                                    Log.e(".....................", marketList.get(position).getName() + "");
                                                    FirebaseDatabase.getInstance().getReference().child("Shops")
                                                            .child(marketList.get(position).getId()).removeValue();
//                                                    FirebaseDatabase.getInstance().getReference().child("Ads")
//                                                            .child(marketList.get(position).getName()).removeValue();

                                                    FirebaseDatabase.getInstance().getReference().child("Ads")
                                                            .orderByChild("shopId").equalTo(marketList.get(position).getId())
                                                            .addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    Log.e("dataAds", dataSnapshot.getValue() + "");
                                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                        Ads ads = data.getValue(Ads.class);
                                                                        FirebaseDatabase.getInstance().getReference().child("Ads").child(ads.getAdvId()).removeValue();
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
                                                    marketList.remove(position);
                                                    marketAdapter.notifyDataSetChanged();

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        marketAdapter.notifyItemRemoved(position);
                                        alertDialog.dismiss();
                                    }
                                });
                                no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                                alertDialog = dialog.create();
                                alertDialog.show();
                                break;
                            case R.id.update:
                                String id = marketList.get(position).getId();
                                Log.e("id///", id);
                                Intent intent = new Intent(getActivity(), UpdateMarketActivity.class);
                                intent.putExtra("id", id);
                                getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE).edit().putString("id", id + "").commit();
                                getActivity().startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                menuPopupHelper.show();
            }
        });
        recyclerView.setAdapter(marketAdapter);
        marketAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                marketList.add(null);
//                marketAdapter.notifyItemInserted(marketList.size() - 1);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int start = marketList.size();
                        int end = start + 10;
                        search(end);
                        marketAdapter.setLoaded();
                    }
                }, 2000);
            }
        });

        if (isNetworkAvailable()) {
            search(10);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "لا يوجد انترنت!" + "\n" +
                    "يجب الاتصال الانترنت لعرض المتاجر", Toast.LENGTH_LONG).show();
        }

        tabs_icon = (TabLayout) getActivity().findViewById(R.id.tabs_icon1);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
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

        EditText search = (EditText) getActivity().findViewById(R.id.search_ed);
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
//                marketAdapter.filter((String) query);
            }
        });
        return v;
    }

    private void search(int end) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Shops").orderByKey().limitToLast(end);
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

    private void getAllTask(DataSnapshot dataSnapshot) {
        marketList.clear();
        progressBar.setVisibility(View.VISIBLE);
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            Log.e("market", singleSnapshot.getValue() + "");
            Market market = singleSnapshot.getValue(Market.class);
            if (marketAdapter == null) {
                marketAdapter = new MarketAdapter(marketList, recyclerView, getActivity());
            }
            marketList.add(market);
            swipeRefreshLayout.setRefreshing(false);
            marketAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }


    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "frutigerltarabic_roman.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void doSearch(String tag) {

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        search(10);
    }
}
