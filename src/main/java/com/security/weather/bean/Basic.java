package com.security.weather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 天气情况的basic;
 */
public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;

        @Override
        public String toString() {
            return "Update{" +
                    "updateTime='" + updateTime + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Basic{" +
                "cityName='" + cityName + '\'' +
                ", weatherId='" + weatherId + '\'' +
                ", update=" + update +
                '}';
    }
}
