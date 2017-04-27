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

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.keys.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_full_screen_image)
public class FullScreenImageActivity extends AppCompatActivity {

    @ViewById(R.id.imageView)
    ImageView mImageView;
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
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(nameUser);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setValues() {
//        tvUser.setText(nameUser); // Name
        progressDialog.setMessage(getString(R.string.pleaseWait));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
//        Glide.with(this).load(urlPhotoUser).into(mImageView);
        Picasso.with(this).load(urlPhotoUser).into(mImageView, new Callback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
            }

            @Override
            public void onError() {
                progressDialog.dismiss();
            }
        });
//        Glide.with(this).load(urlPhotoUser)
//                //.crossFade().thumbnail(0.5f)
//                .listener(new RequestListener<String, GlideDrawable>() {
//
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        progressDialog.dismiss();
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        progressDialog.dismiss();
//
//                        return false;
//                    }
//                }).into(mImageView);
//        Glide.with(this).load(urlPhotoUser).asBitmap().override(640, 640).fitCenter().into(new SimpleTarget<Bitmap>() {
//
//            @Override
//            public void onLoadStarted(Drawable placeholder) {
//                progressDialog.setMessage(getString(R.string.pleaseWait));
//                progressDialog.show();
//                progressDialog.setCanceledOnTouchOutside(false);
//            }
//
//            @Override
//            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                progressDialog.dismiss();
//                mImageView.setImageBitmap(resource);
//            }
//
//            @Override
//            public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                e.printStackTrace();
//                progressDialog.dismiss();
//            }
//        });
    }

}
