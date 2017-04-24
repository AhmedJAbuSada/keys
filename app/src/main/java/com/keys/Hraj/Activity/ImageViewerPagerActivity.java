package com.keys.Hraj.Activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.keys.R;
import com.keys.Hraj.Adapter.ImagesPagerAdapter;
import com.keys.forceRtlIfSupported;

import java.util.ArrayList;

public class ImageViewerPagerActivity extends AppCompatActivity {

    ViewPager mPager;
    ImagesPagerAdapter ipa;
    //HashMap<String, String> urls;
    ArrayList<String> images;
    int currentPos;
    int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        forceRtlIfSupported.forceRtlIfSupported(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_viewer_pager);

        images = (ArrayList<String>) getIntent().getExtras().getSerializable("IMAGES");
        selected = getIntent().getExtras().getInt("SELECTED");
        mPager = (ViewPager) findViewById(R.id.pager);
        ipa = new ImagesPagerAdapter(getSupportFragmentManager(), images);
        mPager.setAdapter(ipa);
        mPager.setCurrentItem(selected);
    }


}
