package com.security.weather.http;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.security.weather.bean.City;
import com.security.weather.bean.Country;
import com.security.weather.bean.Province;
import com.security.weather.bean.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 解析数据的工具类;
 */
public class HandleDataUtil {

    /**
     * 解析省份数据,并将省份数据保存进入数据库中;
     * @param response：json格式的省份数据;
     * @return :true代表解析数据成功,false代表解析数据失败;
     */
    public static boolean handleProviceData(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray provinces=new JSONArray(response);
                for(int i=0;i<provinces.length();i++){
                    JSONObject jsonObject=provinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 解析城市数据,并且将城市数据保存进入数据库;
     * @param response：json格式的城市数据;
     * @return :true代表解析数据成功,false代表解析数据失败;
     */
    public static boolean handleCityData(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray cities=new JSONArray(response);
                for(int i=0;i<cities.length();i++){
                    JSONObject jsonObject=cities.getJSONObject(i);
                    City city=new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析县级数据,并将县城数据保存进入数据库;
     * @param response：json格式的县级数据;
     * @return :true代表解析数据成功,false代表解析数据失败;
     */
    public static boolean handleCountryData(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray countries=new JSONArray(response);
                for(int i=0;i<countries.length();i++){
                    JSONObject jsonObject=countries.getJSONObject(i);
                    Country country=new Country();
                    country.setCountryName(jsonObject.getString("name"));
                    country.setWeatherId(jsonObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析Weather信息;
     * @param response:从服务器上获取的数据;
     * @return
     */
    public static Weather handlerWeatherData(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String content=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(content,Weather.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
