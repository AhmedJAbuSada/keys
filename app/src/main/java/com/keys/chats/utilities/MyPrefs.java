package com.keys.chats.utilities;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface MyPrefs {

    @DefaultString("null")
    String user();

    @DefaultString("null")
    String deviceToken();

    @DefaultBoolean(true)
    boolean isFirstLunch();

}