package com.security.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;

import com.security.weather.bean.Weather;
import com.security.weather.http.HandleDataUtil;
import com.security.weather.http.HttpUtil;
import com.security.weather.util.ConstantUtil;
import com.security.weather.util.SharedPreferenceUtil;
import com.security.weather.util.UIUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 自动更新天气信息的服务;
 */
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 每次开启服务的时候都会执行;
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateBingPicture();
        updateWeather();
        //创建定时任务;
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        //每隔两个小时执行一次;
        int time=2*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+time;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateBingPicture(){
        HttpUtil.getInstance().sendOKHttpRequest(ConstantUtil.PIC_URL, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String text=response.body().string();
                SharedPreferenceUtil.put(UIUtil.getContext(),"bing_pic",text);
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    /**
     * 更新天气信息;
     */
    private void updateWeather(){
        String weather= (String) SharedPreferenceUtil.get(UIUtil.getContext(),"weather","");
        if(!TextUtils.isEmpty(weather)){
            Weather bean= HandleDataUtil.handlerWeatherData(weather);
            if(bean!=null){
                String weatherId=bean.basic.weatherId;
                HttpUtil.getInstance().sendOKHttpRequest(ConstantUtil.WEATHER_URL + weatherId, new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String text=response.body().string();
                        Weather weather=HandleDataUtil.handlerWeatherData(text);
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferenceUtil.put(UIUtil.getContext(),"weather",text);
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                });
            }
        }
    }
}
