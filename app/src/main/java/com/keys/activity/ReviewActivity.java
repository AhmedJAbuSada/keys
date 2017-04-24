package com.keys.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.keys.R;
import com.keys.forceRtlIfSupported;

public class ReviewActivity extends AppCompatActivity {

    private ImageView imageView, back;
private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceRtlIfSupported.forceRtlIfSupported(this);

        setContentView(R.layout.image_preview);
        imageView = (ImageView) findViewById(R.id.preview);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.app_text);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "FrutigerLTArabic_Bold.ttf");
        title.setTypeface(typeface);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        String image = intent.getStringExtra("data");
        Bitmap bitmap = BitmapFactory.decodeFile(image);
//                intent.getParcelableExtra("data");
        imageView.setImageBitmap(bitmap);
    }
}
