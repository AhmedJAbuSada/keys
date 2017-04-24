package com.keys.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.util.Log;

import com.keys.R;
import com.keys.Hraj.Activity.DetailsAdsActivity;
import com.keys.Hraj.Model.Ads;
import com.keys.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class FirebaseNotificationService extends Service {

    SharedPreferences sharedPreferences;
    public FirebaseDatabase mDatabase;
    FirebaseAuth firebaseAuth;
    Context context;
    static String TAG = "FirebaseService";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        setupNotificationListener();
    }


    private boolean alReadyNotified(String key) {
        if (sharedPreferences.getBoolean(key, false)) {
            return true;
        } else {
            return false;
        }
    }


    private void saveNotificationKey(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, true);
        editor.commit();
    }

    private void setupNotificationListener() {

        mDatabase.getReference().child("notifications")
                .child(firebaseAuth.getCurrentUser().getUid())
                .orderByChild("status").equalTo(0)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //  Log.e("notification", dataSnapshot.getValue()+"");
                        if (dataSnapshot != null) {
                            Log.e("notification", dataSnapshot.getValue() + "");
                            Notification notification = dataSnapshot.getValue(Notification.class);

                            JSONObject jsonObject = new JSONObject((Map) dataSnapshot.getValue());
                            try {
                                String advId = jsonObject.getString("advId");
                                Log.e("id", advId + "");
                                notification.setAdsId(advId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //  Log.e("id",notification.getAdsId()+"");
                            if (notification.getType().equals("comments")) {
                                Log.e("notificainID", notification.getAdsId());
                                getAdv(notification, dataSnapshot.getKey());
                            }
                            //   showNotification(context, notification, dataSnapshot.getKey());
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

    private void getAdv(final Notification notification, final String key) {
        Log.e("eeeee", notification.getAdsId() + "");
        FirebaseDatabase.getInstance().getReference().child("Ads").child(notification.getAdsId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Ads ads = dataSnapshot.getValue(Ads.class);
                        showNotification(context, notification, key, ads);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent(getApplicationContext(), FirebaseNotificationService.class));
    }


    private void showNotification(Context context, Notification notification, String notification_key, Ads ads) {
        flagNotificationAsSent(notification_key);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setTicker(notification.getDescription()).setWhen(0)
                .setContentTitle(notification.getDescription())
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getMessage()))
                .setContentText(Html.fromHtml(notification.getMessage()
                ))
                .setAutoCancel(true);

        Intent backIntent = new Intent(context, DetailsAdsActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        backIntent.putExtra("adv", ads);
        Intent intent = new Intent(context, DetailsAdsActivity.class);

        /*  Use the notification type to switch activity to stack on the main activity*/
        if (notification.getType().equals("comments")) {
            intent = new Intent(context, DetailsAdsActivity.class);
        }

        final PendingIntent pendingIntent = PendingIntent.getActivities(context, 900,
                new Intent[]{backIntent}, PendingIntent.FLAG_ONE_SHOT);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(DetailsAdsActivity.class);

        mBuilder.setContentIntent(pendingIntent);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }

    private void flagNotificationAsSent(String notification_key) {
        mDatabase.getReference().child("notifications")
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(notification_key)
                .child("status")
                .setValue(1);
    }


}
