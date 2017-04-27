package com.keys.chats.chat;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.keys.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.video_player_activity)
public class VideoPlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.videoView)
    VideoView videoView;
    @Extra
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @AfterViews
    void afterView() {
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("Video");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
            if (url != null) {
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                Log.e("url", url + "");
                videoView.setOnCompletionListener(this);
                videoView.setVideoURI(Uri.parse(url));
                videoView.start();
            }
    }

    @Override
    public void onCompletion(MediaPlayer v) {
        finish();
    }

}