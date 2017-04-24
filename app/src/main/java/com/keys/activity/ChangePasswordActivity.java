package com.keys.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.keys.R;
import com.keys.forceRtlIfSupported;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private Typeface font;
String gg;
    private EditText newPassword, confirmNewPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceRtlIfSupported.forceRtlIfSupported(this);


        setContentView(R.layout.activity_change_password);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);
        ImageView back = (ImageView) findViewById(R.id.arrow);
        TextView txt = (TextView) findViewById(R.id.logo_app);

        auth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        Button changePassword = (Button) findViewById(R.id.changePass);
        newPassword = (EditText) findViewById(R.id.ed_pass);
        confirmNewPassword = (EditText) findViewById(R.id.ed_confirm);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        applyFont(ChangePasswordActivity.this, linearLayout);
        font = Typeface.createFromAsset(getAssets(), "Alakob.ttf");
        txt.setTypeface(font);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError(getString(R.string.minimum));
                        progressBar.setVisibility(View.GONE);
                    } else if (confirmNewPassword.getText().toString().trim().equals("")) {
                        confirmNewPassword.setError(getString(R.string.err_msg_confirm));
                        progressBar.setVisibility(View.GONE);
                    } else if (!confirmNewPassword.getText().toString().equals(newPassword.getText().toString())) {
                        confirmNewPassword.setError(getString(R.string.err_msg_confirm_pass));
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ChangePasswordActivity.this, getString(R.string.change_update), Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this, getString(R.string.fail_change), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError(getString(R.string.err_msg_password));
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
