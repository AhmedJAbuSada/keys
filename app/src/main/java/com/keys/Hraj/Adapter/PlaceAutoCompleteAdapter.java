package com.keys.Hraj.Adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.keys.R;
import com.keys.widgets.AutoCompletePlace;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private ArrayList<AutoCompletePlace> resultList = new ArrayList<>();
    private ArrayList<AutoCompletePlace> placeList = new ArrayList<>();
    private String data;

    public PlaceAutoCompleteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public AutoCompletePlace getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_text_item, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.tv_text)).setText(getItem(position).getDesc());
        //   ((TextView) convertView.findViewById(R.id.text2)).setText(getItem(position).getAuthor());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    ArrayList<AutoCompletePlace> users;
//
                    users = getPlaces(constraint.toString());
                    notifyDataSetChanged();
                    // Assign the data to the FilterResults
                    filterResults.values = users;
                    filterResults.count = users.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (ArrayList<AutoCompletePlace>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }


    private ArrayList<AutoCompletePlace> getPlaces(final String AutoCompletePlace) {

        new GeoLocTask().execute(AutoCompletePlace);
        return placeList;
    }

    private class GeoLocTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... text) {
            final Map<String, String> params = new HashMap<>();

            params.put("input", (text[0]));
            params.put("key", ("AIzaSyA0XV1i3oDxzx_thX8M2LbNfODOCEcguS0"));
            //  context.getString(R.string.SERVER_KEY);
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyA0XV1i3oDxzx_thX8M2LbNfODOCEcguS0&input=" + text[0];
            try {
                // Fetching the data from we service
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

//
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            Log.i("//////////*",result);
            try {
                ArrayList<AutoCompletePlace> placeArrayList = new ArrayList<>();

                JSONObject jsonObject=new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("ok")) {
                    JSONArray predictions = jsonObject.getJSONArray("predictions");
                    for (int i = 0; i < predictions.length(); i++) {
                        JSONObject jresult = predictions.getJSONObject(i);
                        String description = jresult.getString("description");
                        String place_id = jresult.getString("place_id");
                        AutoCompletePlace place = new AutoCompletePlace();
                        place.setPlaceId(place_id);
                        place.setDesc(description);
                        placeArrayList.add(place);
                    }
                    placeList.clear();
                    placeList.addAll(placeArrayList);
                    notifyDataSetChanged();
                } else {
//Toast fail
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        HttpURLConnection urlConnection;
        URL url = new URL(strUrl);

        // Creating an http connection to communicate with url
        urlConnection = (HttpURLConnection) url.openConnection();

        // Connecting to url
        urlConnection.connect();

        // Reading data from url
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
}