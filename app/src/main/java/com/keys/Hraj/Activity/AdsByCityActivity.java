package com.keys.Hraj.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.keys.R;
import com.keys.Hraj.Adapter.DropDownCitiesAdapter;
import com.keys.forceRtlIfSupported;
import com.keys.Hraj.Model.Cities;

import java.util.ArrayList;
import java.util.List;

public class AdsByCityActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private ImageView back, search, tune;
    private Typeface font;
    private List<Cities> citiesList;
    private PopupWindow popupWindow;
    private double height, width;
    private AlertDialog alertDialog;
    private String city;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceRtlIfSupported.forceRtlIfSupported(this);


        setContentView(R.layout.ads_by_city_activity);

        linearLayout = (LinearLayout) findViewById(R.id.layout);
        back = (ImageView) findViewById(R.id.back);
        search = (ImageView) findViewById(R.id.search);
        tune = (ImageView) findViewById(R.id.tune_icon);

        applyFont(AdsByCityActivity.this, linearLayout);
        citiesList = new ArrayList<>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        city = getSharedPreferences("myPrefs", MODE_PRIVATE).getString("city", "");
        tune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupView = LayoutInflater.from(AdsByCityActivity.this).inflate(R.layout.citydailog, null);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(AdsByCityActivity.this);
                dialog.setView(popupView);
                applyFont(AdsByCityActivity.this, popupView);
                final EditText editDrop = (EditText) popupView.findViewById(R.id.editdrop1);

                editDrop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.drop_down_list, null);
                        RecyclerView recyclerView = (RecyclerView) popupView.findViewById(R.id.recycler_view1);
                        DropDownCitiesAdapter dropDownCitiesAdapter = new DropDownCitiesAdapter(citiesList, new DropDownCitiesAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                String city = citiesList.get(position).getName();
                                editDrop.setText(city);
                                popupWindow.dismiss();
                            }
                        });
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AdsByCityActivity.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(dropDownCitiesAdapter);

                        popupWindow = new PopupWindow(popupView, (int) (width * 0.7), (int) (height * 0.2));
                        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        popupWindow.setElevation(18f);
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.setFocusable(true);
                        popupWindow.showAsDropDown(v);
                    }

                });
                Button agree = (Button) popupView.findViewById(R.id.agree);
                agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                Button disagree = (Button) popupView.findViewById(R.id.disagree);
                disagree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog = dialog.create();
                alertDialog.show();
            }
        });
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
        }
    }

}
