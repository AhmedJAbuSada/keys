package com.keys.Hraj.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keys.R;
import com.keys.Hraj.Model.Comments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {
    private List<Comments> commentsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName, message, time;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.ads_user2);
            message = (TextView) view.findViewById(R.id.text1);
            time = (TextView) view.findViewById(R.id.ads_time2);
            Typeface typeface1 = Typeface.createFromAsset(view.getContext().getAssets(), "frutigerltarabic_roman.ttf");
            userName.setTypeface(typeface1);
            message.setTypeface(typeface1);
            time.setTypeface(typeface1);

        }
    }

    public CommentsAdapter(List<Comments> commentsList, Context context) {
        this.commentsList = commentsList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_row_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Comments comments = commentsList.get(position);
        holder.userName.setText(comments.getUserName());
        holder.message.setText(comments.getMessage());

//        Locale locale = new Locale("ar");
        long milliseconds = 0;
        try {
            SimpleDateFormat  f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", new Locale("ar"));
//            f.setTimeZone(TimeZone.getTimeZone("AST"));

            Date d = f.parse(comments.getDate());
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.time.setText(converteTimestamp(milliseconds + ""));
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    private static CharSequence converteTimestamp(String mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS);
    }
}
