package com.example.zyf.myweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        //装载请求
        Request request=new Request.Builder().url(address).build();
        //发送请求
        client.newCall(request).enqueue(callback);
    }
}
