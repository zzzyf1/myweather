package com.example.zyf.myweather.gson;
//指定城市天气

import java.util.List;

public class Weather {
    public int cityid;
    public String update_time;
    public String city;
    public String country;
    public List<Day> data;
}
