package com.security.weather.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.security.weather.R;
import com.security.weather.bean.Forecast;
import com.security.weather.bean.Weather;
import com.security.weather.http.HandleDataUtil;
import com.security.weather.http.HttpUtil;
import com.security.weather.service.AutoUpdateService;
import com.security.weather.util.ConstantUtil;
import com.security.weather.util.SharedPreferenceUtil;
import com.security.weather.util.UIUtil;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 天气信息
 */
public class WeatherActivaity extends AppCompatActivity {

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;
    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();
        initData();
        initListener();
    }

    /**
     * 初始化监听器;
     */
    private void initListener() {

        //手动刷新天气信息;
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        /**
         * 展示左侧窗体;
         */
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    /**
     * 初始化数据;
     */
    private void initData() {
        //获取信息;
        weatherId = getIntent().getStringExtra("weather_id");
        String weatherData= (String) SharedPreferenceUtil.get(WeatherActivaity.this,"weather","");
        if(!TextUtils.isEmpty(weatherData)){
            Weather weather=HandleDataUtil.handlerWeatherData(weatherData);
            Log.i("WEATHER", "initData: "+weather);
            showWeatherInfo(weather);
        }else{
            requestWeather(weatherId);
            weatherLayout.setVisibility(View.INVISIBLE);
        }
        //加载图片信息;
        String pic= (String) SharedPreferenceUtil.get(UIUtil.getContext(),"bing_pic","");
        if(!TextUtils.isEmpty(pic)){
            Glide.with(WeatherActivaity.this).load(pic).into(bingPicImg);
        }else{
            loadBingPicture();
        }
    }

    /**
     * 初始化View;
     */
    private void initView() {
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
    }


    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        if(weather!=null){
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            forecastLayout.removeAllViews();
            for (Forecast forecast:weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dateText = (TextView) view.findViewById(R.id.date_text);
                TextView infoText = (TextView) view.findViewById(R.id.info_text);
                TextView maxText = (TextView) view.findViewById(R.id.max_text);
                TextView minText = (TextView) view.findViewById(R.id.min_text);
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }
            if (weather.aqi != null) {
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comfort = "舒适度：" + weather.suggestion.comfort.info;
            String carWash = "洗车指数：" + weather.suggestion.carWash.info;
            String sport = "运行建议：" + weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
        }

        //开启自动更新后台服务;
        Intent intent = new Intent(this,AutoUpdateService.class);
        startService(intent);
    }


    /**
     * 根据天气id请求城市天气信息;
     * @param weatherId：城镇的天气id;
     */
    public void requestWeather(String weatherId){
        HttpUtil.getInstance().sendOKHttpRequest(ConstantUtil.WEATHER_URL + weatherId, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData=response.body().string();
                final Weather weather= HandleDataUtil.handlerWeatherData(responseData);
                UIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferenceUtil.put(WeatherActivaity.this,"weather",responseData);
                            //展示天气信息;
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivaity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        //停止刷新;
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                UIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivaity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        //停止刷新;
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPicture();
    }


    /**
     * 加载每日一图;
     */
    private void loadBingPicture(){
        HttpUtil.getInstance().sendOKHttpRequest(ConstantUtil.PIC_URL, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String text=response.body().string();
                //将图片信息保存进入SharedPreference;
                SharedPreferenceUtil.put(UIUtil.getContext(),"bing_pic",text);
                UIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivaity.this).load(text).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(UIUtil.getContext(), "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 设置当前的weatherId;
     * @param weatherId
     */
    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
