package com.keys.chats.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.keys.R;
import com.keys.chats.model.Members;
import com.keys.chats.model.User;

import java.util.ArrayList;

import io.realm.RealmList;


/**
 * Created by AmalKronz on 8/11/2015.
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ItemViewHolder> {
    private SharedPreferences sp;
    private SharedPreferences.Editor spedit;
    private static final int TYPE_Item = 0;
    RealmList<Members> list;
    Context context;

    OnItemClickListener listener;

    public MemberAdapter(Context context, RealmList<Members> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//Inflating layouts
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
        return new ItemViewHolder(itemView, TYPE_Item);

    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {//Display the data at the specified position
        if (context != null) {
            holder.tv_name.setText(list.get(position).getMemberId());
            holder.iv_cancel.setOnClickListener(new View.OnClickListener() {
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
        private ImageView iv_cancel;
        private TextView tv_name;
        int viewType;

        public ItemViewHolder(final View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
//            iv_cancel = (ImageView) itemView.findViewById(R.id.iv_cancel);

        }
    }

    @Override
    public int getItemViewType(int position) {//Return the view type of the item at position
        return TYPE_Item;
    }

}
