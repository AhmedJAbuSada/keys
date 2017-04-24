package com.keys.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.keys.DatabaseSQLite.DBHandler;
import com.keys.R;
import com.keys.forceRtlIfSupported;
import com.keys.Hraj.Model.Cities;
import com.keys.Hraj.Model.Departments;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SplashActivity extends Activity {
    TextView logo_txt;
    private DBHandler dbHandler;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceRtlIfSupported.forceRtlIfSupported(this);

        setContentView(R.layout.splash_activity);

        logo_txt = (TextView) findViewById(R.id.logo_text);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Alakob.ttf");
        logo_txt.setTypeface(typeface);

        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(5 * 1000);
                    getData();
                    if (TextUtils.isEmpty(getSharedPreferences("myPrefs", MODE_PRIVATE).getString("userId", ""))) {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    } else {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    }
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        background.start();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void getData() {
        Query queryDepartment = FirebaseDatabase.getInstance().getReference()
                .child("Departments");
        Query queryCity = FirebaseDatabase.getInstance().getReference()
                .child("Cities");
        dbHandler = new DBHandler(this);

        queryDepartment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Log.e("ddaddd", data.getValue() + "");
                        int key = Integer.parseInt(data.getKey());
                        JSONObject jsonObject = new JSONObject((Map) data.getValue());
                        String name = jsonObject.getString("DeptName");
                        int isActive = Integer.parseInt(jsonObject.getString("IsActive"));
                        Log.i("///data", key + " : " + name + " : " + isActive);

                        Departments departments1 = new Departments();
                        departments1.setId(key);
                        departments1.setName(name);
                        departments1.setIsActive(isActive);
                        dbHandler.addDepartments(departments1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        queryCity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        int key = Integer.parseInt(data.getKey());
                        JSONObject jsonObject = new JSONObject((Map) data.getValue());
                        String name = jsonObject.getString("CityName");
                        int isActive = Integer.parseInt(jsonObject.getString("IsActive"));
                        Log.i("///city", key + " : " + name + " : " + isActive);

                        Cities cities = new Cities();
                        cities.setId(key);
                        cities.setName(name);
                        cities.setIsActive(isActive);
                        dbHandler.addCities(cities);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
