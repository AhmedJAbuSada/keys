package com.keys.activity;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.keys.R;
import com.keys.adapter.StatusAdapter;
import com.keys.forceRtlIfSupported;
import com.keys.model.StatusModel;

import java.util.ArrayList;
import java.util.List;

public class StatusActivity extends AppCompatActivity {

    private TextView new_text, current_status, determine_status;
    private ImageView more, back;
    private EditText current_ed;
    private List<StatusModel> statusList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StatusAdapter statusAdapter;
    private AlertDialog alertDialog;
    private AlertDialog alertDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceRtlIfSupported.forceRtlIfSupported(this);

        setContentView(R.layout.activity_status);


        new_text = (TextView) findViewById(R.id.app_text);
        current_status = (TextView) findViewById(R.id.current_status);
        determine_status = (TextView) findViewById(R.id.determine_status);
        current_ed = (EditText) findViewById(R.id.ed_status);
        more = (ImageView) findViewById(R.id.more_ic);
        back = (ImageView) findViewById(R.id.back);

        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "HelveticaNeueW23forSKY-Reg.ttf");
        new_text.setTypeface(typeface2);

        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "al-jazeera-arabic-regular.ttf");
        current_status.setTypeface(typeface1);
        determine_status.setTypeface(typeface1);
        current_ed.setTypeface(typeface1);

        recyclerView = (RecyclerView) findViewById(R.id.status_recycler_view);
        statusAdapter = new StatusAdapter(statusList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(StatusActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(statusAdapter);
        prepareStatusData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                new MainActivity().alertDialog.dismiss();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Typeface face = Typeface.createFromAsset(getAssets(), "al-jazeera-arabic-regular.ttf");
                View popupView = getLayoutInflater().from(StatusActivity.this).inflate(R.layout.cancel_dialog, null);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(StatusActivity.this);
                dialog.setView(popupView);
                final TextView cancel = (TextView) popupView.findViewById(R.id.cancel);
                cancel.setTypeface(face);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View popupView = getLayoutInflater().from(StatusActivity.this).inflate(R.layout.cancel_dialog_activity, null);
                        popupView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        final AlertDialog.Builder dialog = new AlertDialog.Builder(StatusActivity.this);
                        dialog.setView(popupView);
                        TextView cancel_message = (TextView) popupView.findViewById(R.id.cancel_massage);
                        TextView yes = (TextView) popupView.findViewById(R.id.cancel_yes);
                        TextView no = (TextView) popupView.findViewById(R.id.cancel_no);
                        cancel_message.setTypeface(face);
                        yes.setTypeface(face);
                        no.setTypeface(face);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog1.dismiss();
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog1 = dialog.create();
                        alertDialog1.show();
                    }
                });
                alertDialog = dialog.create();
                alertDialog.show();
                alertDialog.getWindow().setGravity(Gravity.TOP);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alertDialog.getWindow().getAttributes());
                lp.x = -170;
                lp.y = 30;
                alertDialog.getWindow().setAttributes(lp);
                alertDialog.getWindow().setLayout(300, 110);
            }
        });

    }

    private void prepareStatusData() {

        StatusModel status = new StatusModel(1, "متوفر");
        statusList.add(status);
        status = new StatusModel(2, "مشغول");
        statusList.add(status);
        status = new StatusModel(3, "في المدرسة");
        statusList.add(status);
        status = new StatusModel(4, "في السينما");
        statusList.add(status);
        status = new StatusModel(5, "في العمل");
        statusList.add(status);

        statusAdapter.notifyDataSetChanged();
    }
}
