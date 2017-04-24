package com.keys.Hraj.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.keys.MyApplication;
import com.keys.R;
import com.keys.activity.MainActivity;
import com.keys.chats.model.User;
import com.keys.forceRtlIfSupported;
import com.onesignal.OneSignal;

import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity implements LocationListener {
    private EditText editTextEmail, editTextPassword, editTextConfirmPass, editTextMobileNumber;
    private ProgressBar progressBar;
    private Typeface font;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    SharedPreferences sp;
    private String email;
    private String deviceToken;
    private String mobile, password;
    private double currentLatitude;
    private double currentLongitude;

    double lattitude;
    double longittude;
    private LocationManager locationManager;
    private Location location;
    private boolean isGPSEnabled;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 sec

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceRtlIfSupported.forceRtlIfSupported(this);

        setContentView(R.layout.register_main);

        sp = getSharedPreferences("myPrefs", MODE_PRIVATE);

        if (TextUtils.isEmpty(sp.getString("lat", ""))) {
            useWIFI();
        }
        firebaseAuth = FirebaseAuth.getInstance();
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        ImageView arrow_ = (ImageView) findViewById(R.id.arrow);

        editTextEmail = (EditText) findViewById(R.id.ed_user);
        editTextPassword = (EditText) findViewById(R.id.ed_pass);
        editTextConfirmPass = (EditText) findViewById(R.id.ed_confirm);
        editTextMobileNumber = (EditText) findViewById(R.id.ed_mobile);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        TextInputLayout usernameTxt = (TextInputLayout) findViewById(R.id.input_layout_name);
        TextInputLayout passwordTxt = (TextInputLayout) findViewById(R.id.input_layout_password);
        TextInputLayout confirm_passTxt = (TextInputLayout) findViewById(R.id.input_layout_confirm);
        TextInputLayout mobileNumberTxt = (TextInputLayout) findViewById(R.id.input_layout_mobile);

        Button register_btn = (Button) findViewById(R.id.new_btn);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        applyFont(this, layout);
        font = Typeface.createFromAsset(getAssets(), "frutigerltarabic_roman.ttf");
        usernameTxt.setTypeface(font);
        passwordTxt.setTypeface(font);
        confirm_passTxt.setTypeface(font);
        mobileNumberTxt.setTypeface(font);

        arrow_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void registerUser() {
        email = editTextEmail.getText().toString().trim();
        mobile = editTextMobileNumber.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        String confirm_password = editTextConfirmPass.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.err_msg_name));
            return;
        }

        if (!email.matches(emailPattern)) {
            editTextEmail.setError(getString(R.string.err_msg_name));
            return;
        }
        if (TextUtils.isEmpty(mobile)) {
            editTextMobileNumber.setError(getString(R.string.err_msg_mobile));
            return;
        }

        if (!isValidPhone(mobile)) {
            editTextMobileNumber.setError(getString(R.string.err_msg_correct_mobile));
            return;
        }
        if (mobile.length() < 9) {
            editTextPassword.setError(getString(R.string.minimumMobile));
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError(getString(R.string.err_msg_password));
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.minimum));
            return;
        }
        if (TextUtils.isEmpty(confirm_password)) {
            editTextConfirmPass.setError(getString(R.string.err_msg_confirm));
            return;
        }
        if (!confirm_password.equals(password)) {
            editTextConfirmPass.setError(getString(R.string.err_msg_confirm_pass));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        if (isNetworkAvailable()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
                                }
                                saveUserInformation();

                            } else
                                Toast.makeText(RegisterActivity.this, getString(R.string.fail_register), Toast.LENGTH_SHORT).show();

                        }
                    }

            );
        } else {
            Toast.makeText(RegisterActivity.this, "لا يوجد انترنت!" + "\n" +
                    "يجب الاتصال الانترنت لاكتمال عملية التسجيل", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

    }

    private static boolean isValidPhone(String phone) {
        return Pattern.compile("5\\d{8}").matcher(phone).matches();
    }

    private void saveUserInformation() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        long createdAt = System.currentTimeMillis();
        long updatedAt = System.currentTimeMillis();
        String country = "السعودية";
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                if (userId != null) {
                    deviceToken = userId;
                }
            }
        });
        String displayStatus = "";
//        String language = "ar";
//        String lastLoginDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH).format(new Date());
        final String userName = email;
        String fullName = email;
        String statusId = "متاح";
        String picture = "";
        String userORCode = "";
        final String objectId = firebaseUser.getUid();
        String os = "Android";
        String password_ = password;
        String latitude = getSharedPreferences("myPrefs", MODE_PRIVATE).getString("lat", "");
        String longitude = getSharedPreferences("myPrefs", MODE_PRIVATE).getString("lng", "");
        mobile = editTextMobileNumber.getText().toString().trim();
        mobile = "+966" + mobile;

        sp.edit().putString("mobile", mobile + "").apply();
        sp.edit().putString("userName", userName + "").apply();
        MyApplication.myPrefs.deviceToken().put(deviceToken);
        User user = new User(country, displayStatus, deviceToken, email, fullName, latitude,
                longitude, mobile, objectId, os, picture, userORCode, createdAt, updatedAt, statusId);
        databaseReference.child("User").child(objectId).setValue(user);

        databaseReference.child("User").orderByChild("objectId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("===", dataSnapshot.getValue() + "");
                        if (dataSnapshot.exists()) {
                            sp.edit().putString("mobile", mobile + "").apply();
                            sp.edit().putString("userName", userName + "").apply();
                            sp.edit().putString("userId", objectId).apply();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        sp.edit().putString("lat", currentLatitude + "").apply();
        sp.edit().putString("lng", currentLongitude + "").apply();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }


    private void useGPS() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {

            if (location == null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("GPS : ", "Enabled!");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.d("GGG", "Location not null!");
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        lattitude = location.getLatitude();
                        longittude = location.getLongitude();
                        Log.d("GGG", currentLatitude + " " + currentLongitude);

                        getSharedPreferences("myPrefs", MODE_PRIVATE).edit().putString("lat", currentLatitude + "").apply();
                        getSharedPreferences("myPrefs", MODE_PRIVATE).edit().putString("lng", currentLongitude + "").apply();

                    }
                }
            }
        } else
            showSettingsAlert();
    }

    public void useWIFI() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.i("aaa", isGPSEnabled + "");
        if (isGPSEnabled && checkConn(RegisterActivity.this)) {

            if (location == null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("WIFI : ", "Enabled!");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        Log.d("GGG", "Location not null!");
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        lattitude = location.getLatitude();
                        longittude = location.getLongitude();

                        getSharedPreferences("myPrefs", MODE_PRIVATE).edit().putString("lat", currentLatitude + "").apply();
                        getSharedPreferences("myPrefs", MODE_PRIVATE).edit().putString("lng", currentLongitude + "").apply();
                        // Toast.makeText(activity, " wifi lat: " + lat + " lng: " + lng, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        } else
            useGPS();
    }

    public boolean checkConn(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() == null)
            return false;
        if (conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        } else if (!conMgr.getActiveNetworkInfo().isConnected()) {
            return false;
        }
        return false;
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
            // ignore
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setInverseBackgroundForced(true);
        AlertDialog dialog = builder.create();
        builder.setTitle("Enable GPS");
        builder.setMessage("Enable GPS,to get your location?");
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1999);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // show it
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
