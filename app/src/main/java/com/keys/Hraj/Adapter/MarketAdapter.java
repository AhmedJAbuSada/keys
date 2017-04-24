package com.keys.Hraj.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keys.widgets.CircleTransform;
import com.keys.Interface.OnPositionClickListener;
import com.keys.Interface.OnLoadMoreListener;
import com.keys.R;
import com.keys.Hraj.Model.Market;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MarketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Market> marketList;
    private Context context;
    OnPositionClickListener listener;
    FirebaseAuth firebaseAuth;
    private final int VIEW_TYPE_ITEM = 0;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, address;
        ImageView image, more;
        ProgressBar progressPar;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.text);
            address = (TextView) view.findViewById(R.id.address_text);
            image = (ImageView) view.findViewById(R.id.market_img);
            more = (ImageView) view.findViewById(R.id.more);
            progressPar = (ProgressBar) view.findViewById(R.id.imageProgress);
            Typeface typeface1 = Typeface.createFromAsset(view.getContext().getAssets(), "frutigerltarabic_roman.ttf");
            name.setTypeface(typeface1);
            address.setTypeface(typeface1);
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public MarketAdapter(List<Market> marketList, Context context, OnPositionClickListener listener) {
        this.marketList = marketList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.market_list_row, parent, false);

            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_loading_item, parent, false);

            vh = new LoadingViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            Market market = marketList.get(position);
            ((MyViewHolder) holder).name.setText(market.getName());
            ((MyViewHolder) holder).address.setText(market.getAddress());
            ((MyViewHolder) holder).progressPar.setVisibility(View.VISIBLE);
            Glide.with(context).load(market.getImage_market())
                    .crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(context))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            ((MyViewHolder) holder).progressPar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(((MyViewHolder) holder).image);
            firebaseAuth = FirebaseAuth.getInstance();
            if (Objects.equals(market.getUserId(), firebaseAuth.getCurrentUser().getUid())) {
                ((MyViewHolder) holder).more.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClick(v, position);
                    }
                });
            } else {
                ((MyViewHolder) holder).more.setVisibility(View.GONE);
            }
        } else {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_TYPE_LOADING = 1;
        return marketList.get(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    @Override
    public int getItemCount() {
        return marketList.size();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void add(Market market) {
        if (!containsId(marketList, market.getDate())) {
            marketList.add(market);
            Collections.sort(marketList, new Comparator<Market>() {
                @Override
                public int compare(Market o1, Market o2) {
                    return o2.getDate().compareTo(o1.getDate());
                }
            });
            notifyItemInserted(marketList.size() - 1);
        }
    }

    private static boolean containsId(List<Market> list, String city) {
        for (Market object : list) {
            if (object.getDate().equals(city)) {
                return true;
            }
        }
        return false;
    }

    public MarketAdapter(List<Market> marketList, RecyclerView recyclerView, Context context) {
        this.marketList = marketList;
        this.context = context;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    public MarketAdapter(List<Market> marketList, RecyclerView recyclerView, Context context, OnPositionClickListener listener) {
        this.marketList = marketList;
        this.context = context;
        this.listener = listener;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }
}
