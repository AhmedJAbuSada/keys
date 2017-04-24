package com.keys.Hraj.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.keys.R;

/**
 * Created by Fatima on 1/16/2017.
 */
public class CustomGrid extends BaseAdapter {
    private Context context;
    private final String[] txt;
    private final int[] ImageId;

    public CustomGrid(String[] txt, int[] imageId, Context context) {
        this.txt = txt;
        ImageId = imageId;
        this.context = context;
    }

    @Override
    public int getCount() {
        return txt.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = new View(context);
            grid = inflater.inflate(R.layout.ads_item, null);
            TextView txt_ = (TextView) grid.findViewById(R.id.ads_txt);
            ImageView img = (ImageView) grid.findViewById(R.id.ads_image);
            Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "frutigerltarabic_roman.ttf");
            txt_.setTypeface(typeface1);
            txt_.setText(txt[position]);
            img.setImageResource(ImageId[position]);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}
