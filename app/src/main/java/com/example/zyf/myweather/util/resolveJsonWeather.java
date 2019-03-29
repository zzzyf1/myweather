package com.example.zyf.myweather.util;

import com.example.zyf.myweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

//解析天气类对象
public class resolveJsonWeather {
    public static Weather resolveWeather(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            String weatherInfo=jsonObject.toString();
            return new Gson().fromJson(weatherInfo,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
