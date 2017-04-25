package com.keys.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.keys.Hraj.Fragment.FragmentTwo;
import com.keys.Interface.DoSearch;
import com.keys.R;
import com.keys.Utils;
import com.keys.adapter.StatusAdapter;
import com.keys.chats.ChattingActivity_;
import com.keys.chats.model.User;
import com.keys.chats.utilities.Constant;
import com.keys.forceRtlIfSupported;
import com.keys.fragment.AccountFragment;
import com.keys.fragment.AvailableFragment;
import com.keys.fragment.LogoutFragment;
import com.keys.fragment.SettingFragment;
import com.keys.model.StatusModel;
import com.keys.model.UnSeen;
import com.keys.widgets.CircleTransform;
import com.keys.widgets.myBadgeView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;

import static com.keys.R.id.tabLayout;

//import com.app.fatima.keys.Chatting.Fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentTransaction ft;
    private DrawerLayout mDrawerLayout;
    Toolbar toolbar;
    private NavigationView navigationView;

    private StatusAdapter statusAdapter;
    private List<StatusModel> statusList = new ArrayList<>();
    private List<StatusModel> settingList = new ArrayList<>();

    private TextView txtName;
    private TextView txtStatus;
    private ImageView imgProfile;
    private ImageView search_cancel;
    private ImageView img_available;
    ImageView image_toolbar;
    ImageView ic_refresh;
    private EditText search_input, name_input, status_input;
    private RelativeLayout search_layout, toolbar_menu;
    private ProgressBar progressBar;
    public static int navItemIndex = 0;

    private static final String TAG_AVAILABLE = "available";
    private static final String TAG_CHANGE = "change";
    private static final String TAG_SETTINGS = "settings";
    private static final String TAG_LOGOUT = "logout";
    public static String CURRENT_TAG = TAG_AVAILABLE;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final int REQUEST_CODE_PERMISSION1 = 1000;
    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private String[] activityTitles;

    private Handler mHandler;
    RecyclerView recyclerView;
    public AlertDialog alertDialog;

    Fragment fragment;
//    private Fragment mainFragment;
//    private FragmentTwo fragmentTwo;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private GoogleApiClient client;
    StorageReference storageReference;
    private byte[] byte_arr;
    Intent cameraIntent;
    PopupWindow popupWindow;
    String mobileNo;
    private Uri downLoadUri;
    private SharedPreferences sp;
    private myBadgeView badge;
    myBadgeView badge1;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        forceRtlIfSupported.forceRtlIfSupported(this);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale("ar");
        res.updateConfiguration(conf, dm);

        setContentView(R.layout.activity_main);

        image_toolbar = (ImageView) findViewById(R.id.image_toolbar);
        ic_refresh = (ImageView) findViewById(R.id.ic_refresh);

        mHandler = new Handler();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        checkWritingPermission();
//        checkWritingPermission_();
        askForPermissions(new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.READ_CONTACTS,
                        android.Manifest.permission.WRITE_CONTACTS,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CAMERA_PERMISSIONS);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        sp = getSharedPreferences("myPrefs", MODE_PRIVATE);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        auth.addAuthStateListener(authListener);

        mobileNo = sp.getString("mobile", "");
//        String countryId = sp.getString("countryId", "");
//        String deviceToken = sp.getString("deviceToken", "");
        final String displayStatus = sp.getString("displayStatus", "");
//        String email = sp.getString("email", "");
//        String language = sp.getString("language", "");
//        String lastLogin = sp.getString("lastLogin", "");
//        String lastPassword = sp.getString("lastPassword", "");
//        String latitude = sp.getString("latitude", "");
//        String longitude = sp.getString("longitude", "");
        final String userName = sp.getString("userName", "");
//        String registrationDate = sp.getString("registrationDate", "");
//        String statusId = sp.getString("statusId", "");
//        final String userImage = sp.getString("userImage", "");
//        String userCode = sp.getString("userCode", "");
//        String typeId = sp.getString("typeId", "");
//        String key = sp.getString("key", "");
//        String os = sp.getString("os", "");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        TextView app_name = (TextView) findViewById(R.id.app_text);
        app_name.setText(getString(R.string.app_name));
        app_name.setGravity(Gravity.RIGHT);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ImageView menu = (ImageView) findViewById(R.id.menu);
        ImageView search = (ImageView) findViewById(R.id.search);
        ImageView search_back = (ImageView) findViewById(R.id.search_back);
        search_cancel = (ImageView) findViewById(R.id.cancel);
        search_layout = (RelativeLayout) findViewById(R.id.search_layout);
        toolbar_menu = (RelativeLayout) findViewById(R.id.toolbar_menu);
        search_input = (EditText) findViewById(R.id.search_ed);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        final Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            final MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }

        Fragment currentFragment = getActiveFragment();
        if (currentFragment instanceof DoSearch) {
            ((DoSearch) currentFragment).doSearch(search_input.getText().toString().trim());
        }
        View navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtName.setText(userName);

        txtStatus = (TextView) navHeader.findViewById(R.id.status);
        if (!TextUtils.isEmpty(displayStatus)) {
            txtStatus.setText(displayStatus);
        } else {
            txtStatus.setHint(getString(R.string.write_status));
            txtStatus.setHintTextColor(Color.WHITE);
        }
        name_input = (EditText) navHeader.findViewById(R.id.hidden_edit_view);
        status_input = (EditText) navHeader.findViewById(R.id.hidden_edit_status);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        img_available = (ImageView) navHeader.findViewById(R.id.img_available);
        progressBar = (ProgressBar) navHeader.findViewById(R.id.progress);
        if (isNetworkAvailable()) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("00000", dataSnapshot.getValue() + "");
                    User user = dataSnapshot.getValue(User.class);
                    sp.edit().putString("mobile", user.getMobileNo()).commit();
                    sp.edit().putString("countryId", user.getCountry()).commit();
                    sp.edit().putString("deviceToken", user.getDeviceToken()).commit();
                    sp.edit().putString("displayStatus", user.getDisplayStatus()).commit();
                    sp.edit().putString("email", user.getEmail()).commit();
//                    sp.edit().putString("language", user.getLanguage()).commit();
//                    sp.edit().putString("lastLogin", user.getLastLoginDate()).commit();
                    sp.edit().putString("latitude", user.getLatitude()).commit();
                    sp.edit().putString("longitude", user.getLongitude()).commit();
                    sp.edit().putString("fullName", user.getFullName()).commit();
                    sp.edit().putString("statusId", String.valueOf(user.getStatusId())).commit();
                    sp.edit().putString("userImage", user.getPicture()).commit();
                    sp.edit().putString("userQRCode", user.getUserQRCode()).commit();
                    sp.edit().putString("key", user.getObjectId()).commit();
                    sp.edit().putString("os", user.getOs()).commit();

                    txtName.setText(user.getFullName());
                    if (!TextUtils.isEmpty(user.getDisplayStatus())) {
                        txtStatus.setText(user.getDisplayStatus());
                    } else {
                        txtStatus.setHint(getString(R.string.write_status));
                        txtStatus.setHintTextColor(Color.WHITE);
                    }
                    if (!TextUtils.isEmpty(user.getPicture())) {
                        progressBar.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext())
                                .load(user.getPicture()).crossFade().thumbnail(0.5f)
                                .bitmapTransform(new CircleTransform(getApplicationContext()))
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(imgProfile);
                    } else {
                        Glide.with(getApplicationContext())
                                .load(R.drawable.user_default_avatar).crossFade()
                                .thumbnail(0.5f).bitmapTransform(new CircleTransform(getApplicationContext()))
                                .listener(new RequestListener<Integer, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(imgProfile);

                    }

                    MenuItem mi = m.getItem(0);
                    mi.setTitle(user.getStatusId());
                    if (TextUtils.equals(user.getStatusId(), "متاح")) {
                        mi.setIcon(R.drawable.ic_visible);
                        img_available.setImageResource(R.drawable.ic_visible);
                    } else if (TextUtils.equals(user.getStatusId(), "مشغول")) {
                        mi.setIcon(R.drawable.ic_busy);
                        img_available.setImageResource(R.drawable.ic_busy);
                    } else if (TextUtils.equals(user.getStatusId(), "غير مرئي")) {
                        mi.setIcon(R.drawable.ic_unvisible);
                        img_available.setImageResource(R.drawable.ic_unvisible);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(MainActivity.this, "لا يوجد انترنت!" + "\n" +
                    "يجب الاتصال الانترنت لاكتمال عرض البيانات", Toast.LENGTH_LONG).show();
        }


       Utils.applyFont(MainActivity.this, mDrawerLayout);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "FrutigerLTArabic_Bold.ttf");
        app_name.setTypeface(typeface);
        prepareStatusData();
        prepareSettingData();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar_menu.setVisibility(View.GONE);
                search_layout.setVisibility(View.VISIBLE);
                search_input.requestFocus();
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(search_input,
                        InputMethodManager.SHOW_IMPLICIT);
            }
        });

        search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar_menu.setVisibility(View.VISIBLE);
                search_layout.setVisibility(View.GONE);
                search_input.clearFocus();
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(search_input.getWindowToken(), 0);
                search_input.setText("");
            }
        });

        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_cancel.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_input.setText("");
            }
        });

        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "frutigerltarabic_roman.ttf");
        txtName.setTypeface(typeface1);
        txtStatus.setTypeface(typeface1);
        status_input.setTypeface(typeface1);
        name_input.setTypeface(typeface1);
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.change_img, null);
                popupView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                RelativeLayout camera_layout, image_layout, cancel_layout;
                TextView camera_txt, image_txt, cancel_txt, title;

                camera_layout = (RelativeLayout) popupView.findViewById(R.id.camera_layout);
                image_layout = (RelativeLayout) popupView.findViewById(R.id.gallery_layout);
                cancel_layout = (RelativeLayout) popupView.findViewById(R.id.cancel_layout);

                camera_txt = (TextView) popupView.findViewById(R.id.camera);
                image_txt = (TextView) popupView.findViewById(R.id.gallery);
                cancel_txt = (TextView) popupView.findViewById(R.id.cancel_text);
                title = (TextView) popupView.findViewById(R.id.img_title);

                Typeface typeface = Typeface.createFromAsset(getAssets(), "frutigerltarabic_roman.ttf");
                camera_txt.setTypeface(typeface);
                image_txt.setTypeface(typeface);
                cancel_txt.setTypeface(typeface);
                title.setTypeface(typeface);

                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                cancel_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                camera_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        checkWritingPermission();
                        startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                        popupWindow.dismiss();
                    }
                });
                image_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, SELECT_PICTURE);
                        popupWindow.dismiss();
                    }
                });
                if (!isDeviceSupportCamera()) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry! Your device doesn't support camera",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(v, Gravity.BOTTOM, v.getBottom(), v.getBottom());

                popupWindow.showAsDropDown(v);
            }
        });
        loadNavHeader();
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_AVAILABLE;
            loadHomeFragment();
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSlidingMenu();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        TabLayout tabs = (TabLayout) findViewById(tabLayout);

        TabLayout.Tab tab = tabs.newTab();
        tab.setCustomView(R.layout.tab_badge);
        TextView tab_text_1 = (TextView) tab.getCustomView().findViewById(R.id.tab_text);
        tab_text_1.setTypeface(typeface1);
        tab_text_1.setText("الحراج");

        TabLayout.Tab tab1 = tabs.newTab();
        tab1.setCustomView(R.layout.tab_badge);
        TextView tab_text_2 = (TextView) tab1.getCustomView().findViewById(R.id.tab_text);
        tab_text_2.setTypeface(typeface1);
        tab_text_2.setText("المحادثات");


        badge = new myBadgeView(this, tab.getCustomView().findViewById(R.id.tab_badge));
        badge1 = new myBadgeView(this, tab1.getCustomView().findViewById(R.id.tab_badge));

        FirebaseDatabase.getInstance().getReference().child("Unseen")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int totalCount = 0;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            UnSeen unSeen = data.getValue(UnSeen.class);
                            int count = unSeen.getCount();
                            totalCount = totalCount + count;
                        }
                        badge.updateTabBadge(totalCount);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


//        mainFragment = new MainFragment();

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabs.addTab(tab, false);
        tabs.addTab(tab1, true);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "frutigerltarabic_roman.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                image_toolbar.setVisibility(View.GONE);
                ic_refresh.setVisibility(View.GONE);
                fragment = new FragmentTwo();
                replaceFragment(fragment);
                break;
            case 1:
                fragment = ChattingActivity_.builder().build();
                replaceFragment(fragment);
                break;
        }
    }

    public Fragment getActiveFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager()
                .getBackStackEntryCount() - 1).getName();
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void loadNavHeader() {
        //   txtName.setText(sp.getString("userName",""));

        txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_input.setVisibility(View.VISIBLE);
                name_input.setText(txtName.getText());
                txtName.setVisibility(View.GONE);
            }
        });
        name_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String user_name = name_input.getText().toString();
                txtName.setText(user_name);
            }
        });
        name_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name_input.getVisibility() == View.VISIBLE) {
                    name_input.setVisibility(View.VISIBLE);
                    txtName.setVisibility(View.GONE);
                } else {
                    name_input.setVisibility(View.GONE);
                    txtName.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                //
                //
            }
        });
        name_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    name_input.setVisibility(View.GONE);
                    txtName.setVisibility(View.VISIBLE);
                    String user_name = name_input.getText().toString();
                    if (!TextUtils.isEmpty(user_name)) {
                        if (isNetworkAvailable()) {
                            databaseReference.child("userName").setValue(user_name);
                        } else {
                            mDrawerLayout.closeDrawer(navigationView);

                            Toast.makeText(MainActivity.this, "لا يوجد انترنت!" + "\n" +
                                    "يجب الاتصال الانترنت لاكتمال اضافة اسمك", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        txtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_input.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(txtStatus.getText().toString())) {
                    status_input.setHint(getString(R.string.write_status));
                } else {
                    status_input.setText(txtStatus.getText());
                }
//                txtStatus.setText(!TextUtils.isEmpty(displayStatus) ? displayStatus : getString(R.string.write_status));
                txtStatus.setVisibility(View.GONE);
            }
        });
        status_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtStatus.setText(status_input.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String status = status_input.getText().toString();
                txtStatus.setText(status);

            }
        });
        status_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    status_input.setVisibility(View.GONE);
                    txtStatus.setVisibility(View.VISIBLE);
                    String status = status_input.getText().toString();
                    if (!TextUtils.isEmpty(status)) {
                        if (isNetworkAvailable())
                            databaseReference.child("displayStatus").setValue(status);
                        else Toast.makeText(MainActivity.this, "لا يوجد انترنت!" + "\n" +
                                "يجب الاتصال الانترنت لاكتمال إضافة حالتك", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                return false;
            }
        });
        status_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status_input.getVisibility() == View.VISIBLE) {
                    status_input.setVisibility(View.VISIBLE);
                    txtStatus.setVisibility(View.GONE);
                } else {
                    status_input.setVisibility(View.GONE);
                    txtStatus.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    private void loadHomeFragment() {
        selectNavMenu();
        setToolbarTitle();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            if (CURRENT_TAG.equals(TAG_AVAILABLE)) {
            } else
                mDrawerLayout.closeDrawer(navigationView);
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                getHomeFragment();
                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return new AvailableFragment();
            case 1:
                return new AccountFragment();
            case 2:
                return new SettingFragment();
            case 3:
                return new LogoutFragment();
            default:
                return new AvailableFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }


    private void setUpNavigationView() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_available:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_AVAILABLE;
                        View popupView = LayoutInflater.from(MainActivity.this).inflate(R.layout.custome_dialog_list, null);
                        popupView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setView(popupView);
                        recyclerView = (RecyclerView) popupView.findViewById(R.id.recycler_view1);
                        statusAdapter = new StatusAdapter(statusList, new StatusAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String status = statusList.get(position).getTitle();
                                int image = statusList.get(position).getImage();
                                item.setIcon(image);
                                img_available.setImageResource(image);
                                sp.edit().putInt("image", image).commit();
                                databaseReference.child("statusId").setValue(status);
                                item.setTitle(status);
                                alertDialog.dismiss();
                            }
                        });
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(statusAdapter);
                        alertDialog = dialog.create();
                        alertDialog.show();
                        Display display = getWindowManager().getDefaultDisplay();
                        int height = display.getHeight();
                        int width = display.getWidth();
                        alertDialog.getWindow().setLayout((int) (width * 0.7), (int) (height * 0.33));

                        break;
                    case R.id.nav_profile:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_CHANGE;
                        startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_logout:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_LOGOUT;
                        signOut();
                        break;
                    default:
                        navItemIndex = 0;
                }
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                item.setChecked(true);

                loadHomeFragment();
                return true;
            }
        });
    }

    private void toggleSlidingMenu() {
        if (mDrawerLayout.isDrawerOpen(navigationView)) {
            mDrawerLayout.closeDrawer(navigationView);
        } else {
            mDrawerLayout.openDrawer(navigationView);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        boolean shouldLoadHomeFragOnBackPress = true;
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_AVAILABLE;
                loadHomeFragment();
                return;
            }
        }
        Log.e("CONT", getSupportFragmentManager().getBackStackEntryCount() + "  " + fragment.getTag());
        if (getFragmentManager().getBackStackEntryCount() == 0) finish();
        super.onBackPressed();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        auth.addAuthStateListener(authListener);
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK
                && null != data) {
            if (isNetworkAvailable()) {
                progressBar.setVisibility(View.VISIBLE);
                Uri selectedImage = data.getData();
                String imageName = "ProfileImage_" + auth.getCurrentUser().getUid() + ".jpg";
                final StorageReference filePath = storageReference.child("UsersProfile").child("Images").child(imageName);
                filePath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downLoadUri = taskSnapshot.getDownloadUrl();
                        Log.e("path", downLoadUri + "");
                        Glide.with(getApplicationContext()).load(downLoadUri)
                                .crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getApplicationContext()))
                                .listener(new RequestListener<Uri, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(imgProfile);
                        if (downLoadUri != null) {
                            FirebaseDatabase.getInstance().getReference().
                                    child("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())//.setValue("userImage",downLoadUri);
                                    .child("picture").setValue(downLoadUri + "");

                        }

                    }
                });
            } else {
//                Toast.makeText(MainActivity.this, "لا يوجد انترنت!" + "\n" +
//                        "يجب الاتصال الانترنت لاكتمال إضافة الصورة", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                && null != data) {
            if (isNetworkAvailable()) {
                progressBar.setVisibility(View.VISIBLE);
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    byte_arr = bytes.toByteArray();
                    //  String image_str = Base64.encodeToString(byte_arr, 0);
                    fo.write(byte_arr);
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final StorageReference filePath = storageReference.child("UsersProfile").child("Images").child(auth.getCurrentUser().getUid() + ".jpg");
                filePath.putBytes(byte_arr).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downLoadUri = taskSnapshot.getDownloadUrl();
                        Log.e("path", downLoadUri + "");
                        Glide.with(getApplicationContext()).load(downLoadUri)
                                .crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getApplicationContext()))
                                .listener(new RequestListener<Uri, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).into(imgProfile);
                        if (downLoadUri != null) {
                            FirebaseDatabase.getInstance().getReference().
                                    child("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())//.setValue("userImage",downLoadUri);
                                    .child("picture").setValue(downLoadUri + "");

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

            }

        } else {
//            Toast.makeText(MainActivity.this, "لا يوجد انترنت!" + "\n" +
//                    "يجب الاتصال الانترنت لاكتمال إضافة الصورة", Toast.LENGTH_SHORT).show();
//            progressBar.setVisibility(View.GONE);
        }
    }

//    private void checkWritingPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION1);
//        }
//    }
//
//    private void checkWritingPermission_() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_PERMISSION);
//        }
//    }

    private boolean isDeviceSupportCamera() {
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    private void prepareStatusData() {

        StatusModel status = new StatusModel(1, "متاح", R.drawable.ic_visible);
        statusList.add(status);
        status = new StatusModel(2, "مشغول", R.drawable.ic_busy);
        statusList.add(status);
        status = new StatusModel(3, "غير مرئي", R.drawable.ic_unvisible);
        statusList.add(status);

    }

    private void prepareSettingData() {

        StatusModel status = new StatusModel(1, "مجموعة جديدة");
        settingList.add(status);
        status = new StatusModel(2, " رسالة جماعية جديدة");
        settingList.add(status);
        status = new StatusModel(3, "الحالة ");
        settingList.add(status);
        status = new StatusModel(4, "الإعدادات");
        settingList.add(status);

    }

    //sign out method
    public void signOut() {
        auth.signOut();
        sp.edit().clear().commit();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected final void askForPermissions(String[] permissions, int requestCode) {
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissionsToRequest.toArray(new String[permissionsToRequest.size()]), requestCode);
        }
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        if (requestCode == REQUEST_CODE_PERMISSION) {
//            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                AlertDialog alertDialog = new AlertDialog.Builder(
//                        MainActivity.this).create();
//                alertDialog.setTitle("Alert Dialog");
//                alertDialog.setMessage("هل تسمح لهذا التطبيق بقراءة جهات الاتصال الخاصة بك");
//
//                alertDialog.setButton("نعم", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                alertDialog.show();
//            }
//        }
//        if (requestCode == REQUEST_CODE_PERMISSION1) {
//            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                AlertDialog alertDialog = new AlertDialog.Builder(
//                        MainActivity.this).create();
//                alertDialog.setTitle("Alert Dialog");
//                alertDialog.setMessage("هل تسمح لهذا التطبيق باستخدام الكمرا");
//
//                alertDialog.setButton("نعم", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                alertDialog.show();
//            }
//        }
    }

}
