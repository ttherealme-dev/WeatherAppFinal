package com.example.weatherapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.weatherapp.data.WeatherEntry;
import com.example.weatherapp.utils.JSONUtils;
import com.example.weatherapp.utils.NetworkUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActivityWeather extends AppCompatActivity {
    private double lat;
    private double lng;
    private TextView textViewDate;
    private TextView textViewCity;
    private TextView textViewHumidity;
    private TextView textViewPressure;
    private TextView textViewWind;
    private TextView textViewWeatherDescription;
    private TextView textViewHourly1;
    private TextView textViewHourly2;
    private TextView textViewHourly3;
    private TextView textViewHourly4;
    private TextView textViewCurTemp;

    private TextView textViewMon;
    private TextView textViewTue;
    private TextView textViewWed;
    private TextView textViewThu;
    private TextView textViewFri;
    private TextView textViewSat;
    private TextView textViewSun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 50.45466);
        lng = intent.getDoubleExtra("lng", 30.5238);


        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(lat, lng);
        Log.i("Инфа", String.valueOf(jsonObject));
        WeatherEntry currentWeather = JSONUtils.getCurrentWeatherEntryFromJSON(jsonObject);

        textViewDate = findViewById(R.id.textViewDate);
        String date = getDateAndTime1(currentWeather.getDate());
        textViewDate.setText(date);

        textViewCity = findViewById(R.id.textViewCity);
        String city = currentWeather.getTimezone();
        textViewCity.setText(city);

        textViewCurTemp = findViewById(R.id.textViewCurTemp);
        int curTemp = (int) currentWeather.getDayTemp();
        textViewCurTemp.setText("+ " + Integer.toString(curTemp)+ "°");

        textViewWeatherDescription = findViewById(R.id.textViewWeatherDescription);
        String description = currentWeather.getDescription();
        textViewWeatherDescription.setText(description);


        ArrayList<WeatherEntry> hourlyForecast = JSONUtils.getHourlyWeatherEntryFromJSON(jsonObject);


        WeatherEntry twelveHours = hourlyForecast.get(0);
        String date11 = String.valueOf(getDateAndTime2(twelveHours.getDate()));
        String dayTemp11 = (Integer.toString((int) twelveHours.getDayTemp()));
        String description11 = twelveHours.getDescription();
        textViewHourly3 = findViewById(R.id.textViewHourly3);
        textViewHourly3.setText(date11 + "\n" + dayTemp11 + "°" + "\n" + description11);

        WeatherEntry midnightHours = hourlyForecast.get(12);
        String date0 = String.valueOf(getDateAndTime2(midnightHours.getDate()));
        String dayTemp0 = (Integer.toString((int) midnightHours.getDayTemp()));
        String description0 = midnightHours.getDescription();
        textViewHourly4 = findViewById(R.id.textViewHourly4);
        textViewHourly4.setText(date0 + "\n" + dayTemp0 + "°" + "\n" + description0);

        ArrayList<WeatherEntry> dailyForecast = JSONUtils.getDailyWeatherEntryFromJSON(jsonObject);

        WeatherEntry tuesday = dailyForecast.get(1);
        String tuesdayDate = getDateAndTime1(tuesday.getDate());
        String tuesdayDayTemp = Integer.toString((int) tuesday.getDayTemp());
        String tuesdaynightDayTemp = Integer.toString((int) tuesday.getNightTemp());
        String tuesdayDescription = tuesday.getDescription();
        textViewTue = findViewById(R.id.textViewTue);
        textViewTue.setText(tuesdayDate + ", " + " " + tuesdayDayTemp + "°" + "/" + tuesdaynightDayTemp + "°" + "  " + tuesdayDescription);

        WeatherEntry wednesday = dailyForecast.get(2);
        String wednesdayDate = getDateAndTime1(wednesday.getDate());
        String wednesdayDayTemp = Integer.toString((int) wednesday.getDayTemp());
        String wednesdayNightTemp = Integer.toString((int) wednesday.getNightTemp());
        String wednesdayDescription = wednesday.getDescription();
        textViewWed = findViewById(R.id.textViewWed);
        textViewWed.setText(wednesdayDate + ", " + " " + wednesdayDayTemp + "°" + "/" + wednesdayNightTemp + "°" + "  " + wednesdayDescription);

        WeatherEntry thursday = dailyForecast.get(3);
        String thursdayDate = getDateAndTime1(thursday.getDate());
        String thursdayDayTemp = Integer.toString((int) thursday.getDayTemp());
        String thursdaynightDayTemp = Integer.toString((int) thursday.getNightTemp());
        String thursdayDescription = thursday.getDescription();
        textViewThu = findViewById(R.id.Thu);
        textViewThu.setText(thursdayDate + ", " + " " + thursdayDayTemp + "°" + "/" + thursdaynightDayTemp + "°" + "  " + thursdayDescription);

        WeatherEntry friday = dailyForecast.get(4);
        String fridayDate = getDateAndTime1(friday.getDate());
        String fridayDayTemp = Integer.toString((int) friday.getDayTemp());
        String fridaynightDayTemp = Integer.toString((int) friday.getNightTemp());
        String fridayDescription = friday.getDescription();
        textViewFri = findViewById(R.id.textViewFri);
        textViewFri.setText(fridayDate + ", " + " " + fridayDayTemp + "°" + "/" + fridaynightDayTemp + "°" + "  " + fridayDescription);

        WeatherEntry saturday = dailyForecast.get(5);
        String saturdayDate = getDateAndTime1(saturday.getDate());
        String saturdayDayTemp = Integer.toString((int) saturday.getDayTemp());
        String saturdaynightDayTemp = Integer.toString((int) saturday.getNightTemp());
        String saturdayDescription = saturday.getDescription();
        textViewSat = findViewById(R.id.textViewSat);
        textViewSat.setText(saturdayDate + ", " + " " + saturdayDayTemp + "°" + "/" + saturdaynightDayTemp + "°" + "  " + saturdayDescription);

        WeatherEntry sunday = dailyForecast.get(6);
        String sundayDate = getDateAndTime1(sunday.getDate());
        String sundayDayTemp = Integer.toString((int) sunday.getDayTemp());
        String sundaynightDayTemp = Integer.toString((int) sunday.getNightTemp());
        String sundayDescription = sunday.getDescription();
        textViewSun = findViewById(R.id.textViewSun);
        textViewSun.setText(sundayDate + ", " + " " + sundayDayTemp + "°" + "/" + sundaynightDayTemp + "°" + "  " + sundayDescription);

    }

    public static String getDateAndTime1(long seconds) {
        return new SimpleDateFormat("d MMMM", Locale.ENGLISH).format(new Date(seconds * 1000));
    }
    public static String getDateAndTime2(long seconds) {
        return new SimpleDateFormat("H:00", Locale.ENGLISH).format(new Date(seconds * 1000));
    }
}