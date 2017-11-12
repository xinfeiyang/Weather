package com.security.weather.bean;

import android.test.MoreAsserts;

import com.google.gson.annotations.SerializedName;

/**
 * 天气情况的Now信息；
 */
public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;

        @Override
        public String toString() {
            return "More{" +
                    "info='" + info + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Now{" +
                "temperature='" + temperature + '\'' +
                ", more=" + more +
                '}';
    }
}
