package com.keys.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keys.R;
import com.keys.model.StatusModel;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder> {
    private List<StatusModel> statusList;
    private OnItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;
        public RelativeLayout item;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.text_available);
            image = (ImageView) view.findViewById(R.id.image);
            item = (RelativeLayout) view.findViewById(R.id.layout);
            Typeface typeface1 = Typeface.createFromAsset(view.getContext().getAssets(), "frutigerltarabic_roman.ttf");
            title.setTypeface(typeface1);
        }
    }

    public StatusAdapter(List<StatusModel> statusList) {
        this.statusList = statusList;
    }

    public StatusAdapter(List<StatusModel> statusList, OnItemClickListener listener) {
        this.statusList = statusList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custome_dialog, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        StatusModel status = statusList.get(position);
        holder.title.setText(status.getTitle());
        holder.image.setImageResource(status.getImage());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}