package com.keys.chats.chats;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.keys.R;
import com.keys.chats.model.User;

import java.util.List;


/**
 * Created by AmalKronz on 8/11/2015.
 */
public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ItemViewHolder> {
    private SharedPreferences sp;
    private SharedPreferences.Editor spedit;
    private static final int TYPE_Item = 0;
    List<User> list;
    Context context;

    //    OnChecked listener;
    OnItemClickListener listener;
    private boolean onBind;

    public FilterAdapter(Context context, List<User> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//Inflating layouts
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_contact_to_group_item, parent, false);
        return new ItemViewHolder(itemView, TYPE_Item);

    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {//Display the data at the specified position
        holder.setIsRecyclable(false);
        if (context != null) {
            holder.tv_filter.setText(list.get(position).getFullName());
            holder.cb_filter.setSelected(list.get(position).getCheck());
            if (list.get(position).getCheck()) {
//                holder.cb_filter.setEnabled(false);
                holder.cb_filter.setChecked(true);
                holder.cb_filter.setTag(list.get(position));
            }
            holder.cb_filter.setTag(list.get(position));
            holder.cb_filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {//Returns the total number of items in the data set hold by the adapter
        return list.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cb_filter;
        private TextView tv_filter;
        int viewType;

        public ItemViewHolder(final View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            tv_filter = (TextView) itemView.findViewById(R.id.name);
            cb_filter = (CheckBox) itemView.findViewById(R.id.add);

        }
    }

    @Override
    public int getItemViewType(int position) {//Return the view type of the item at position
        return TYPE_Item;
    }


}
