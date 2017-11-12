package com.security.weather.http;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 *网络连接工具类
 */
public class HttpUtil {

    private OkHttpClient okHttpClient;
    private static HttpUtil instance;
    private final static long CONNECT_TIMEOUT = 60;//超时时间，秒
    private final static long READ_TIMEOUT = 60;//读取时间，秒
    private final static long WRITE_TIMEOUT = 60;//写入时间，秒


    /**
     * 私有化构造方法,配置OkHttpClient;
     */
    private HttpUtil(){
        okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 单利模式;
     * @return :HttpUtil
     */
    public static HttpUtil getInstance(){
        if(instance==null){
            synchronized (HttpUtil.class){
                if(instance==null){
                    instance=new HttpUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 发送GET请求
     * @param url:请求地址;
     * @param callback:结果回调;
     */
    public void sendOKHttpRequest(String url, Callback callback){
        //创建一个Request
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

}
