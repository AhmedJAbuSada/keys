package com.keys.Hraj.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keys.Interface.OnLoadMoreListener;
import com.keys.R;
import com.keys.Hraj.Activity.DetailsAdsActivity;
import com.keys.Hraj.Model.Ads;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Ads> adsArrayList;
    private Context context;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loading;
    private final int VIEW_TYPE_ITEM = 0;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public void add(Ads ads) {
        if (!containsId(adsArrayList, ads.getDate())) {
            adsArrayList.add(ads);
            Collections.sort(adsArrayList, new Comparator<Ads>() {
                @Override
                public int compare(Ads o1, Ads o2) {
                    return o2.getDate().compareTo(o1.getDate());
                }
            });
            setLoaded();
            notifyItemInserted(adsArrayList.size() - 1);
        }
    }

    public static boolean containsId(List<Ads> list, String city) {
        for (Ads object : list) {
            if (object.getDate().equals(city)) {
                return true;
            }
        }
        return false;
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        ImageView image;
        ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.ads_txt);
            image = (ImageView) view.findViewById(R.id.ads_image);
            progressBar = (ProgressBar) view.findViewById(R.id.imageProgress);
            Typeface typeface1 = Typeface.createFromAsset(view.getContext().getAssets(), "frutigerltarabic_roman.ttf");
            name.setTypeface(typeface1);
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public CustomGridAdapter(Context context, List<Ads> adsArrayList) {
        this.adsArrayList = adsArrayList;
        this.context = context;
    }

    public CustomGridAdapter(List<Ads> adsMainsList, RecyclerView recyclerView, Context context) {
        this.adsArrayList = adsMainsList;
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

    @Override
    public int getItemViewType(int position) {
        int VIEW_TYPE_LOADING = 1;
        return adsArrayList.get(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_item, parent, false);

            vh = new MyViewHolder(view);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_loading_item, parent, false);

            vh = new LoadingViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            final Ads ads = adsArrayList.get(position);
            ((MyViewHolder) holder).name.setText(ads.getTitle());
            ((MyViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
            Glide.with(context).load(ads.getImageUrls().get(0)).crossFade().thumbnail(0.5f)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            ((MyViewHolder) holder).progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(((MyViewHolder) holder).image);

            ((MyViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailsAdsActivity.class);
                    intent.putExtra("adv", ads);
                    context.startActivity(intent);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailsAdsActivity.class);
                    intent.putExtra("adv", ads);
                    context.startActivity(intent);
                }
            });
        } else {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);

        }

    }

    @Override
    public int getItemCount() {
        return adsArrayList.size();
    }


}
