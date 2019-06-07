package com.example.zyf.myweather.db;

import org.litepal.crud.DataSupport;

public class User extends DataSupport {
    private String weatherId;
    private String city;//所属的城市

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
