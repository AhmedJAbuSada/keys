package com.keys.Hraj.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keys.widgets.CircleTransform;
import com.keys.Interface.OnLoadMoreListener;
import com.keys.R;
import com.keys.Hraj.Activity.DetailsAdsActivity;
import com.keys.Hraj.Model.Ads;
import com.keys.model.UnSeen;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class AdsMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Ads> adsMainsList;
    private Context context;
    private final int VIEW_TYPE_ITEM = 0;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Ads adv;

    public void add(Ads ads) {
        if (!containsId(adsMainsList, ads.getDate())) {
            adsMainsList.add(ads);
            Collections.sort(adsMainsList, new Comparator<Ads>() {
                @Override
                public int compare(Ads o1, Ads o2) {
                    return o2.getDate().compareTo(o1.getDate());
                }
            });
            notifyItemInserted(adsMainsList.size() - 1);
        }
    }

    private static boolean containsId(List<Ads> list, String city) {
        for (Ads object : list) {
            if (object.getCity().equals(city)) {
                return true;
            }
        }
        return false;
    }

    public AdsMainAdapter(List<Ads> adsMainsList, RecyclerView recyclerView, Context context) {
        this.adsMainsList = adsMainsList;
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
        return adsMainsList.get(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.main_list_row, parent, false);

            vh = new MyViewHolder(v);
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

            final Ads adsMain = adsMainsList.get(position);
            ((MyViewHolder) holder).name_market.setText(adsMain.getTitle());
            ((MyViewHolder) holder).name_user.setText(adsMain.getUserName());

            FirebaseDatabase.getInstance().getReference().child("Unseen")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("advId").equalTo(adsMain.getAdvId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("dataUnseen", dataSnapshot.getValue() + "");
                            int totalCount = 0;
                            int count = 0;
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Log.e("data unseen", data.getValue() + "");
                                UnSeen unSeen = data.getValue(UnSeen.class);
                                count = unSeen.getCount();
                                totalCount = totalCount + count;
                            }
                            if (count > 0) {
                                ((MyViewHolder) holder).count.setVisibility(View.VISIBLE);
                                ((MyViewHolder) holder).count.setText(Integer.toString(count));
                            } else {
                                ((MyViewHolder) holder).count.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            int width = display.getWidth();
            ((MyViewHolder) holder).image.getLayoutParams().height = (int) (width * 0.23);
            ((MyViewHolder) holder).image.getLayoutParams().width = (int) (width * 0.23);

            ((MyViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
            Glide.with(context).load(adsMain.getImageUrls().get(0))
                    .crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(context))
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
                    })
                    .into(((MyViewHolder) holder).image);


            SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            long milliseconds = 0;
            try {
                Date d = f.parse(adsMain.getDate());
                milliseconds = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ((MyViewHolder) holder).time.setText(converteTimestamp(milliseconds + ""));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailsAdsActivity.class);
                    intent.putExtra("adv", adsMain);
                    context.startActivity(intent);
                }
            });
        } else {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return adsMainsList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name_market, name_user, time, count;
        ImageView image;
        ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            name_market = (TextView) view.findViewById(R.id.text);
            name_user = (TextView) view.findViewById(R.id.ads_user);
            time = (TextView) view.findViewById(R.id.ads_time);
            image = (ImageView) view.findViewById(R.id.market_img);
            progressBar = (ProgressBar) view.findViewById(R.id.imageProgress);
            count = (TextView) view.findViewById(R.id.tab_badge);
            Typeface typeface1 = Typeface.createFromAsset(view.getContext().getAssets(), "frutigerltarabic_roman.ttf");
            name_user.setTypeface(typeface1);
            name_market.setTypeface(typeface1);
            count.setTypeface(typeface1);
            time.setTypeface(typeface1);

        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

//    public AdsMainAdapter(List<Ads> adsMainsList, Context context) {
//        this.adsMainsList = adsMainsList;
//        this.context = context;
//    }

    private static CharSequence converteTimestamp(String mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS);
    }
}
