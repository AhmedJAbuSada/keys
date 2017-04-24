package com.keys.Hraj.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keys.R;
import com.keys.Service.FirebaseNotificationService;
import com.keys.activity.LoginActivity;
import com.keys.Hraj.Adapter.CommentsAdapter;
import com.keys.chats.model.User;
import com.keys.forceRtlIfSupported;
import com.keys.Hraj.Model.Ads;
import com.keys.Hraj.Model.Comments;
import com.keys.model.Notification;
import com.keys.model.UnSeen;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class DetailsAdsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener, SwipeRefreshLayout.OnRefreshListener {

    private EditText editMessage;
    private TextView textView;
    private long milliseconds;
    private Ads adv;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private List<Comments> commentsList;
    private CommentsAdapter commentsAdapter;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceRtlIfSupported.forceRtlIfSupported(this);

        setContentView(R.layout.activity_details_ads);
        adv = (Ads) getIntent().getSerializableExtra("adv");
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.ads_layout);
        ImageView back = (ImageView) findViewById(R.id.back);
        textView = (TextView) findViewById(R.id.textView);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(this);


        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        SliderLayout sliderLayout = (SliderLayout) findViewById(R.id.slider);
        TextView txtTitle = (TextView) findViewById(R.id.ads_txt);
        TextView txtDescription = (TextView) findViewById(R.id.ads_det);
        TextView txtUserName = (TextView) findViewById(R.id.ads_user);
        TextView txtDate = (TextView) findViewById(R.id.ads_time);
        editMessage = (EditText) findViewById(R.id.message);
        ImageView send = (ImageView) findViewById(R.id.send);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        commentsList = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(commentsList, DetailsAdsActivity.this);
        recyclerView.setAdapter(commentsAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setUpAuthListener();
        fetchUsersOnline();

        deleteUSeenComment();

        startService(new Intent(this, FirebaseNotificationService.class));

        txtTitle.setText(adv.getTitle());
        txtDescription.setText(adv.getDescription());
        txtUserName.setText(adv.getUserName());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentsAds();
            }
        });

        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            Date d = f.parse(adv.getDate());
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtDate.setText(converteTimestamp(milliseconds + ""));

        final ArrayList<String> adsArrayList = adv.getImageUrls();
        Log.e("arrayList", adsArrayList + "");

        for (int i = 0; i < adsArrayList.size(); i++) {
            Log.e("000000000", adsArrayList.get(i));
            TextSliderView textSliderView = new TextSliderView(DetailsAdsActivity.this);
            textSliderView
                    .image(adsArrayList.get(i))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView slider) {
                            Intent i = new Intent(DetailsAdsActivity.this, ImageViewerPagerActivity.class);
                            i.putExtra("IMAGES", adsArrayList);
                            i.putExtra("SELECTED", slider.getBundle().getInt("position"));

                            startActivity(i);
                        }
                    });
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putSerializable("extra", adsArrayList.get(i));
            textSliderView.getBundle()
                    .putInt("position", i);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setCurrentPosition(0, true);
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);

        Query query = FirebaseDatabase.getInstance().getReference().child("Comments").orderByChild("advId").equalTo(adv.getAdvId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getAllComments(dataSnapshot);
                recyclerView.scrollToPosition(commentsList.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        applyFont(DetailsAdsActivity.this, layout);
    }

    private void deleteUSeenComment() {

        databaseReference.child("Unseen").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(adv.getAdvId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count = 0;
                        final UnSeen unSeen = new UnSeen();
                        unSeen.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        unSeen.setAdsId(adv.getAdvId());
                        unSeen.setCount(count);
                        if (dataSnapshot.exists()) {
                            databaseReference.child("Unseen").child(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid()).child(adv.getAdvId()).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getAllComments(DataSnapshot dataSnapshot) {
        commentsList.clear();
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            Log.e("comments", singleSnapshot.getValue() + "");
            if (singleSnapshot.exists()) {
                final Comments comments = singleSnapshot.getValue(Comments.class);
                Collections.sort(commentsList, new Comparator<Comments>() {
                    @Override
                    public int compare(Comments o1, Comments o2) {
                        return (String.valueOf(o1.getDate())).compareTo((String.valueOf(o2.getDate())));
                    }
                });
                textView.setVisibility(View.GONE);
                commentsList.add(comments);
                recyclerView.smoothScrollToPosition(commentsAdapter.getItemCount() - 1);
                commentsAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            } else {
                textView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    public void applyFont(final Context context, final View v) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "frutigerltarabic_roman.ttf");
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }


    private void setUpAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {
                    logOut();
                }
            }
        };
    }

    private void fetchUsersOnline() {
        DatabaseReference databaseRef = mDatabase.getReference("User");
        Query mPostRef = databaseRef.orderByPriority().limitToLast(500);

        Log.d("dataSnapshot", String.valueOf(mPostRef));

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("dataSnapshot", String.valueOf(dataSnapshot.getValue()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("dataSnapshot", String.valueOf(databaseError));
            }
        });
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public static CharSequence converteTimestamp(String mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void addCommentsAds() {
        String message = editMessage.getText().toString().trim();
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH).format(new Date());
        userName = getSharedPreferences("myPrefs", MODE_PRIVATE).getString("userName", "");
        String adsId = adv.getAdvId();
        Random r = new Random();
        String commentId = "comment_" + r.nextInt(1000 + 1);

        if (TextUtils.isEmpty(message)) {
            editMessage.setError("يجب ادخال التعليق!");
            return;
        }
        Comments comments = new Comments(date, adsId, userName, message, commentId);
        databaseReference.child("Comments").child(commentId).setValue(comments);
        editMessage.setText("");
        addNotification();
        addUnseenMsgCountData();
    }

    public void addNotification() {
        Query query = FirebaseDatabase.getInstance().getReference().child("User");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.e("users", dataSnapshot1.getValue() + "");
                    User user = dataSnapshot1.getValue(User.class);
                    String userkey = user.getObjectId();
                    String adv_id = adv.getAdvId();
                    Log.e("key///", userkey);
                    Log.e("id///", adv_id);
                    String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH).format(new Date());
                    Notification notification = new Notification();
                    String message = "تعليق جديد من  " + userName + " \n"
                            + "  على الاعلان " + adv.getTitle();
                    String userId = mAuth.getCurrentUser().getUid();
                    notification.setMessage(message);
                    notification.setDescription(getString(R.string.app_name));
                    notification.setTimestamp(date);
                    notification.setUser_id(userId);
                    notification.setUserName(user.getFullName());
                    notification.setStatus(0);
                    notification.setType("comments");
                    notification.setAdsId(adv_id);
                    Map<String, Object> map = notification.toMap();
                    Log.d("stringmap", map.toString());
                    if (!(user.getObjectId()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        databaseReference.child("notifications").child(user.getObjectId()).push().setValue(map);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addUnseenMsgCountData() {

        Query query = FirebaseDatabase.getInstance().getReference().child("User");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.e("users", dataSnapshot1.getValue() + "");
                    final User user = dataSnapshot1.getValue(User.class);
                    final String userkey = user.getObjectId();
                    final String adv_id = adv.getAdvId();

                    Log.e("key///", userkey);
                    Log.e("id///", adv_id);
                    if (!(user.getObjectId()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        databaseReference.child("Unseen").child(user.getObjectId()).child(adv_id)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int count = 1;
                                        final UnSeen unSeen = new UnSeen();
                                        unSeen.setUserId(userkey);
                                        unSeen.setAdsId(adv_id);
                                        unSeen.setCount(count);
                                        Map<String, Object> map = unSeen.toMap();
                                        if (dataSnapshot.exists()) {
                                            int countExist = dataSnapshot.getValue(UnSeen.class).getCount();
                                            count = countExist + 1;
                                            unSeen.setCount(count);
                                            map = unSeen.toMap();
                                            databaseReference.child("Unseen").child(user.getObjectId()).child(adv_id).setValue(map);
                                        } else {
                                            databaseReference.child("Unseen").child(user.getObjectId()).child(adv_id).setValue(map);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        editMessage.setText("");
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);

                Query query = FirebaseDatabase.getInstance().getReference().child("Comments")
                        .orderByChild("advId").equalTo(adv.getAdvId());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        getAllComments(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }, 3000);
    }
}
