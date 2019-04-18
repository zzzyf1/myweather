package com.example.zyf.myweather.db;

import org.litepal.crud.DataSupport;

public class User extends DataSupport {
    private String weatherId;
    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
