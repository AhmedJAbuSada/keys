package com.keys.chats.chat;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keys.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_full_screen_image)
public class FullScreenImageActivity extends AppCompatActivity {

    @ViewById(R.id.imageView)
    TouchImageView mImageView;
//    @ViewById(R.id.avatar)
//    ImageView ivUser;
//    @ViewById(R.id.title)
//    TextView tvUser;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @Extra
    String nameUser;
    @Extra
    String urlPhotoUser;
    @Extra
    String urlPhotoClick;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void afterView() {
        bindViews();
        setValues();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.gc();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void bindViews() {
        progressDialog = new ProgressDialog(this);
        toolbar.setTitle(nameUser);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setValues() {
//        tvUser.setText(nameUser); // Name
//        Glide.with(this).load(urlPhotoUser).centerCrop().transform(new CircleTransform(this)).override(40, 40).into(ivUser);

        Glide.with(this).load(urlPhotoUser).asBitmap().override(640, 640).fitCenter().into(new SimpleTarget<Bitmap>() {

            @Override
            public void onLoadStarted(Drawable placeholder) {
                progressDialog.setMessage(getString(R.string.pleaseWait));
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                progressDialog.dismiss();
                mImageView.setImageBitmap(resource);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

}
