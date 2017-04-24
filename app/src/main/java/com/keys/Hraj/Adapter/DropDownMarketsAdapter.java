package com.keys.Hraj.Adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keys.R;
import com.keys.Hraj.Model.Market;

import java.util.List;

/**
 * Created by Fatima on 12/21/2016.
 */


public class DropDownMarketsAdapter extends RecyclerView.Adapter<DropDownMarketsAdapter.MyViewHolder> {
    private List<Market> statusList;
    OnItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public LinearLayout item;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.item_val);
            item = (LinearLayout) view.findViewById(R.id.layout);
            Typeface typeface1 = Typeface.createFromAsset(view.getContext().getAssets(), "frutigerltarabic_roman.ttf");
            title.setTypeface(typeface1);
        }
    }


    public DropDownMarketsAdapter(List<Market> statusList) {
        this.statusList = statusList;
    }

    public DropDownMarketsAdapter(List<Market> statusList, OnItemClickListener listener) {
        this.statusList = statusList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drop_down_list_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Market status = statusList.get(position);
        holder.title.setText(status.getName());
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
        public void onItemClick(View view, int position);
    }

}