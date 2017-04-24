package com.keys.Hraj.Activity;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.keys.R;
import com.keys.Hraj.Adapter.PlaceAutoCompleteAdapter;
import com.keys.Hraj.Adapter.PlaceJSONParser;
import com.keys.forceRtlIfSupported;
import com.keys.widgets.AutoCompletePlace;
import com.keys.widgets.DelayAutoCompleteTextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class AddLocationActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, LocationListener {
    //private MapController mapController;
    private FloatingActionButton ok, gps;
    private Toolbar tool_bar;
    private int requestCode;
    final private double MAP_DEFAULT_LATITUDE = 24.350584;
    final private double MAP_DEFAULT_LONGITUDE = 53.9663425;
    private LatLng startPoint;
    private double lattitude = -1;
    private double longittude = -1;
    private LocationManager locationManager;
    private Location location;
    private boolean isGPSEnabled;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 sec
    double lat, lng;
    private GoogleMap map;
    private SharedPreferences sp;
    private SharedPreferences.Editor spedit;
    Activity activity;
    private MarkerOptions myMarker;
    private String userId;
    Intent intent;
    ProgressBar progressBar, progressSearch;
    private RelativeLayout parentPanel;
    private TextView confirm;
    private RelativeLayout rl_confirm;
    private double myLat, myLon;
    private AutoCompleteTextView myLocation;
    AutoCompleteTextView atvPlaces;
    PlacesTask placesTask;
    ParserTask parserTask;
    private String placeId;
    private String placeDesc;
    private ParserLocationTask parserLocationTask;
    private PlaceLocationTask placeLocationTask;
    private AlertDialog dialog;
    private ParserGeoLocationTask parserGeoLocationTask;
    private String address;
    private DelayAutoCompleteTextView ed_place;
    private ProgressBar prog;
    MapFragment mapFragment;
    private ImageView mapType;
    private boolean click;
    private String latLon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        sp = getSharedPreferences("myPrefs", MODE_PRIVATE);
        spedit = sp.edit();
        forceRtlIfSupported.forceRtlIfSupported(this);

        setContentView(R.layout.activity_add_location);

        userId = sp.getString("userId", "");

        Intent inte = getIntent();
        requestCode = inte.getIntExtra("Request", -1);
        parentPanel = (RelativeLayout) findViewById(R.id.parentPanel);
        confirm = (TextView) findViewById(R.id.confirm);
        mapType = (ImageView) findViewById(R.id.mapType);

        ed_place = (DelayAutoCompleteTextView) findViewById(R.id.etPlaceAddress);
        ed_place.setThreshold(0);
        ed_place.setAdapter(new PlaceAutoCompleteAdapter(this));
        prog = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        ed_place.setLoadingIndicator(prog);
        ed_place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AutoCompletePlace user = (AutoCompletePlace) adapterView.getItemAtPosition(position);
                ed_place.setText(user.getDesc());
                placeId = user.getPlaceId();
                prog.setVisibility(View.GONE);
                placeLocationTask = new PlaceLocationTask();
                placeLocationTask.execute(placeId);
            }
        });
        ed_place.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ed_place.setError(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                ed_place.setError(null);

            }
        });

//        applyFont();


        //  SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        mapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMapType();
            }
        });
        rl_confirm = (RelativeLayout) findViewById(R.id.rl_confirm);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), android.graphics.PorterDuff.Mode.MULTIPLY);
        progressSearch = (ProgressBar) findViewById(R.id.progressSearch);

        //ok = (FloatingActionButton) findViewById(R.id.ok);
        rl_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClicked();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClicked();
            }
        });

    }

//    private void applyFont() {
//        if (sp.getString("language", "").equalsIgnoreCase(AppConst.ARABIC) ||
//                getResources().getConfiguration().locale.getDisplayLanguage().equalsIgnoreCase(getResources()
//                        .getString(R.string.Arabic))) {
//            AppConst.applyFont(activity, parentPanel);
//            Log.i("lang ar", sp.getString("language", ""));
//
//        } else if (sp.getString("language", "").equalsIgnoreCase(AppConst.ENGLISH) ||
//                getResources().getConfiguration().locale.getDisplayLanguage().equalsIgnoreCase(getResources()
//                        .getString(R.string.English))) {
//            Log.i("lang en ", sp.getString("language", ""));
//            AppConst.applyFontEng(activity, parentPanel);
//        } else
//            AppConst.applyFont(activity, parentPanel);
//    }

    public void changeMapType() {
        if (map != null) {
            int type = map.getMapType();
            switch (type) {
                case GoogleMap.MAP_TYPE_NORMAL:
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;
                case GoogleMap.MAP_TYPE_SATELLITE:
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    break;
                case GoogleMap.MAP_TYPE_TERRAIN:
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;
                case GoogleMap.MAP_TYPE_HYBRID:
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
            }
        }
    }

    private void onClicked() {
        getSharedPreferences("myPrefs", MODE_PRIVATE).edit().putString("lattitude", lattitude + "").commit();
        getSharedPreferences("myPrefs", MODE_PRIVATE).edit().putString("longittude", longittude + "").commit();
        finish();
    }

//    private void addLocation(final double lattitude, final double longittude, final String address) {
//        progress.showNow();
//        progressBar.setVisibility(View.VISIBLE);
//
//        if (userId != null & address != null & lattitude != 0 & longittude != 0) {
//            progress.hideNow();
//            progressBar.setVisibility(View.GONE);
//
//            setResult(102, intent);
//            finish();
//        } else {
//            progress.hideNow();
//            progressBar.setVisibility(View.GONE);
//
//            Toast.makeText(activity, getString(R.string.error), Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void useGPS() {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnabled) {

            if (location == null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("GPS : ", "Enabled!");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.d("GGG", "Location not null!");
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        lattitude = location.getLatitude();
                        longittude = location.getLongitude();
                        startPoint = new LatLng(lat, lng);
                        Log.d("GGG", lat + " " + lng);
                        //  map.clear();
                        LatLng coordinate = new LatLng(lat, lng);
//                        myMarker = new MarkerOptions().position(coordinate)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
//                        map.addMarker(myMarker);
                        CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                        map.moveCamera(center);
                        map.animateCamera(zoom);
                        spedit.putString("lat", lat + "").commit();
                        spedit.putString("lng", lng + "").commit();
                        // Toast.makeText(activity, " wifi lat: " + lat + " lng: " + lng, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        } else
            showSettingsAlert();
    }

    public void useWIFI() {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.i("aaa", isGPSEnabled + "");
        if (isGPSEnabled && checkConn(activity)) {

            if (location == null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("WIFI : ", "Enabled!");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        Log.d("GGG", "Location not null!");
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                        lattitude = location.getLatitude();
                        longittude = location.getLongitude();
                        startPoint = new LatLng(lat, lng);
                        Log.d("GGG", lat + " " + lng);
//                        map.clear();
                        LatLng coordinate = new LatLng(lat, lng);
//                        myMarker = new MarkerOptions().position(coordinate)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
//                        map.addMarker(myMarker);
                        CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                        map.moveCamera(center);
                        map.animateCamera(zoom);
                        spedit.putString("lat", lat + "").commit();
                        spedit.putString("lng", lng + "").commit();
                        // Toast.makeText(activity, " wifi lat: " + lat + " lng: " + lng, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        } else
            useGPS();
    }

    public boolean checkConn(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() == null)
            return false;
        if (conMgr.getActiveNetworkInfo().isConnected()) {
            return true;
        } else if (!conMgr.getActiveNetworkInfo().isConnected()) {
            return false;
        }
        return false;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity).setInverseBackgroundForced(true);
        dialog = builder.create();
        // AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.delete_dialoge, null, false);
        dialog.setView(vi, 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(true);
       // LinearLayout parentPanel = (LinearLayout) vi.findViewById(R.id.parentPanel);
       // TextView tittle = (TextView) vi.findViewById(R.id.title);
      //  TextView message = (TextView) vi.findViewById(R.id.message);
        TextView ok = (TextView) vi.findViewById(R.id.btn_ok);
        TextView cancle = (TextView) vi.findViewById(R.id.btn_change);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1999);
                dialog.dismiss();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }


//    public void stopUsingGPS() {
//        if (locationManager != null) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            locationManager.removeUpdates(AddLocationActivity.this);
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setOnCameraIdleListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setCompassEnabled(false);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(25.247003, 44.395114), 5));
        map.animateCamera(CameraUpdateFactory.zoomTo(5));
        map.setMyLocationEnabled(true);
        if (!TextUtils.isEmpty(sp.getString("lat", ""))) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(sp.getString("lat", "")),
                    Double.parseDouble(sp.getString("lng", ""))));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            map.moveCamera(center);
            map.animateCamera(zoom);
        }//else
        useWIFI();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        hideKeyBoard();
        map.clear();
        lattitude = latLng.latitude;
        longittude = latLng.longitude;

        LatLng coordinate = new LatLng(lattitude, longittude);
//        myMarker = new MarkerOptions().position(coordinate).title("My Position").snippet("Position")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
        //map.addMarker(myMarker);
        CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        map.moveCamera(center);
        map.animateCamera(zoom);
        GeoLocTask geoLocTask = new GeoLocTask();
        String latLon = lattitude + "," + longittude;
        geoLocTask.execute(latLon);
        prog.setVisibility(View.VISIBLE);
        ed_place.setText("");
        ed_place.setHint(getString(R.string.searching));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // if (requestCode==100){
        map.clear();
        lattitude = latLng.latitude;
        longittude = latLng.longitude;

        LatLng coordinate = new LatLng(lattitude, longittude);
//        myMarker = new MarkerOptions().position(coordinate)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
//        map.addMarker(myMarker);
        CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        map.moveCamera(center);
        map.animateCamera(zoom);
        GeoLocTask geoLocTask = new GeoLocTask();
        String latLon = lattitude + "," + longittude;
        geoLocTask.execute(latLon);
        prog.setVisibility(View.VISIBLE);
        ed_place.setHint(getString(R.string.searching));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(activity, marker.getSnippet(), Toast.LENGTH_LONG).show();

    }

    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void close(View v) {
        hideKeyBoard();
        finish();
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        HttpURLConnection urlConnection;
        URL url = new URL(strUrl);
        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.connect();
        try (InputStream iStream = urlConnection.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Ex while download url", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onCameraIdle() {
        if (click) {
            click = false;
        } else {
            Log.i("aaa", map.getCameraPosition().target.latitude + " : " + map.getCameraPosition().target.longitude);
            lattitude = map.getCameraPosition().target.latitude;
            longittude = map.getCameraPosition().target.longitude;
            LatLng latlang = new LatLng(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude);
            map.clear();
//            Marker marker1 = map.addMarker(new MarkerOptions().position(latlang).draggable(true));
//            animateMarker(marker1, latlang, false);

            GeoLocTask geoLocTask = new GeoLocTask();
            latLon = lattitude + "," + longittude;
            geoLocTask.execute(latLon);
            prog.setVisibility(View.VISIBLE);
            ed_place.setHint(getString(R.string.searching));
            click = false;

        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            String data = "";
            String input;
            input = Uri.encode(place[0], "utf-8");
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?key=AIzaSyA0XV1i3oDxzx_thX8M2LbNfODOCEcguS0&input=" + input;
            Log.i("url", url);

            try {
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.i("***-", jObject + "");
                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(final List<HashMap<String, String>> result) {
            progressSearch.setVisibility(View.GONE);

            final String[] from = new String[]{"description"};
            int[] to = new int[]{R.id.tv_text};

            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, R.layout.simple_text_item, from, to);

            atvPlaces.setAdapter(adapter);
            atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    placeId = result.get(position).get("_id");
                    placeDesc = result.get(position).get("description");
                    placeLocationTask = new PlaceLocationTask();
                    placeLocationTask.execute(placeId);
                }
            });
        }
    }

    private class PlaceLocationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... placeId) {
            String data = "";

            String input;

            input = Uri.encode(placeId[0], "utf-8");

            String url = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyApKg_q24D4htWZKc4tOSuiG1DHtmhRfDA&placeid=" + input;
            Log.i("url", url);

            try {
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parserLocationTask = new ParserLocationTask();
            parserLocationTask.execute(result);
        }
    }

    private class ParserLocationTask extends AsyncTask<String, Integer, AutoCompletePlace> {

        JSONObject jObject;

        @Override
        protected AutoCompletePlace doInBackground(String... jsonData) {

            AutoCompletePlace places = new AutoCompletePlace();

            //PlaceJSONParser placeJsonParser = new PlaceJSONParser();
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.i("***-", jObject + "");
                JSONObject result = jObject.getJSONObject("result");
                JSONObject geometry = result.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                places.setDesc(placeDesc);
                places.setPlaceId(placeId);
                places.setLat(lat);
                places.setLon(lng);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(AutoCompletePlace result) {
            InputMethodManager im = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            lattitude = result.getLat();
            longittude = result.getLon();
            LatLng coordinate = new LatLng(lattitude, longittude);
            click = true;
            CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
            map.moveCamera(center);
        }
    }

    private class GeoLocTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... latLong) {
            String data = "";
            String input = latLong[0];
            String link = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + input + "&key=AIzaSyB6dHB4BHYdXQWuKmxUjhFp_4r0VXO-Mqo&location_type=GEOMETRIC_CENTER";
            Log.i("link", link);

            try {
                data = downloadUrl(link);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parserGeoLocationTask = new ParserGeoLocationTask();
            parserGeoLocationTask.execute(result);
        }
    }

    private class ParserGeoLocationTask extends AsyncTask<String, Integer, String> {

        JSONObject jObject;

        @Override
        protected String doInBackground(String... jsonData) {
            // AutoCompletePlace places = new AutoCompletePlace();
            // PlaceJSONParser placeJsonParser = new PlaceJSONParser();
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.i("***-", jObject + "");
                String status = jObject.getString("status");
                if (status.equalsIgnoreCase("OK")) {
                    JSONArray result = jObject.getJSONArray("results");
                    JSONObject data = result.getJSONObject(0);
                    address = data.getString("formatted_address");
                } else address = "";

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return address;
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.isEmpty(address)) {
                ed_place.setText(latLon);
                ed_place.setHint(getString(R.string.no_result));
                prog.setVisibility(View.GONE);
            } else {
                ed_place.setText(address);
                prog.setVisibility(View.GONE);
            }
        }
    }

}
