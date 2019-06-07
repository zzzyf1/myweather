package com.example.zyf.myweather.util;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SyncHttpUtil {
    public static String sendOkHttpRequest(String address)
    {
        OkHttpClient client=new OkHttpClient.Builder().readTimeout(5,TimeUnit.SECONDS).build();
        Request request=new Request.Builder().url(address).build();
        Call call=client.newCall(request);
        try{
            Response response=call.execute();
            return response.body().toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
