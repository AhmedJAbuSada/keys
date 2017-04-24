package com.keys.activity;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.keys.R;
import com.keys.forceRtlIfSupported;

public class Profile extends AppCompatActivity {

    TextView title, change_img, text;
    EditText profile_txt, mobile_no, status_txt;
    ImageView done, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceRtlIfSupported.forceRtlIfSupported(this);

        setContentView(R.layout.activity_profile);


        done = (ImageView) findViewById(R.id.done);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.app_text);
        change_img = (TextView) findViewById(R.id.change_img);
        text = (TextView) findViewById(R.id.text2);

        profile_txt = (EditText) findViewById(R.id.txt_user);
        mobile_no = (EditText) findViewById(R.id.ed_mobile);
        status_txt = (EditText) findViewById(R.id.ed_status);


        Typeface typeface = Typeface.createFromAsset(getAssets(), "HelveticaNeueW23forSKY-Reg.ttf");
        title.setTypeface(typeface);

        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "al-jazeera-arabic-regular.ttf");
        change_img.setTypeface(typeface2);
        profile_txt.setTypeface(typeface2);
        status_txt.setTypeface(typeface2);
        mobile_no.setTypeface(typeface2);
        text.setTypeface(typeface2);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
