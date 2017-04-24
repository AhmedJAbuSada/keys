package com.keys.Hraj.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keys.DatabaseSQLite.DBHandler;
import com.keys.R;
import com.keys.Hraj.Adapter.DropDownCitiesAdapter;
import com.keys.Hraj.Adapter.DropDownDepartmentsAdapter;
import com.keys.Hraj.Adapter.DropDownMarketsAdapter;
import com.keys.Hraj.Adapter.GridView_Adapter;
import com.keys.forceRtlIfSupported;
import com.keys.Hraj.Model.Ads;
import com.keys.Hraj.Model.Cities;
import com.keys.Hraj.Model.Departments;
import com.keys.Hraj.Model.Market;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AddAdsActivity extends AppCompatActivity {
    private EditText editTitle, editDept, editCity, editShop, editDes;
    private Typeface font;
    private RecyclerView recyclerView;
    private DropDownCitiesAdapter dropDownCitiesAdapter;
    private DropDownDepartmentsAdapter dropDownDepartmentsAdapter;
    private DropDownMarketsAdapter dropDownMarketsAdapter;
    private List<Market> marketList = new ArrayList<>();
    private List<Cities> citiesList = new ArrayList<>();
    private List<Departments> departmentList = new ArrayList<>();
    private DBHandler db;
    private RecyclerView selectedImageGridView;
    private static final int CustomGallerySelectedId = 1;
    public static final String CustomGalleryIntentKey = "ImageArray";
    public ArrayList<File> imageUrls;
    public ArrayList<String> downloadImageUrls;

    private String AdvId, title, department, city, shop, description, latitude, longitude, date, userId, shopId;
    private boolean isActive;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Button addAds;
    private ImageView location_img;
    private PopupWindow popupWindow;
    private List<String> selectedImages;
    private StorageReference storageReference;
    private Uri downLoadUri;
    private String userName;
    private int REQUEST_CODE_PERMISSION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceRtlIfSupported.forceRtlIfSupported(this);
        setContentView(R.layout.activity_add_ads);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        RelativeLayout attach_img = (RelativeLayout) findViewById(R.id.attach_img);
        RelativeLayout location_layout = (RelativeLayout) findViewById(R.id.location_layout);
        checkWritingPermission();
        selectedImageGridView = (RecyclerView) findViewById(R.id.gridView);
        selectedImageGridView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, GridLayoutManager.HORIZONTAL, true);
        selectedImageGridView.setLayoutManager(layoutManager);

        ImageView back = (ImageView) findViewById(R.id.back);

        editTitle = (EditText) findViewById(R.id.editdrop);
        editDept = (EditText) findViewById(R.id.editdrop1);
        editCity = (EditText) findViewById(R.id.editdrop2);
        editShop = (EditText) findViewById(R.id.editdrop3);
        editDes = (EditText) findViewById(R.id.details_ed);
        addAds = (Button) findViewById(R.id.save);
        location_img = (ImageView) findViewById(R.id.location_img);
        editDept.setFocusable(false);
        editCity.setFocusable(false);
        editShop.setFocusable(false);

        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.input_layout_address);
        TextInputLayout textInputLayout1 = (TextInputLayout) findViewById(R.id.input_layout_details);
        userName = getSharedPreferences("myPrefs", MODE_PRIVATE).getString("userName", "");
        Log.e("userName", userName);

        applyFont(AddAdsActivity.this, layout);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();


        font = Typeface.createFromAsset(getAssets(), "frutigerltarabic_roman.ttf");
        textInputLayout.setTypeface(font);
        textInputLayout1.setTypeface(font);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        prepareDepartmentsData();
        prepareCitiesData();
        prepareMarketsData();
        getSharedImages();

        addAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    Log.e("shop id", shopId);
                    addNewAds();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        attach_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.attach_img:
                        //Start Custom Gallery Activity by passing intent id
                        startActivityForResult(new Intent(AddAdsActivity.this, CustomGallery_Activity.class), CustomGallerySelectedId);
                        break;
                }
            }
        });
        location_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddAdsActivity.this, AddLocationActivity.class));
            }
        });

        editDept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.drop_down_list, null);
                recyclerView = (RecyclerView) popupView.findViewById(R.id.recycler_view1);
                dropDownDepartmentsAdapter = new DropDownDepartmentsAdapter(departmentList, new DropDownDepartmentsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status = departmentList.get(position).getName();
                        editDept.setText(status);
                        popupWindow.dismiss();
                    }
                });
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(dropDownDepartmentsAdapter);

                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setElevation(18f);
                }
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(v);
            }

        });


        editCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.drop_down_list, null);
                recyclerView = (RecyclerView) popupView.findViewById(R.id.recycler_view1);
                dropDownCitiesAdapter = new DropDownCitiesAdapter(citiesList, new DropDownCitiesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String status = citiesList.get(position).getName();
                        editCity.setText(status);
                        popupWindow.dismiss();
                    }
                });
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(dropDownCitiesAdapter);

                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setElevation(18f);
                }
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(v);
            }

        });
        editShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.drop_down_list, null);
                recyclerView = (RecyclerView) popupView.findViewById(R.id.recycler_view1);
                dropDownMarketsAdapter = new DropDownMarketsAdapter(marketList, new DropDownMarketsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        shopId = marketList.get(position).getId();
                        String status = marketList.get(position).getName();
                        editShop.setText(status);
                        popupWindow.dismiss();
                    }
                });
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(dropDownMarketsAdapter);

                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.setElevation(18f);
                }
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(v);
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

    private void prepareDepartmentsData() {
        db = new DBHandler(this);
        departmentList = db.getAllDepartments();
    }

    private void prepareCitiesData() {
        db = new DBHandler(this);
        citiesList = db.getAllCities();
    }

    private void prepareMarketsData() {
        Query queryCity = FirebaseDatabase.getInstance().getReference().child("Shops").orderByChild("userId")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        queryCity.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Log.e("market____", dataSnapshot.getValue() + "");
                marketList.clear();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.e("market", singleSnapshot.getValue() + "");
                    Market market = singleSnapshot.getValue(Market.class);
                    marketList.add(market);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode) {
            case CustomGallerySelectedId:
                if (resultcode == RESULT_OK) {
                    String imagesArray = imagereturnintent.getStringExtra(CustomGalleryIntentKey);//get Intent data
                    //Convert string array into List by splitting by ',' and substring after '[' and before ']'
                    selectedImages = Arrays.asList(imagesArray.substring(1, imagesArray.length() - 1).split(", "));

                    loadGridView(new ArrayList<>(selectedImages));//call load gridview method by passing converted list into arrayList
                }
                break;

        }
    }

    private void loadGridView(ArrayList<String> strings) {
        GridView_Adapter adapter = new GridView_Adapter(this, strings, false);
        selectedImageGridView.setAdapter(adapter);
    }

    //Read Shared Images
    private void getSharedImages() {

        //If Intent Action equals then proceed
        if (Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())
                && getIntent().hasExtra(Intent.EXTRA_STREAM)) {
            ArrayList<Parcelable> list =
                    getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);//get Parcelabe list
            ArrayList<String> selectedImages = new ArrayList<>();

            //Loop to all parcelable list
            for (Parcelable parcel : list) {
                Uri uri = (Uri) parcel;//get URI
                String sourcepath = getPath(uri);//Get Path of URI
                selectedImages.add(sourcepath);//add images to arraylist
            }
            loadGridView(selectedImages);//call load gridview
        }
    }


    //get actual path of uri
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void addNewAds() throws IOException {
        title = editTitle.getText().toString().trim();
        department = editDept.getText().toString().trim();
        city = editCity.getText().toString().trim();
        shop = editShop.getText().toString().trim();

        description = editDes.getText().toString().trim();
        userId = firebaseAuth.getCurrentUser().getUid();
        date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH).format(new Date());
        isActive = false;
        userName = getSharedPreferences("myPrefs", MODE_PRIVATE).getString("userName", "");

//        final String image = downLoadUri + "";
        Random r = new Random();

        AdvId = "ads_" + r.nextInt(1000 + 1);

        if (TextUtils.isEmpty(title)) {
            editTitle.setError("يجب ادخال اسم الاعلان");
            return;
        }
        if (TextUtils.isEmpty(department)) {
            editDept.setError("يجب ادخال القسم");
            return;
        }
        if (TextUtils.isEmpty(city)) {
            editCity.setError("يجب ادخال المدينة");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            editDes.setError("يجب ادخال وصف المتجر");
            return;
        }
        if (TextUtils.isEmpty(latitude) && TextUtils.isEmpty(longitude)) {
            Toast.makeText(AddAdsActivity.this, "يجب ادخال الموقع الجغرافي", Toast.LENGTH_SHORT).show();
            return;
        }
        imageUrls = new ArrayList<>();
        downloadImageUrls = new ArrayList<>();
        for (int i = 0; i < selectedImages.size(); i++) {
            File file = new File(selectedImages.get(i));
            Log.e("file", file + "");
            imageUrls.add(file);
        }
        Log.e("images", imageUrls + "");


        if (selectedImages != null) {
            int i;
            for (i = 0; i < imageUrls.size(); i++) {
                final StorageReference filePath = storageReference.child("MarketImage").child("Images").child("Image_" + r.nextInt(1000 + 1) + ".jpg");
                final int finalI = i;
                filePath.putFile(Uri.fromFile(imageUrls.get(i))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downLoadUri = taskSnapshot.getDownloadUrl();
                        String image = downLoadUri + "";
                        downloadImageUrls.add(downLoadUri + "");
                        Log.e("path", downLoadUri + "");
                        Log.e("path", image + "");
//                    Glide.with(getApplicationContext()).load(downLoadUri)
//                            .crossFade().thumbnail(0.5f).bitmapTransform(new CircleTransform(getApplicationContext())).into(market_img);
                        if (finalI == imageUrls.size() - 1) {
                            Ads ads = new Ads(AdvId, title, department, city, shopId,
                                    shop, description, downloadImageUrls, latitude,
                                    longitude, date, userName, isActive);

                            databaseReference.child("Ads").child(AdvId).setValue(ads);
                        }
                    }
                });
            }
        } else {
            Toast.makeText(this, "يجب إرفاق صورة المتجر", Toast.LENGTH_SHORT).show();
            return;
        }
        getSharedPreferences("myPrefs", MODE_PRIVATE).edit().remove("lattitude").commit();
        getSharedPreferences("myPrefs", MODE_PRIVATE).edit().remove("longittude").commit();
        Toast.makeText(this, "saved", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        latitude = getSharedPreferences("myPrefs", MODE_PRIVATE).getString("lattitude", "");
        longitude = getSharedPreferences("myPrefs", MODE_PRIVATE).getString("longittude", "");

        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            location_img.getDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        } else {
            location_img.getDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

        }
    }

    private void checkWritingPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        AddAdsActivity.this).create();
                alertDialog.setTitle("تنبيه!!!");
                alertDialog.setMessage("هل تسمح لهذا التطبيق باستخدام معرض الصور");

                alertDialog.setButton("نعم", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.show();
            }
        }
    }
}
