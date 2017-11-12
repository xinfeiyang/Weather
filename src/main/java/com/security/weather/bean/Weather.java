package com.security.weather.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 天气状况
 */
public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    @Override
    public String toString() {
        return "Weather{" +
                "status='" + status + '\'' +
                ", basic=" + basic +
                ", aqi=" + aqi +
                ", now=" + now +
                ", suggestion=" + suggestion +
                ", forecastList=" + forecastList +
                '}';
    }
}
