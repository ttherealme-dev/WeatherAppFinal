package com.example.weatherapp.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
public class NetworkUtils {
    private static final int REQUEST_TIMEOUT = 3000;
    public static final String KEY_LOADER_URL = "url";

    private static final String API_KEY = "0246ec7d4bf6fca92b15293a8a2d11ea";
    private static final String BASE_URL_FORECAST = "https://api.openweathermap.org/data/2.5/onecall";
    private static final String API_KEY_PARAMS = "appid";
    private static final String LATITUDE_PARAMS = "lat";
    private static final String LONGITUDE_PARAMS = "lon";
    private static final String UNITS_PARAMS = "units";

    public static JSONObject getJSONFromNetwork(double latitude, double longitude) {
        return getJSONFromNetwork(Double.toString(latitude), Double.toString(longitude));
    }

    public static JSONObject getJSONFromNetwork(String latitude, String longitude) {
        JSONObject result = null;
        try {
            result = new GetDataTask().execute(buildURL(latitude, longitude)).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static URL buildURL(Double latitude, Double longitude) {
        return buildURL(String.valueOf(latitude), String.valueOf(longitude));
    }

    public static URL buildURL(String latitude, String longitude) {
        String exclude = "daily";
        Uri uri = Uri.parse(BASE_URL_FORECAST).buildUpon()
                .appendQueryParameter(LATITUDE_PARAMS, latitude)
                .appendQueryParameter(LONGITUDE_PARAMS, longitude)
                .appendQueryParameter(UNITS_PARAMS, "metric")
                .appendQueryParameter(API_KEY_PARAMS, API_KEY)
                .build();
        URL result = null;
        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static class GetDataTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {
            if (urls == null || urls.length == 0) {
                return null;
            }
            JSONObject result = null;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                connection.setRequestProperty(API_KEY_PARAMS, API_KEY);
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder builderResult = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    builderResult.append(line);
                    line = reader.readLine();
                }
                result = new JSONObject(builderResult.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }

    public interface LoadingProcessListener {
        void onLoadingStart();
    }

    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        private LoadingProcessListener loadingProcessListener;
        private Bundle bundle;

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        public JSONLoader(@NonNull Context context, Bundle bundle, LoadingProcessListener loadingProcessListener) {
            super(context);
            this.loadingProcessListener = loadingProcessListener;
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (loadingProcessListener != null) {
                loadingProcessListener.onLoadingStart();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle == null || !bundle.containsKey(KEY_LOADER_URL)) {
                return null;
            }
            String urlAsString = bundle.getString(KEY_LOADER_URL);
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null) {
                return null;
            }
            JSONObject result = null;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(REQUEST_TIMEOUT);
                connection.setConnectTimeout(REQUEST_TIMEOUT);
                connection.setRequestProperty(API_KEY_PARAMS, API_KEY);
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                StringBuilder builderResult = new StringBuilder();
                String line = reader.readLine();
                while (line != null) {
                    builderResult.append(line);
                    line = reader.readLine();
                }
                result = new JSONObject(builderResult.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }

    public static boolean isInternetConnection(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }






    public static class CityAsyncTask extends AsyncTask<String, String, String> {
        Activity act;
        double latitude;
        double longitude;

        public CityAsyncTask(Activity act, double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            this.act = act;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            Geocoder geocoder = new Geocoder(act, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                Log.e("Addresses", "-->" + addresses);
                if (addresses != null & addresses.size() > 0) {
                    Address address = addresses.get(0);
                    if (address.getLocality() != null) {
                        result = address.getLocality();
                    }
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

        }
    }
}