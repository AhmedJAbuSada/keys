package com.keys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by HSM on 1/16/2015.
 */
public class Utils {
    public static String FONT_NAME = "frutigerltarabic_roman.ttf";
    static Typeface font;

    public static void applyFont(final Context context, final View v) {
        font = Typeface.createFromAsset(context.getAssets(),FONT_NAME);
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
    public static String currentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sp = new SimpleDateFormat("dd-MM-yyyy");
        String date = sp.format(c.getTime());
        return date;
    }

    public static boolean compareDate(String date2) {
        Calendar c = Calendar.getInstance();
//        SimpleDateFormat sp = new SimpleDateFormat("dd-MM-yyyy");
        String date1 = currentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date dateToday = null;
        Date dateInput = null;
        try {
            dateToday = sdf.parse(date1);
            dateInput = sdf.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean d = dateToday.after(dateInput) || dateToday.equals(dateInput);
        Log.i("/////**- ", d + "");
        return d;
    }

    public static int random4() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 1000 + r.nextInt(1000));
    }

    public static int calAge(String age) {

        String[] x = age.split("-");
        Log.i("////,", x[0]);
        int year = Integer.parseInt(x[2]);
        int month = Integer.parseInt(x[1]);
        int day = Integer.parseInt(x[0]);
        Calendar cal = Calendar.getInstance();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, month, day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
        if (a < 0)
            throw new IllegalArgumentException("Age < 0");
        return a;

    }

    public static int random9() {
        Random r = new Random(System.currentTimeMillis());
        return ((1 + r.nextInt(2)) * 100000000 + r.nextInt(100000000));
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void setupUI(final Activity activity, View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        hideSoftKeyboard(activity);
                    } catch (Exception e) {
                    }
                    return false;
                }

            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(activity, innerView);
            }
        }
    }

    public static boolean isOnline(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static void showCustomToast(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
//        View view = toast.getView();
//        view.setBackgroundColor(Color.RED);
//        TextView text = (TextView) view.findViewById(android.R.id.message);
        /*here you can do anything with text*/
//        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showSweetAlert(Context context, String msg) {
//        new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
//                .setTitleText("البوابة")
//                .setContentText(msg)
//                .setCustomImage(R.drawable.logo_icon)
//                .show();
    }

    public static String listToCsv(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String str : list) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append(str);
        }
        return sb.toString();
    }

    public static boolean hasNewBill() {


        return false;
    }

//    public static void call(Context context, String str) {
//        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + str)));
//
//    }

    public static void sendEmail(Context context, String email) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        //i.putExtra(Intent.EXTRA_SUBJECT, subject);
        //i.putExtra(Intent.EXTRA_TEXT, body);
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void openWeb(Context context, String url) {
        Uri uri = null;
        try {
            uri = Uri.parse(url);
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void setStrictMode() {
        // Activate StrictMode
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                // alternatively .detectAll() for all detectable problems
                .penaltyLog()
                .penaltyDeath()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                // alternatively .detectAll() for all detectable problems
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

    public static String getHtml(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("<HTML><HEAD><meta charset=\"UTF-8\" /></HEAD><body dir=\"rtl\">");
        sb.append(str);
        sb.append("</body></HTML>");
        Log.d("FORMATTED HTML", sb.toString());
        return sb.toString();
    }


}
