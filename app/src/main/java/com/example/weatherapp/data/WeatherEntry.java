package com.example.weatherapp.data;

public class WeatherEntry {
        private String timezone;
        private int date;
        private double dayTemp;
        private double nightTemp;
        private String description;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public double getDayTemp() {
        return dayTemp;
    }

    public void setDayTemp(double dayTemp) {
        this.dayTemp = dayTemp;
    }

    public double getNightTemp() {
        return nightTemp;
    }

    public void setNightTemp(double nightTemp) {
        this.nightTemp = nightTemp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WeatherEntry(String timezone, int date, double maxTemp, double minTemp, String description) {
        this.timezone = timezone;
        this.date = date;
        this.dayTemp = maxTemp;
        this.nightTemp = minTemp;
        this.description = description;
    }
}


