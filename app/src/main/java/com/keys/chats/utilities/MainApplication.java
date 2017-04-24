/*
package com.app.fatima.keys.chats.utilities;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.keyschat.R;
import com.onesignal.OneSignal;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

@EApplication
public class MainApplication extends Application{
    public static Gson gson;

    @Pref
    public static MyPrefs_ myPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.font_name))
                .setFontAttrId(R.attr.fontPath)
                .build());
        init();

    }

    void init(){
        Realm.init(this);
        MultiDex.install(this);
        OneSignal.startInit(this).init();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }
}
*/
