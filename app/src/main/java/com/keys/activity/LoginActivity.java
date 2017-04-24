package com.keys.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.keys.Hraj.Activity.RegisterActivity;
import com.keys.MyApplication;
import com.keys.R;
import com.keys.forceRtlIfSupported;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class LoginActivity extends Activity {

    TextView logo_txt, remember_txt, create_txt, register_txt;
    EditText user_name, password;
    TextInputLayout nameTxt, passwordTxt;
    Button log_btn;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private SharedPreferences sp;
    private Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        forceRtlIfSupported.forceRtlIfSupported(this);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.logo_main);

        sp = getSharedPreferences("myPrefs", MODE_PRIVATE);
        logo_txt = (TextView) findViewById(R.id.logo_app);
        remember_txt = (TextView) findViewById(R.id.remember_text);
        register_txt = (TextView) findViewById(R.id.register_text);
        create_txt = (TextView) findViewById(R.id.create_text);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        user_name = (EditText) findViewById(R.id.txt_user);
        password = (EditText) findViewById(R.id.txt_pass);

        nameTxt = (TextInputLayout) findViewById(R.id.input_layout_name);
        passwordTxt = (TextInputLayout) findViewById(R.id.input_layout_password);
        log_btn = (Button) findViewById(R.id.log_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        applyFont(this, layout);

        Typeface font1 = Typeface.createFromAsset(getAssets(), "Alakob.ttf");
        sp.getString("mobile", "");
        logo_txt.setTypeface(font1);

        Typeface font2 = Typeface.createFromAsset(getAssets(), "frutigerltarabic_roman.ttf");
        nameTxt.setTypeface(font2);
        passwordTxt.setTypeface(font2);
        log_btn.setTypeface(font2);
        remember_txt.setTypeface(font2);
        register_txt.setTypeface(font2);
        create_txt.setTypeface(font2);

        remember_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                    @Override
                    public void idsAvailable(String userId, String registrationId) {
                        if (userId != null) {
                            MyApplication.myPrefs.deviceToken().put(userId);
                        }
                    }
                });
                String email = user_name.getText().toString();
                final String password_ = password.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    user_name.setError(getString(R.string.err_msg_name));
                    return;
                }
                if (password_.isEmpty()) {
                    password.setError(getString(R.string.err_msg_password));
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                if (isNetworkAvailable()) {
                    auth.signInWithEmailAndPassword(email, password_).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                if (password_.length() < 6) {
                                    password.setError(getString(R.string.minimum));
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                getUserData(task.getResult().getUser().getUid());

                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "لا يوجد انترنت!" + "\n" +
                            "يجب الاتصال الانترنت لاكتمال عملية التسجيل", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

        register_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getUserData(final String uid) {
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("User").orderByChild("objectId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("//// dataSnapshot", dataSnapshot.getValue() + "");
                if (dataSnapshot != null || dataSnapshot.getChildren().iterator().hasNext()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Log.i("////", data.getValue() + "");
                        JSONObject jData = new JSONObject((Map) data.getValue());
                        Log.i("////", jData + "");
                        try {
                            String objectId = jData.getString("objectId");
                            String mobileNo = jData.getString("mobileNo");
                            String countryId = jData.getString("country");
                            String deviceToken = jData.getString("deviceToken");
                            String email = jData.getString("email");
                            String displayStatus = jData.getString("displayStatus");
//                            String language = jData.getString("language");
//                            String lastLogin = jData.getString("lastLoginDate");
                            String latitude = jData.getString("latitude");
                            String longitude = jData.getString("longitude");
//                            String userName = jData.getString("userName");
                            String statusId = jData.getString("statusId");
                            String userImage = jData.getString("picture");
                            String userCode = jData.getString("userQRCode");
                            String fullName = jData.getString("fullName");
                            MyApplication.myPrefs.user().put(fullName);
                            double createdAt = jData.getDouble("createdAt");
                            double updatedAt = jData.getDouble("updatedAt");
//                            String password = jData.getString("password");
                            String os = jData.getString("os");

                            sp.edit().putString("mobileNo", mobileNo).commit();
                            sp.edit().putString("country", countryId).commit();
                            sp.edit().putString("deviceToken", deviceToken).commit();
                            sp.edit().putString("displayStatus", displayStatus).commit();
                            sp.edit().putString("email", email).commit();
//                            sp.edit().putString("language", language).commit();
//                            sp.edit().putString("lastLoginDate", lastLogin).commit();
                            sp.edit().putString("latitude", latitude).commit();
                            sp.edit().putString("longitude", longitude).commit();
                            sp.edit().putString("userName", fullName).commit();
                            sp.edit().putString("statusId", statusId).commit();
                            sp.edit().putString("picture", userImage).commit();
                            sp.edit().putString("userORCode", userCode).commit();
                            sp.edit().putString("objectId", objectId).commit();
                            sp.edit().putString("os", os).commit();
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);

            }
        });

    }

    public void applyFont(final Context context, final View v) {
        font = Typeface.createFromAsset(context.getAssets(), "frutigerltarabic_roman.ttf");
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    applyFont(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(font);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(font);
            } else if (v instanceof RadioButton) {
                ((RadioButton) v).setTypeface(font);
            } else if (v instanceof TextInputLayout) {
                ((TextInputLayout) v).setTypeface(font);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
