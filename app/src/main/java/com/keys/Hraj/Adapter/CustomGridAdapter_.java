package com.keys.Hraj.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.keys.R;
import com.keys.Hraj.Activity.DetailsAdsActivity;
import com.keys.Hraj.Model.Ads;
import com.bumptech.glide.Glide;

import java.util.List;

public class CustomGridAdapter_ extends RecyclerView.Adapter<CustomGridAdapter_.MyViewHolder> {
    private List<Ads> adsArrayList;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        ImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.ads_txt);
            image = (ImageView) view.findViewById(R.id.ads_image);
            Typeface typeface1 = Typeface.createFromAsset(view.getContext().getAssets(), "frutigerltarabic_roman.ttf");
            name.setTypeface(typeface1);
        }
    }


    public CustomGridAdapter_(Context context, List<Ads> adsArrayList) {
        this.adsArrayList = adsArrayList;
        this.context = context;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_item, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            final Ads ads = adsArrayList.get(position);
            holder.name.setText(ads.getTitle());
            Glide.with(context).load(ads.getImageUrls().get(0)).crossFade().thumbnail(0.5f).into(holder.image);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailsAdsActivity.class);
                    intent.putExtra("adv", ads);
                    context.startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return adsArrayList.size();
    }


}
