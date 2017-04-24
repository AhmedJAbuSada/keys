package com.keys.Hraj.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.keys.Hraj.Fragment.ImageFragment;

import java.util.ArrayList;

public class ImagesPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<String> items;
    public ImagesPagerAdapter(FragmentManager fm, ArrayList<String> objects) {
        super(fm);
        items = objects;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("///",items.get(position));
        return  ImageFragment.newInstance(items.get(position), "");
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
