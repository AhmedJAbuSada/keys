package com.keys.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.keys.Service.FirebaseNotificationService;


public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        context.startService(new Intent(context, FirebaseNotificationService.class));

    }
}
