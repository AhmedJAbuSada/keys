package com.keys.Hraj.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keys.widgets.CircleTransform;
import com.keys.R;
import com.keys.forceRtlIfSupported;
import com.keys.Hraj.Model.Market;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AddMarketActivity extends AppCompatActivity {
    private Typeface font;
    private ImageView market_img;
    private EditText market_name, market_address, market_phone, market_description, market_url;
    private FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String name, address, phone, description, url, date, userId;
    private int isActive;
    Intent cameraIntent;
    PopupWindow popupWindow;
    private ProgressBar progressBar;


    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final int SELECT_PICTURE = 1;
    private byte[] byte_arr;
    private Uri downLoadUri;
    private String shopId;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceRtlIfSupported.forceRtlIfSupported(this);

        setContentView(R.layout.activity_add_market);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        LinearLayout market_layout = (LinearLayout) findViewById(R.id.market_layout);
        ImageView back = (ImageView) findViewById(R.id.back);
        market_img = (ImageView) findViewById(R.id.market_img);
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        TextInputLayout textInputLayout1 = (TextInputLayout) findViewById(R.id.input_layout_address);
        TextInputLayout textInputLayout2 = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        TextInputLayout textInputLayout3 = (TextInputLayout) findViewById(R.id.input_layout_des);
        TextInputLayout input_layout_url = (TextInputLayout) findViewById(R.id.input_layout_url);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        market_name = (EditText) findViewById(R.id.market_name);
        market_address = (EditText) findViewById(R.id.address);
        market_phone = (EditText) findViewById(R.id.mobile);
        market_description = (EditText) findViewById(R.id.description);
        market_url = (EditText) findViewById(R.id.url);

        Button add_market = (Button) findViewById(R.id.save);


        applyFont(AddMarketActivity.this, market_layout);

        font = Typeface.createFromAsset(getAssets(), "frutigerltarabic_roman.ttf");
        textInputLayout.setTypeface(font);
        textInputLayout1.setTypeface(font);
        textInputLayout2.setTypeface(font);
        textInputLayout3.setTypeface(font);
        input_layout_url.setTypeface(font);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        market_img.setOnClickListener(new View.OnClickListener() {
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
                        checkWritingPermission();
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

        add_market.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewMarket();
            }
        });
    }

    public void addNewMarket() {
        name = market_name.getText().toString().trim();
        address = market_address.getText().toString().trim();
        phone = market_phone.getText().toString().trim();
        description = market_description.getText().toString().trim();
        url = market_url.getText().toString().trim();
        userId = firebaseAuth.getCurrentUser().getUid();
        date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH).format(new Date());
        isActive = 1;
//        final String image = downLoadUri + "";
        Random r = new Random();

        shopId = "shop_" + r.nextInt(1000 + 1);
//        if (TextUtils.isEmpty(shopId)) {
//            shopId = databaseReference.push().getKey();
//        }
        if (TextUtils.isEmpty(name)) {
            market_name.setError("يجب ادخال اسم المتجر");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            market_address.setError("يجب ادخال العنوان");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            market_phone.setError("يجب ادخال رقم الجوال");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            market_description.setError("يجب ادخال وصف المتجر");
            return;
        }
        if (market_img.getDrawable() == null) {
            Toast.makeText(this, "يجب إرفاق صورة المتجر", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (downLoadUri == null) {
//            Toast.makeText(this, "يجب إرفاق صورة المتجر", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (byte_arr != null) {
//uploadImage
            final StorageReference filePath = storageReference.child("MarketImage").child("Images").child("Image_" + r.nextInt(1000 + 1) + ".jpg");
            filePath.putBytes(byte_arr).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downLoadUri = taskSnapshot.getDownloadUrl();
                    Log.e("path", downLoadUri + "");
                    String image = downLoadUri + "";
//                    Glide.with(getApplicationContext()).load(downLoadUri)
//                            .crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getApplicationContext())).into(market_img);
                    if (downLoadUri != null) {
                        Market market = new Market(shopId, name, address, phone, description, url, image, date, isActive, userId);

                        databaseReference.child("Shops").child(shopId).setValue(market);
                        finish();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        } else if (selectedImage != null) {
            final StorageReference filePath = storageReference.child("MarketImage").child("Images").child("Image_" + r.nextInt(1000 + 1) + ".jpg");
            filePath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downLoadUri = taskSnapshot.getDownloadUrl();
                    String image = downLoadUri + "";
                    Log.e("path", downLoadUri + "");
                    Log.e("path", image + "");
//                    Glide.with(getApplicationContext()).load(downLoadUri)
//                            .crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getApplicationContext())).into(market_img);
                    if (downLoadUri != null) {
                        Market market = new Market(shopId, name, address, phone, description, url, image, date, isActive, userId);
                        databaseReference.child("Shops").child(shopId).setValue(market);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(this, "يجب إرفاق صورة المتجر", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "saved", Toast.LENGTH_LONG).show();
        finish();
    }

    private void checkWritingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
        }
    }

    private boolean isDeviceSupportCamera() {
        return getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK
                && null != data) {
            // progressBar.setVisibility(View.VISIBLE);
            selectedImage = data.getData();
            Glide.with(getApplicationContext()).load(selectedImage)
                    .crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getApplicationContext())).into(market_img);

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                && null != data) {
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
                fo.write(byte_arr);
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Glide.with(getApplicationContext()).load(destination)
                    .crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getApplicationContext())).into(market_img);

        } else {
            Toast.makeText(this, "You haven't picked Image",
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        AddMarketActivity.this).create();
                alertDialog.setTitle("Alert Dialog");
                alertDialog.setMessage("Welcome to AndroidHive.info");

                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.show();
            }
        }
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


}
