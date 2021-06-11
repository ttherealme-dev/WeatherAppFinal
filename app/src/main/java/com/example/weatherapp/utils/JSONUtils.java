package com.example.weatherapp.utils;

import android.util.Log;

import com.example.weatherapp.data.WeatherEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {

    public static WeatherEntry getCurrentWeatherEntryFromJSON(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        try {
            String timezone = jsonObject.getString("timezone");
            JSONObject jsonObjectCurrent = jsonObject.getJSONObject("current");
            int date = jsonObjectCurrent.getInt("dt");
            double currentTemp = jsonObjectCurrent.getDouble("temp");
            String description = jsonObjectCurrent.getJSONArray("weather").getJSONObject(0).getString("description");
            return new WeatherEntry(timezone, date, currentTemp, 0, description);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<WeatherEntry> getDailyWeatherEntryFromJSON(JSONObject jsonObject) {
        ArrayList<WeatherEntry> dailyWeather = new ArrayList<>();
        if (jsonObject == null) {
            return null;
        }
        try {
            String timezone = jsonObject.getString("timezone");
            JSONArray jsonArrayDaily = jsonObject.getJSONArray("daily");
            for (int i = 0; i < 7; i ++) {

                JSONObject jsonObjectDaily = jsonArrayDaily.getJSONObject(i);
                int date = jsonObjectDaily.getInt("dt");

                double dayTemp = jsonObjectDaily.getJSONObject("temp").getDouble("day");
                double nightTemp = jsonObjectDaily.getJSONObject("temp").getDouble("night");

                String description = jsonObjectDaily.getJSONArray("weather").getJSONObject(0).getString("description");
                dailyWeather.add(new WeatherEntry(timezone, date, dayTemp, nightTemp, description));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dailyWeather;
    }

    public static ArrayList<WeatherEntry> getHourlyWeatherEntryFromJSON(JSONObject jsonObject) {
        ArrayList<WeatherEntry> HourlyWeather = new ArrayList<>();
        if (jsonObject == null) {
            return HourlyWeather;
        }
        try {
                String timezone = jsonObject.getString("timezone");
                JSONArray jsonArrayHourly = jsonObject.getJSONArray("hourly");
                for (int i = 0; i < jsonArrayHourly.length(); i++) {
                    JSONObject jsonObjectHourly = jsonArrayHourly.getJSONObject(i);
                    int date = jsonObjectHourly.getInt("dt");
                    double dayTemp = jsonObjectHourly.getDouble("temp");
                    double nightTemp = jsonObjectHourly.getDouble("temp");
                    String description = jsonObjectHourly.getJSONArray("weather").getJSONObject(0).getString("description");
                    Log.i("Инфа", description);
                    WeatherEntry weatherEntry = new WeatherEntry(timezone, date, dayTemp, nightTemp, description);
                    HourlyWeather.add(weatherEntry);
            }return HourlyWeather;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return HourlyWeather;
    }
}
