package com.keys.chats.contacts;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.keys.R;
import com.keys.chats.chat.ChatActivity_;
import com.keys.chats.model.Contact;
import com.keys.chats.model.User;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_contact_details)
public class ContactDetailsActivity extends AppCompatActivity {
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.picture)
    ImageView picture;
    @ViewById(R.id.username)
    TextView username;
    @ViewById(R.id.start_chat)
    TextView start_chat;

    @Extra
    Contact contact;

    @Extra
    User contactUser;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void afterView() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getString(R.string.contact_details));
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Glide.with(ContactDetailsActivity.this).load(contact.getPicture())
                .override(100, 100)
                .fitCenter()
                .into(picture);
        username.setText(contact.getFullName());

        user = new User(contact.getCountry(), contact.getDisplayStatus(), contact.getDeviceToken(),
                contact.getEmail(), contact.getFullName(), contact.getLatitude(), contact.getLongitude(),
                contact.getMobileNo(), contact.getObjectId(), contact.getOs(), contact.getPicture(),
                contact.getUserQRCode(), contact.getUpdatedAt(), contact.getCreatedAt(), contact.getStatusId());
    }

    @Click
    void start_chat() {
        if (contactUser == null)
            ChatActivity_.intent(ContactDetailsActivity.this).user(user).start();
        else
            ChatActivity_.intent(ContactDetailsActivity.this).user(contactUser).start();
    }
}
